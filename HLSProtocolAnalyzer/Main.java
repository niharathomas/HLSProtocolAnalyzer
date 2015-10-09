package HLSProtocolAnalyzer;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Main {
	public static void main(String[] args) throws IOException {
		ProtocolAnalyzerUI app = new ProtocolAnalyzerUI();
        Document doc = Jsoup.connect("http://localhost:80/Arris").get();
        System.out.println("Begin");
        for (Element file : doc.select("td a")) {
            System.out.println(file.attr("href"));
        }
    }

}
