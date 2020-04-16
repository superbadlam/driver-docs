package ru.driverdocs.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.driverdocs.DriverDocsSetting;
import ru.driverdocs.helpers.ui.AbstractController;
import ru.driverdocs.helpers.ui.ActionColumn;
import ru.driverdocs.helpers.ui.ErrorInformer2;
import ru.driverdocs.rxrepositories.RouteRepository;
import ru.driverdocs.ui.data.RouteImpl;

import java.io.IOException;
import java.util.List;

public class RouteEditorController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RouteEditorController.class);
    private static final String FXML_FILE = "/fxml/RouteEditorView.fxml";
    private final RouteRepository repo = DriverDocsSetting.getInstance().getRouteRepository();
    private final ErrorInformer2 errorInformer = new ErrorInformer2(DriverDocsSetting.getInstance().getCssUrl());

    @FXML
    private TextField txtName;
    @FXML
    private Button btnApply;
    @FXML
    private TableView<RouteImpl> tblEmployers;
    @FXML
    private TableColumn<RouteImpl, String> colName;
    @FXML
    private TableColumn<RouteImpl, String> colDelete;

    public static RouteEditorController build() throws IOException {
        RouteEditorController c = new RouteEditorController();
        c.load(FXML_FILE);
        return c;
    }

    @FXML
    private void initialize() {
        setupDriverTable();
        setupNameColumn();
        setupButtonApply();
    }

    private void setupButtonApply() {
        btnApply.setOnAction(ev -> {
            try {
                log.trace("добавим новый маршрут: route-name='{}'", txtName.getText());

                String name = txtName.getText().trim();

                if (txtName.getText().trim().isEmpty())
                    throw new IllegalArgumentException("необходимо указать название маршрута");

                long d = repo.create(name).blockingGet();
                tblEmployers.getItems().add(RouteImpl.createOf(d, name));

                txtName.setText("");

            } catch (Exception e) {
                log.warn("не удалось добавить маршрут", e);
                errorInformer.displayError("не удалось добавить маршрут", e);
            }
        });
    }

    private void setupDriverTable() {
        colName.prefWidthProperty().bind(tblEmployers.widthProperty().multiply(0.85));
        colDelete.prefWidthProperty().bind(tblEmployers.widthProperty().multiply(0.15));

        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDelete.setCellFactory(col -> new ActionColumn<>(selRow -> {
            try {
                RouteImpl route = tblEmployers.getItems().get(selRow);
                log.trace("удалим маршрут: id={}, name={}", route.getId(), route.getName());
                repo.delete(route.getId()).blockingAwait();
                tblEmployers.getItems().remove(selRow.intValue());
            } catch (Exception e) {
                log.warn("не удалось удалить маршрут", e);
                errorInformer.displayError("не удалось удалить маршрут", e);
            }
        }));

        List<RouteImpl> drivers = repo.findAll().map(RouteImpl::createOf).toList().blockingGet();
        tblEmployers.getItems().addAll(drivers);
    }

    private void setupNameColumn() {
        colName.setCellFactory(TextFieldTableCell.forTableColumn());
        colName.setOnEditCommit((TableColumn.CellEditEvent<RouteImpl, String> t) -> {
            try {
                RouteImpl d = t.getTableView().getItems().get(t.getTablePosition().getRow());
                log.trace("обновим название маршрута: route-id={}, old-name={}, new-name={}", d.getId(), t.getOldValue(), t.getNewValue());
                repo.update(d.getId(), t.getNewValue()).blockingAwait();
                d.setName(t.getNewValue());
            } catch (Exception e) {
                errorInformer.displayError("не удалось обновить название маршрута", e);
            }
        });

    }

//    private class ActionColumn extends TableCell<RouteImpl, String> {
//        Button btn = new Button("удалить");
//
//        public ActionColumn() {
//            btn.setOnAction(ev -> {
//                try {
//                    RouteImpl route = getTableView().getItems().get(getTableRow().getIndex());
//                    log.trace("удалим маршрут: id={}, name={}", route.getId(), route.getName());
//                    repo.delete(route.getId()).blockingAwait();
//                    tblEmployers.getItems().remove(getTableRow().getIndex());
//                } catch (Exception e) {
//                    log.warn("не удалось удалить маршрут", e);
//                    errorInformer.displayError("не удалось удалить маршрут", e);
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
