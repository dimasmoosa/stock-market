package data;

import java.io.IOException;
import java.util.ArrayList;

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
	public Element getCallsTableBody(String ticker) throws IOException {
		Element callsTableBody = null;
		String url = getOptionsDataURL(ticker);
		
		try {
			Document doc = Jsoup.connect(url).timeout(10000).get();
			
			Elements tables = doc.select("table");
			
			callsTableBody = tables.get(0); 
			
			callsTableBody = callsTableBody.selectFirst("tbody"); 
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return callsTableBody;
	}
	
	/**
	 * 
	 * @param ticker of the stock/ETF
	 * @return Puts table
	 * @throws IOException
	 */
	public Element getPutsTableBody(String ticker) throws IOException {
		Element putsTableBody = null;
		String url = getOptionsDataURL(ticker);
		
		try {
			Document doc = Jsoup.connect(url).timeout(10000).get();
			
			Elements tables = doc.select("table");
			
			putsTableBody = tables.get(1); 
			
			putsTableBody = putsTableBody.selectFirst("tbody"); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return putsTableBody;
	}
	
	/**
	 * @param Element - tbody element
	 * @return Elements - rows of the the tbody element
	 */
	public Elements getRows(Element tableBody) {
		Elements rows = null;
		
		try {
			rows = tableBody.select("[class*=data-row]"); //can also use .ChildNodes() method to retrieve the child nodes (the rows in this case)
		}
		catch(Exception e) {
			System.out.println(e);
		}
		
		return rows;
	}

	/**
	 * A method to retrieve the ITM (in the money) rows of a table
	 * 
	 * @param Element table you want the ITM rows of
	 * @return Elements - ITM rows of the table
	 */
	public Elements getRowsITM(Element table) {
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
	 * A method to retrieve the OTM (out of the money) rows of a table
	 * 
	 * @param Element - table you want the OTM rows of
	 * @return Elements - OTM rows of the table
	 */
	public Elements getRowsOTM(Element table) {
		Elements outOfTheMoneyRows = null;
		
		try{
			outOfTheMoneyRows = table.select("[class*=data-row]:not([class*=in-the-money])");
		}
		catch(Exception e) {
			System.out.println(e);
		}
		
		return outOfTheMoneyRows;
	}
	
	/**
	 * A method that retrieves the index of the closest OTM row index to ATM
	 * 
	 * @param ticker - the ticker symbol of the stock/ETF
	 * @return the index of the "ATM" row, which is actually the first OTM row index 
	 * @throws IOException
	 */
	public int getCallsATMIndex(String ticker) throws IOException {
		int index = 0;
		Element tbody = null;
		
		try {
			tbody = getCallsTableBody(ticker);
			index = getRowsITM(tbody).size();
		}
		catch(Exception e) {
			System.out.println(e);
		}
		
		return index;
	}
	
	/**
	 *  A method that retrieves the index of the closest ITM row index to ATM
	 * 
	 * @param ticker - the ticker symbol of the stock/ETf
	 * @return the index of the "ATM" row, which UNLIKE CALLS ATM INDEX is the first ITM row index
	 */
	public int getPutsATMIndex(String ticker) {
		int index = 0;
		Element tbody = null;
		
		try {
			tbody = getPutsTableBody(ticker);
			index = getRowsOTM(tbody).size();
		}
		catch(Exception e) {
			System.out.println(e);
		}
		
		return index;
	}
	
	
	private ArrayList<Double> getCallsLastPriceArray(String ticker) {
		ArrayList<Double> callsLastPrice = new ArrayList<>();
		
		
		
		return callsLastPrice;
	}
	

}
