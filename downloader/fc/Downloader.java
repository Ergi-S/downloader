package downloader.fc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.concurrent.Task;

/**
 * The class that represents the functional core of our download manager
 */
public class Downloader extends Task<String> {
	public static final int CHUNK_SIZE = 1024;

	private ReentrantLock lock;
	private Condition isPaused; // used for await (wait) and signal (notify)

	URL url;
	int content_length;
	BufferedInputStream in;

	String filename;
	File temp;
	FileOutputStream out;

	ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper(this, "progress", 0);

	int size = 0;
	int count = 0;
	private boolean downloading; // boolean which tells if the thread is downloading or not

	public Downloader(String uri) {
		try {
			url = new URL(uri);

			URLConnection connection = url.openConnection();
			content_length = connection.getContentLength();

			in = new BufferedInputStream(connection.getInputStream());

			String[] path = url.getFile().split("/");
			filename = path[path.length - 1];
			temp = File.createTempFile(filename, ".part");
			out = new FileOutputStream(temp);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		lock = new ReentrantLock();
		isPaused = lock.newCondition();
		downloading = true; // Once created, it starts downloading right away
	}

	public String toString() {
		return url.toString();
	}

	protected String download() throws InterruptedException {
		byte buffer[] = new byte[CHUNK_SIZE];

		while (count >= 0) {
			lock.lock();
			try {
				while (!downloading) // guard condition
					isPaused.await(); // thread not downloading, so it is paused, so we wait
				try {
					out.write(buffer, 0, count);
				} catch (IOException e) {
					continue;
				}

				size += count;
				updateProgress(1. * size, content_length);
				Thread.sleep(1000);

				try {
					count = in.read(buffer, 0, CHUNK_SIZE);
				} catch (IOException e) {
					continue;
				}

			} finally {
				lock.unlock(); // Always unlock after locking
			}
			System.out.print(".");
		}

		if (size < content_length) {
			temp.delete();
			System.out.println("Download Interrupted");
			throw new InterruptedException();
		}

		temp.renameTo(new File(filename));
		System.out.println(filename + " Download Complete \n");
		return filename;
	}

	public String getFilename() {
		return filename;
	}

	@Override // Task method
	protected String call() throws Exception {
		try {
			download();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return filename;
	}

	public void play() {
		lock.lock();
		try {
			downloading = true;
			isPaused.signal(); // notify the thread to continue the download
		} finally {
			lock.unlock();
		}
	}

	public void pause() {
		lock.lock();
		try {
			downloading = false;
		} finally {
			lock.unlock();
		}
	}
}