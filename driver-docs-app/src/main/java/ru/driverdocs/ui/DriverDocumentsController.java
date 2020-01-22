package ru.driverdocs.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.driverdocs.DriverDocsSetting;
import ru.driverdocs.helpers.ui.AbstractController;
import ru.driverdocs.helpers.ui.ErrorInformer2;
import ru.driverdocs.rxrepositories.DriverLicenseRepository;
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

    public static DriverDocumentsController build() throws IOException {
        DriverDocumentsController c = new DriverDocumentsController();
        c.load(FXML_FILE);
        return c;
    }

    @FXML
    private void initialize() {
        cmbDrivers.getItems().addAll(driverRepository.findAll().map(DriverImpl::createOf).toList().blockingGet());

        btnLicApply.disableProperty().bind(cmbDrivers.valueProperty().isNull());
        btnRefApply.disableProperty().bind(cmbDrivers.valueProperty().isNull());

        btnLicApply.setOnAction(ev -> {
            //TODO: проверим для тек. водителя удостоверение в БД. Если нет, то создадим новое иначе обновляем
        });

        // сделать проперти currDriver, currLicense, currMedRef
        // забиндить currDriver на cmbDrivers.value
        // при изменении currDriver вытягивать из БД currLicense, currMedRef


        cmbDrivers.setOnAction(ev -> {
            errorInformer.displayInfo(cmbDrivers.getValue().toString());
            //TODO: найти вод.удостоверение по ID водителя
            //TODO: найти мед.справку по ID водителя
        });
    }
}