package ru.driverdocs.ui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.driverdocs.DriverDocsSetting;
import ru.driverdocs.domain.DriverLicense;
import ru.driverdocs.helpers.ui.AbstractController;
import ru.driverdocs.helpers.ui.ErrorInformer2;
import ru.driverdocs.rxrepositories.DriverLicenseRepository;
import ru.driverdocs.rxrepositories.DriverLicenseValidator;
import ru.driverdocs.rxrepositories.DriverRepository;

import java.io.IOException;
import java.time.LocalDate;

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
    private SimpleObjectProperty<DriverLicense> currLic = new SimpleObjectProperty<>();


    public static DriverDocumentsController build() throws IOException {
        DriverDocumentsController c = new DriverDocumentsController();
        c.load(FXML_FILE);
        return c;
    }

    @FXML
    private void initialize() {
        //TODO: refactor for this method
        cmbDrivers.getItems().addAll(driverRepository.findAll().map(DriverImpl::createOf).toList().blockingGet());

        btnLicApply.disableProperty().bind(cmbDrivers.valueProperty().isNull());
        btnRefApply.disableProperty().bind(cmbDrivers.valueProperty().isNull());

        btnLicApply.setOnAction(ev -> {
            long id;
            String series = txtLicSeries.getText();
            String number = txtLicNumber.getText();
            LocalDate startdate = dtLicStart.getValue();
            LocalDate enddate = dtLicEnd.getValue();
            long driverId = currDriver.get().getId();

            if (!DriverLicenseValidator.isValidSeries(series)) {
                log.warn("серия водительского удостоверения имеет некорректное значение: series={}", series);
                errorInformer.displayWarning("серия имеет некорректное значение!");
            }
            if (!DriverLicenseValidator.isValidNumber(number)) {
                log.warn("номер водительского удостоверения имеет некорректное значение: number={}", number);
                errorInformer.displayWarning("номер имеет некорректное значение!");
            }
            if (!DriverLicenseValidator.isValidDateRange(startdate, enddate)) {
                log.warn("дата начала и/или дата окончания " +
                        "вод. удостоверения имеют не корректное значения : " +
                        "startdate={}, enddate={}", startdate, enddate);
                errorInformer.displayWarning("некорректные дата!");
            }
            if (currLic.get() == null) {
                id = driverLicenseRepository.create(driverId, series, number, startdate, enddate).blockingGet();
            } else {
                id = currLic.get().getId();
                driverLicenseRepository.update(id, series, number, startdate, enddate);
            }
            currLic.set(new DriverLicense() {
                @Override
                public long getId() {
                    return id;
                }

                @Override
                public String getSeries() {
                    return series;
                }

                @Override
                public String getNumber() {
                    return number;
                }

                @Override
                public LocalDate getStartdate() {
                    return startdate;
                }

                @Override
                public LocalDate getEnddate() {
                    return enddate;
                }
            });

        });

        currDriver.bind(cmbDrivers.valueProperty());
        currLic.set(null);

        currDriver.addListener(ev -> {
            log.trace("выбран водитель: driver={}", currDriver.get());
            try {
                currLic.set(driverLicenseRepository.findByDriverId(currDriver.get().getId()).blockingGet());
                log.trace("нашли вод. удостоверение: license={}", currLic.get());
            } catch (Exception e) {
                log.warn("не удалось найти удостоверение для водителя: driver={}", currDriver.get());
                currLic.set(null);
            }
        });
        currLic.addListener(ev -> {

            String series = "";
            String number = "";
            LocalDate startdate = null;
            LocalDate enddate = null;

            if (currLic.get() != null) {
                series = currLic.get().getSeries();
                number = currLic.get().getNumber();
                startdate = currLic.get().getStartdate();
                enddate = currLic.get().getEnddate();
            }
            txtLicSeries.setText(series);
            txtLicNumber.setText(number);
            dtLicStart.setValue(startdate);
            dtLicEnd.setValue(enddate);
        });

    }
}