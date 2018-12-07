package downloader.ui;

import downloader.fc.Downloader;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public class Download extends BorderPane{
	/*
	Lock m_lock;
	*/
	
	public Download (Downloader downloader) {
		Thread th = new Thread(downloader);
		th.setDaemon(true);
		
		ProgressBar pb = new ProgressBar(downloader.progressProperty().doubleValue());
		pb.setMaxWidth(Double.MAX_VALUE);
		Button play = new Button(">");
		Button remove = new Button("X");
		remove.setOnAction((e) -> {
			removeDownload();
		});
		ToolBar plare = new ToolBar(play, remove);
		setCenter(pb);
		setRight(plare);
		setTop(new Text(downloader.toString()));
		
		setPadding(new Insets(2));
		setMaxWidth(Double.MAX_VALUE);
		downloader.progressProperty().addListener((observable, o, n) -> {
			Platform.runLater(() -> {
				pb.progressProperty().setValue(n);
			});
		});
		th.start();
	}
	
	public void removeDownload() {
		System.out.println("Delete");
	}
}
