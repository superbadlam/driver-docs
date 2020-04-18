package ru.driverdocs.ui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.driverdocs.DriverDocsSetting;
import ru.driverdocs.domain.Employer;
import ru.driverdocs.domain.EmployerLicense;
import ru.driverdocs.helpers.ui.ErrorInformer2;
import ru.driverdocs.rxrepositories.EmployerLicenseRepository;
import ru.driverdocs.ui.data.EmployerImpl;
import ru.driverdocs.ui.data.EmployerLicenseImpl;
import ru.driverdocs.ui.validator.EmployerLicenseValidator;

import java.util.NoSuchElementException;

import static ru.driverdocs.ui.ControlUtils.load;
import static ru.driverdocs.ui.ControlUtils.whenFocusLost;

public class EmployerLicenceControl extends VBox {
    private static final String FXML_FILE = "/fxml/EmployerLicenceControl.fxml";
    private static final Logger log = LoggerFactory.getLogger(EmployerLicenceControl.class);
    private final EmployerLicenseRepository employerLicenseRepository
            = DriverDocsSetting.getInstance().getEmployerLicenseRepository();
    private final SimpleObjectProperty<EmployerImpl> currEmployer
            = new SimpleObjectProperty<>();
    private final EmployerLicenseImpl currLicense = new EmployerLicenseImpl();
    private final EmployerLicenseValidator validator = new EmployerLicenseValidator();
    private final ErrorInformer2 errorInformer =
            new ErrorInformer2(DriverDocsSetting.getInstance().getCssUrl());

    @FXML
    private TextField txtLicSeries;
    @FXML
    private TextField txtLicNumber;
    @FXML
    private DatePicker dtLicStart;
    @FXML
    private Button btnLicApply;
    @FXML
    private Button btnLicDelete;

    public EmployerLicenceControl() {
        load(this, FXML_FILE);
    }

    public SimpleObjectProperty<EmployerImpl> employerProperty() {
        return currEmployer;
    }

    protected void clearControl() {
        currLicense.setId(0);
        currLicense.setSeries("");
        currLicense.setNumber("");
        currLicense.setStartdate(null);
    }

    private void findLicense(Employer employer) {
        try {
            EmployerLicense license = employerLicenseRepository
                    .findByEmployerId(employer.getId()).blockingGet();
            currLicense.setId(license.getId());
            currLicense.setSeries(license.getSeries());
            currLicense.setNumber(license.getNumber());
            currLicense.setStartdate(license.getStartdate());
            log.trace("нашли лицензию предпринимателя: employer={} license={}", employer, currLicense);
        } catch (NoSuchElementException e) {
            clearControl();
            log.warn("не удалось найти лицензию предпринимателя: employer={}", employer);
        }
    }

    @FXML
    private void initialize() {


        btnLicApply.disableProperty().bind(currLicense.invalidProperty());
        btnLicDelete.disableProperty().bind(currLicense.idProperty().isEqualTo(0));
        txtLicSeries.disableProperty().bind(currEmployer.isNull());
        txtLicNumber.disableProperty().bind(currEmployer.isNull());
        dtLicStart.disableProperty().bind(currEmployer.isNull());

        currLicense.seriesProperty().bindBidirectional(txtLicSeries.textProperty());
        currLicense.numberProperty().bindBidirectional(txtLicNumber.textProperty());
        currLicense.startdateProperty().bindBidirectional(dtLicStart.valueProperty());

        whenFocusLost(txtLicSeries, this::validateSeries);
        whenFocusLost(txtLicNumber, this::validateNumber);
        whenFocusLost(dtLicStart, this::validateDate);

        btnLicApply.setOnAction(this::onApplyClick);
        btnLicDelete.setOnAction(this::onDeleteClick);

        currEmployer.addListener((observable, oldValue, newValue) -> findLicense(newValue));

    }

    private void onDeleteClick(ActionEvent actionEvent) {
        try {
            employerLicenseRepository.delete(currLicense.getId()).blockingAwait();
            log.info("удалили лицензию предпринимателя: employer={}, license={}", currEmployer.get(), currLicense);
            clearControl();
        } catch (Exception e) {
            log.warn(String.format("не удалось удалить лицензию предпринимателя: driver=%s, license=%s",
                    currEmployer.get(), currLicense), e);
            errorInformer.displayWarning("не удалось удалить лицензию предпринимателя");
        }
    }

    private void onApplyClick(ActionEvent actionEvent) {
        if (currLicense.getId() == 0) {
            currLicense.setId(
                    employerLicenseRepository.create(
                            currEmployer.get().getId(),
                            currLicense.getSeries(), currLicense.getNumber(),
                            currLicense.getStartdate())
                            .blockingGet()
            );
            log.info("создали новую лицензию предпринимателя: employer={}, license={}",
                    currEmployer.get(), currLicense.toString());
        } else {
            employerLicenseRepository.update(
                    currLicense.getId(), currLicense.getSeries(), currLicense.getNumber(),
                    currLicense.getStartdate()).blockingAwait();
            log.info("обновили лицензию предпринимателя: employer={}, license={}", currEmployer.get(), currLicense);
        }
    }


    private void validateDate() {
        if (!validator.isValidDate(dtLicStart.getValue())) {
            log.warn("дата начала лицензии предпринимателя " +
                    "startdate={}", dtLicStart.getValue());
            errorInformer.displayWarning("некорректная дата!");
            dtLicStart.setValue(null);
        }
    }

    private void validateSeries() {
        if (!validator.isValidSeries(txtLicSeries.getText())) {
            log.warn("серия водительского удостоверения имеет некорректное значение: series={}",
                    txtLicSeries.getText());
            errorInformer.displayWarning("серия имеет некорректное значение!");
            txtLicSeries.clear();
        }
    }

    private void validateNumber() {
        if (!validator.isValidNumber(txtLicNumber.getText())) {
            log.warn("номер водительского удостоверения имеет некорректное значение: number={}", txtLicNumber.getText());
            errorInformer.displayWarning("номер имеет некорректное значение!");
            txtLicNumber.clear();
        }
    }
}