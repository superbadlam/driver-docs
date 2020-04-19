package ru.driverdocs.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.function.Predicate;

public class ControlUtils {

    public static <C extends Pane> void load(C control, String fxmlFile) {
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

    public static void highlightOnWhen(TextField textField, Predicate<String> predicate) {
        if (predicate.test(textField.getText())) {
            textField.setStyle("-fx-control-inner-background: red");
        }
    }

    public static void highlightOff(TextField textField) {
        textField.setStyle("-fx-control-inner-background: white");
    }
}
