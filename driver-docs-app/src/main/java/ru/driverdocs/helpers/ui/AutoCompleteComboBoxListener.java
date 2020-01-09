package ru.driverdocs.helpers.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * https://codedump.io/share/WwZ36zwgZsx3/1/autocomplete-combobox-in-javafx
 *
 * @param <T>
 */
public class AutoCompleteComboBoxListener<T> implements EventHandler<KeyEvent> {

    private ComboBox<T> comboBox;
    private ObservableList<T> data;
    private boolean moveCaretToPos = false;
    private int caretPos;

    private boolean isON = true;


    public AutoCompleteComboBoxListener(final ComboBox<T> comboBox) {
        this.comboBox = comboBox;
        data = comboBox.getItems();
        this.comboBox.setEditable(true);
        this.comboBox.setOnKeyPressed(t -> comboBox.hide());
    }

    @Override
    public void handle(KeyEvent event) {

        if (event.getCode() == KeyCode.F2) {
            isON = !isON;
            this.comboBox.getEditor().setText("");
        }

        if (isON) {
            ObservableList<T> list = FXCollections.observableArrayList();
            for (int i = 0; i < data.size(); i++) {
                if (comboBox.getConverter().toString(data.get(i)).toLowerCase().startsWith(
                        AutoCompleteComboBoxListener.this.comboBox.getEditor().getText().toLowerCase())) {
                    list.add(data.get(i));
                }
            }
            String t = comboBox.getEditor().getText();

            comboBox.setItems(list);
            comboBox.getEditor().setText(t);
            if (!moveCaretToPos) {
                caretPos = -1;
            }
            moveCaret(t.length());
            if (!list.isEmpty()) {
                comboBox.show();
            }
        } else {//if(isON){
            comboBox.setItems(data);
            KeyCode keyCode = event.getCode();
            if (!event.isControlDown() && keyCode == KeyCode.ENTER) {
                if (comboBox.isArmed())
                    comboBox.show();
                else
                    comboBox.hide();

            }
        }
    }

    private void moveCaret(int textLength) {
        if (caretPos == -1)
            comboBox.getEditor().positionCaret(textLength);
        else
            comboBox.getEditor().positionCaret(caretPos);
        moveCaretToPos = false;
    }
}
