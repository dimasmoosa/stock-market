/**
 * The main application
 */
package application;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;

import data.HistoricalData;

/**
 * @author Dimas Moosa
 *
 */
public class Application {

	
	public static void main(String[] args) throws ParseException {
		
		//Application app = new Application(); //start the app (not sure what to do with this...)
		 //prompt user for stock they want info on
		 //return info to user
		
		HistoricalData hd = new HistoricalData(); //instantiate HistoricalData object

		
		//try to call method that retrieves the closing prices
		try {
			LocalDate[] temp0 = hd.getDate("AAPL", 5); 
			double[] temp = hd.getAdjClosePrice("AAPL", 5); 
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	

}
