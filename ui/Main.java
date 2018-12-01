package ui;

import downloader.fc.Downloader;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

	VBox downloads = new VBox();
	@Override
	public void start(Stage stage) {
		ScrollPane total = new ScrollPane(downloads);
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
