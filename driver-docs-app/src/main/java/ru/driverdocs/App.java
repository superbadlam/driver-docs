package ru.driverdocs;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.davidmoten.rx.jdbc.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.driverdocs.helpers.ui.ApplicationCloseHandler;
import ru.driverdocs.helpers.ui.ApplicationEx2;
import ru.driverdocs.helpers.ui.Controller;
import ru.driverdocs.helpers.ui.ErrorInformer2;

public class App extends ApplicationEx2 {
    public static final String IMG_APP_ICON = "/png/app.png";
    public static final String version = "1.0.20191228.alpha";
    public static final String APPLICATION_NAME = "driver-docs";
    private static Logger log = LoggerFactory.getLogger(App.class);
    private final Database db = DriverDocsSetting.getInstance().getDatabase();
    private final String cssUrl = DriverDocsSetting.getInstance().getCssUrl();
    private final ErrorInformer2 errorInformer = new ErrorInformer2(cssUrl);

    public App() {
        super();
    }


    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {
        log.info("запустили приложение");
        stage = primaryStage;
        stage.getIcons().add(new Image(getClass().getResource(IMG_APP_ICON).toString()));
        stage.setOnCloseRequest(event -> cleanup());

        showMainWnd();
        stage.show();
    }

    private void cleanup() {
        db.close();
        closeListeners.forEach(ApplicationCloseHandler::performCleanup);
        log.info("закрыли приложение");
    }

    private void showMainWnd() {
        try {
            MainController c = MainController.build();
            Pane p = c.getRootPane();
            stage.setScene(new Scene(p, p.getPrefHeight(), p.getPrefWidth()));
            stage.setResizable(true);
            stage.setTitle(String.format("%s-%s", APPLICATION_NAME, version));
            Controller.maximizeStage(stage);
            applyStylesheets();

        } catch (Exception ex) {
            log.error("не удалось отображить главное окно", ex);
            errorInformer.displayError("не удалось отображить главное окно", ex);
        }
    }

    private void applyStylesheets() {
        if (cssUrl != null)
            stage.getScene().getStylesheets().add(cssUrl);
    }
}
