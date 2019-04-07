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
		
		String ticker = "AAPL";
		
		try {			
			System.out.println(od.getRows(od.getCallsTableBody("AAPL")).size());
			System.out.println(od.getRowsITM(od.getCallsTableBody("AAPL")).size());
			System.out.println(od.getRowsOTM(od.getCallsTableBody("AAPL")).size());
			System.out.println("\n\n");
			System.out.println(od.getRows(od.getPutsTableBody("AAPL")).size());
			System.out.println(od.getRowsITM(od.getPutsTableBody("AAPL")).size());
			System.out.println(od.getRowsOTM(od.getPutsTableBody("AAPL")).size());
			System.out.println("\n\n");
			System.out.println("Index of calls ATM " + od.getCallsATMIndex("AAPL"));
			System.out.println("Index of puts ATM " + od.getPutsATMIndex("AAPL"));
			
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	

}
