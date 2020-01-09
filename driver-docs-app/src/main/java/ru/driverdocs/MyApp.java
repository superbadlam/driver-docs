package ru.driverdocs;

import io.reactivex.Observable;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MyApp extends Application {

    private static Logger logger = LoggerFactory.getLogger(MyApp.class);

    @Override
    public void start(Stage stage) {

        ComboBox<String> letterCombo = new ComboBox<>();
        letterCombo.getItems().setAll("A", "B", "C", "D", "E", "F");

        ComboBox<Integer> numberCombo = new ComboBox<>();
        numberCombo.getItems().setAll(1,2,3,4,5);

        Observable<String> letterSelections =
                JavaFxObservable.valuesOf(letterCombo.valueProperty());
        Observable<Integer> numberSelections =
                JavaFxObservable.valuesOf(numberCombo.valueProperty());
        Observable.combineLatest(letterSelections, numberSelections, (l, n) -> l + "-" + n)
                .subscribe(System.out::println,
                        Throwable::printStackTrace,
                        () -> System.out.println("Done!")
                );
        HBox root = new HBox();
        root.getChildren().setAll(letterCombo,numberCombo);
        stage.setScene(new Scene(root));
        stage.show();
    }
}
