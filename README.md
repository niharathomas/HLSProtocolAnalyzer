<b>HLS Protocol Analyzer app to validate input HLS files</b>

To run: <br>
``` java AppRunner.java```
<br>
<br>
This opens a window which accepts the path to the top level HLS File directory.
The program loops through all files in the directory and separates them into Media Playlist or Media Files.
The playlist files are then looped through and analyzed per the [spec](https://tools.ietf.org/html/draft-pantos-http-live-streaming-13).
The results are output to an excel file.
