package ru.driverdocs.helpers.ui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public interface Controller {

	void setRootPane(Pane pane);
	Pane getRootPane();

	static Stage createStage(String title, Scene scene, Stage owner, String url2skin){
		if(url2skin!=null)
			scene.getStylesheets().add(url2skin);
		
		Stage stage=new Stage();
		stage.setScene(scene);
		stage.sizeToScene();
		stage.centerOnScreen();
		stage.setTitle(title);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(owner);
		stage.setResizable(true);

		return stage;
	}
	
	static void maximazeStage(Stage stage){
		Platform.runLater(() -> stage.setMaximized(true));
	}
}
