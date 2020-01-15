package ru.driverdocs.helpers.ui;


import javafx.application.Application;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;


public abstract class ApplicationEx2 extends Application {
    protected Stage stage;
    protected List<ApplicationCloseHandler> closeListeners = new ArrayList<>();

    public boolean addCloseListeners(ApplicationCloseHandler e) {
        return closeListeners.add(e);
    }

    public boolean removeCloseListeners(ApplicationCloseHandler o) {
        return closeListeners.remove(o);
    }

    public Stage getStage() {
        return stage;
    }
}
