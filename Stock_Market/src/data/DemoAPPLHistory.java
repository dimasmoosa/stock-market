package data;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class DemoAPPLHistory {
	static ArrayList<String> adjCloseArrayListTemp = new ArrayList<>();
	static double[] adjClosePrice;
	
	public static void main(String[] args) throws IOException {
		
		//establish connection to the url containing the document. Using APPL history page on yahoo finance for this example
		//create document (the html page) object pulled from that page
		Document doc =  Jsoup.connect("https://finance.yahoo.com/quote/AAPL/history?p=AAPL").timeout(6000).get();
		
		
		//create element object and assign the first <tbody> element of the document to it
		Element table = doc.select("table").get(0);
		
		
		Elements rows = table.select("tr"); //get the rows of the table. We only want rows with 7 nodes (columns) though
		int sizeOfRows = rows.size(); //size of rows
		
		for(int i = 0; i < sizeOfRows; i++) { //iterate over the rows
			Element row = rows.get(i); //get the current row
			Elements cols = row.select("td"); //get the columns for the current row
			
			if(cols.size() == 7) { //if there are 7 columns (which indicate a normal row that has a adjusted closing price)
				Element col = cols.get(5); //get the 6th [position 5] column because it contains the adjusted close price
				Elements span = col.select("span"); //get the span (contains the value) of the 6th col (adjusted close price)
				
				String spanString = span.toString(); //store the whole span in a string 
				//the spanStrings look like --> <span data-reactid="1532">211.65</span>
				
				String[] spanStringSplitArray = spanString.split(">"); //split the span by ">" now it's split into 2 parts
				//choosing [0] gives something like "<span data-reactid="1532"" and choosing [1] gives something like "211.65</span>"
				
				String secondPartOfSpan = spanStringSplitArray[1]; //stores something like "211.65</span" into a string
				
				String[] secondSplit = secondPartOfSpan.split("<"); //split again. This time by < 
				//will split into something like "211.65" and "/span"
				
				String adjClose = secondSplit[0]; //first part [0] contains the value we need, assign the value to a string
				adjCloseArrayListTemp.add(adjClose); //store the string value into an arraylist
				
				
			}
			
		}
		
		adjClosePrice = new double[adjCloseArrayListTemp.size()];
		
		//convert string array of the close price values to an array of type double
		for(int i = 0; i < adjCloseArrayListTemp.size(); i ++) {
			adjClosePrice[i] = Double.parseDouble(adjCloseArrayListTemp.get(i));
			System.out.println(adjClosePrice[i]);
		}
		
	}
	
	

}
