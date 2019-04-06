package data;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OptionsData {
	/*
	 * Layout of the options tables
	 *        0                1           2           3        4     5       6         7        8           9                  10
	 * contract name | last trade date | strike | last price | bid | ask | change | % change | volume | open interest | implied volatility 
	 */
	
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
	 * @return Calls table
	 * @throws IOException
	 */
	public Element getCallsTable(String ticker) throws IOException {
		Element callsTable;
		String url = getOptionsDataURL(ticker);
		
		Document doc = Jsoup.connect(url).timeout(10000).get();
		
		Elements tables = doc.select("table");
		
		callsTable = tables.get(0); //is there a better way of doing this? what if the table positions change?
		
		callsTable = callsTable.selectFirst("tbody"); //--------------------------------------delete this?
		
		return callsTable;
	}
	
	/**
	 * 
	 * @param ticker of the stock/ETF
	 * @return Puts table
	 * @throws IOException
	 */
	public Element getPutsTable(String ticker) throws IOException {
		//--------------------------------------------------------------------------------------------------------------
		// this method along with the getCallsTable method returns the whole table. do we only want the contents though?
		// the contents are stored in the "tbody" element of the "table"... those contain the actual rows of data
		// WILL PROBABLY HAVE TO DO THIS SINCE, CURRENTLY, THE GETOUTOFTHEMONEY METHOD AS IMPLEMENTED MESSES UP BC OF THIS
		//--------------------------------------------------------------------------------------------------------------
		Element putsTable;
		String url = getOptionsDataURL(ticker);
		
		Document doc = Jsoup.connect(url).timeout(10000).get();
		
		Elements tables = doc.select("table");
		
		putsTable = tables.get(1); //is there a better way of doing this? what if the table positions change?
		
		putsTable = putsTable.selectFirst("tbody"); //-----------------------------------------------delete this?
		
		return putsTable;
	}

	/**
	 * 
	 * @return
	 */
	public Elements getInTheMoneyRows(Element table) {
		Elements inTheMoneyRows = null;

		try{
			inTheMoneyRows = table.select("[class*=in-the-money]");
		}
		catch(Exception e) {
			System.out.println(e);
		}
		
		return inTheMoneyRows;
	}
	
	/**
	 * 
	 * @return
	 */
	public Elements getOutOfTheMoneyRows(Element table) {
		Elements outOfTheMoneyRows = null;
		
		try{
			outOfTheMoneyRows = table.select("[class!=data-row]"); //need a better selector. might have to rely on getting index
		}
		catch(Exception e) {
			System.out.println(e);
		}
		
		return outOfTheMoneyRows;
	}
	
	//public int getIndexOfAtTheMoney
	

}
