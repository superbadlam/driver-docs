package ru.driverdocs.ui;

import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DatePickerCell<S> extends TableCell<S, LocalDate> {
    private static final String datePattern = "dd/MM/yyyy";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);
    private final DatePicker datePicker;

    public DatePickerCell() {

        super();
        this.datePicker = new DatePicker();
        datePicker.setPromptText(datePattern);
        datePicker.setEditable(true);

        datePicker.setOnAction(t -> {
            LocalDate date = datePicker.getValue();
            setText(formatter.format(date));
            commitEdit(date);
        });

        setAlignment(Pos.CENTER);
    }

    @Override
    public void updateItem(LocalDate item, boolean empty) {

        super.updateItem(item, empty);


        if (empty || item == null || getTableRow() == null) {
            setText(null);
            setGraphic(null);
        } else {
            datePicker.setValue(item);
            setText(formatter.format(item));
            setGraphic(this.datePicker);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
    }


    @Override
    public void startEdit() {
        if (!isEditable()
                || !getTableView().isEditable()
                || !getTableColumn().isEditable()) {
            return;
        }
        super.startEdit();
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

}
