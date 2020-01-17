package ru.driverdocs;


import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.driverdocs.helpers.ui.AbstractController;
import ru.driverdocs.helpers.ui.ErrorInformer2;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public final class MainController extends AbstractController {

    public static final String FXML_MAIN_VIEW = "/fxml/MainWnd.fxml";
    private static Logger log = LoggerFactory.getLogger(MainController.class);
    private final ErrorInformer2 errorInformer = new ErrorInformer2(DriverDocsSetting.getInstance().getCssUrl());
    //    private App app;
    @FXML
    private ChoiceBox<Integer> cbFontSizeList;
    @FXML
    private BorderPane mainView;
    @FXML
    private Label lblToday;
    @FXML
    private Tab tabRefBooks;
    @FXML
    private Tab tabReports;
    @FXML
    private TabPane tabPane;


    private MainController() {
        super();
    }

    public static MainController build() throws IOException {
        MainController c = new MainController();
        c.load(FXML_MAIN_VIEW);
        return c;
    }

    @FXML
    private void initialize() {
        cbFontSizeList.getItems().addAll(8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 48);
        cbFontSizeList.valueProperty().addListener((observable, oldValue, newValue) -> {
            log.trace("font size changed from {} to {}", oldValue, newValue);
            mainView.setStyle(String.format("-fx-font-size: %dpt;", newValue));
        });
        lblToday.setText((new SimpleDateFormat("dd-MM-yyyy").format(new Date())));

        try {
            DriverEditorController driverEditorController = DriverEditorController.build();

            tabRefBooks.setContent(driverEditorController.getRootPane());
        } catch (IOException e) {
            log.error("не удалось отбразить закладки", e);
            errorInformer.displayError("не удалось отбразить закладки", e);
        }
    }
}
