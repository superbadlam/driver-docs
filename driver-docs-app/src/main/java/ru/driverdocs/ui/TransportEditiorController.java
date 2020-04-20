package ru.driverdocs.ui;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.driverdocs.DriverDocsSetting;
import ru.driverdocs.helpers.ui.AbstractController;
import ru.driverdocs.helpers.ui.ErrorInformer2;
import ru.driverdocs.rxrepositories.EmployerRepository;
import ru.driverdocs.rxrepositories.TransportRepository;
import ru.driverdocs.ui.data.EmployerImpl;
import ru.driverdocs.ui.data.TransportImpl;

import java.io.IOException;

public final class TransportEditiorController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(TransportEditiorController.class);
    private static final String FXML_FILE = "/fxml/TransportEditorView.fxml";
    private final EmployerRepository employerRepository = DriverDocsSetting.getInstance().getEmployerRepository();
    private final TransportRepository transportRepository
            = DriverDocsSetting.getInstance().getTransportRepository();
    private final ErrorInformer2 errorInformer = new ErrorInformer2(DriverDocsSetting.getInstance().getCssUrl());

    @FXML
    private ComboBox<EmployerImpl> cmbEmployers;
    @FXML
    private TransportTableControl tableControl;
    @FXML
    private TransportInputsControl inputsControl;


    private TransportEditiorController() {
    }

    public static TransportEditiorController build() throws IOException {
        TransportEditiorController c = new TransportEditiorController();
        c.load(FXML_FILE);
        return c;
    }

    @FXML
    private void initialize() {
        cmbEmployers.getItems().addAll(
                employerRepository.findAll().map(EmployerImpl::createOf).toList().blockingGet());

        cmbEmployers.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> tableControl.setItems(
                        transportRepository.findByEmployerId(
                                newValue.getId()).map(TransportImpl::createOf).toList().blockingGet()));

        tableControl.setOnDeleteTransport(this::deleteTransport);
        inputsControl.initialTransportProperty().bind(tableControl.editTransportProperty());
        inputsControl.setOnInsertTransport(this::insertTransport);
        inputsControl.setOnUpdateTransport(this::updateTransport);
    }

    private void updateTransport(TransportImpl transport) {
        transportRepository.update(
                transport.getId(), transport.getPlateNo(), transport.getMarka(), transport.getModel()
                , transport.getSeats(), transport.getPassportSeries(), transport.getPassportNumber()
                , transport.getCertificateSeries(), transport.getCertificateNumber()
        ).blockingAwait();
        tableControl.updateItem(transport);
    }

    private void deleteTransport(TransportImpl transport) {
        transportRepository.delete(transport.getId()).blockingAwait();
    }

    private void insertTransport(TransportImpl transport) {
        transport.setId(
                transportRepository.create(cmbEmployers.getValue().getId(),
                        transport.getPlateNo(),
                        transport.getMarka(),
                        transport.getModel(),
                        transport.getSeats(),
                        transport.getPassportSeries(),
                        transport.getPassportNumber(),
                        transport.getCertificateSeries(),
                        transport.getCertificateNumber()).blockingGet());
        tableControl.addItem(transport);
    }
}
