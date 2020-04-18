package ru.driverdocs.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Control;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ControlUtils {

    public static <C extends VBox> void load(C control, String fxmlFile) {
        FXMLLoader fxmlLoader = new FXMLLoader(control.getClass().getResource(fxmlFile));
        fxmlLoader.setRoot(control);
        fxmlLoader.setController(control);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static void whenFocusLost(Control control, Performer performer) {
        control.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue)
                performer.perform();
        });
    }

    public static void whenFocusSet(Control control, Performer performer) {
        control.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                performer.perform();
        });
    }
}
