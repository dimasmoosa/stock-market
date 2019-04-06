/**
 * The main application
 */
package application;

import java.io.IOException;
import java.text.ParseException;

import data.HistoricalData;
import data.OptionsData;
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
		OptionsData od = new OptionsData();
		Stats stats = new Stats();

		
		try {
//			double success20 = stats.getPositiveSuccessRate("AAPL", 10, 1.75);
//			System.out.println("success: " + success20);
			
			System.out.println(od.getInTheMoneyRows(od.getCallsTable("AAPL")).size());
			System.out.println(od.getInTheMoneyRows(od.getPutsTable("AAPL")).size());
			
			System.out.println(od.getOutOfTheMoneyRows(od.getCallsTable("AAPL")).size());
			System.out.println(od.getOutOfTheMoneyRows(od.getPutsTable("AAPL")).size());
			
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	

}
