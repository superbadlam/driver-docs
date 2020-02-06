package ru.driverdocs.ui;

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
import ru.driverdocs.rxrepositories.DriverLicenseValidator;
import ru.driverdocs.rxrepositories.DriverRepository;

import java.io.IOException;

public class DriverDocumentsController extends AbstractController {
    private static final String FXML_FILE = "/fxml/DriverDocumentsEditorView.fxml";
    private static Logger log = LoggerFactory.getLogger(DriverDocumentsController.class);
    private final ErrorInformer2 errorInformer = new ErrorInformer2(DriverDocsSetting.getInstance().getCssUrl());
    private final DriverRepository driverRepository = DriverDocsSetting.getInstance().getDriverRepository();
    private final DriverLicenseRepository driverLicenseRepository = DriverDocsSetting.getInstance().getDriverLicenseRepository();
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
                log.info("создали новое вод. удостоверение: driver={} license={}", currDriver.get(), currLicense.toString());
            } else {
                driverLicenseRepository.update(currLicense.getId(), currLicense.getSeries(), currLicense.getNumber(),
                        currLicense.getStartdate(), currLicense.getEnddate()).blockingAwait();
                log.info("обновили вод. удостоверение: driver={} license={}", currDriver.get(), currLicense.toString());
            }
        }

    };
    private MedicalReferenceImpl currReference = new MedicalReferenceImpl();
    private final InvalidationListener driverChangeListener = ev -> {
        log.trace("выбран водитель: driver={}", currDriver.get());
        findLicense();
        findReference();
    };
    private EventHandler<ActionEvent> referenceApplyAction = actionEvent -> {
        //TODO проверить currReference
        if (currReference.getId() == 0) {
            //TODO create new medical reference
        } else {
            //TODO update current medical refernce
        }
    };

    private void findReference() {
        try {
            MedicalReference reference = null;// TODO: найти мед. справку по ид водителя
            currReference.setId(reference.getId());
            currReference.setSeries(reference.getSeries());
            currReference.setNumber(reference.getNumber());
            currReference.setStartdate(reference.getStartdate());
            log.trace("нашли вод. справку: driver={} reference={}", currDriver.get(), currReference);
        } catch (Exception e) {
            currReference.setId(0);
            currReference.setSeries("");
            currReference.setNumber("");
            currReference.setStartdate(null);
            log.warn("не удалось найти вод. справку: driver={}", currDriver.get());
        }
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
            currLicense.setId(0);
            currLicense.setSeries("");
            currLicense.setNumber("");
            currLicense.setStartdate(null);
            currLicense.setEnddate(null);
            log.warn("не удалось найти удостоверение для водителя: driver={}", currDriver.get());
        }
    }


    public static DriverDocumentsController build() throws IOException {
        DriverDocumentsController c = new DriverDocumentsController();
        c.load(FXML_FILE);
        return c;
    }

    @FXML
    private void initialize() {
        cmbDrivers.getItems().addAll(driverRepository.findAll().map(DriverImpl::createOf).toList().blockingGet());
        currDriver.bind(cmbDrivers.valueProperty());
        currDriver.addListener(driverChangeListener);

        btnLicApply.disableProperty().bind(cmbDrivers.valueProperty().isNull());
        btnRefApply.disableProperty().bind(cmbDrivers.valueProperty().isNull());


        currLicense.seriesProperty().bindBidirectional(txtLicSeries.textProperty());
        currLicense.numberProperty().bindBidirectional(txtLicNumber.textProperty());
        currLicense.startdateProperty().bindBidirectional(dtLicStart.valueProperty());
        currLicense.enddateProperty().bindBidirectional(dtLicEnd.valueProperty());
        btnLicApply.setOnAction(licenseApplyAction);


        currReference.seriesProperty().bindBidirectional(txtRefSeries.textProperty());
        currReference.numberProperty().bindBidirectional(txtRefNumber.textProperty());
        currReference.startdateProperty().bindBidirectional(dtRefStart.valueProperty());
        btnRefApply.setOnAction(referenceApplyAction);

    }
}