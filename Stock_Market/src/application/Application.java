/**
 * The main application
 */
package application;

import java.io.IOException;
import java.text.ParseException;

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

		
		try {
			double success = stats.getSuccessRate("AAPL", 10, 3);
			
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	

}
