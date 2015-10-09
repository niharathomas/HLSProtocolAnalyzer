package HLSProtocolAnalyzer;

final class PlaylistTagConstants {
	/**
	 * Valid tags per the spec are defined
	 * Compare and check for the presence of invalid tags in the input file
	 */
	private PlaylistTagConstants() {
        throw new AssertionError("Not allowed");
    }
	
	final static String EXTM3U = "#EXTM3U";
	final static String EXT_X_STREAM_INF = "#EXT-X-STREAM-INF";

}
