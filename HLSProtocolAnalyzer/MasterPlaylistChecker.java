package HLSProtocolAnalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FilenameUtils;

public class MasterPlaylistChecker extends FileChecker{

	public MasterPlaylistChecker(String inBaseURL, String inFileName, ArrayList<String> inMediaSegments, 
			ArrayList<String> inPlaylistFiles, ExcelResultWriter inResultWriter) {
		super(inBaseURL, inFileName, inMediaSegments, inPlaylistFiles, inResultWriter);
	}
	
	public void runChecks(BufferedReader bufReader){
		System.out.println("Checking MasterPlayList file...");
		try {
			while ((inputLine = bufReader.readLine()) != null) {
				lineNumber++;
				if (lineNumber == 1) {
					checkFirstTag(inputLine, lineNumber);
				} 
				else if (!inputLine.startsWith(new String("#"))) {
					// If line is not a "tagged" line, check for valid file type depending on the input file
					// String fileExtension = FilenameUtils.getExtension(inputLine);
					checkFileTypes("m3u8", "Media Playlist");
				} 
				else if (inputLine.startsWith(new String("#"))) {
					checkBasicTags();
					
				}
				checkValidURIs("Media Playlist", mediaPlaylists);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void checkMasterPlaylistTags(){
		
	}

}
