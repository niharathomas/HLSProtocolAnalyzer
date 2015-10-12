package HLSProtocolAnalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MediaPlaylistChecker extends FileChecker{
	Integer mediaSequence = 0;
	List<String> mandatoryMediaPlaylistTags = Arrays.asList("EXT-X-VERSION", "EXTINF", "EXT-X-TARGETDURATION",
			"EXT-X-ENDLIST");
	// List to store the tags found in the file
	private ArrayList<String> tags = new ArrayList<String>();

	public MediaPlaylistChecker(String inBaseURL, String inFileName, ArrayList<String> inMediaSegments, 
			ArrayList<String> inPlaylistFiles, ExcelResultWriter inResultWriter) {
		super(inBaseURL, inFileName, inMediaSegments, inPlaylistFiles, inResultWriter);
	}
	
	public void runChecks(BufferedReader bufReader){
		System.out.println("MasterPlayList!");
		try {
			while ((inputLine = bufReader.readLine()) != null) {
				lineNumber++;
				if (lineNumber == 1) {
					System.out.println("Checking first line");
					checkFirstTag(inputLine, lineNumber);
				} 
				else if (!inputLine.startsWith(new String("#"))) {

					System.out.println("Checking non-# lines");
					// String fileExtension = FilenameUtils.getExtension(inputLine);
					checkFileTypes("ts", "Media Segment");
					
				} 
				else if (inputLine.startsWith(new String("#"))) {

					System.out.println("Checking # lines");
					checkBasicTags();
					checkMediaSegmentTags();
					checkMediaPlaylistTags();
					
				}
				// checkValidURIs("Media Segment", mediaSegments);
			}
			checkMediaSegmentSequence();
			checkMandatoryTags();
			System.out.println(tagsInFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void checkMediaSegmentSequence(){
		for (int i=0; i< mediaSegments.size(); i++){
			String inputFile = mediaSegments.get(i);
			Integer fileSequence = Integer.parseInt(inputFile.substring(inputFile.indexOf("_") + 1, inputFile.indexOf(".")));
			if (fileSequence != (mediaSequence + i)){
				resultWriter.writeNewRecord("Invalid File Sequence", fileName,
						"Invalid sequence: " + inputFile + ". Expected sequence: "
								+ (mediaSequence + i));
			}
			
		}
	}
	
	public void checkMediaSegmentTags(){
		if (inputLine.startsWith(new String("#EXTINF"))) {
			tagsInFile.put(lineNumber, "EXTINF");
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
							"Media Segment duration at line #" + lineNumber+ " should be floating-point for compatibility versions >= 3");
				}
				else{
					Double mediaSegmentDuration = Double.parseDouble(stringDuration);
				}
			}
			else {
				loggerWrapper.myLogger.info("Invalid version");
			}
			checkDuration(stringDuration);
			
		} 
	}
	
	public void checkDuration(String stringDuration){
		Double mediaSegmentDuration = Double.parseDouble(stringDuration);
		loggerWrapper.myLogger
				.info("Duration of the next media segment is: "
						+ duration);
		//if (mediaSegmentDuration.equals(duration)) {
		if (mediaSegmentDuration <= duration) {
			loggerWrapper.myLogger
					.info("Valid duration for the media segment");
		} else {
			loggerWrapper.myLogger
					.severe("Error!!! Media segment duration greater than maximum allowed");
			resultWriter.writeNewRecord("Invalid Media Segment Duration", fileName, "Error at line #: " + lineNumber + ". Media segment duration greater than maximum allowed");
		}
		
	}

	public void checkMediaPlaylistTags(){
		System.out.println("Checking Media Playlist Tags");
		if (inputLine.startsWith(new String("#EXT-X-TARGETDURATION"))) {
			if (tagsInFile.containsValue("EXT-X-TARGETDURATION")) {
				loggerWrapper.myLogger
						.severe("ERROR!!! Repeated tag #EXT-X-TARGETDURATION at line # "
								+ lineNumber);
			} else {
				tagsInFile.put(lineNumber, "EXT-X-TARGETDURATION");
				loggerWrapper.myLogger
						.info("#EXT-X-TARGETDURATION at line: "
								+ lineNumber);
				duration = Double.parseDouble(inputLine.substring(inputLine.indexOf(":") + 1));
				// String title =
				// inputLine.substring(inputLine.indexOf(" "));
				loggerWrapper.myLogger
						.info("Maximum Media Segment duration: " + duration + " . Filename = " + fileName);
			}
	}
		else if (inputLine
				.startsWith(new String("#EXT-X-MEDIA-SEQUENCE"))) {
			// Media segment exists? If yes --> Error
			// Else, save media segment duration
			if (tagsInFile.containsValue("EXT-X-MEDIA-SEQUENCE")) {
				loggerWrapper.myLogger
				.severe("ERROR!!! Repeated tag EXT-X-MEDIA-SEQUENCE at line # "
						+ lineNumber);;
			} else {
				tagsInFile.put(lineNumber, "EXT-X-MEDIA-SEQUENCE");
				mediaSequence = Integer.parseInt(inputLine.substring(inputLine
						.indexOf(":") + 1));
				loggerWrapper.myLogger
						.info("Media Sequence number of the first media segment: "
								+ mediaSequence);
			}
		} else if (inputLine.startsWith(new String("#EXT-X-ENDLIST"))) {
			if (!inputLine.equals("#EXT-X-ENDLIST")){
				resultWriter.writeNewRecord("Invalid tag", fileName,
						"Tag " + inputLine + " found at line #: " + lineNumber);
			}
			else{
				tagsInFile.put(lineNumber, "EXT-X-ENDLIST");
			}
			loggerWrapper.myLogger
					.info("End of media playlist. No more media segments after this tag.");
		}
}
	
	public void checkMandatoryTags(){
		
		for (String value: tagsInFile.values()){
			tags.add(value);
		}
		for (String tag: mandatoryMediaPlaylistTags){
			if (!tags.contains(tag)){
				resultWriter.writeNewRecord("Missing " + tag, fileName, 
						"Expected " + tag + " at EOF");
			}
		}
	}
}
