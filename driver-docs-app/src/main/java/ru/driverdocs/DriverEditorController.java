package ru.driverdocs;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.driverdocs.helpers.ui.AbstractController;

import java.io.IOException;
import java.time.LocalDate;

public class DriverEditorController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(DriverEditorController.class);
    private static final String FXML_FILE = "/fxml/DriverEditorView.fxml";
    private final String cssUrl = DriverDocsSetting.getInstance().getCssUrl();
    @FXML
    private TextField txtLastname;
    @FXML
    private TextField txtFirsname;
    @FXML
    private TextField txtSecondname;
    @FXML
    private DatePicker dtBirthdate;
    @FXML
    private Button btnApply;
    @FXML
    private TableView<DriverImpl> tblDrivers;
    @FXML
    private TableColumn<DriverImpl, String> colLastname;
    @FXML
    private TableColumn<DriverImpl, String> colFirstname;
    @FXML
    private TableColumn<DriverImpl, String> colSecondname;
    @FXML
    private TableColumn<DriverImpl, LocalDate> colBirthdate;
    @FXML
    private TableColumn<DriverImpl, String> colDelete;

    public static DriverEditorController build() throws IOException {
        DriverEditorController c = new DriverEditorController();
        c.load(FXML_FILE);
        return c;
    }

    @FXML
    private void initialize() {
        setupDriverTable();
        setupLastnameColumn();
        setupFirstnameColumn();
        setupSecondnameColumn();
        setupBirthdateColumn();
        setupButtonApply();

        tblDrivers.getItems().add(new DriverImpl(1, "фамилия1", "имя1", "отчество1", LocalDate.now().minusDays(1)));
        tblDrivers.getItems().add(new DriverImpl(2, "фамилия2", "имя2", "отчество2", LocalDate.now().minusDays(2)));
        tblDrivers.getItems().add(new DriverImpl(3, "фамилия3", "имя3", "отчество3", LocalDate.now().minusDays(3)));
    }

    private void setupButtonApply() {
        btnApply.setOnAction(ev -> {
            log.info("добавим нового водителя: lastname={}, firstname={}, secondname={}, bithdate={}", txtLastname.getText(), txtFirsname.getText(), txtSecondname.getText(), dtBirthdate.getValue());
            tblDrivers.getItems().add(new DriverImpl(4, txtLastname.getText(), txtFirsname.getText(), txtSecondname.getText(), dtBirthdate.getValue()));
            txtLastname.setText("");
            txtFirsname.setText("");
            txtSecondname.setText("");
            dtBirthdate.setValue(null);
        });
    }

    private void setupDriverTable() {
        colLastname.prefWidthProperty().bind(tblDrivers.widthProperty().multiply(0.25));
        colFirstname.prefWidthProperty().bind(tblDrivers.widthProperty().multiply(0.20));
        colSecondname.prefWidthProperty().bind(tblDrivers.widthProperty().multiply(0.25));
        colBirthdate.prefWidthProperty().bind(tblDrivers.widthProperty().multiply(0.15));
        colDelete.prefWidthProperty().bind(tblDrivers.widthProperty().multiply(0.15));

        colLastname.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        colFirstname.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        colSecondname.setCellValueFactory(new PropertyValueFactory<>("secondname"));
        colBirthdate.setCellValueFactory(new PropertyValueFactory<>("birthdate"));

        colDelete.setCellFactory(col -> new ActionColumn());
    }

    private void setupLastnameColumn() {
        colLastname.setCellFactory(TextFieldTableCell.forTableColumn());
        colLastname.setOnEditCommit((TableColumn.CellEditEvent<DriverImpl, String> t) -> {
            DriverImpl d = t.getTableView().getItems().get(t.getTablePosition().getRow());
            log.info("обновим фамилию водителя: driver-id={}, old-lastname={}, new-lastname={}", d.getId(), t.getOldValue(), t.getNewValue());
            d.setLastname(t.getNewValue());
        });

    }

    private void setupFirstnameColumn() {
        colFirstname.setCellFactory(TextFieldTableCell.forTableColumn());
        colFirstname.setOnEditCommit((TableColumn.CellEditEvent<DriverImpl, String> t) -> {
            DriverImpl d = t.getTableView().getItems().get(t.getTablePosition().getRow());
            log.info("обновим имя водителя: driver-id={}, old-firstname={}, new-firstname={}", d.getId(), t.getOldValue(), t.getNewValue());
            d.setFirstname(t.getNewValue());
        });
    }

    private void setupSecondnameColumn() {
        colSecondname.setCellFactory(TextFieldTableCell.forTableColumn());
        colSecondname.setOnEditCommit((TableColumn.CellEditEvent<DriverImpl, String> t) -> {
            DriverImpl d = t.getTableView().getItems().get(t.getTablePosition().getRow());
            log.info("обновим отчество водителя: driver-id={}, old-secondname={}, new-secondname={}", d.getId(), t.getOldValue(), t.getNewValue());
            d.setSecondname(t.getNewValue());
        });
    }

    private void setupBirthdateColumn() {
        colBirthdate.setCellFactory(param -> new DatePickerCell<>());
        colBirthdate.setOnEditCommit((TableColumn.CellEditEvent<DriverImpl, LocalDate> t) -> {
            DriverImpl d = t.getTableView().getItems().get(t.getTablePosition().getRow());
            log.info("обновим дату рождения водителя: driver-id={}, old-birthdate={}, new-birthdate={}", d.getId(), t.getOldValue(), t.getNewValue());
            d.setBirthdate(t.getNewValue());
        });
    }

    private static class ActionColumn extends TableCell<DriverImpl, String> {
        Button btn = new Button("удалить");

        public ActionColumn() {
            btn.setOnAction(ev -> {
                DriverImpl driver = getTableView().getItems().get(getTableRow().getIndex());
                log.trace("удалим водителя: id={}, lastname={},firstname={}, secondname={}", driver.getId(), driver.getLastname(), driver.getFirstname(), driver.getSecondname());
                getTableView().getItems().remove(getTableRow().getIndex());
            });
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

}
