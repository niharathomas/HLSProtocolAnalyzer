package HLSProtocolAnalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class MediaPlaylistChecker extends FileChecker{

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
					
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
