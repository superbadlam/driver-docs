package ru.driverdocs.ui;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DriverMedicalReferenceControl extends VBox {
    private static final String FXML_FILE = "/fxml/DriverMedicalReferenceControl.fxml";
    private static Logger log = LoggerFactory.getLogger(DriverMedicalReferenceControl.class);
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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML_FILE));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public String getSeries() {
        return txtRefSeries.textProperty().get();
    }

    public void setSeries(String value) {
        txtRefSeries.textProperty().set(value);
    }

    public StringProperty SeriesProperty() {
        return txtRefSeries.textProperty();
    }

    @FXML
    private void initialize() {
        setSeries("12 34");
    }
}