package ui;

import downloader.fc.Downloader;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ProgressBar;

public class Download extends ProgressBar{
	/*
	Downloader m_downloader;
	Thread m_thread;
	ProgressBar m_pb;
	Lock m_lock;
	*/
	
	public Download (Downloader downloader) {
		Thread th = new Thread(downloader);
		th.setDaemon(true);
		
		setPadding(new Insets(2));
		setMaxWidth(Double.MAX_VALUE);
		downloader.progressProperty().addListener((observable, o, n) -> {
			Platform.runLater(() -> {
				progressProperty().setValue(n);
				System.out.print(".");
				System.out.flush();
			});
		});
		th.start();
	}
}
