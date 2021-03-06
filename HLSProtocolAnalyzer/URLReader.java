package HLSProtocolAnalyzer;

import java.awt.List;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.validator.routines.UrlValidator;

// import static HLSProtocolAnalyzer.PlaylistTagConstants;

public class URLReader {
	private int lineNumber = 0;
	private static String inputURL;
	private static URL url;
	private static String baseURL;
	public String fileName;
	FileChecker checkFile;
	private static InputStream inStream;
	private static BufferedReader bufReader;
	private static String inputLine;
	ExcelResultWriter resultWriter;
	// private FileChecker_old fileChecker = new FileChecker_old();
	private LoggerWrapper loggerWrapper = LoggerWrapper.getInstance();
	private ArrayList<String> masterFileList = new ArrayList<String>();
	// If extension is .ts, store in mediaFiles List
	private ArrayList<String> mediaSegments = new ArrayList<String>();
	// If extension is .m3u8, store in mediaPlaylists List
	private ArrayList<String> playlistFiles = new ArrayList<String>();

	public URLReader(String inputURL) {
		this.inputURL = inputURL;
		
		// Creating excel file to save errors
		resultWriter = new ExcelResultWriter();
		resultWriter.createExcelFile();
	}

	public ArrayList<String> getMasterFileList(String inputURL) {
		/*
		 * Checks if input URL is valid, if not returns an error
		 * If valid, gets all the URLs on the page and puts them in a masterFileList array list
		 */
		loggerWrapper.myLogger.info("Getting list of files...");
			try {
				Document doc;
				doc = Jsoup.connect(inputURL).get();
				for (Element file : doc.select("td a")) {
					this.masterFileList.add(file.attr("href"));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return this.masterFileList;
	}

	public void fileSeparator() {
		/*
		 * Takes masterFileList as the input and splits files based on type .ts
		 * media files are added to the mediaFiles ArrayList .m3u8 playlist
		 * files are added to the playlistFiles ArrayList
		 */
		for (String file : masterFileList) {
			if ((FilenameUtils.getExtension(file)).equals("ts")) {
				mediaSegments.add(file);
			} else if ((FilenameUtils.getExtension(file)).equals("m3u8")) {
				playlistFiles.add(file);
			} else {
				loggerWrapper.myLogger.severe("Invalid file extension: " + file);
			}
		}
	}
	
	public void fileLoop(String inputURL){
		/*
		 * Takes playlistFiles array list
		 * Loops through all files and runs checks on them
		 */
		baseURL = FilenameUtils.getPath(inputURL);
		loggerWrapper.myLogger.info("Base URL is:" + baseURL);

		System.out.println("Looping through files...");
		for (int i = 0; i < playlistFiles.size(); i++) {
			String fileName = playlistFiles.get(i);
			if (i == 0){
				checkFile = new MasterPlaylistChecker(baseURL, fileName, mediaSegments, playlistFiles, resultWriter);
			}
			else{
				checkFile = new MediaPlaylistChecker(baseURL, fileName, mediaSegments, playlistFiles, resultWriter);
			}
			
			try {
				url = new URL(baseURL + fileName);
				inStream = url.openStream();
				bufReader = new BufferedReader(new InputStreamReader(inStream));

				System.out.println("File under test: " + fileName);
				checkFile.runChecks(bufReader);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Completed Checks. Errors in Results.xls");
	}

	public Boolean isValidURL() {
		/*HttpURLConnection connection = null;
		try {
			URL myurl = new URL(inputURL);
			connection = (HttpURLConnection) myurl.openConnection();
			// Setting request to header to reduce load
			connection.setRequestMethod("HEAD");
			int code = connection.getResponseCode();
			return true;
		} catch (MalformedURLException e) {
			return false;

		} catch (ProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}*/
		
		UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
		if (urlValidator.isValid(inputURL)) {
		   return true;
		} else {
		   return false;
		}
		
	}

}
