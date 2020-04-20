package ru.driverdocs.ui;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.driverdocs.DriverDocsSetting;
import ru.driverdocs.domain.Transport;
import ru.driverdocs.helpers.ui.ActionColumn;
import ru.driverdocs.helpers.ui.ErrorInformer2;
import ru.driverdocs.ui.data.TransportImpl;

import java.util.Collection;
import java.util.function.Consumer;

import static ru.driverdocs.ui.ControlUtils.load;

public class TransportTableControl extends BorderPane {
    private static final String FXML_FILE = "/fxml/TransportTableControl.fxml";
    private static final Logger log = LoggerFactory.getLogger(TransportTableControl.class);
    private final SimpleObjectProperty<TransportImpl> editTransportProperty = new SimpleObjectProperty<>();
    private final ErrorInformer2 errorInformer =
            new ErrorInformer2(DriverDocsSetting.getInstance().getCssUrl());

    private Consumer<TransportImpl> onDeleteTransport;

    @FXML
    private TableView<TransportImpl> tblTransports;
    @FXML
    private TableColumn<TransportImpl, String> colPlateNo;
    @FXML
    private TableColumn<TransportImpl, String> colMarka;
    @FXML
    private TableColumn<TransportImpl, String> colModel;
    @FXML
    private TableColumn<TransportImpl, Integer> colSeats;
    @FXML
    private TableColumn<TransportImpl, String> colPassportSeries;
    @FXML
    private TableColumn<TransportImpl, String> colPassportNumber;
    @FXML
    private TableColumn<TransportImpl, String> colCertificateSeries;
    @FXML
    private TableColumn<TransportImpl, String> colCertificateNumber;
    @FXML
    private TableColumn<TransportImpl, String> colDelete;


    public TransportTableControl() {
        load(this, FXML_FILE);
    }

    public ReadOnlyObjectProperty<TransportImpl> editTransportProperty() {
        return editTransportProperty;
    }

    public void setOnDeleteTransport(Consumer<TransportImpl> onDeleteTransport) {
        this.onDeleteTransport = onDeleteTransport;
    }

    @FXML
    private void initialize() {
        colModel.prefWidthProperty().bind(tblTransports.widthProperty().multiply(0.25));
        colMarka.prefWidthProperty().bind(tblTransports.widthProperty().multiply(0.20));
        colPlateNo.prefWidthProperty().bind(tblTransports.widthProperty().multiply(0.25));
        colSeats.prefWidthProperty().bind(tblTransports.widthProperty().multiply(0.15));
        colPassportNumber.prefWidthProperty().bind(tblTransports.widthProperty().multiply(0.25));
        colPassportSeries.prefWidthProperty().bind(tblTransports.widthProperty().multiply(0.20));
        colCertificateNumber.prefWidthProperty().bind(tblTransports.widthProperty().multiply(0.25));
        colCertificateSeries.prefWidthProperty().bind(tblTransports.widthProperty().multiply(0.15));
        colDelete.prefWidthProperty().bind(tblTransports.widthProperty().multiply(0.15));

        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colMarka.setCellValueFactory(new PropertyValueFactory<>("marka"));
        colPlateNo.setCellValueFactory(new PropertyValueFactory<>("plateNo"));
        colSeats.setCellValueFactory(new PropertyValueFactory<>("seats"));
        colPassportNumber.setCellValueFactory(new PropertyValueFactory<>("passportNumber"));
        colPassportSeries.setCellValueFactory(new PropertyValueFactory<>("passportSeries"));
        colCertificateNumber.setCellValueFactory(new PropertyValueFactory<>("certificateNumber"));
        colCertificateSeries.setCellValueFactory(new PropertyValueFactory<>("certificateSeries"));

        colDelete.setCellFactory(col -> new ActionColumn<>(selRow -> {
            try {
                TransportImpl transport = tblTransports.getItems().get(selRow);
                log.trace("удалим транспорт: {}", transport);
                onDeleteTransport.accept(transport);
                tblTransports.getItems().remove(selRow.intValue());
            } catch (Exception e) {
                log.warn("не удалось удалить транспорт", e);
                errorInformer.displayError("не удалось удалить транспорт", e);
            }
        }));

        tblTransports.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                editTransportProperty.setValue(tblTransports.getSelectionModel().getSelectedItem());
            }
        });
    }

    public void setItems(Collection<Transport> collection) {
        tblTransports.getItems().clear();
        collection.stream()
                .map(TransportImpl::createOf)
                .forEach(t -> tblTransports.getItems().add(t));

    }

    public void addItem(Transport transport) {
        tblTransports.getItems().add(TransportImpl.createOf(transport));
    }

    public void updateItem(Transport newValue) {
        if (editTransportProperty.get() != null)
            editTransportProperty.get().copyState(newValue);
        editTransportProperty.set(null);
    }
}