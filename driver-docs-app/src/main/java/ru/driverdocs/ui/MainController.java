package ru.driverdocs.ui;


import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.driverdocs.DriverDocsSetting;
import ru.driverdocs.helpers.ui.AbstractController;
import ru.driverdocs.helpers.ui.ErrorInformer2;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public final class MainController extends AbstractController {

    public static final String FXML_MAIN_VIEW = "/fxml/MainWnd.fxml";
    private static final Logger log = LoggerFactory.getLogger(MainController.class);
    private final ErrorInformer2 errorInformer = new ErrorInformer2(DriverDocsSetting.getInstance().getCssUrl());
    //    private App app;
    @FXML
    private ChoiceBox<Integer> cbFontSizeList;
    @FXML
    private BorderPane mainView;
    @FXML
    private Label lblToday;
    @FXML
    private Tab tabDrivers;
    @FXML
    private Tab tabEmployers;
    @FXML
    private Tab tabTransport;
    @FXML
    private Tab tabReports;
    @FXML
    private Tab tabRoutes;
    @FXML
    private TabPane tabPane;


    private DriverEditorController driverController;
    private RouteEditorController routeController;
    private EmployerEditiorController employerController;
    private TransportEditorController transportController;

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


        tabPane.getSelectionModel().selectedItemProperty().addListener((observableValue, oldTab, newTab) -> {
            if (newTab == tabDrivers) {
                showDriverEditorTab();
            } else if (newTab == tabRoutes) {
                showRouteEditorTab();
            } else if (newTab == tabEmployers) {
                showEmployerEditorTab();
            } else if (newTab == tabTransport) {
                showTransportEditorTab();
            }
        });

        tabPane.getSelectionModel().select(tabRoutes);
    }

    private void showTransportEditorTab() {
        try {
            if (transportController == null) {
                transportController = TransportEditorController.build();
                tabTransport.setContent(transportController.getRootPane());
            }
        } catch (IOException e) {
            log.warn("не удалось отобразить UI для редактирования транспорта", e);
            errorInformer.displayError("не удалось отобразить UI для редактирования транспорта", e);
        }
    }

    private void showEmployerEditorTab() {
        try {
            if (employerController == null) {
                employerController = EmployerEditiorController.build();
                tabEmployers.setContent(employerController.getRootPane());
            }
        } catch (IOException e) {
            log.warn("не удалось отобразить UI для редактирования предпринимателей", e);
            errorInformer.displayError("не удалось отобразить UI для редактирования предпринимателей", e);
        }
    }

    private void showRouteEditorTab() {
        try {
            if (routeController == null) {
                routeController = RouteEditorController.build();
                tabRoutes.setContent(routeController.getRootPane());
            }
        } catch (IOException e) {
            log.warn("не удалось отобразить UI для редактирования списка маршрутов", e);
            errorInformer.displayError("не удалось отобразить UI для редактирования списка маршрутов", e);
        }
    }

    private void showDriverEditorTab() {
        try {
            if (driverController == null) {
                driverController = DriverEditorController.build();
                tabDrivers.setContent(driverController.getRootPane());
            }
        } catch (IOException e) {
            log.warn("не удалось отобразить UI для редактирования водителей", e);
            errorInformer.displayError("не удалось отобразить UI для редактирования водителей", e);
        }
    }
}
