package data;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OptionsData {
	
	/**
	 * A method to get the Yahoo Finance URL for options data on a specified ticker
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
	
	/**
	 * 
	 * @param ticker of the stock/ETF
	 * @return rows of the calls contracts table
	 * @throws IOException
	 */
	private Elements getCallsRows(String ticker) throws IOException {
		Elements callsRows;
		String url = getOptionsDataURL(ticker);
		
		Document doc = Jsoup.connect(url).timeout(10000).get();
		
		Elements tables = doc.select("tables");
		
		Element callsTable = tables.get(0);
		
		callsRows = callsTable.select("tr");
		
		return callsRows;
	}
	
	/**
	 * 
	 * @param ticker of the stock/ETF
	 * @return rows of the puts contracts table
	 * @throws IOException
	 */
	private Elements getPutsRows(String ticker) throws IOException {
		Elements putsRows;
		String url = getOptionsDataURL(ticker);
		
		Document doc = Jsoup.connect(url).timeout(10000).get();
		
		Elements tables = doc.select("tables");
		
		Element putsTable = tables.get(1);
		
		putsRows = putsTable.select("tr");
		
		return putsRows;
	}
	
	/*
	 * Layout of the options tables
	 *        0                1           2           3        4     5       6         7        8           9                  10
	 * contract name | last trade date | strike | last price | bid | ask | change | % change | volume | open interest | implied volatility 
	 */
	
	
	

}
