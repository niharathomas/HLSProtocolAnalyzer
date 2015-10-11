package HLSProtocolAnalyzer;

import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.net.*;

import org.apache.poi.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.validator.routines.UrlValidator;

public class FileChecker_old {
	private int lineNumber = 0;
	private double duration;
	private int version;
	private static String baseURL;
	private static String inputLine;
	private static String fileName;
	public ExcelResultWriter resultWriter = new ExcelResultWriter();
	// List to store the basic tags found in the file
	private Map<Integer, String> basicTags = new HashMap<Integer, String>(200);
	// If extension is .ts, store in mediaFiles List
	private ArrayList<String> mediaFiles = new ArrayList<String>(20);
	// If extension is .m3u8, store in mediaPlaylists List
	private ArrayList<String> mediaPlaylists = new ArrayList<String>(20);
	private LoggerWrapper loggerWrapper = LoggerWrapper.getInstance();
	List<String> mandatoryTags = Arrays.asList("#EXTM3U");

	/**
	 * Takes arguments passed via command line (TODO: Change to accept options
	 * from the UI) Runs tests based on provided input
	 */

	// Check Syntax
	// Check for invalid tags
	// Check for the presence of all mandatory tags
	// Check for incorrect data
	// Check segment length
	// Filename format check

	public void fileCheckLoop(BufferedReader bufReader, String inBaseURL, String inputFileName) {
		// File that is being checked
		fileName = inputFileName;
		baseURL = inBaseURL;
		// Run tests

		// Creating excel file to save errors
		resultWriter.createExcelFile();
		
		// Validate Basic tags
		try {
			while ((inputLine = bufReader.readLine()) != null) {
				lineNumber++;
				if (inputLine.length() > 0) {
					basicTagCheck(inputLine, lineNumber);
					//checkValidURL();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Check validity of URL's in the file
		checkValidURL();
		
		// Check media file sequence
		checkMediaFileSequence();

		// Check for the presence of all mandatory tags
		if (mandatoryTagCheck(basicTags)) {
			loggerWrapper.myLogger.info("All mandatory tags are present");
		} else {
			loggerWrapper.myLogger.severe("Missing mandatory tags!!");
		}

	}

	public void basicTagCheck(String inputLine, int lineNumber) {
		/*
		 * Check for validity and syntax of the basic tags in the input file Add
		 * tags from each line to an array list with each tag mapped to the
		 * corresponding line number
		 */

		// String tag = getTag(inputLine);

		if (lineNumber == 1) {
			checkFirstTag(inputLine, lineNumber);
		} else if (!inputLine.startsWith(new String("#"))) {
			if (FilenameUtils.getExtension(inputLine).equals("m3u8")) {

				System.out.println("Media playlist URI : " + inputLine);
				String mediaFileName = FilenameUtils.getName(inputLine);

				mediaFiles.add(mediaFileName);
			}

			// If line does not start with a #, begin file type checks.

			else if (FilenameUtils.getExtension(inputLine).equals("ts")) {
				System.out.println("Media Segment : " + inputLine);

				mediaFiles.add(inputLine);
			} else {
				System.out.println("Invalid Line: " + lineNumber + " "
						+ inputLine);
			}

		} else if (inputLine.startsWith(new String("#"))) {
			if (inputLine.startsWith(new String("#EXTM3U"))
					&& (lineNumber != 1)) {
				loggerWrapper.myLogger
						.severe("ERROR!!! #EXTM3U must be on line 1. Found at line # "
								+ lineNumber);
			} else if (inputLine.startsWith(new String("#EXT-X-VERSION"))) {
				if (basicTags.containsValue("#EXT-X-VERSION")) {
					resultWriter.writeNewRecord("Repeated Tag", fileName,
							"Repeated tag #EXT-X-VERSION at line # "
									+ lineNumber);
					loggerWrapper.myLogger
							.severe("ERROR!!! Repeated tag #EXT-X-VERSION at line # "
									+ lineNumber);
				} else {

					basicTags.put(lineNumber, "#EXT-X-VERSION");
					loggerWrapper.myLogger.info("Line: " + lineNumber + " "
							+ inputLine);
					
					// Store version number
					version = Integer.parseInt(inputLine.substring(inputLine.indexOf(":") + 1));
					System.out.println(version);
					
				}
			} else if (inputLine.startsWith(new String("#EXTINF"))) {
				if (basicTags.containsValue("#EXTINF")) {
					loggerWrapper.myLogger.info("First Media segment in file "
							+ lineNumber);
				} else {
					loggerWrapper.myLogger.info("Media segment in file "
							+ lineNumber);
				}
				basicTags.put(lineNumber, "#EXTINF");
				loggerWrapper.myLogger.info("#EXTINF at line: " + lineNumber);
				String stringDuration = inputLine.substring(inputLine.indexOf(":") + 1, inputLine.indexOf(","));
				
				// If version < 3, duration MUST be an integer
				// If version >= 3, duration MUST be a floating point number
				if (version < 3){
					if(stringDuration.contains(".")){
						resultWriter.writeNewRecord("Invalid Media Segment Duration", fileName,
								"Media Segment duration at line #" + lineNumber+ " should be an integer for compatibility versions < 3");
					}
					else{
						Integer mediaSegmentDuration = Integer.decode(stringDuration);
					}
				}
				else if (version >= 3){
					if(!stringDuration.contains(".")){
						resultWriter.writeNewRecord("Invalid Media Segment Duration", fileName,
								"Media Segment duration at line #" + lineNumber+ " should be floating-point for compatibility versions < 3");
					}
					else{
						Double mediaSegmentDuration = Double.parseDouble(stringDuration);
					}
				}
				else {
					loggerWrapper.myLogger.info("Valid Media segment duration");
				}
				
				Double mediaSegmentDuration = Double.parseDouble(stringDuration);
				loggerWrapper.myLogger
						.info("Duration of the next media segment is: "
								+ mediaSegmentDuration);
				//if (mediaSegmentDuration.equals(duration)) {
				if (mediaSegmentDuration <= duration) {
					loggerWrapper.myLogger
							.info("Valid duration for the media segment");
				} else {
					loggerWrapper.myLogger
							.severe("Error!!! Media segment duration greater than maximum allowed");
					resultWriter.writeNewRecord("Invalid Media Segment Duration", fileName, "Error at line #: " + lineNumber + ". Media segment duration greater than maximum allowed");
				}
				
			
				
			} else if (inputLine.startsWith(new String("#EXT-X-TARGETDURATION"))) {
				if (basicTags.containsValue("#EXT-X-TARGETDURATION")) {
					loggerWrapper.myLogger
							.severe("ERROR!!! Repeated tag #EXT-X-TARGETDURATION at line # "
									+ lineNumber);
				} else {
					basicTags.put(lineNumber, "#EXT-X-TARGETDURATION");
					loggerWrapper.myLogger
							.info("#EXT-X-TARGETDURATION at line: "
									+ lineNumber);
					duration = Double.parseDouble(inputLine.substring(inputLine.indexOf(":") + 1));
					// String title =
					// inputLine.substring(inputLine.indexOf(" "));
					loggerWrapper.myLogger
							.info("Maximum Media Segment duration: " + duration);
					// System.out.println("Title: " + title);
				}
			} else if (inputLine
					.startsWith(new String("#EXT-X-MEDIA-SEQUENCE"))) {
				// Media segment exists? If yes --> Error
				// Else, save media segment duration
				if (basicTags.containsValue("Media Segment")) {
					int mediaSegmentLine = (Integer) getlineNumberForTag(
							basicTags, "Media Segment");
					loggerWrapper.myLogger.severe("ERROR!!! At line # "
							+ lineNumber + ". Media segment(at line number "
							+ mediaSegmentLine
							+ ") before #EXT-X-MEDIA-SEQUENCE.");
				} else {
					basicTags.put(lineNumber, "#EXT-X-MEDIA-SEQUENCE");
					String sequenceNumber = inputLine.substring(inputLine
							.indexOf(":"));
					loggerWrapper.myLogger
							.info("Media Sequence number of the first media segment: "
									+ sequenceNumber);
				}
			} else if (inputLine.startsWith(new String("#EXT-X-ENDLIST"))) {
				if (!inputLine.equals("#EXT-X-ENDLIST")){
					resultWriter.writeNewRecord("Invalid tag", fileName,
							"Tag " + inputLine + " found at line #: " + lineNumber + ". Expected EXT-X-ENDLIST.");
				}
				else{
					System.out.println("No Error");
				}
				loggerWrapper.myLogger
						.info("#EXT-X-ENDLIST at line: "
								+ lineNumber
								+ ". End of media playlist. No more media segments after this tag.");
			} else if (inputLine.startsWith(new String("#EXT-X-STREAM-INF"))) {
				basicTags.put(lineNumber, "#EXT-X-STREAM-INF");
				loggerWrapper.myLogger.info("Variant stream at line: "
						+ lineNumber);
				String programId = inputLine.substring(
						inputLine.indexOf("=") + 1, inputLine.indexOf(","));
				// Get Bandwidth
				// String bandwidthString =
				// inputLine.substring(inputLine.indexOf(","));
				// String bandwidth =
				// bandwidthString.substring(inputLine.indexOf("="));
				// System.out.println("Bandwidth: " + bandwidth);
				loggerWrapper.myLogger.info("ProgramID: " + programId);

			} else {
				System.out.println("Invalid Line: " + lineNumber + " "
						+ inputLine);
			}

		} else {
			System.out.println("Invalid Line: " + lineNumber + " " + inputLine);

		}

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
	
	public void checkValidURL(){
		for (String file: mediaFiles){
			String url = baseURL + file;
			System.out.println(url);
			/*HttpURLConnection connection = null;

			connection = (HttpURLConnection) url.openConnection();
			// Setting request to header to reduce load
			connection.setRequestMethod("HEAD");
			int code = connection.getResponseCode();
			System.out.println("" + code);*/
			
			UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
		    if (urlValidator.isValid(url)) {
		        System.out.println("Valid URL");
		    }
		    else {
		        System.out.println("Invalid URL" + url);
		        resultWriter.writeNewRecord("Invalid URL", fileName, "Invalid URL found in file");
		    }

		}
	}

	public void checkMediaFileSequence(){
		System.out.println(mediaFiles);
		List<Integer> sequence = new ArrayList<Integer>();
		for (String file: mediaFiles){
			sequence.add(Integer.parseInt(file.substring(file.indexOf("_") + 1, file.indexOf("."))));
		}
		Collections.sort(sequence);
		// Integer value = sequence.get(0);
		for (int i = 1; i < sequence.size(); i++){
			int x = sequence.get(i - 1) + 1;
			int y = sequence.get(i);
			if(x != y){
				int missingSequence = sequence.get(i -1) + 1;
				resultWriter.writeNewRecord("Invalid Sequence", fileName, "Invalid sequence found at line #: " + lineNumber + " Missing media sequence " + missingSequence 
						+ ". Found " + sequence.get(i));
				int value = sequence.get(i -1) + 2;
				while((sequence.get(i) - value) > 0){
					resultWriter.writeNewRecord("Missing Media Files", fileName, "Missing media file " + value);
					value++;
				}
			}
		}
	}
	
	public boolean mandatoryTagCheck(Map<Integer, String> basicTags) {
		/*
		 * Checks for all mandatory tags #EXTM3U #EXT-X-VERSION
		 */
		// Get all values from basicTags into a tags array
		String[] tags = new String[basicTags.size()];
		basicTags.values().toArray(tags);
		return Arrays.asList(tags).containsAll(mandatoryTags);
	}

	public static Object getlineNumberForTag(Map inBasicTags, String inTagValue) {
		for (Object o : inBasicTags.keySet()) {
			if (inBasicTags.get(o).equals(inTagValue)) {
				return o;
			}
		}
		return null;
	}

	public void getTag(String inputLine) {
		// Add method to return the tag given an inputLine

	}

}
