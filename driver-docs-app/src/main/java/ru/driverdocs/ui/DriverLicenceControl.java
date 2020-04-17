package ru.driverdocs.ui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.driverdocs.DriverDocsSetting;
import ru.driverdocs.domain.DriverLicense;
import ru.driverdocs.helpers.ui.ErrorInformer2;
import ru.driverdocs.rxrepositories.DriverLicenseRepository;
import ru.driverdocs.ui.data.DriverImpl;
import ru.driverdocs.ui.data.DriverLicenseImpl;
import ru.driverdocs.ui.validator.DriverLicenseValidator;

import java.io.IOException;
import java.util.NoSuchElementException;

public class DriverLicenceControl extends VBox {
    private static final String FXML_FILE = "/fxml/DriverLicenceControl.fxml";
    private static final Logger log = LoggerFactory.getLogger(DriverLicenceControl.class);
    private final DriverLicenseRepository driverLicenseRepository
            = DriverDocsSetting.getInstance().getDriverLicenseRepository();
    private final SimpleObjectProperty<DriverImpl> currDriver
            = new SimpleObjectProperty<>();
    private final DriverLicenseImpl currLicense = new DriverLicenseImpl();
    private final ErrorInformer2 errorInformer
            = new ErrorInformer2(DriverDocsSetting.getInstance().getCssUrl());
    private final DriverLicenseValidator validator = new DriverLicenseValidator();

    @FXML
    private TextField txtLicSeries;
    @FXML
    private TextField txtLicNumber;
    @FXML
    private DatePicker dtLicStart;
    @FXML
    private DatePicker dtLicEnd;
    @FXML
    private Button btnLicApply;
    @FXML
    private Button btnLicDelete;

    public DriverLicenceControl() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML_FILE));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public SimpleObjectProperty<DriverImpl> driverProperty() {
        return currDriver;
    }

    private void clearControl() {
        currLicense.setId(0);
        currLicense.setSeries("");
        currLicense.setNumber("");
        currLicense.setStartdate(null);
        currLicense.setEnddate(null);
    }

    private void findLicense(DriverImpl driver) {
        try {
            DriverLicense license = driverLicenseRepository.findByDriverId(driver.getId()).blockingGet();
            currLicense.setId(license.getId());
            currLicense.setSeries(license.getSeries());
            currLicense.setNumber(license.getNumber());
            currLicense.setStartdate(license.getStartdate());
            currLicense.setEnddate(license.getEnddate());
            log.trace("нашли вод. удостоверение для водителя: driver={} license={}", driver, currLicense);
        } catch (NoSuchElementException e) {
            clearControl();
            log.warn("не удалось найти удостоверение для водителя: driver={}", driver);
        }
    }

    @FXML
    private void initialize() {

        dtLicStart.valueProperty().addListener(ev -> dtLicEnd.setValue(
                dtLicStart.getValue() == null ? null : dtLicStart.getValue().plusYears(10)));

        btnLicApply.disableProperty().bind(currLicense.invalidProperty());
        btnLicDelete.disableProperty().bind(currLicense.idProperty().isEqualTo(0));
        txtLicSeries.disableProperty().bind(currDriver.isNull());
        txtLicNumber.disableProperty().bind(currDriver.isNull());
        dtLicStart.disableProperty().bind(currDriver.isNull());
        dtLicEnd.setDisable(true);

        currLicense.seriesProperty().bindBidirectional(txtLicSeries.textProperty());
        currLicense.numberProperty().bindBidirectional(txtLicNumber.textProperty());
        currLicense.startdateProperty().bindBidirectional(dtLicStart.valueProperty());
        currLicense.enddateProperty().bindBidirectional(dtLicEnd.valueProperty());

        whenFocusLost(txtLicSeries, this::validateSeries);
        whenFocusLost(txtLicNumber, this::validateNumber);
        whenFocusLost(dtLicStart, this::validateDateRange);

        btnLicApply.setOnAction(this::onApplyClick);
        btnLicDelete.setOnAction(this::onDeleteClick);

        currDriver.addListener((observable, oldValue, newValue) -> findLicense(newValue));

    }

    private void onDeleteClick(ActionEvent actionEvent) {
        try {
            driverLicenseRepository.delete(currLicense.getId()).blockingAwait();
            log.info("удалили вод. удостоверение: driver={}, license={}", currDriver.get(), currLicense.toString());
            clearControl();
        } catch (Exception e) {
            log.warn("не удалось удалить вод. удостоверение: driver={}, license={}", currDriver.get(), currLicense.toString());
            errorInformer.displayWarning("не удалось удалить вод. удостоверение!");
        }
    }

    private void onApplyClick(ActionEvent actionEvent) {
        if (currLicense.getId() == 0) {
            currLicense.setId(
                    driverLicenseRepository.create(
                            currDriver.get().getId(),
                            currLicense.getSeries(), currLicense.getNumber(),
                            currLicense.getStartdate(), currLicense.getEnddate())
                            .blockingGet()
            );
            log.info("создали новое вод. удостоверение: driver={}, license={}", currDriver.get(), currLicense.toString());
        } else {
            driverLicenseRepository.update(currLicense.getId(), currLicense.getSeries(), currLicense.getNumber(),
                    currLicense.getStartdate(), currLicense.getEnddate()).blockingAwait();
            log.info("обновили вод. удостоверение: driver={}, license={}", currDriver.get(), currLicense.toString());
        }
    }

    private void whenFocusLost(Control control, ValidatePerformer validatePerformer) {
        control.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue)
                validatePerformer.perform();
        });
    }

    private void validateDateRange() {
        if (!validator.isValidDateRange(dtLicStart.getValue(), dtLicEnd.getValue())) {
            log.warn("дата начала и/или дата окончания " +
                    "вод. удостоверения имеют некорректное значения : " +
                    "startdate={}, enddate={}", dtLicStart.getValue(), dtLicEnd.getValue());
            errorInformer.displayWarning("некорректные дата!");
            dtLicStart.setValue(null);
            dtLicEnd.setValue(null);
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