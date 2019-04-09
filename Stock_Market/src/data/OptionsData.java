package data;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OptionsData {
	private String yahooFinanceBaseURL = "https://finance.yahoo.com";
	
	/*
	 * Layout of the options tables
	 *        0                1           2           3        4     5       6         7        8           9                  10
	 * contract name | last trade date | strike | last price | bid | ask | change | % change | volume | open interest | implied volatility 
	 * 
	 * 
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
	private Element getCallsTableBody(String ticker) throws IOException {
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
	private Element getPutsTableBody(String ticker) throws IOException {
		Element putsTableBody = null;
		String url = getOptionsDataURL(ticker);
		
		try {
			Document doc = Jsoup.connect(url).timeout(10000).get();
			
			Elements tables = doc.select("table");
			
			putsTableBody = tables.get(1); 
			
			putsTableBody = putsTableBody.selectFirst("tbody"); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return putsTableBody;
	}
	
	/**
	 * @param Element - tbody element
	 * @return Elements - rows of the the tbody element
	 */
	protected Elements getRows(Element tableBody) {
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
	 * A method that retrieves a row from a table
	 * 
	 * @param tableBody 
	 * @param index
	 * @return row
	 */
	protected Element getRow(Element tableBody, int index) {
		Element row = null;
		Elements rows = null;
		
		rows = getRows(tableBody);
		row = rows.get(index);
		
		return row;
	}

	/**
	 * A method to retrieve the ITM (in the money) rows of a table
	 * 
	 * @param Element table you want the ITM rows of
	 * @return Elements - ITM rows of the table
	 */
	protected Elements getRowsITM(Element table) {
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
	protected Elements getRowsOTM(Element table) {
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
	 * A method that retrieves the index of the closest ITM row index to ATM
	 * 
	 * @param ticker - the ticker symbol of the stock/ETF
	 * @return the index of the "ATM" row, which is actually the first OTM row index 
	 * @throws IOException
	 */
	protected int getCallsATMIndex(String ticker) throws IOException {
		int index = 0;
		Element tbody = null;
		
		try {
			tbody = getCallsTableBody(ticker);
			index = getRowsITM(tbody).size() - 1; //minus 1 since we get the size
			//we use ITM rows because those appear first
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
	protected int getPutsATMIndex(String ticker) {
		int index = 0;
		Element tbody = null;
		
		try {
			tbody = getPutsTableBody(ticker);
			index = getRowsOTM(tbody).size(); //don't have to be -1 since puts rows are structured OTM rows then ITM rows
			//we use OTM rows because those appear first
		}
		catch(Exception e) {
			System.out.println(e);
		}
		
		return index;
	}
	
	/**
	 * A helper method that takes in a row element and spits out the strike price URL path that is appended to the Yahoo Finance options URL
	 * 
	 * @param row
	 * @return
	 */
	private String getStrikePath(Element row) {
		String strikePath = null;
		
		//a looks like ---> <a href="the url here" data-symbol="AAPL">195.00</a>
		Element column = row.select("td").get(2); //3rd td (column) is the one containing the strike
		Element a = column.selectFirst("a"); //select the a attribute which contains the path. can also get element by attribute "href" same thing
		String aString = a.toString(); //convert it to string so it can be manipulated
		String[] firstSplit = aString.split("\""); //split by "
		String firstString = firstSplit[1]; //get the [1] of that split string ---> the url here" data-symbol="AAPL">195.00</a>
		String[] secondSplit = firstString.split("\""); //split by " 
		strikePath = secondSplit[0]; //get the [0] of that split string --> the url here
		
		return strikePath; //return the value
	}

	/**
	 * 
	 * @param ticker
	 * @return
	 * @throws IOException
	 */
	protected String getClosestITMCallOptionStrikeURL(String ticker) throws IOException {
		String url = null;
		
		try {
			int index = getCallsATMIndex(ticker); //get the ATM row (which is first ITM for calls)
			Element row = getRow(getCallsTableBody(ticker), index); //get the tr (row) of the index			
			String secondHalfURL = getStrikePath(row); //get the strike path of the tr 
			url = yahooFinanceBaseURL + secondHalfURL; //append the path to the yahoo finance option URL
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return url;
	}
	
	/**
	 * 
	 * @param ticker
	 * @return
	 */
	protected String getClosestOTMCallOptionStrikeURL(String ticker) {
		String url = null;
		
		try {
			int index = getCallsATMIndex(ticker) + 1; //+ 1 since OTM comes after ITM for calls table. getCallsATM returns the closest ITM to ATM
			Element row = getRow(getCallsTableBody(ticker), index);
			String secondHalfURL = getStrikePath(row);
			url = yahooFinanceBaseURL + secondHalfURL;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return url;
	}
	
	protected String getClosestITMPutOptionStrikeURL(String ticker) {
		String url = null;
		
		try {
			int index = getPutsATMIndex(ticker); //aasldfjalsdfjsf
			Element row = getRow(getPutsTableBody(ticker), index);
			String secondHalfURL = getStrikePath(row);
			url = yahooFinanceBaseURL + secondHalfURL;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return url;
	}
	
	
	protected String getClosestOTMPutOptionStrikeURL(String ticker) {
		String url = null;
		
		try {
			int index = getPutsATMIndex(ticker) - 1; // -1 since puts are structured OTM -> ATM -> ITM. getPutsATMIndex() returns closest ITM to ATM
			Element row = getRow(getPutsTableBody(ticker), index);
			String secondHalfURL = getStrikePath(row);
			url = yahooFinanceBaseURL + secondHalfURL;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return url;
	}
	
	/*
	 * URLs for the option contracts are structured this way
	 * [ticker][YYMMDD][C or P for call or put][########]
	 * for example
	 * AAPL190412C00200000 --> AAPL 04/12/19 Call 200.00 or [Apple][4/12/19 expiration date][Call option][200]
	 * [AAPL][190412][C][0020000]
	 * 
	 * characters | 6 digits | C or P character | 8 digits.. up to tens of thousands with two decimal places 
	 * 
	 * need a method that will parse a row and determine what
	 */
	
	/*
	 * we can create methods that retrieve a specific ticker/date/option type/strike using the URL... they have a certain format, for example...
	 * https://finance.yahoo.com/quote/AAPL190412C00200000?p=AAPL190412C00200000
	 * url = "https://finance.yahoo.com/quote/" + contract + "?p=" + contract
	 * contract in this case is the way yahoo finance formats their contracts, for example...
	 * AAPL190412C00200000
	 * 
	 */
	
	//-------------------------------------------------------------------------------------------------------------------------------------------
	//this method needs more work. maybe create overloaded methods that accept different formats of dates and stuff?
	//also have to figure out how to handle the strike price because it won't always work out with having 2 leading 0s and 3 0s at the end.
	//that only works with triple digit integers. what if the number is 8.5? also have to deal with decimal problem. 200.0 as opposed to 200..
	//currrently casting it
	public String getContractURL(String ticker, String expirationDate, String contractType, double strike) {
		String url = null;
		
		String contract = ticker + expirationDate + contractType + "00" + (int) strike + "000";
		url = "https://finance.yahoo.com/quote/" + contract + "?p=" + contract;
		
		return url;
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------
	
	
//	public ArrayList<Double> getCallsLastPriceArray(String ticker) {
//		ArrayList<Double> callsLastPrice = new ArrayList<>();
//		
//		
//		
//		return callsLastPrice;
//	}
	
//	public double getCallsLastPrice() {
//		double callsLastPrice = 0;
//		
//		
//		return callsLastPrice;
//	}
	
//	public int[] getCallsStrikePriceArray(String ticker) {
//		int[] callsStrikePriceArray = null;
//		
//		return callsStrikePriceArray;
//	}
	
//	private int getCallsStrikePrice(String ticker) {
//		int callsStrikePrice = 0;
//		
//		return callsStrikePrice;
//	}
	

}
