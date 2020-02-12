package ru.driverdocs.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.driverdocs.DriverDocsSetting;
import ru.driverdocs.helpers.ui.AbstractController;
import ru.driverdocs.helpers.ui.ActionColumn;
import ru.driverdocs.helpers.ui.DatePickerCell;
import ru.driverdocs.helpers.ui.ErrorInformer2;
import ru.driverdocs.rxrepositories.DriverRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class DriverEditorController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(DriverEditorController.class);
    private static final String FXML_FILE = "/fxml/DriverEditorView.fxml";
    private final DriverRepository repo = DriverDocsSetting.getInstance().getDriverRepository();
    private final ErrorInformer2 errorInformer = new ErrorInformer2(DriverDocsSetting.getInstance().getCssUrl());

    @FXML
    private TextField txtLastname;
    @FXML
    private TextField txtFirstname;
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
    }

    private void setupButtonApply() {
        btnApply.setOnAction(ev -> {
            try {
                log.trace("добавим нового водителя: lastname='{}', firstname='{}', secondname='{}', birthdate='{}'", txtLastname.getText(), txtFirstname.getText(), txtSecondname.getText(), dtBirthdate.getValue());

                String lastname = txtLastname.getText().trim();
                String firstname = txtFirstname.getText().trim();
                String secondname = txtSecondname.getText().trim();
                LocalDate birthdate = dtBirthdate.getValue();

                if (txtLastname.getText().trim().isEmpty())
                    throw new IllegalArgumentException("необходимо указать фамилию");

                if (txtFirstname.getText().trim().isEmpty())
                    throw new IllegalArgumentException("необходимо указать имя");

                if (dtBirthdate.getValue() == null)
                    throw new IllegalArgumentException("необходимо указать дату рождения");


                long d = repo.create(lastname, firstname, secondname, birthdate).blockingGet();
                tblDrivers.getItems().add(new DriverImpl(d, lastname, firstname, secondname, birthdate));

                txtLastname.setText("");
                txtFirstname.setText("");
                txtSecondname.setText("");
                dtBirthdate.setValue(null);

            } catch (Exception e) {
                errorInformer.displayError("не удалось добавить водителя", e);
            }
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

        colDelete.setCellFactory(col -> new ActionColumn<>(selRow -> {
            try {
                DriverImpl driver = tblDrivers.getItems().get(selRow);
                log.trace("удалим водителя: id={}, lastname={},firstname={}, secondname={}",
                        driver.getId(), driver.getLastname(), driver.getFirstname(), driver.getSecondname());
                repo.delete(driver.getId()).blockingAwait();
                tblDrivers.getItems().remove(selRow.intValue());
            } catch (Exception e) {
                log.warn("не удалось удалить водителя", e);
                errorInformer.displayError("не удалось удалить водителя", e);
            }
        }));

        List<DriverImpl> drivers = repo.findAll().map(DriverImpl::createOf).toList().blockingGet();
        tblDrivers.getItems().addAll(drivers);
    }

    private void setupLastnameColumn() {
        colLastname.setCellFactory(TextFieldTableCell.forTableColumn());
        colLastname.setOnEditCommit((TableColumn.CellEditEvent<DriverImpl, String> t) -> {
            try {
                DriverImpl d = t.getTableView().getItems().get(t.getTablePosition().getRow());
                log.trace("обновим фамилию водителя: driver-id={}, old-lastname={}, new-lastname={}", d.getId(), t.getOldValue(), t.getNewValue());
                repo.updateLastname(d.getId(), t.getNewValue()).blockingAwait();
                d.setLastname(t.getNewValue());
            } catch (Exception e) {
                errorInformer.displayError("не удалось обновить фамилию водителя", e);
            }
        });

    }

    private void setupFirstnameColumn() {
        colFirstname.setCellFactory(TextFieldTableCell.forTableColumn());
        colFirstname.setOnEditCommit((TableColumn.CellEditEvent<DriverImpl, String> t) -> {
            try {
                DriverImpl d = t.getTableView().getItems().get(t.getTablePosition().getRow());
                log.trace("обновим имя водителя: driver-id={}, old-firstname={}, new-firstname={}", d.getId(), t.getOldValue(), t.getNewValue());
                repo.updateFirstname(d.getId(), t.getNewValue()).blockingAwait();
                d.setFirstname(t.getNewValue());
            } catch (Exception e) {
                errorInformer.displayError("не удалось обновить имя водителя", e);
            }
        });
    }

    private void setupSecondnameColumn() {
        colSecondname.setCellFactory(TextFieldTableCell.forTableColumn());
        colSecondname.setOnEditCommit((TableColumn.CellEditEvent<DriverImpl, String> t) -> {
            try {
                DriverImpl d = t.getTableView().getItems().get(t.getTablePosition().getRow());
                log.trace("обновим отчество водителя: driver-id={}, old-secondname={}, new-secondname={}", d.getId(), t.getOldValue(), t.getNewValue());
                repo.updateSecondname(d.getId(), t.getNewValue()).blockingAwait();
                d.setSecondname(t.getNewValue());
            } catch (Exception e) {
                errorInformer.displayError("не удалось обновить отчество водителя", e);
            }
        });
    }

    private void setupBirthdateColumn() {
        colBirthdate.setCellFactory(param -> new DatePickerCell<>());
        colBirthdate.setOnEditCommit((TableColumn.CellEditEvent<DriverImpl, LocalDate> t) -> {
            try {
                DriverImpl d = t.getTableView().getItems().get(t.getTablePosition().getRow());
                log.trace("обновим дату рождения водителя: driver-id={}, old-birthdate={}, new-birthdate={}", d.getId(), t.getOldValue(), t.getNewValue());
                repo.updateBirthdate(d.getId(), t.getNewValue()).blockingAwait();
                d.setBirthdate(t.getNewValue());
            } catch (Exception e) {
                errorInformer.displayError("не удалось обновить дату рождения водителя", e);
            }
        });
    }

//    private class ActionColumn extends TableCell<DriverImpl, String> {
//        Button btn = new Button("удалить");
//
//        public ActionColumn() {
//            btn.setOnAction(ev -> {
//                try {
//                    DriverImpl driver = getTableView().getItems().get(getTableRow().getIndex());
//                    log.trace("удалим водителя: id={}, lastname={},firstname={}, secondname={}", driver.getId(), driver.getLastname(), driver.getFirstname(), driver.getSecondname());
//                    repo.delete(driver.getId()).blockingAwait();
//                    tblDrivers.getItems().remove(getTableRow().getIndex());
//                } catch (Exception e) {
//                    errorInformer.displayError("не удалось удалить водителя", e);
//                }
//
//            });
//        }
//
//        @Override
//        protected void updateItem(String item, boolean empty) {
//            super.updateItem(item, empty);
//            if (empty || getTableRow() == null) {
//                setGraphic(null);
//            } else {
//                setGraphic(btn);
//            }
//        }
//    }

}
