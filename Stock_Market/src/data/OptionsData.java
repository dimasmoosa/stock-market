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
	 * 
	 * below is the regex pattern (not including the quotes) to validate a options contract (made myself so not sure if it validates 100% perfectly)
	 * 
	 * "\\w+|\\w+.\\w+\\d{6}[c|C|p|P]\\d{8}"
	 * 
	 */
	/**
	 * A method that gets the Yahoo Finance URL of a contract
	 * 
	 * @param ticker The ticker symbol of the security
	 * @param expirationDate The expiration date of the option contract. Accepts MMddyyyy or ddMMyyyy
	 * @param contractType The contract type (call or put). Accepts "c" "C" "p" "P" "call" "Call" "put" or "Put" as input
	 * @param strike The strike price of the contract
	 * @return
	 */
	public String getContractURL(String ticker, String expirationDate, String contractType, double strike) {
		String url = null;
		String stringStrike = "";
		
		/*
		 * validation/transformation for ticker
		 */
		
		
		/*
		 * validation/transformation for expiration date
		 */
		
		
		/*
		 * validation/transformation for contract type
		 */
		if(contractType == "call" | contractType == "Call") {
			contractType = "C";
		}
		
		if(contractType == "put" | contractType == "Put") {
			contractType = "P";
		}
		
		/*
		 * validation/transformation for strike 
		 */
		//if the double entered is an integer... we can make the string an integer value so that it doesn't have a decimal
		if(strike == Math.floor(strike) && !Double.isInfinite(strike)) {
			int intStrike = (int) strike;
			stringStrike = Integer.toString(intStrike);
		}
		else {
			stringStrike = Double.toString(strike);
		}

		boolean hasDecimal = stringStrike.contains(".");
		
		//if the strike price has a decimal then we have to figure out how many leading and trailing 0s to add to it for the URL
		if(hasDecimal) {
			String[] splitArray = stringStrike.split("."); //split by the decimal
			
			String leftPart = splitArray[0]; //store the digits before the decimal in a string
			String rightPart = splitArray[1]; //store the digits after the decimal in a string
			
			int leftLength = leftPart.length(); //get length of digits before decimal
			int rightLength = rightPart.length(); //get length of digits after decimal
			
			if(leftLength > 5) {
				System.out.println("The length of digits before the decimal place is too large. Try a number with a length of 5 or smaller.");
				System.exit(0);
			}
			if(rightLength > 2) {
				System.out.println("The length of digits after the decimal place is too large. Try a number with a length of 2 or smaller.");
				System.exit(0);
			}
			
			
			switch(leftLength) {
			
			case 0: 
				leftPart = "00000";
				break;
				
			case 1:
				leftPart = "0000" + leftPart;
				break;
				
			case 2:
				leftPart = "000" + leftPart;
				break;
			
			case 3:
				leftPart = "00" + leftPart;
				break;
			
			case 4:
				leftPart = "0" + leftPart;
				break;
			
			case 5:
				break;
			
			}
			
			switch(rightLength) {
			
			case 0:
				rightPart = "000";
				break;
			
			case 1:
				rightPart = rightPart + "00";
				break;
				
			case 2:
				rightPart = rightPart + "0";
				break;
			
			}
			
			stringStrike = leftPart+rightPart;
			
		}
		//if the strike price doesn't have a decimal then we can just add the leading 0s to it
		else if (!hasDecimal) {
			int digitlength = stringStrike.length();
			
			switch(digitlength) {
			
			case 0: 
				System.out.println("Can't have an empty strike price");
				System.exit(0);
				break;
				
			case 1:
				stringStrike = "0000" + stringStrike;
				break;
				
			case 2:
				stringStrike = "000" + stringStrike;
				break;
			
			case 3:
				stringStrike = "00" + stringStrike;
				break;
			
			case 4:
				stringStrike = "0" + stringStrike;
				break;
			
			case 5:
				break;
			
			}
			
			//add two zeros as the decimal values if it's an integer
			stringStrike = stringStrike + "000";
		}
		
		/*
		 * ending validation for strike
		 */
		
		
		String contract = ticker + expirationDate + contractType + stringStrike;
		
		/*
		 * last check validation to make sure the full string created matches the regex for strings allowed
		 * *DISCLAIMER* this is a regex I personally wrote so it may not be 100% correct
		 * 
		 * (\\w+|\\w+.\\w+) - one or more characters OR one or more characters plus . plus one or more characters (e.g. F or BRK.B)
		 * (\\d{6}) - 6 digits (e.g. 210419 or 042119)
		 * (c|C|p|P) - c or C or p or P (e.g. C)
		 * (\\d{8}) - 8 digits (e.g. 00200000)
		 * 
		 */
		if(contract.matches("(\\w+|\\w+.\\w+)(\\d{6})(c|C|p|P)(\\d{8})")) {
			url = "https://finance.yahoo.com/quote/" + contract + "?p=" + contract;
		}

		
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
