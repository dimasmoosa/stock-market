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
			double success20 = stats.getPositiveSuccessRate("AAPL", 4, 5);
			System.out.println("success: " + success20);

//			System.out.println(stats.getAverageWeeklyMovement("AAPL", 15));
//			double[] prices = hd.getAdjClosePrice("AAPL", 10);
//			for(int i = 0; i < prices.length; i++) {
//				System.out.println(prices[i]);
//			}
			
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	

}
