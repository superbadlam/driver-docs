package ru.driverdocs.ui;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import ru.driverdocs.DriverDocsSetting;
import ru.driverdocs.helpers.ui.AbstractController;
import ru.driverdocs.rxrepositories.EmployerRepository;
import ru.driverdocs.rxrepositories.TransportRepository;
import ru.driverdocs.ui.data.EmployerImpl;
import ru.driverdocs.ui.data.TransportImpl;

import java.io.IOException;

public final class TransportEditorController extends AbstractController {
    private static final String FXML_FILE = "/fxml/TransportEditorView.fxml";
    private final EmployerRepository employerRepository = DriverDocsSetting.getInstance().getEmployerRepository();
    private final TransportRepository transportRepository
            = DriverDocsSetting.getInstance().getTransportRepository();

    @FXML
    private ComboBox<EmployerImpl> cmbEmployers;
    @FXML
    private TransportTableControl tableControl;
    @FXML
    private TransportInputsControl inputsControl;


    private TransportEditorController() {
    }

    public static TransportEditorController build() throws IOException {
        TransportEditorController c = new TransportEditorController();
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

        if (cmbEmployers.getValue() == null)
            throw new RuntimeException("Не указан предприниматель. Не возможно вставить новую запись о транспорте.");

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
