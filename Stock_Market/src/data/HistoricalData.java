package data;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HistoricalData {
	
	
	/**
	 * A method that retrieves the Yahoo Finance historical data URL for the specified ticker 
	 * 
	 * @param String ticker
	 * @return String url
	 */
	private String getHistoricalDataURL(String ticker) {
		String urlPartOne = "https://finance.yahoo.com/quote/"; //first part of url
		String urlPartTwo = "/history?p="; //second part of url
		
		String url = urlPartOne + ticker + urlPartTwo + ticker; //construct full url for yahoo finance historical data page
		
		return url; //return the url
	}
	

	/**
	 * A method that retrieves the rows of a historical data page on Yahoo Finance
	 * 
	 * @param ticker of the stock/ETF
	 * @return Elements rows
	 * @throws IOException
	 */
	private Elements getHistoricalDataRows(String ticker) throws IOException {
		String url = getHistoricalDataURL(ticker); //get the URL
		Response response = Jsoup.connect(url).followRedirects(true).execute(); //get the reponse of the URL
		String urlResponse = response.url().toString(); //store the response in a string
		
		boolean matches = url.equals(urlResponse); //does the URL match the response we get? store that value in a boolean
		
		//if the response doesn't match the URL we specified, that means it's a redirect and the ticker is probably wrong or something else
		if(!matches) {
			System.out.println("The web page requested hit a redirect. Please make sure the ticker symbol is valid.");
			System.exit(0); //close the method... exit
		}
		
		Document doc = Jsoup.connect(url).timeout(10000).get(); //get the HTML document from the URL
		
		//select the rows of table. table > tbody > tr is the structure. table and tbody are "Element" objects while tr is an "Elements" object
		Elements rows = doc.select("table tbody tr"); 
		
		return rows;
		
	}
	
	/*
	 * Layout of the historical data table
	 *   0     1      2      3      4          5         6
	 * date | open | high | low | close* | adj close | volume
	 */
	
	
	/**
	 * A method that retrieves the closing prices for the specified amount of trading days and specified ticker
	 * 
	 * @param The ticker symbol
	 * @param The amount of trading days the user wants closing prices for (Amount = from now to X trading days ago)
	 * @return An array of type double 
	 * @throws IOException
	 */
	public double[] getAdjClosePrice(String ticker, int amount) throws IOException {
		double[] adjClose;
		ArrayList<String> stringArrayList = new ArrayList<>();
		
		Elements rows = getHistoricalDataRows(ticker);
		
		if(amount > rows.size()) {
			System.out.print("Sorry, the amount you requested is greater than the amount available. ");
			amount = rows.size();
			
			adjClose = new double[rows.size()]; //instantiate the array to size of the rows available
			
			for(int i = 0; i < rows.size(); i++) { //iterate over amount of rows asked for
				Element row = rows.get(i);
				Elements cols = row.select("td");
				
				//rows with 7 columns have the data we need. Some rows only have 2 (date and dividend) which doesn't contain the adjusted close price
				if(cols.size() == 7) { 
					Element col = cols.get(5); //get the column containing the adjusted close price
					Element span = col.selectFirst("span"); //looks like "<span data-reactid="63">190.46</span>"
					
					//need to split the span Element object to get the value. Will need to convert value to String[] to use split
					String spanString = span.toString(); //string
					
					//convert string to string array, split into two [0] and [1] by <
					String[] splitStringArrayOne = spanString.split(">"); 
					String splitStringOne = splitStringArrayOne[1]; //assign second half to string --> "190.46</span>"
					
					String[] splitStringArrayTwo = splitStringOne.split("<"); //split again by > this time... first part will contain the value
					String splitStringTwo = splitStringArrayTwo[0]; //assign the value to a string
					
					stringArrayList.add(splitStringTwo);
					
				}
					
			}
			
			
		}
		
		else if (amount <= rows.size()) {
			//this retrieves the latest amount 
			adjClose = new double[amount]; //instantiate array to amount requested
			
			for(int i = 0; i < amount; i++) { //iterate over amount of rows asked for
				Element row = rows.get(i); 
				//System.out.println(row);
				Elements cols = row.select("td"); 
				//System.out.println(cols);
				
				//rows with 7 columns have the data we need. Some rows only have 2 (date and dividend) which doesn't contain the adjusted close price
				if(cols.size() == 7) { 
					Element col = cols.get(5); //get the column containing the adjusted close price
					Element span = col.selectFirst("span"); //looks like "<span data-reactid="63">190.46</span>"
					
					//need to split the span Element object to get the value. Will need to convert value to String[] to use split
					String spanString = span.toString(); //string
					
					//convert string to string array, split into two [0] and [1] by <
					String[] splitStringArrayOne = spanString.split(">"); 
					String splitStringOne = splitStringArrayOne[1]; //assign second half to string --> "190.46</span>"
					
					String[] splitStringArrayTwo = splitStringOne.split("<"); //split again by > this time... first part will contain the value
					String splitStringTwo = splitStringArrayTwo[0]; //assign the value to a string
					
					stringArrayList.add(splitStringTwo);
					
				}
				
				
			}
		}
		
		//instantiate double array to size of the arraylist of string
		adjClose = new double[stringArrayList.size()];
		
		//convert the strings to Double objects then double and assign it to our double array
		for(int i = 0; i < stringArrayList.size(); i++) {
			adjClose[i] = Double.parseDouble(stringArrayList.get(i));
			//System.out.println(adjClose[i]);
		}
		
		System.out.println("Done retrieving latest " + amount + " adjusted closing prices for " + ticker + ".\n");
		
		//return the double array of the adjusted close prices
		return adjClose;
		
	}
	
	
	/**
	 * A method that retrieves the last X amount of dates for a ticker that the user specified
	 * 
	 * @param ticker symbol of the stock/ETF/etc.
	 * @param amount of trading days the user wants dates for (Amount = from now to X trading days ago)
	 * @return An array of LocalDate objects 
	 * @throws IOException
	 * @throws ParseException
	 */
	public LocalDate[] getDates(String ticker, int amount) throws IOException, ParseException {
		LocalDate dates[];
		ArrayList<String> stringArrayList = new ArrayList<>();
		
		Elements rows = getHistoricalDataRows(ticker);
		
		if(amount > rows.size()) { //if amount asked for is greater than rows available, let user know and then retrieve max amount of records
			System.out.print("Sorry, the amount you requested is greater than the amount available. "
					+ "We were only able to get " + rows.size() + " records for " + ticker + ". ");
			amount = rows.size();
			
			for(int i = 0; i < rows.size(); i++) {
				Element row = rows.get(i); //get the row
				Elements cols = row.select("td"); //get the columns of the row
				
				if(cols.size() == 7) {
					Element col = cols.get(0); //if valid row, get the 0th position column which contains the date
					
					//parse this column line so we can get the date... looks like the following line
					//<td class="Py(10px) Ta(start) Pend(10px)" data-reactid="67"><span data-reactid="68">Mar 26, 2019</span></td>
					String colValue = col.toString(); //convert column value to string and store in variable
					
					String[] firstSplitArray = colValue.split(">"); //split the string by >
					String firstSplitString = firstSplitArray[2]; //get [2].. looks like "Mar 26, 2019</span"
					
					String[] secondSplitArray = firstSplitString.split("<"); //split into two by <
					String secondSplitString = secondSplitArray[0]; //choose first half.. looks like "Mar 26, 2019"
					
					stringArrayList.add(secondSplitString);
					
				}
				
			}
			
		}
		
		else if (amount <= rows.size()) {
			System.out.print("Retrieving latest " + amount + " dates for " + ticker + ". ");
			
			for(int i = 0; i < amount; i++) {
				Element row = rows.get(i);
				Elements cols = row.select("td");
				
				if(cols.size() == 7) {
					Element col = cols.get(0); //if valid row, get the 0th position column which contains the date
					
					//parse this column line so we can get the date... looks like the following line
					//<td class="Py(10px) Ta(start) Pend(10px)" data-reactid="67"><span data-reactid="68">Mar 26, 2019</span></td>
					String colValue = col.toString(); //convert column value to string and store in variable
					
					String[] firstSplitArray = colValue.split(">"); //split the string by >
					String firstSplitString = firstSplitArray[2]; //get [2].. looks like "Mar 26, 2019</span"
					
					String[] secondSplitArray = firstSplitString.split("<"); //split into two by <
					String secondSplitString = secondSplitArray[0]; //choose first half.. looks like "Mar 26, 2019"
					
					stringArrayList.add(secondSplitString);
					
				}
				
			}
			
		}
		
		//instantiate the LocalDate array with the size of the ArrayList<String.
		dates = new LocalDate[stringArrayList.size()]; 
		
		//specify the format that is used by those strings scraped from yahoo finance
		String pattern = "MMM d, y";
		
		//instantiate DateTimeFormatter object with the specified pattern
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		
		//iterate over the values of the array list and assign each index to the LocalDate array
		for(int i = 0; i < dates.length; i++) { 
			String dateString = stringArrayList.get(i);
			LocalDate date = LocalDate.parse(dateString, formatter);
			dates[i] = date;
			//System.out.println(date);
			//date.format(formatter) <-- if we wanted to convert back to string with the same format
		}
		
		System.out.println("Done retrieving.");
		
		return dates;
	}

}
