package HLSProtocolAnalyzer;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

public abstract class FileChecker {
	protected int lineNumber = 0;
	private double duration;
	private int version;
	private static String baseURL;
	protected static String inputLine;
	protected static String fileName;
	ExcelResultWriter resultWriter;
	private LoggerWrapper loggerWrapper = LoggerWrapper.getInstance();
	
	// List to store the tags found in the file
	private Map<Integer, String> tags = new HashMap<Integer, String>(200);
	// List to store all tags found in the input file
	private Map<Integer, String> tagsInFile = new HashMap<Integer, String>(50);
	// Array list to store media segments found in the input file
	protected ArrayList<String> mediaSegments = new ArrayList<String>();
	// Array list to store valid media segments found at the top level
	protected ArrayList<String> validMediaSegments = new ArrayList<String>();
	// Array list to store media playlists found in the input file
	protected ArrayList<String> mediaPlaylists = new ArrayList<String>();
	// Array list to store valid media playlists found at the top level
	protected ArrayList<String> validMediaPlaylists = new ArrayList<String>();
	// Array list that will be used to compare valid URIs to URIs from the file
	ArrayList<String> list = new ArrayList<String>();
	List<String> mandatoryTags = Arrays.asList("#EXTM3U");
	
	public FileChecker(String inBaseURL, String inFileName, ArrayList<String> inMediaSegments, 
			ArrayList<String> inPlaylistFiles, ExcelResultWriter inResultWriter){
		baseURL = inBaseURL;
		fileName = inFileName;
		validMediaSegments = inMediaSegments;
		validMediaPlaylists = inPlaylistFiles;
		resultWriter = inResultWriter;
	}
	
	public void checkDuplicateTag(){
		/*
		 * Input Tag to be checked
		 * Takes input tags and validates that the tag does not already exist in the tagsInFile hashmap
		 */
		
	}
	
	public void checkFirstTag(String inputLine, int lineNumber) {
		//if (inputLine.startsWith(new String("#EXTM3U"))) {
		if (inputLine.equals("#EXTM3U")) {
			if (tagsInFile.containsValue("#EXTM3U")) {
				loggerWrapper.myLogger
						.severe("ERROR!!! Repeated tag #EXTM3U at line # "
								+ lineNumber);
			} else {
				tagsInFile.put(lineNumber, "#EXTM3U");
				loggerWrapper.myLogger.info("Valid first line of input URL");
			}
		} else {
			resultWriter.writeNewRecord("Invalid tag", fileName,
					"Expected EXTM3U on the first line of the playlist");
			loggerWrapper.myLogger
					.severe("Expected EXTM3U on the first line of the playlist");
		}
	}
	
	public void checkFileTypes(String extenstion, String fileType){
		/*
		 * Checks for the file types found in the master playlist or media playlist
		 * If Master playlist: accepts m3u8 and "Media Playlist" as the input
		 * If Media Playlist: accepts ts and "Media Segment" as th input
		 * All other input values are rejected
		 */
		if (FilenameUtils.getExtension(inputLine).equals(extenstion)) {

			System.out.println(fileType + " URI : " + inputLine);
			String name = FilenameUtils.getName(inputLine);
			if (fileType.equals("Media Playlist")){
				loggerWrapper.myLogger.info("Media Playlist at lineNumber " + lineNumber);
				mediaPlaylists.add(name);
			}
			else if (fileType.equals("Media Segment")){
				mediaSegments.add(name);
			}
			else{
				loggerWrapper.myLogger.severe("Invalid filetype at lineNumber " + lineNumber);
			}
		}
	}
	
	public void checkBasicTags(){
		// Basic tag EXTM3U and EXT-X-VERSION checks
		if (inputLine.startsWith(new String("#EXT-X-VERSION"))) {

			System.out.println("Basic tag: " + inputLine);
			System.out.println(tagsInFile);
			if (tagsInFile.containsValue("#EXT-X-VERSION")) {
				System.out.println(fileName + " ****** " + lineNumber);
				resultWriter.writeNewRecord("Repeated Tag", fileName,
						"Repeated tag #EXT-X-VERSION at line # "
								+ lineNumber);
				loggerWrapper.myLogger
						.severe("ERROR!!! Repeated tag #EXT-X-VERSION at line # "
								+ lineNumber);
			} else {
				System.out.println("No repeated basic tags");
				tagsInFile.put(lineNumber, "#EXT-X-VERSION");
				System.out.println(tagsInFile);
				loggerWrapper.myLogger.info("Line: " + lineNumber + " "
						+ inputLine);
				
				// Store version number
				version = Integer.parseInt(inputLine.substring(inputLine.indexOf(":") + 1));
			}
		}
		
	}
	
	public void checkValidURIs(String fileType, ArrayList<String> checkList){
		if (fileType.equals("Media Playlist")){
			list = validMediaPlaylists;
		}
		else if (fileType.equals("Media Segment")){
			list = validMediaSegments;
		}
		else{
			loggerWrapper.myLogger.severe("Invalid file type");
		}
		for (String file: checkList){
			if (!list.contains(file)){
				resultWriter.writeNewRecord("URI does not exist", fileName,
						file + " at line # "
								+ lineNumber +" does not exist");
			}
		}
		
	}
	
	public void checkValidTags(){
		// Checks that all tags needed for a given file type match
	}
	
	abstract void runChecks(BufferedReader bufReader);

}
