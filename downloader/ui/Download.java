package downloader.ui;

import downloader.fc.Downloader;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Class which visualisez the state of a download. 
 * Each Download is represented as a BorderPance
 */
public class Download extends BorderPane {
	boolean paused = false; // Indicates the function and the display icon of the playPause Button

	public Download(Downloader downloader) {
		Thread th = new Thread(downloader);
		th.setDaemon(true);

		ProgressBar pb = new ProgressBar(downloader.progressProperty().doubleValue());
		pb.setMaxWidth(Double.MAX_VALUE);
		Button playPause = new Button("▮▮");
		playPause.setOnAction((e) -> {
			if (paused) {
				playPause.setText("▮▮");
				downloader.play();
				paused = false;
			} else {
				playPause.setText("▶");
				downloader.pause();
				paused = true;
			}
		});
		Button remove = new Button("🞫");
		remove.setOnAction((e) -> {
			downloader.cancel(); // We can do this thanks to the Task Class
			VBox par = (VBox) getParent();
			par.getChildren().remove(this);
		});
		ToolBar plare = new ToolBar(playPause, remove);
		setCenter(pb);
		setRight(plare);
		setTop(new Text(downloader.toString()));

		setPadding(new Insets(2));
		setMaxWidth(Double.MAX_VALUE);
		downloader.progressProperty().addListener((observable, o, n) -> {
			Platform.runLater(() -> {
				pb.setProgress((double) n);
				if ((double) n == 1.0) {
					playPause.setText("✔");
					playPause.setDisable(true);
				}
			});
		});
		th.start();
	}
}
