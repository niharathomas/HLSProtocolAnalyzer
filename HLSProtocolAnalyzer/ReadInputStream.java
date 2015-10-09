package HLSProtocolAnalyzer;

import java.awt.List;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;

// import static HLSProtocolAnalyzer.PlaylistTagConstants;

public class ReadInputStream {
	private int lineNumber = 0;
	private static String inputURL;
	private static URL url;
	private static String baseURL;
	public String fileName;
	private static InputStream inStream;
	private static BufferedReader bufReader;
	private static String inputLine;
	private FileChecker fileChecker = new FileChecker();
	private LoggerWrapper loggerWrapper = LoggerWrapper.getInstance();

	public void ReadInputStream(String inputURL) {
		this.inputURL = inputURL;
	}

	public void printStream(String inputURL) {
		if (isValidURL(inputURL)) {
			try {
				url = new URL(inputURL);
				System.out.println("Accessing url...");

				baseURL = FilenameUtils.getPath(inputURL);
				fileName = FilenameUtils.getName(inputURL);
				loggerWrapper.myLogger.info("Base URL is:" + baseURL);

				inStream = url.openStream();
				bufReader = new BufferedReader(new InputStreamReader(inStream));

				System.out.println("Beginning tests...");
				fileChecker.fileCheckLoop(bufReader, baseURL, fileName);

				bufReader.close();
			} catch (MalformedURLException mue) {
				// Catch exception and print stacktrace
				System.out.println("MalformedURLException caught!");
				mue.printStackTrace();
				System.exit(1);
			} catch (IOException ioe) {
				// Catch exception and print stacktrace
				System.out.println("IOException caught!");
				ioe.printStackTrace();
				System.exit(1);
			}
		}

		else {
			System.out.println("Invalid URL");
		}
	}

	public Boolean isValidURL(String inBaseURL) {
		HttpURLConnection connection = null;
		try {
			URL myurl = new URL(inBaseURL);
			connection = (HttpURLConnection) myurl.openConnection();
			// Setting request to header to reduce load
			connection.setRequestMethod("HEAD");
			int code = connection.getResponseCode();
			System.out.println("" + code);
			return true;
		} catch (MalformedURLException e) {
			// Handle invalid URL
			return false;

		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

}
