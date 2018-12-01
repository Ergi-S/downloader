package downloader.fc;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;

import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;

/* creer un thread est facile. Implemnts runnable et a une methode run.
 * il suffit de faire
 * t = new Thread(new Downloader) // s'abonne au run du downloader
 * t.start // lance le thread
 */

public class Downloader implements Runnable {
	public static final int CHUNK_SIZE = 1024;

	URL url;
	int content_length;
	BufferedInputStream in;
	
	String filename;
	File temp;
	FileOutputStream out;
	
	ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper(this, "progress", 0); //pas public avant

	int size = 0;
	int count = 0;
	
	public Downloader(String uri) {
		try {
			url = new URL(uri);
			
			URLConnection connection = url.openConnection();
			content_length = connection.getContentLength();
			
			in = new BufferedInputStream(connection.getInputStream());
			
			String[] path = url.getFile().split("/");
			filename = path[path.length-1];
			temp = File.createTempFile(filename, ".part");
			out = new FileOutputStream(temp);
		}
		catch(MalformedURLException e) { throw new RuntimeException(e); }
		catch(IOException e) { throw new RuntimeException(e); }
	}

	public String toString() {
		return url.toString();
	}
	
	public ReadOnlyDoubleProperty progressProperty() {
		return progress.getReadOnlyProperty();
	}
	
	public String download() throws InterruptedException { //protected avant
		byte buffer[] = new byte[CHUNK_SIZE];
		
		while(count >= 0) {
			try {
				out.write(buffer, 0, count);
			}
			catch(IOException e) { continue; }
			
			size += count;
			progress.setValue(1.*size/content_length); 
			/* les petits points qui avancent sont executes sur cette ligne
			 * runlater renvoie un bout de code pour qu'il so
			 */
			Thread.sleep(1000);
			
			try {
				count = in.read(buffer, 0, CHUNK_SIZE);
			}
			catch(IOException e) { continue; }
		}
		
		if(size < content_length) {
			temp.delete();
			throw new InterruptedException();
		}
			
		temp.renameTo(new File(filename));
		return filename;
	}
	
	public void run() {
		try {
			download();
		}
		catch(InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
};
