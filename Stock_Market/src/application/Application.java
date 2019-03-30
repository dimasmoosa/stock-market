/**
 * The main application
 */
package application;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;

import data.HistoricalData;
import data.Stats;

/**
 * @author Dimas Moosa
 * 
 * 
 */
public class Application {

	
	public static void main(String[] args) throws ParseException {
		
		//Application app = new Application(); //start the app (not sure what to do with this...)
		 //prompt user for stock they want info on
		 //return info to user
		
		HistoricalData hd = new HistoricalData(); //instantiate HistoricalData object
		Stats stats = new Stats();

		
		//try to call method that retrieves the closing prices
		try {
			double average = stats.getAverageWeeklyMovement("AAPL", 2);
			System.out.println(average);
			
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	

}
