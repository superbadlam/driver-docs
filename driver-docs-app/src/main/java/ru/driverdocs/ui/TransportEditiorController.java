package ru.driverdocs.ui;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.driverdocs.DriverDocsSetting;
import ru.driverdocs.helpers.ui.AbstractController;
import ru.driverdocs.helpers.ui.ErrorInformer2;
import ru.driverdocs.rxrepositories.EmployerRepository;
import ru.driverdocs.ui.data.EmployerImpl;

import java.io.IOException;

public final class TransportEditiorController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(TransportEditiorController.class);
    private static final String FXML_FILE = "/fxml/TransportEditorView.fxml";
    private final EmployerRepository repo = DriverDocsSetting.getInstance().getEmployerRepository();
    private final ErrorInformer2 errorInformer = new ErrorInformer2(DriverDocsSetting.getInstance().getCssUrl());
    private final EmployerImpl currEmployer = new EmployerImpl();

    @FXML
    private ComboBox<EmployerImpl> cmbEmployers;
    @FXML
    private TransportTableControl tableControl;
    @FXML
    private TransportInputsControl inputsControl;


    private TransportEditiorController() {
        currEmployer.resetState();
    }

    public static TransportEditiorController build() throws IOException {
        TransportEditiorController c = new TransportEditiorController();
        c.load(FXML_FILE);
        return c;
    }

    @FXML
    private void initialize() {
        log.info("hgkjsgkd");
//        tableControl.prefHeightProperty().bind(tablePane.prefHeightProperty().multiply(0.95));
//        tableControl.prefWidthProperty().bind(tablePane.prefWidthProperty().multiply(0.95));
//        setupInputs();
//        setupTable();
//        setupButtonApply();
    }

}
