package downloader.ui;

import downloader.fc.Downloader;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

	VBox downloads = new VBox(); // We put our downloads (BorderPanes) in a VBox
	@Override
	public void start(Stage stage) {
		ScrollPane sp = new ScrollPane(downloads); // And that VBox in a ScrollPane
		sp.setPannable(true);
		sp.setFitToWidth(true);
		
		TextArea urlInput = new TextArea();
		urlInput.setPrefHeight(50);
		Button urlAdd = new Button("Add");
		urlAdd.setPrefSize(50,50);
		urlAdd.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				download(urlInput.getText());
			}
		});
		// A BorderPane so it always sticks at the bottom of the window
		BorderPane inputAdd = new BorderPane(urlInput);
		inputAdd.setRight(urlAdd);
		
		BorderPane total = new BorderPane(sp);
		total.setBottom(inputAdd);
		total.setPrefHeight(480);

		
		stage.setTitle("downloader");
		stage.setScene(new Scene(total));
		
		for (String param : getParameters().getRaw())
			download(param);
		
		stage.show();	
		
	}
	
	void download(String url) {
		Downloader downloader;
		try {
			downloader = new Downloader(url);
		} catch (RuntimeException e) {
			Alert alert = new Alert(AlertType.ERROR, "URL non valide !");
			alert.showAndWait();
			return;
		}
		downloads.getChildren().add(new Download(downloader));
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
