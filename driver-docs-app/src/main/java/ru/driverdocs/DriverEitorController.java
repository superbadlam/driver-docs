package ru.driverdocs;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import ru.driverdocs.helpers.ui.AbstractController;

import java.io.IOException;

public class DriverEitorController extends AbstractController {

    private static final String FXML_FILE = "/fxml/DriverEditorView.fxml";
    private final String cssUrl = DriverDocsSetting.getInstance().getCssUrl();

    @FXML
    private TextField txtLastname;
    @FXML
    private TextField txtFirsname;
    @FXML
    private TextField txtSecondname;
    @FXML
    private DatePicker dtBitrhdate;
    @FXML
    private Button btnApply;
    @FXML
    private TableView<DriverImpl> tblDrivers;

    public static DriverEitorController build() throws IOException {
        DriverEitorController c = new DriverEitorController();
        c.load(FXML_FILE);
        return c;
    }
}
