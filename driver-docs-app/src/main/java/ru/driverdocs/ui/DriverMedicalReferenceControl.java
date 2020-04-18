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
import ru.driverdocs.domain.MedicalReference;
import ru.driverdocs.helpers.ui.ErrorInformer2;
import ru.driverdocs.rxrepositories.MedicalRefRepository;
import ru.driverdocs.ui.data.DriverImpl;
import ru.driverdocs.ui.data.MedicalReferenceImpl;
import ru.driverdocs.ui.validator.MedicalRefValidator;

import static ru.driverdocs.ui.ControlUtils.load;
import static ru.driverdocs.ui.ControlUtils.whenFocusLost;

public final class DriverMedicalReferenceControl extends VBox {
    private static final String FXML_FILE = "/fxml/DriverMedicalReferenceControl.fxml";
    private static final Logger log = LoggerFactory.getLogger(DriverMedicalReferenceControl.class);
    private final MedicalRefRepository medicalRefRepository =
            DriverDocsSetting.getInstance().getMedicalRefRepository();
    private final SimpleObjectProperty<DriverImpl> currDriver =
            new SimpleObjectProperty<>();
    private final MedicalReferenceImpl currReference = new MedicalReferenceImpl();
    private final MedicalRefValidator validator = new MedicalRefValidator();
    private final ErrorInformer2 errorInformer =
            new ErrorInformer2(DriverDocsSetting.getInstance().getCssUrl());
    @FXML
    private TextField txtRefSeries;
    @FXML
    private TextField txtRefNumber;
    @FXML
    private DatePicker dtRefStart;
    @FXML
    private Button btnRefApply;
    @FXML
    private Button btnRefDelete;


    public DriverMedicalReferenceControl() {
        load(this, FXML_FILE);
    }

    protected void clearControl() {
        currReference.setId(0);
        currReference.setSeries("");
        currReference.setNumber("");
        currReference.setStartdate(null);
    }

    private void findReference(DriverImpl driver) {
        try {
            MedicalReference reference = medicalRefRepository.findByDriverId(driver.getId()).blockingGet();
            currReference.setId(reference.getId());
            currReference.setSeries(reference.getSeries());
            currReference.setNumber(reference.getNumber());
            currReference.setStartdate(reference.getStartdate());
            log.trace("нашли вод. справку: driver={} reference={}", currDriver.get(), currReference);
        } catch (Exception e) {
            clearControl();
            log.warn("не удалось найти вод. справку: driver={}", currDriver.get());
        }
    }


    public SimpleObjectProperty<DriverImpl> driverProperty() {
        return currDriver;
    }

    @FXML
    private void initialize() {


        currReference.seriesProperty().bindBidirectional(txtRefSeries.textProperty());
        currReference.numberProperty().bindBidirectional(txtRefNumber.textProperty());
        currReference.startdateProperty().bindBidirectional(dtRefStart.valueProperty());


        btnRefApply.setOnAction(this::onApplyClick);
        btnRefDelete.setOnAction(this::onDeleteClick);

        btnRefDelete.disableProperty().bind(currReference.idProperty().isEqualTo(0));
        btnRefApply.disableProperty().bind(currReference.invalidProperty());
        txtRefSeries.disableProperty().bind(currDriver.isNull());
        txtRefNumber.disableProperty().bind(currDriver.isNull());
        dtRefStart.disableProperty().bind(currDriver.isNull());

        whenFocusLost(txtRefSeries, this::validateSeries);
        whenFocusLost(txtRefNumber, this::validateNumber);
        whenFocusLost(dtRefStart, this::validateDateRange);

        currDriver.addListener((observable, oldValue, newValue) -> findReference(newValue));
    }

    private void onApplyClick(ActionEvent actionEvent) {
        if (currReference.getId() == 0) {
            currReference.setId(
                    medicalRefRepository.create(
                            currDriver.get().getId(),
                            currReference.getSeries(),
                            currReference.getNumber(),
                            currReference.getStartdate()
                    ).blockingGet());
            log.info("создали новую мед. справку: driver={}, medicalRef={}", currDriver.get(), currReference);
        } else {
            medicalRefRepository
                    .update(
                            currReference.getId(),
                            currReference.getSeries(),
                            currReference.getNumber(),
                            currReference.getStartdate())
                    .blockingAwait();
            log.info("обновили мед. справку: driver={}, medicalRef={}", currDriver.get(), currReference);
        }
    }

    private void onDeleteClick(ActionEvent actionEvent) {
        try {
            medicalRefRepository.delete(currReference.getId()).blockingAwait();
            log.info("удалили мед. справку: driver={}, license={}", currDriver.get(), currReference);
            clearControl();
        } catch (Exception e) {
            log.warn("не удалось удалить мед. справку: driver={}, license={}", currDriver.get(), currReference);
            errorInformer.displayWarning("не удалось удалить currReference!");
        }
    }

    private void validateDateRange() {
        if (!validator.isValidDateRange(currReference.getStartdate())) {
            log.warn("мед. справка имеет не корректную дату выдачи: series={}", currReference.getSeries());
            errorInformer.displayWarning(" дата выдачи некорректное значение!");
            currReference.setStartdate(null);
        }
    }

    private void validateNumber() {
        if (!validator.isValidNumber(currReference.getNumber())) {
            log.warn("мед. справка имеет не корректный номер: number={}", currReference.getNumber());
            errorInformer.displayWarning("номер имеет некорректное значение!");
            currReference.setNumber("");
        }
    }

    private void validateSeries() {
        if (!validator.isValidSeries(currReference.getSeries())) {
            log.warn("мед. справка имеет не корректную серию: series={}", currReference.getSeries());
            errorInformer.displayWarning("серия имеет некорректное значение!");
            currReference.setSeries("");
        }
    }
}