package ru.driverdocs.ui;

import io.reactivex.Flowable;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.driverdocs.DriverDocsSetting;
import ru.driverdocs.domain.DriverLicense;
import ru.driverdocs.domain.MedicalReference;
import ru.driverdocs.helpers.ui.AbstractController;
import ru.driverdocs.helpers.ui.ErrorInformer2;
import ru.driverdocs.rxrepositories.DriverLicenseRepository;
import ru.driverdocs.rxrepositories.MedicalRefRepository;

import java.io.IOException;

public class DriverDocumentsController extends AbstractController {
    private static final String FXML_FILE = "/fxml/DriverDocumentsEditorView.fxml";
    private static Logger log = LoggerFactory.getLogger(DriverDocumentsController.class);
    private final ErrorInformer2 errorInformer = new ErrorInformer2(DriverDocsSetting.getInstance().getCssUrl());
    private final Flowable<DriverImpl> driversSupplier =
            DriverDocsSetting.getInstance().getDriverRepository().findAll().map(DriverImpl::createOf);
    private final DriverLicenseRepository driverLicenseRepository = DriverDocsSetting.getInstance().getDriverLicenseRepository();
    private final MedicalRefRepository medicalRefRepository = DriverDocsSetting.getInstance().getMedicalRefRepository();

    @FXML
    private ComboBox<DriverImpl> cmbDrivers;
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
    private TextField txtRefSeries;
    @FXML
    private TextField txtRefNumber;
    @FXML
    private DatePicker dtRefStart;
    @FXML
    private Button btnRefApply;
    @FXML
    private Button btnLicDelete;
    @FXML
    private Button btnRefDelete;

    private SimpleObjectProperty<DriverImpl> currDriver = new SimpleObjectProperty<>();
    private DriverLicenseImpl currLicense = new DriverLicenseImpl();
    private final EventHandler<ActionEvent> licenseApplyAction = ev -> {
        if (!DriverLicenseValidator.isValidSeries(currLicense.getSeries())) {
            log.warn("серия водительского удостоверения имеет некорректное значение: series={}", currLicense.getSeries());
            errorInformer.displayWarning("серия имеет некорректное значение!");
            currLicense.setSeries("");

        } else if (!DriverLicenseValidator.isValidNumber(currLicense.getNumber())) {
            log.warn("номер водительского удостоверения имеет некорректное значение: number={}", currLicense.getNumber());
            errorInformer.displayWarning("номер имеет некорректное значение!");
            currLicense.setNumber("");

        } else if (!DriverLicenseValidator.isValidDateRange(currLicense.getStartdate(), currLicense.getEnddate())) {
            log.warn("дата начала и/или дата окончания " +
                    "вод. удостоверения имеют некорректное значения : " +
                    "startdate={}, enddate={}", currLicense.getStartdate(), currLicense.getEnddate());
            errorInformer.displayWarning("некорректные дата!");
            currLicense.setStartdate(null);
            currLicense.setEnddate(null);

        } else {
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

    };
    private MedicalReferenceImpl currReference = new MedicalReferenceImpl();

    private final InvalidationListener driverChangeListener = ev -> {
        log.trace("выбран водитель: driver={}", currDriver.get());
        if (currDriver.get() != null) {
            findLicense();
            findReference();
        } else {
            clearLicenseInfo();
            clearReferenceInfo();
        }
    };
    private EventHandler<ActionEvent> referenceApplyAction = actionEvent -> {
        if (!MedicalRefValidator.isValidSeries(currReference.getSeries())) {
            log.warn("мед. справка имеет не корректную серию: series={}", currReference.getSeries());
            errorInformer.displayWarning("серия имеет некорректное значение!");
            currReference.setSeries("");
        } else if (!MedicalRefValidator.isValidNumber(currReference.getNumber())) {
            log.warn("мед. справка имеет не корректный номер: number={}", currReference.getNumber());
            errorInformer.displayWarning("номер имеет некорректное значение!");
            currReference.setNumber("");
        } else if (!MedicalRefValidator.isValidDateRange(currReference.getStartdate())) {
            log.warn("мед. справка имеет не корректную дату выдачи: series={}", currReference.getSeries());
            errorInformer.displayWarning(" дата выдачи некорректное значение!");
            currReference.setStartdate(null);
        } else {
            if (currReference.getId() == 0) {
                currReference.setId(
                        medicalRefRepository.create(
                                currDriver.get().getId(),
                                currReference.getSeries(),
                                currReference.getNumber(),
                                currReference.getStartdate()
                        ).blockingGet());
                log.info("создали новую мед. справку: driver={}, medicalRef={}", currDriver.get(), currReference.toString());
            } else {
                medicalRefRepository
                        .update(
                                currReference.getId(),
                                currReference.getSeries(),
                                currReference.getNumber(),
                                currReference.getStartdate())
                        .blockingAwait();
                log.info("обновили мед. справку: driver={}, medicalRef={}", currDriver.get(), currReference.toString());
            }
        }
    };
    private EventHandler<ActionEvent> licenseDeleteAction = actionEvent -> {
        try {
            driverLicenseRepository.delete(currLicense.getId()).blockingAwait();
            log.info("удалили вод. удостоверение: driver={}, license={}", currDriver.get(), currLicense.toString());
            clearLicenseInfo();
        } catch (Exception e) {
            log.warn("не удалось удалить вод. удостоверение: driver={}, license={}", currDriver.get(), currLicense.toString());
            errorInformer.displayWarning("не удалось удалить вод. удостоверение!");
        }

    };
    private EventHandler<ActionEvent> referenceDeleteAction = actionEvent -> {
        try {
            medicalRefRepository.delete(currReference.getId()).blockingAwait();
            log.info("удалили мед. справку: driver={}, license={}", currDriver.get(), currReference.toString());
            clearReferenceInfo();
        } catch (Exception e) {
            log.warn("не удалось удалить мед. справку: driver={}, license={}", currDriver.get(), currReference.toString());
            errorInformer.displayWarning("не удалось удалить currReference!");
        }

    };

    public static DriverDocumentsController build() throws IOException {
        DriverDocumentsController c = new DriverDocumentsController();
        c.load(FXML_FILE);
        return c;
    }


    private void clearLicenseInfo() {
        currLicense.setId(0);
        currLicense.setSeries("");
        currLicense.setNumber("");
        currLicense.setStartdate(null);
        currLicense.setEnddate(null);
    }

    private void findReference() {
        try {
            MedicalReference reference = medicalRefRepository.findByDriverId(currDriver.get().getId()).blockingGet();
            currReference.setId(reference.getId());
            currReference.setSeries(reference.getSeries());
            currReference.setNumber(reference.getNumber());
            currReference.setStartdate(reference.getStartdate());
            log.trace("нашли вод. справку: driver={} reference={}", currDriver.get(), currReference);
        } catch (Exception e) {
            clearReferenceInfo();
            log.warn("не удалось найти вод. справку: driver={}", currDriver.get());
        }
    }

    private void clearReferenceInfo() {
        currReference.setId(0);
        currReference.setSeries("");
        currReference.setNumber("");
        currReference.setStartdate(null);
    }

    private void findLicense() {
        try {
            DriverLicense license = driverLicenseRepository.findByDriverId(currDriver.get().getId()).blockingGet();
            currLicense.setId(license.getId());
            currLicense.setSeries(license.getSeries());
            currLicense.setNumber(license.getNumber());
            currLicense.setStartdate(license.getStartdate());
            currLicense.setEnddate(license.getEnddate());
            log.trace("нашли вод. удостоверение для водителя: driver={} license={}", currDriver.get(), currLicense);
        } catch (Exception e) {
            clearLicenseInfo();
            log.warn("не удалось найти удостоверение для водителя: driver={}", currDriver.get());
        }
    }

    public void refresh() {
        cmbDrivers.getItems().setAll(driversSupplier.toList().blockingGet());
        cmbDrivers.setValue(null);
        log.trace("обновили список водителей: количество-водителей={}", cmbDrivers.getItems().size());
    }

    @FXML
    private void initialize() {

        refresh();
        currDriver.bind(cmbDrivers.valueProperty());
        currDriver.addListener(driverChangeListener);

//        dtLicEnd.setDisable(true);
        dtLicStart.valueProperty().addListener(ev -> dtLicEnd.setValue(
                dtLicStart.getValue() == null ? null : dtLicStart.getValue().plusYears(10)));

        btnLicApply.disableProperty().bind(cmbDrivers.valueProperty().isNull());
        btnRefApply.disableProperty().bind(cmbDrivers.valueProperty().isNull());
        btnLicDelete.disableProperty().bind(currLicense.idProperty().isEqualTo(0));
        btnRefDelete.disableProperty().bind(currReference.idProperty().isEqualTo(0));


        currLicense.seriesProperty().bindBidirectional(txtLicSeries.textProperty());
        currLicense.numberProperty().bindBidirectional(txtLicNumber.textProperty());
        currLicense.startdateProperty().bindBidirectional(dtLicStart.valueProperty());
        currLicense.enddateProperty().bindBidirectional(dtLicEnd.valueProperty());
        btnLicApply.setOnAction(licenseApplyAction);
        btnLicDelete.setOnAction(licenseDeleteAction);


        currReference.seriesProperty().bindBidirectional(txtRefSeries.textProperty());
        currReference.numberProperty().bindBidirectional(txtRefNumber.textProperty());
        currReference.startdateProperty().bindBidirectional(dtRefStart.valueProperty());
        btnRefApply.setOnAction(referenceApplyAction);
        btnRefDelete.setOnAction(referenceDeleteAction);
    }
}