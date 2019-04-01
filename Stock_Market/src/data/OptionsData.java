package data;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OptionsData {
	
	/**
	 * A method to get the URL for options data on a specified ticker
	 * 
	 * @param ticker String
	 * @return URL String
	 */
	private String getOptionsDataURL(String ticker) {
		String urlPartOne = "https://finance.yahoo.com/quote/";
		String urlPartTwo = "/options?p=";
		
		String url = urlPartOne + ticker + urlPartTwo + ticker;
		
		return url;
	}
	
	private Elements getCallsRows(String ticker) throws IOException {
		Elements callsRows;
		String url = getOptionsDataURL(ticker);
		
		Document doc = Jsoup.connect(url).timeout(10000).get();
		
		Elements tables = doc.select("tables");
		
		Element callsTable = tables.get(0);
		
		callsRows = callsTable.select("tr");
		
		return callsRows;
	}
	
	private Elements getPutsRows(String ticker) throws IOException {
		Elements putsRows;
		String url = getOptionsDataURL(ticker);
		
		Document doc = Jsoup.connect(url).timeout(10000).get();
		
		Elements tables = doc.select("tables");
		
		Element putsTable = tables.get(1);
		
		putsRows = putsTable.select("tr");
		
		return putsRows;
	}
	
	
	

}
