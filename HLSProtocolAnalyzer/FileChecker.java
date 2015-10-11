package HLSProtocolAnalyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FileChecker {
	private int lineNumber = 0;
	private double duration;
	private int version;
	private static String baseURL;
	private static String inputLine;
	private static String fileName;
	public ExcelResultWriter resultWriter;
	private LoggerWrapper loggerWrapper = LoggerWrapper.getInstance();
	
	// List to store the basic tags found in the file
	private Map<Integer, String> basicTags = new HashMap<Integer, String>(200);
	// List to store all tags found in the input file
	private Map<Integer, String> tagsInFile = new HashMap<Integer, String>();
	// If extension is .ts, store in mediaFiles List
	private ArrayList<String> mediaFiles = new ArrayList<String>(20);
	// If extension is .m3u8, store in mediaPlaylists List
	private ArrayList<String> mediaPlaylists = new ArrayList<String>(20);
	List<String> mandatoryTags = Arrays.asList("#EXTM3U");
	
	public FileChecker(String inBaseURL, String inFileName){
		baseURL = inBaseURL;
		resultWriter = new ExcelResultWriter();
		fileName = inFileName;
	}
	
	public void checkDuplicateTag(){
		/*
		 * Input Tag to be checked
		 * Takes input tags and validates that the tag does not already exist in the tagsInFile hashmap
		 */
		
	}
	
	public void checkFirstTag(String inputLine, int lineNumber) {
		if (inputLine.startsWith(new String("#EXTM3U"))) {
			if (basicTags.containsValue("#EXTM3U")) {
				loggerWrapper.myLogger
						.severe("ERROR!!! Repeated tag #EXTM3U at line # "
								+ lineNumber);
			} else {
				basicTags.put(lineNumber, "#EXTM3U");
				loggerWrapper.myLogger.info("Valid first line of input URL");
			}
		} else {
			resultWriter.writeNewRecord("Invalid tag", fileName,
					"Expected EXTM3U on the first line of the playlist");
			loggerWrapper.myLogger
					.severe("Expected EXTM3U on the first line of the playlist");
		}
	}
	
	public void checkEndTag(){
		
	}
	
	public void isMasterPlaylist(){
		
	}
	
	abstract void runChecks();

}
