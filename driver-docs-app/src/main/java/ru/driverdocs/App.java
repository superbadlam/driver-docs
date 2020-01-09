package ru.driverdocs;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.driverdocs.helpers.ui.ApplicationEx2;
import ru.driverdocs.helpers.ui.Controller;

import java.io.File;
import java.net.MalformedURLException;

public class App extends ApplicationEx2 {

    public static final String IMG_APP_ICON = "/png/app.png";
    public static final String FXML_MAIN_VIEW = "/fxml/MainWnd.fxml";

    private static final String SKIN_CSS ="cfg/skin.css";
    public final static String version = "1.0.20191228.alpha";
    public static final String APPLICATION_NAME = "driver-docs";

    private static Logger log = LoggerFactory.getLogger(App.class);
    private String cssUrl;

    public App() {
        loadCSS();
    }

    private void loadCSS() {
        try {
            File skinFile = new File(SKIN_CSS);
            if(skinFile.exists()) {
                cssUrl = skinFile.toURI().toURL().toString();
                log.trace("будет использован скин - {}",cssUrl);
            }
            else {
                log.warn("не удалось найти файл скина {}",skinFile.getAbsolutePath());
            }
        } catch (MalformedURLException e) {
            log.warn("не удалось загрузить скин", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        log.info("запустили приложение");
        stage = primaryStage;
        stage.getIcons().add(new Image(getClass().getResource(IMG_APP_ICON).toString()));
        stage.setOnCloseRequest(event -> log.info("закрыли приложение"));

        showMainWnd();
        primaryStage.show();
    }

    private void showMainWnd() {
        try{
            MainController c = MainController.build(this);
            Pane p = c.getRootPane();
            stage.setScene(new Scene(p, p.getPrefHeight(), p.getPrefWidth()));
            stage.setResizable(true);
            stage.setTitle(String.format("%s-%s", APPLICATION_NAME, version));
            Controller.maximazeStage(stage);
            applyStylesheets();

        } catch (Exception ex) {
            log.error("не удалось отображить главное окно", ex);
        }
    }

    private void applyStylesheets() {
        if (cssUrl != null)
            stage.getScene().getStylesheets().add(cssUrl);
    }
}
