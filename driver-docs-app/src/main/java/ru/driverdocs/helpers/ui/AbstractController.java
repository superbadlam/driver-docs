package ru.driverdocs.helpers.ui;


import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.io.InputStream;


public abstract class AbstractController implements Controller {

    private Pane rootPane;

    @Override
    public Pane getRootPane() {
        return rootPane;
    }

    @Override
    public void setRootPane(Pane pane) {
        this.rootPane = pane;
    }

    public void load(String fxmFile) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setController(this);

        try (InputStream in = getClass().getResourceAsStream(fxmFile)) {
            if (rootPane != null) {
                loader.setRoot(rootPane);
                loader.load(in);
            } else rootPane = loader.load(in);
        }
    }


}
