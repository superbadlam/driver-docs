package ru.driverdocs.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.driverdocs.DriverDocsSetting;
import ru.driverdocs.helpers.ui.AbstractController;
import ru.driverdocs.helpers.ui.ActionColumn;
import ru.driverdocs.helpers.ui.ErrorInformer2;
import ru.driverdocs.rxrepositories.EmployerRepository;
import ru.driverdocs.ui.data.EmployerImpl;
import ru.driverdocs.ui.validator.EmployerValidator;

import java.io.IOException;
import java.util.List;

import static ru.driverdocs.ui.ControlUtils.*;

public final class EmployerEditiorController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(EmployerEditiorController.class);
    private static final String FXML_FILE = "/fxml/EmployerEditorView.fxml";
    private final EmployerRepository repo = DriverDocsSetting.getInstance().getEmployerRepository();
    private final ErrorInformer2 errorInformer = new ErrorInformer2(DriverDocsSetting.getInstance().getCssUrl());
    private final EmployerValidator validator = new EmployerValidator();
    private final EmployerImpl newEmployer = new EmployerImpl();
    private EmployerImpl editEmployer;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtInn;
    @FXML
    private TextField txtOgrn;
    @FXML
    private TextField txtAddress;
    @FXML
    private Button btnApply;
    @FXML
    private TableView<EmployerImpl> tblEmployers;
    @FXML
    private TableColumn<EmployerImpl, String> colName;
    @FXML
    private TableColumn<EmployerImpl, String> colInn;
    @FXML
    private TableColumn<EmployerImpl, String> colOgrn;
    @FXML
    private TableColumn<EmployerImpl, String> colAddress;
    @FXML
    private TableColumn<EmployerImpl, String> colDelete;
    @FXML
    private EmployerLicenceControl licenceControl;


    private EmployerEditiorController() {
        newEmployer.resetState();
    }

    public static EmployerEditiorController build() throws IOException {
        EmployerEditiorController c = new EmployerEditiorController();
        c.load(FXML_FILE);
        return c;
    }

    @FXML
    private void initialize() {
        setupInputs();
        setupTable();
        setupButtonApply();
    }

    //    private void setupNameColumn() {
//        colName.setCellFactory(TextFieldTableCell.forTableColumn());
//        colName.setOnEditCommit((TableColumn.CellEditEvent<EmployerImpl, String> t) -> {
//            try {
//                EmployerImpl d = t.getTableView().getItems().get(t.getTablePosition().getRow());
//                log.trace("обновим employer name: employer-id={}, old-name={}, new-name={}", d.getId(), t.getOldValue(), t.getNewValue());
//                repo.updateName(d.getId(), t.getNewValue()).blockingAwait();
//                d.setName(t.getNewValue());
//            } catch (Exception e) {
//                errorInformer.displayError("не удалось обновить employer name", e);
//            }
//        });
//    }
    private void setupTable() {
        colName.prefWidthProperty().bind(tblEmployers.widthProperty().multiply(0.25));
        colInn.prefWidthProperty().bind(tblEmployers.widthProperty().multiply(0.20));
        colOgrn.prefWidthProperty().bind(tblEmployers.widthProperty().multiply(0.25));
        colAddress.prefWidthProperty().bind(tblEmployers.widthProperty().multiply(0.15));
        colDelete.prefWidthProperty().bind(tblEmployers.widthProperty().multiply(0.15));

        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colInn.setCellValueFactory(new PropertyValueFactory<>("inn"));
        colOgrn.setCellValueFactory(new PropertyValueFactory<>("ogrn"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        colDelete.setCellFactory(col -> new ActionColumn<>(selRow -> {
            try {
                EmployerImpl employer = tblEmployers.getItems().get(selRow);
                log.trace("удалим препринимателя: id={}, name={}, inn={}, ogrn={}, address={}",
                        employer.getId(), employer.getName(), employer.getInn(),
                        employer.getOgrn(), employer.getAddress());
                repo.delete(employer.getId()).blockingAwait();
                tblEmployers.getItems().remove(selRow.intValue());
            } catch (Exception e) {
                log.warn("не удалось удалить препринимателя", e);
                errorInformer.displayError("не удалось удалить препринимателя", e);
            }
        }));

        tblEmployers.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                editEmployer = tblEmployers.getSelectionModel().getSelectedItem();
                newEmployer.copyState(editEmployer);
            }
        });

        List<EmployerImpl> drivers = repo.findAll().map(EmployerImpl::createOf).toList().blockingGet();
        tblEmployers.getItems().addAll(drivers);

        licenceControl.employerProperty().bind(tblEmployers.getSelectionModel().selectedItemProperty());
    }

    private void setupButtonApply() {
        btnApply.setOnAction(ev -> {
            if (newEmployer.getId() == 0) {
                insertNewEmployer();
            } else {
                updateEmployer();
            }
        });
    }

    private void updateEmployer() {
        try {
            log.trace("обновим предпринимателя: " +
                            "id={}, name='{}', address='{}', inn='{}', ogrn='{}'",
                    newEmployer.getId(), newEmployer.getName(), newEmployer.getAddress(),
                    newEmployer.getInn(), newEmployer.getOgrn());

            repo.update(newEmployer.getId(), newEmployer.getName(), newEmployer.getInn(),
                    newEmployer.getOgrn(), newEmployer.getAddress()).blockingAwait();

            editEmployer.copyState(newEmployer);
            newEmployer.resetState();

        } catch (Exception e) {
            errorInformer.displayError("не удалось обновить предпринимателя", e);
        }
    }

    private void insertNewEmployer() {
        try {
            log.trace("добавим нового предпринимателя: " +
                            "name='{}', address='{}', inn='{}', ogrn='{}'",
                    newEmployer.getName(), newEmployer.getAddress(), newEmployer.getInn(), newEmployer.getOgrn());

            newEmployer.setId(
                    repo.create(newEmployer.getName(), newEmployer.getInn(),
                            newEmployer.getOgrn(), newEmployer.getAddress()).blockingGet());

            tblEmployers.getItems().add(EmployerImpl.createOf(newEmployer));
            newEmployer.resetState();

        } catch (Exception e) {
            errorInformer.displayError("не удалось добавить предпринимателя", e);
        }
    }

    private void setupInputs() {
        newEmployer.innProperty().bindBidirectional(txtInn.textProperty());
        newEmployer.ogrnProperty().bindBidirectional(txtOgrn.textProperty());
        newEmployer.addressProperty().bindBidirectional(txtAddress.textProperty());
        newEmployer.nameProperty().bindBidirectional(txtName.textProperty());

        whenFocusLost(txtInn, () -> highlightOnWhen(txtInn, s -> !validator.isValidInn(s)));
        whenFocusSet(txtInn, () -> highlightOff(txtInn));
        whenFocusLost(txtOgrn, () -> highlightOnWhen(txtOgrn, s -> !validator.isValidOgrn(s)));
        whenFocusSet(txtOgrn, () -> highlightOff(txtOgrn));
        whenFocusLost(txtName, () -> highlightOnWhen(txtName, s -> !validator.isValidName(s)));
        whenFocusSet(txtName, () -> highlightOff(txtName));
        whenFocusLost(txtAddress, () -> highlightOnWhen(txtAddress, s -> !validator.isValidAddress(s)));
        whenFocusSet(txtAddress, () -> highlightOff(txtAddress));

        btnApply.disableProperty().bind(newEmployer.invalidProperty());
    }

//    private void highlightOnWhen(TextField textField, Predicate<String> predicate) {
//        if (predicate.test(textField.getText())) {
//            textField.setStyle("-fx-control-inner-background: red");
//        }
//    }
//
//    private void highlightOff(TextField textField) {
//        textField.setStyle("-fx-control-inner-background: white");
//    }
}
