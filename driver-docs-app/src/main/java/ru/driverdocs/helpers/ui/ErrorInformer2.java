package ru.driverdocs.helpers.ui;

import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class ErrorInformer2 {

    private String cssUrl;

    public ErrorInformer2() {
        super();
        cssUrl = null;
    }

    public ErrorInformer2(String cssUrl) {
        super();
        this.cssUrl = cssUrl;
    }


    public void displayError(String title, String header, String errMsg) {
        Alert a = new Alert(AlertType.ERROR, errMsg, ButtonType.OK);
        a.setTitle(title);
        a.setHeaderText(header);

        /*установим стили*/
        DialogPane dialogPane = a.getDialogPane();
        if (cssUrl != null)
            dialogPane.getStylesheets().add(cssUrl);
        dialogPane.getStyleClass().add("dialog-error");

        a.showAndWait();
    }

    public void displayError(String errMsg) {
        displayError("произошла ошибка", "О, ужассс!!!", errMsg);
    }


    public void displayError(String msg, Throwable e) {
        Alert alert = new Alert(AlertType.ERROR);

        alert.setTitle("произошла ошибка");
        alert.setHeaderText(msg);
        alert.setContentText(msg);


        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);

        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);


        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        DialogPane dialogPane = alert.getDialogPane();
        if (cssUrl != null)
            dialogPane.getStylesheets().add(cssUrl);
        dialogPane.getStyleClass().add("dialog-exception");

        alert.showAndWait();
    }

    public void displayInfo(String title, String header, String infoMsg) {
        Alert a = new Alert(AlertType.INFORMATION, infoMsg, ButtonType.OK);
        a.setTitle(title);
        a.setHeaderText(header);

        /*установим стили*/
        DialogPane dialogPane = a.getDialogPane();
        if (cssUrl != null)
            dialogPane.getStylesheets().add(cssUrl);
        dialogPane.getStyleClass().add("dialog-information");

        a.showAndWait();
    }

    public void displayInfo(String infoMsg) {
        displayInfo("информация", "к сведению", infoMsg);
    }

    public void displayWarning(String title, String header, String warningMsg, String detailsText) {


        Alert alert = new Alert(AlertType.ERROR);

        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(warningMsg);

        if (detailsText != null) {
            Label label = new Label("details:");

            TextArea textArea = new TextArea(detailsText);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);

            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);
            // Set expandable Exception into the dialog pane.
            alert.getDialogPane().setExpandableContent(expContent);

        }

        DialogPane dialogPane = alert.getDialogPane();
        if (cssUrl != null)
            dialogPane.getStylesheets().add(cssUrl);
        dialogPane.getStyleClass().add("dialog-warning");

        alert.showAndWait();
    }


    public void displayWarning(String warningMsg, String detailsText) {
        displayWarning("предупреждение", "Внимание", warningMsg, detailsText);
    }

    public void displayWarning(String warningMsg) {
        displayWarning("предупреждение", "Внимание", warningMsg, null);
    }
}