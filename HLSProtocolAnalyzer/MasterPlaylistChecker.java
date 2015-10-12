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
		System.out.println("MasterPlayList!");
		try {
			while ((inputLine = bufReader.readLine()) != null) {
				lineNumber++;
				System.out.println("Line is: " + inputLine);
				if (lineNumber == 1) {
					System.out.println("Checking first line");
					checkFirstTag(inputLine, lineNumber);
				} 
				else if (!inputLine.startsWith(new String("#"))) {
					System.out.println("Checking non-# lines");
					// If line is not a "tagged" line, check for valid file type depending on the input file
					// String fileExtension = FilenameUtils.getExtension(inputLine);
					checkFileTypes("m3u8", "Media Playlist");
				} 
				else if (inputLine.startsWith(new String("#"))) {
					System.out.println("Checking # lines");
					checkBasicTags();
					
				}
				checkValidURIs("Media Playlist", mediaPlaylists);
				System.out.println("Playlists: " + mediaPlaylists);
				System.out.println("valid playlists: " + validMediaPlaylists);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void checkMasterPlaylistTags(){
		
	}

}
