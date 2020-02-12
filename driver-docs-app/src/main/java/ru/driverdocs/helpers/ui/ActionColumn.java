package ru.driverdocs.helpers.ui;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public final class ActionColumn<T> extends TableCell<T, String> {
    private static final Logger log = LoggerFactory.getLogger(ActionColumn.class);
    final Button btn = new Button("удалить");


    public ActionColumn(Consumer<Integer> actionConsumer) {
        btn.setOnAction(ev -> actionConsumer.accept(getTableRow().getIndex()));
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || getTableRow() == null) {
            setGraphic(null);
        } else {
            setGraphic(btn);
        }
    }
}