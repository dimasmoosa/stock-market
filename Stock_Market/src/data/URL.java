package data;

public class URL {
	//Haven't implemented this class yet
	
	/**
	 * A method that retrieves the Yahoo Finance historical data URL for the specified ticker 
	 * 
	 * @param String ticker
	 * @return String url
	 */
	protected String getHistoricalDataURL(String ticker) {
		String urlPartOne = "https://finance.yahoo.com/quote/"; //first part of url
		String urlPartTwo = "/history?p="; //second part of url
		
		String url = urlPartOne + ticker + urlPartTwo + ticker; //construct full url for yahoo finance historical data page
		
		return url; //return the url
	}
	
	/**
	 * A method to get the URL for options data on a specified ticker
	 * 
	 * @param ticker String
	 * @return URL String
	 */
	protected String getOptionsDataURL(String ticker) {
		String urlPartOne = "https://finance.yahoo.com/quote/";
		String urlPartTwo = "/options?p=";
		
		String url = urlPartOne + ticker + urlPartTwo + ticker;
		
		return url;
	}

}
