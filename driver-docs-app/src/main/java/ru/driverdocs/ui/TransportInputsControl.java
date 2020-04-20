package ru.driverdocs.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.driverdocs.DriverDocsSetting;
import ru.driverdocs.helpers.ui.ErrorInformer2;
import ru.driverdocs.ui.data.TransportImpl;
import ru.driverdocs.ui.validator.TransportValidator;

import java.util.function.Consumer;

import static ru.driverdocs.ui.ControlUtils.*;

public class TransportInputsControl extends VBox {
    private static final String FXML_FILE = "/fxml/TransportInputsControl.fxml";
    private static final Logger log = LoggerFactory.getLogger(TransportInputsControl.class);
    private final SimpleObjectProperty<TransportImpl> initialTransportProperty = new SimpleObjectProperty<>();
    private final TransportImpl transport;
    private final TransportValidator validator = new TransportValidator();
    private final ErrorInformer2 errorInformer =
            new ErrorInformer2(DriverDocsSetting.getInstance().getCssUrl());

    private Consumer<TransportImpl> onUpdateTransport;
    private Consumer<TransportImpl> onInsertTransport;
    @FXML
    private TextField txtPlateNo;
    @FXML
    private TextField txtMarka;
    @FXML
    private TextField txtModel;
    @FXML
    private Spinner<Integer> spinnerSeats;
    @FXML
    private TextField txtPassportSeries;
    @FXML
    private TextField txtPassportNumber;
    @FXML
    private TextField txtCertificateSeries;
    @FXML
    private TextField txtCertificateNumber;
    @FXML
    private Button btnTransportApply;

    public TransportInputsControl() {

        transport = new TransportImpl();
        load(this, FXML_FILE);
    }

    public void setOnUpdateTransport(Consumer<TransportImpl> onUpdateTransport) {
        this.onUpdateTransport = onUpdateTransport;
    }

    public void setOnInsertTransport(Consumer<TransportImpl> onInsertTransport) {
        this.onInsertTransport = onInsertTransport;
    }

    public ObjectProperty<TransportImpl> initialTransportProperty() {
        return initialTransportProperty;
    }


    @FXML
    private void initialize() {
        spinnerSeats.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 300, 0));

        btnTransportApply.disableProperty().bind(transport.invalidProperty());
//        txtPlateNo.disableProperty().bind(initialTransportProperty.isNull());
//        txtMarka.disableProperty().bind(initialTransportProperty.isNull());
//        txtModel.disableProperty().bind(initialTransportProperty.isNull());
//        spinnerSeats.disableProperty().bind(initialTransportProperty.isNull());
//        txtPassportSeries.disableProperty().bind(initialTransportProperty.isNull());
//        txtPassportNumber.disableProperty().bind(initialTransportProperty.isNull());
//        txtCertificateSeries.disableProperty().bind(initialTransportProperty.isNull());
//        txtCertificateNumber.disableProperty().bind(initialTransportProperty.isNull());


        whenFocusLost(txtPlateNo, () -> highlightOnWhen(txtPlateNo, s -> !validator.isValidPlateNo(s)));
        whenFocusSet(txtPlateNo, () -> highlightOff(txtPlateNo));

        whenFocusLost(txtMarka, () -> highlightOnWhen(txtMarka, s -> !validator.isValidMarka(s)));
        whenFocusSet(txtMarka, () -> highlightOff(txtMarka));

        whenFocusLost(txtModel, () -> highlightOnWhen(txtModel, s -> !validator.isValidModel(s)));
        whenFocusSet(txtModel, () -> highlightOff(txtModel));

        whenFocusLost(txtPassportSeries, () -> highlightOnWhen(txtPassportSeries, s -> !validator.isValidPassportSeries(s)));
        whenFocusSet(txtPassportSeries, () -> highlightOff(txtPassportSeries));

        whenFocusLost(txtPassportNumber, () -> highlightOnWhen(txtPassportNumber, s -> !validator.isValidPassportNumber(s)));
        whenFocusSet(txtPassportNumber, () -> highlightOff(txtPassportNumber));

        whenFocusLost(txtCertificateSeries, () -> highlightOnWhen(txtCertificateSeries, s -> !validator.isValidCertificateSeries(s)));
        whenFocusSet(txtCertificateSeries, () -> highlightOff(txtCertificateSeries));

        whenFocusLost(txtCertificateNumber, () -> highlightOnWhen(txtCertificateNumber, s -> !validator.isValidCertificateNumber(s)));
        whenFocusSet(txtCertificateNumber, () -> highlightOff(txtCertificateNumber));

        initialTransportProperty.addListener((observable, oldValue, newValue) -> transport.copyState(newValue));

        transport.plateNoProperty().bindBidirectional(txtPlateNo.textProperty());
        transport.markaProperty().bindBidirectional(txtMarka.textProperty());
        transport.modelProperty().bindBidirectional(txtModel.textProperty());
        transport.seatsProperty().bindBidirectional(spinnerSeats.getValueFactory().valueProperty());
        transport.passportSeriesProperty().bindBidirectional(txtPassportSeries.textProperty());
        transport.passportNumberProperty().bindBidirectional(txtPassportNumber.textProperty());
        transport.certificateSeriesProperty().bindBidirectional(txtCertificateSeries.textProperty());
        transport.certificateNumberProperty().bindBidirectional(txtCertificateNumber.textProperty());


        btnTransportApply.setOnAction(this::onApplyClick);
    }

    private void onApplyClick(ActionEvent actionEvent) {
        if (transport.getId() == 0) {
            insertTransport(transport);
        } else {
            updateTransport(transport);
        }
        transport.resetState();
    }

    private void updateTransport(TransportImpl transport) {
        try {
            log.trace("выполним обновление транспорта: {}", transport);
            onUpdateTransport.accept(transport);
        } catch (Exception e) {
            log.error("Не удалось обновить транспорт", e);
            errorInformer.displayError("Не удалось обновить транспорт", e);
        }
    }

    private void insertTransport(TransportImpl transport) {
        try {
            log.trace("выполним создание нового транспорта: {}", transport);
            onInsertTransport.accept(transport);
            log.trace("transport created: {}", transport);
        } catch (Exception e) {
            log.error("Не удалось создать новый транспорт", e);
            errorInformer.displayError("Не удалось создать новый транспорт", e);
        }
    }

}