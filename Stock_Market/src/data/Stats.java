package data;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

public class Stats {
	//A class that will have functionalities that retrieve statistics about historical data of a stock, ETC, or other security specified
	
	
	/**
	 * A method that gets the average weekly movement for the specified stock for the past specified amount of weeks.
	 * 
	 * @param The ticker symbol of the security
	 * @param The amount of most recent weeks the user wants to get data for
	 * @return The average weekly movement of the ticker for the last X specified weeks
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public double getAverageWeeklyMovement(String ticker, int weeks) throws IOException, ParseException {
		double avg; //the average weekly movement
		DecimalFormat df = new DecimalFormat("#.00"); //this can be used to round our average to two decimal places
		double[] prices; //array containing the last X prices
		double fridayClose; //Friday close
		double mondayClose; //Monday close
		double weeklyDifference; //Friday - Monday difference
		//ArrayList<Double> allWeeklyDifference = new ArrayList<>(); //ArrayList of all the weekly differences
		double weeklyMovement;
		ArrayList<Double> allWeeklyMovement = new ArrayList<>(); //ArrayList of all the weekly movements
		double weeklyMovementSum = 0; //sum of all the weekly movements
		
		LocalDate currentDate = LocalDate.now(); //current date
		DayOfWeek currentDayOfWeek = currentDate.getDayOfWeek(); //current day of week (ENUM DayOfWeek object)
		String currentDayOfWeekString = currentDayOfWeek.toString(); //string version of day of week
		
		int days = weeks * 5; //the amount of trading days 
		int offset; //this will be the offset between the current date and the last Friday
		
		
		//instantiate the HistoricalData class so we can use its methods
		HistoricalData hd = new HistoricalData();

		
		switch(currentDayOfWeekString) {
		
			//if it's sunday, the array will have Friday as date at index 0 since Sunday (and Saturday) are not trading days
			case "SUNDAY": offset = 0; 
			//instantiate the LocalDate[] objects after we get the max amount of days we need to cover the amount of weeks the user is asking for
			prices = hd.getAdjClosePrice(ticker, days + offset); 
			if(days + offset > prices.length) {
				weeks = prices.length / 5;
				System.out.println("We were only able to retrieve the last " + weeks + " weeks of data. \n");
			}
			for(int i = offset; i < prices.length; i += 5 ) { //increment by 5 at end to get next week
				fridayClose = prices[i]; //get Friday close price... which will be at the offset index
				if(i + 4 >= prices.length) {
					break;
				}
				mondayClose = prices[i+4]; //get Monday close price... which will be 4 indices ahead of Friday index
				weeklyDifference = fridayClose - mondayClose; //difference between Friday and Monday close
				//allWeeklyDifference.add(weeklyDifference); //add the difference to the difference array list in case we ever want to use it
				weeklyMovement = (weeklyDifference / mondayClose) * 100; //the movement amount in a percentage
				allWeeklyMovement.add(weeklyMovement); //add the weekly movement to the array list containing all weekly movements
				
			}
			break;
			
			case "SATURDAY": offset = 0; //see above
			prices = hd.getAdjClosePrice(ticker, days + offset); 
			//sometimes we can't get the amount of weeks requested because of the way the historical data class is set up
			//it's due to some rows not having the adjusted closing price which reduces the length of the array
			//this is why prices.length is used in the for loop.
			//this message lets the user know we were only able to get X amount of rows
			if(days + offset > prices.length) {
				weeks = prices.length / 5;
				System.out.println("We were only able to retrieve the last " + weeks + " weeks of data. \n");
			}
			for(int i = offset; i < prices.length; i += 5) { 
				fridayClose = prices[i]; 
				if(i + 4 >= prices.length) { //break out of loop if we reach scenario mentioned in above if statement
					break;
				}
				mondayClose = prices[i+4]; 
				weeklyDifference = fridayClose - mondayClose; 
				//allWeeklyDifference.add(weeklyDifference); 
				weeklyMovement = (weeklyDifference / mondayClose) * 100; 
				allWeeklyMovement.add(weeklyMovement); 
				
				
			}
			break;
			
			case "FRIDAY": offset = 0; //if today is Friday, then we can start counting the weeks from today to last Monday and so on
			System.out.println("Note: If the market is still open (closes at 4:30 PM on regular trading days), "
					+ "the closing price will change by market close so that means the weekly movement will be off by a bit");
			prices = hd.getAdjClosePrice(ticker, days + offset);
			if(days + offset > prices.length) {
				weeks = prices.length / 5;
				System.out.println("We were only able to retrieve the last " + weeks + " weeks of data. \n");
			}
			for(int i = offset; i < prices.length; i += 5 ) { //increment by 5 at end to get next week
				fridayClose = prices[i]; //get Friday close price... which will be at the offset index
				if(i + 4 >= prices.length) {
					break;
				}
				mondayClose = prices[i+4]; //get Monday close price... which will be 4 indices ahead of Friday index
				weeklyDifference = fridayClose - mondayClose; //difference between Friday and Monday close
				//allWeeklyDifference.add(weeklyDifference); //add the difference to the difference array list in case we ever want to use it
				weeklyMovement = (weeklyDifference / mondayClose) * 100; //the movement amount in a percentage
				allWeeklyMovement.add(weeklyMovement); //add the weekly movement to the array list containing all weekly movements
				
			}
			break;
			
			//if today is Thursday, then we can't use the current week. 
			//Have to traverse to last week (starting from last Friday)... will be last Friday - Monday of that week... and so on
			case "THURSDAY": offset = 4; 
			prices = hd.getAdjClosePrice(ticker, days + offset);
			if(days + offset > prices.length) {
				weeks = prices.length / 5;
				System.out.println("We were only able to retrieve the last " + weeks + " weeks of data. \n");
			}
			for(int i = offset; i < prices.length; i += 5 ) { //increment by 5 at end to get next week
				fridayClose = prices[i]; //get Friday close price... which will be at the offset index
				if(i + 4 >= prices.length) {
					break;
				}
				mondayClose = prices[i+4]; //get Monday close price... which will be 4 indices ahead of Friday index
				weeklyDifference = fridayClose - mondayClose; //difference between Friday and Monday close
				//allWeeklyDifference.add(weeklyDifference); //add the difference to the difference array list in case we ever want to use it
				weeklyMovement = (weeklyDifference / mondayClose) * 100; //the movement amount in a percentage
				allWeeklyMovement.add(weeklyMovement); //add the weekly movement to the array list containing all weekly movements
				
			}
			break;
			
			case "WEDNESDAY": offset = 3; //Wednesday to last Friday = 3 days... you get the point
			prices = hd.getAdjClosePrice(ticker, days + offset);
			if(days + offset > prices.length) {
				weeks = prices.length / 5;
				System.out.println("We were only able to retrieve the last " + weeks + " weeks of data. \n");
			}
			for(int i = offset; i < prices.length; i += 5 ) { //increment by 5 at end to get next week
				fridayClose = prices[i]; //get Friday close price... which will be at the offset index
				if(i + 4 >= prices.length) {
					break;
				}
				mondayClose = prices[i+4]; //get Monday close price... which will be 4 indices ahead of Friday index
				weeklyDifference = fridayClose - mondayClose; //difference between Friday and Monday close
				//allWeeklyDifference.add(weeklyDifference); //add the difference to the difference array list in case we ever want to use it
				weeklyMovement = (weeklyDifference / mondayClose) * 100; //the movement amount in a percentage
				allWeeklyMovement.add(weeklyMovement); //add the weekly movement to the array list containing all weekly movements
				
			}
			break;
			
			case "TUESDAY": offset = 2;
			prices = hd.getAdjClosePrice(ticker, days + offset);
			if(days + offset > prices.length) {
				weeks = prices.length / 5;
				System.out.println("We were only able to retrieve the last " + weeks + " weeks of data. \n");
			}
			for(int i = offset; i < prices.length; i += 5 ) { //increment by 5 at end to get next week
				fridayClose = prices[i]; //get Friday close price... which will be at the offset index
				if(i + 4 >= prices.length) {
					break;
				}
				mondayClose = prices[i+4]; //get Monday close price... which will be 4 indices ahead of Friday index
				weeklyDifference = fridayClose - mondayClose; //difference between Friday and Monday close
				//allWeeklyDifference.add(weeklyDifference); //add the difference to the difference array list in case we ever want to use it
				weeklyMovement = (weeklyDifference / mondayClose) * 100; //the movement amount in a percentage
				allWeeklyMovement.add(weeklyMovement); //add the weekly movement to the array list containing all weekly movements
				
			}
			break;
			
			case "MONDAY": offset = 1;
			prices = hd.getAdjClosePrice(ticker, days + offset);
			if(days + offset > prices.length) {
				weeks = prices.length / 5;
				System.out.println("We were only able to retrieve the last " + weeks + " weeks of data. \n");
			}
			for(int i = offset; i < prices.length; i += 5 ) { //increment by 5 at end to get next week
				fridayClose = prices[i]; //get Friday close price... which will be at the offset index
				if(i + 4 >= prices.length) {
					break;
				}
				mondayClose = prices[i+4]; //get Monday close price... which will be 4 indices ahead of Friday index
				weeklyDifference = fridayClose - mondayClose; //difference between Friday and Monday close
				//allWeeklyDifference.add(weeklyDifference); //add the difference to the difference array list in case we ever want to use it
				weeklyMovement = (weeklyDifference / mondayClose) * 100; //the movement amount in a percentage
				allWeeklyMovement.add(weeklyMovement); //add the weekly movement to the array list containing all weekly movements
				
			}
			break;
		
		}
		
		System.out.println("DISCLAIMER: If there are any holidays/off days during the trading week, the average weekly movement will be inaccurate. "
				+ "\nThis is due to the nature of the way this method works, which takes into account the Friday and Monday closing prices. "
				+ "\nThis means any days off during that week will affect the calculation by offsetting the week depending on how many days "
				+ "the markets were closed. \nFor example, if the market is closed Wednesday, the method will return the last Friday to prior Tuesday "
				+ "as the week. \n");
		
		//iterate over all the weekly movement, add them up to get the sum
		for(int i = 0; i < allWeeklyMovement.size(); i++) {
			//get the double value of Double object at index i... get its absolute value so we can get the accurate movement
			weeklyMovementSum += Math.abs(allWeeklyMovement.get(i).doubleValue()); 
		}
		
		avg = weeklyMovementSum / allWeeklyMovement.size(); // divide the sum by the amount of weeks to get the average
		
		avg = Double.parseDouble(df.format(avg)); //round the average to two decimal places
		
		System.out.println("Done retrieving the average weekly movement (in percent) for " + ticker + " in the last " + weeks + " week(s).\n");
		
		return avg;
	}	
	
	
	public double getSuccessRate(String ticker, int weeks, double threshold) throws IOException {
		double successRate = 0;
		
		//----------------------------------------------------------------------------------------------------------------------------------
		DecimalFormat df = new DecimalFormat("#.00"); //this can be used to round our average to two decimal places
		double[] prices; //array containing the last X prices
		double fridayClose; //Friday close
		double mondayClose; //Monday close
		double weeklyDifference; //Friday - Monday difference
		ArrayList<Double> allWeeklyDifference = new ArrayList<>(); //ArrayList of all the weekly differences
		double weeklyMovement;
		ArrayList<Double> allWeeklyMovement = new ArrayList<>(); //ArrayList of all the weekly movements
		int amountExceeded = 0; //number of times the threshold specified was exceeded
		
		LocalDate currentDate = LocalDate.now(); //current date
		DayOfWeek currentDayOfWeek = currentDate.getDayOfWeek(); //current day of week (ENUM DayOfWeek object)
		String currentDayOfWeekString = currentDayOfWeek.toString(); //string version of day of week
		
		int days = weeks * 5; //the amount of trading days 
		int offset; //this will be the offset between the current date and the last Friday
		
		
		//instantiate the HistoricalData class so we can use its methods
		HistoricalData hd = new HistoricalData();

		
		switch(currentDayOfWeekString) {
		
			//if it's sunday, the array will have Friday as date at index 0 since Sunday (and Saturday) are not trading days
			case "SUNDAY": offset = 0; 
			//instantiate the LocalDate[] objects after we get the max amount of days we need to cover the amount of weeks the user is asking for
			prices = hd.getAdjClosePrice(ticker, days + offset); 
			if(days + offset > prices.length) {
				weeks = prices.length / 5;
				System.out.println("We were only able to retrieve the last " + weeks + " weeks of data. \n");
			}
			for(int i = offset; i < prices.length; i += 5 ) { //increment by 5 at end to get next week
				fridayClose = prices[i]; //get Friday close price... which will be at the offset index
				if(i + 4 >= prices.length) {
					break;
				}
				mondayClose = prices[i+4]; //get Monday close price... which will be 4 indices ahead of Friday index
				weeklyDifference = fridayClose - mondayClose; //difference between Friday and Monday close
				allWeeklyDifference.add(weeklyDifference); //add the difference to the difference array list in case we ever want to use it
				weeklyMovement = (weeklyDifference / mondayClose) * 100; //the movement amount in a percentage
				allWeeklyMovement.add(weeklyMovement); //add the weekly movement to the array list containing all weekly movements
				
			}
			break;
			
			case "SATURDAY": offset = 0; //see above
			prices = hd.getAdjClosePrice(ticker, days + offset); 
			//sometimes we can't get the amount of weeks requested because of the way the historical data class is set up
			//it's due to some rows not having the adjusted closing price which reduces the length of the array
			//this is why prices.length is used in the for loop.
			//this message lets the user know we were only able to get X amount of rows
			if(days + offset > prices.length) {
				weeks = prices.length / 5;
				System.out.println("We were only able to retrieve the last " + weeks + " weeks of data. \n");
			}
			for(int i = offset; i < prices.length; i += 5) { 
				fridayClose = prices[i]; 
				if(i + 4 >= prices.length) { //break out of loop if we reach scenario mentioned in above if statement
					break;
				}
				mondayClose = prices[i+4]; 
				weeklyDifference = fridayClose - mondayClose; 
				allWeeklyDifference.add(weeklyDifference); 
				weeklyMovement = (weeklyDifference / mondayClose) * 100; 
				allWeeklyMovement.add(weeklyMovement); 
				
				
			}
			break;
			
			case "FRIDAY": offset = 0; //if today is Friday, then we can start counting the weeks from today to last Monday and so on
			System.out.println("Note: If the market is still open (closes at 4:30 PM on regular trading days), "
					+ "the closing price will change by market close so that means the weekly movement will be off by a bit");
			prices = hd.getAdjClosePrice(ticker, days + offset);
			if(days + offset > prices.length) {
				weeks = prices.length / 5;
				System.out.println("We were only able to retrieve the last " + weeks + " weeks of data. \n");
			}
			for(int i = offset; i < prices.length; i += 5 ) { //increment by 5 at end to get next week
				fridayClose = prices[i]; //get Friday close price... which will be at the offset index
				if(i + 4 >= prices.length) {
					break;
				}
				mondayClose = prices[i+4]; //get Monday close price... which will be 4 indices ahead of Friday index
				weeklyDifference = fridayClose - mondayClose; //difference between Friday and Monday close
				allWeeklyDifference.add(weeklyDifference); //add the difference to the difference array list in case we ever want to use it
				weeklyMovement = (weeklyDifference / mondayClose) * 100; //the movement amount in a percentage
				allWeeklyMovement.add(weeklyMovement); //add the weekly movement to the array list containing all weekly movements
				
			}
			break;
			
			//if today is Thursday, then we can't use the current week. 
			//Have to traverse to last week (starting from last Friday)... will be last Friday - Monday of that week... and so on
			case "THURSDAY": offset = 4; 
			prices = hd.getAdjClosePrice(ticker, days + offset);
			if(days + offset > prices.length) {
				weeks = prices.length / 5;
				System.out.println("We were only able to retrieve the last " + weeks + " weeks of data. \n");
			}
			for(int i = offset; i < prices.length; i += 5 ) { //increment by 5 at end to get next week
				fridayClose = prices[i]; //get Friday close price... which will be at the offset index
				if(i + 4 >= prices.length) {
					break;
				}
				mondayClose = prices[i+4]; //get Monday close price... which will be 4 indices ahead of Friday index
				weeklyDifference = fridayClose - mondayClose; //difference between Friday and Monday close
				allWeeklyDifference.add(weeklyDifference); //add the difference to the difference array list in case we ever want to use it
				weeklyMovement = (weeklyDifference / mondayClose) * 100; //the movement amount in a percentage
				allWeeklyMovement.add(weeklyMovement); //add the weekly movement to the array list containing all weekly movements
				
			}
			break;
			
			case "WEDNESDAY": offset = 3; //Wednesday to last Friday = 3 days... you get the point
			prices = hd.getAdjClosePrice(ticker, days + offset);
			if(days + offset > prices.length) {
				weeks = prices.length / 5;
				System.out.println("We were only able to retrieve the last " + weeks + " weeks of data. \n");
			}
			for(int i = offset; i < prices.length; i += 5 ) { //increment by 5 at end to get next week
				fridayClose = prices[i]; //get Friday close price... which will be at the offset index
				if(i + 4 >= prices.length) {
					break;
				}
				mondayClose = prices[i+4]; //get Monday close price... which will be 4 indices ahead of Friday index
				weeklyDifference = fridayClose - mondayClose; //difference between Friday and Monday close
				allWeeklyDifference.add(weeklyDifference); //add the difference to the difference array list in case we ever want to use it
				weeklyMovement = (weeklyDifference / mondayClose) * 100; //the movement amount in a percentage
				allWeeklyMovement.add(weeklyMovement); //add the weekly movement to the array list containing all weekly movements
				
			}
			break;
			
			case "TUESDAY": offset = 2;
			prices = hd.getAdjClosePrice(ticker, days + offset);
			if(days + offset > prices.length) {
				weeks = prices.length / 5;
				System.out.println("We were only able to retrieve the last " + weeks + " weeks of data. \n");
			}
			for(int i = offset; i < prices.length; i += 5 ) { //increment by 5 at end to get next week
				fridayClose = prices[i]; //get Friday close price... which will be at the offset index
				if(i + 4 >= prices.length) {
					break;
				}
				mondayClose = prices[i+4]; //get Monday close price... which will be 4 indices ahead of Friday index
				weeklyDifference = fridayClose - mondayClose; //difference between Friday and Monday close
				allWeeklyDifference.add(weeklyDifference); //add the difference to the difference array list in case we ever want to use it
				weeklyMovement = (weeklyDifference / mondayClose) * 100; //the movement amount in a percentage
				allWeeklyMovement.add(weeklyMovement); //add the weekly movement to the array list containing all weekly movements
				
			}
			break;
			
			case "MONDAY": offset = 1;
			prices = hd.getAdjClosePrice(ticker, days + offset);
			if(days + offset > prices.length) {
				weeks = prices.length / 5;
				System.out.println("We were only able to retrieve the last " + weeks + " weeks of data. \n");
			}
			for(int i = offset; i < prices.length; i += 5 ) { //increment by 5 at end to get next week
				fridayClose = prices[i]; //get Friday close price... which will be at the offset index
				if(i + 4 >= prices.length) {
					break;
				}
				mondayClose = prices[i+4]; //get Monday close price... which will be 4 indices ahead of Friday index
				weeklyDifference = fridayClose - mondayClose; //difference between Friday and Monday close
				allWeeklyDifference.add(weeklyDifference); //add the difference to the difference array list in case we ever want to use it
				weeklyMovement = (weeklyDifference / mondayClose) * 100; //the movement amount in a percentage
				allWeeklyMovement.add(weeklyMovement); //add the weekly movement to the array list containing all weekly movements
				
			}
			break;
		
		}
		
		System.out.println("DISCLAIMER: If there are any holidays/off days during the trading week, the average weekly movement will be inaccurate. "
				+ "\nThis is due to the nature of the way this method works, which takes into account the Friday and Monday closing prices. "
				+ "\nThis means any days off during that week will affect the calculation by offsetting the week depending on how many days "
				+ "the markets were closed. \nFor example, if the market is closed Wednesday, the method will return the last Friday to prior Tuesday "
				+ "as the week. \n");
		
		for(int i = 0; i < allWeeklyMovement.size(); i++) {
			if(allWeeklyMovement.get(i) > threshold) {
				amountExceeded++;
			}
		}
		
		double failureRate = (double) amountExceeded / weeks;
		successRate = (1 - failureRate) * 100;
		successRate = Double.parseDouble(df.format(successRate));

		System.out.println("Done retrieving the success rate of " + ticker + " for a " + threshold + "% threshold in the last " 
							+ weeks + " weeks. ");
		System.out.println(ticker + " succesfully stayed within +/- " + threshold + "% movement for " 
							+ successRate + "% of weeks in the last " + weeks + " weeks. \n");
		
		//----------------------------------------------------------------------------------------------------------------------------------
		
		
		
		return successRate;
	}
	
	
	//create method for positive successRate (staying within +X%)
//	public double getNegativeSuccessRate() {
//		
//	}
	
	
	//create method for negative successRate (staying within -X%)
//	public double getPositiveSuccessRate(){
//		
//	}

}
