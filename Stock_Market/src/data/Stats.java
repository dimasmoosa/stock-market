package data;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

public class Stats {
	//A class that will have functionalities that retrieve statistics about historical data of a stock, ETC, or other security specified
	
	private DecimalFormat df = new DecimalFormat("#.00");
	
	
	/**
	 * A method that returns an ArrayList<Double> containing the past specified number of weekly movements of a specified ticker
	 * 
	 * @param ticker
	 * @param weeks
	 * @return
	 * @throws IOException
	 */
	public ArrayList<Double> getWeeklyMovementPercent(String ticker, int weeks) throws IOException {
		double weeklyMovement = 0;
		ArrayList<Double> allWeeklyMovement = new ArrayList<>(); //ArrayList of all the weekly movements
		
		double[] prices; //array containing the last X prices
		double fridayClose = 0; //Friday close
		double mondayClose = 0; //Monday close
		double weeklyDifference = 0; //Friday - Monday difference

		LocalDate currentDate = LocalDate.now(); //current date
		DayOfWeek currentDayOfWeek = currentDate.getDayOfWeek(); //current day of week (ENUM DayOfWeek object)
		String currentDayOfWeekString = currentDayOfWeek.toString(); //string version of day of week
		
		int offset = 0;		
		int days = weeks * 5;
		
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
			weeklyMovement = (weeklyDifference / mondayClose) * 100; //the movement amount in a percentage
			allWeeklyMovement.add(weeklyMovement); //add the weekly movement to the array list containing all weekly movements
			
		}
		break;
	
	}
		
		return allWeeklyMovement;
	}
	
	
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
		ArrayList<Double> weeklyMovementArray = getWeeklyMovementPercent(ticker, weeks);
		
		double weeklyMovementSum = 0;
		double avg = 0;
		
		//iterate over all the weekly movement, add them up to get the sum
		for(int i = 0; i < weeklyMovementArray.size(); i++) {
			//get the double value of Double object at index i... get its absolute value so we can get the accurate movement
			weeklyMovementSum += Math.abs(weeklyMovementArray.get(i).doubleValue()); 
		}
		
		avg = weeklyMovementSum / weeklyMovementArray.size(); // divide the sum by the amount of weeks to get the average
		
		avg = Double.parseDouble(df.format(avg)); //round the average to two decimal places
		
		
		/*
		System.out.println("DISCLAIMER: If there are any holidays/off days during the trading week, the average weekly movement will be inaccurate. "
				+ "\nThis is due to the nature of the way this method works, which takes into account the Friday and Monday closing prices. "
				+ "\nThis means any days off during that week will affect the calculation by offsetting the week depending on how many days "
				+ "the markets were closed. \nFor example, if the market is closed Wednesday, the method will return the last Friday to prior Tuesday "
				+ "as the week. \n");
		*/
		
		
		System.out.println("Done retrieving the average weekly movement (in percent) for " + ticker + " in the last " + weeks + " week(s).\n");
		
		return avg;

	}	
	
	/**
	 * A method that gets the failure rate of a specified stock/ETF staying within a specified % threshold over the specified amount of past weeks
	 * For example, the rate at which Apple DID NOT stay within +/- 5% movement from Monday close to Friday close in the last 10 weeks
	 * This method returns 1-getSuccessRate()
	 * 
	 * @param ticker
	 * @param weeks
	 * @param threshold
	 * @return
	 * @throws IOException
	 */
	public double getFailureRate(String ticker, int weeks, double threshold) throws IOException { 
		double failureRate = 0;
		
		int amountExceeded = 0;
		
		ArrayList<Double> weeklyMovementArray = getWeeklyMovementPercent(ticker, weeks);
		
		for(int i = 0; i < weeklyMovementArray.size(); i++) {
			if(Math.abs(weeklyMovementArray.get(i)) > threshold) {
				amountExceeded++;
			}
		}
		
		failureRate = (double) amountExceeded / weeks;
		failureRate *= 100;
		failureRate = Double.parseDouble(df.format(failureRate));
		
		return failureRate;
	}
	

	/**
	 * A method that gets the success rate of a specified stock/ETF staying within a specified % threshold over the specified amount of past weeks
	 * For example, the rate at which Apple stayed within +/- 5% movement from Monday close to Friday close in the last 10 weeks
	 * 
	 * @param ticker
	 * @param weeks
	 * @param threshold (the percentage)
	 * @return
	 * @throws IOException
	 */
	public double getSuccessRate(String ticker, int weeks, double threshold) throws IOException { 
		double successRate = 0;

		successRate = 100 - getFailureRate(ticker, weeks, threshold);
		successRate = Double.parseDouble(df.format(successRate));

		return successRate;
	}
	
	/**
	 * A method that returns the positive movement success rate. This is the amount of times the ticker stayed within +X%
	 * over the past few weeks divided by the amount of positive movements the ticker had
	 * 
	 * @param ticker
	 * @param weeks
	 * @param threshold
	 * @return
	 * @throws IOException
	 */
	public double getPositiveSuccessRate(String ticker, int weeks, double threshold) throws IOException {
		double positiveSuccessRate = 0;
	
		ArrayList<Double> weeklyMovement = getWeeklyMovementPercent(ticker, weeks);
		
		int amountNotExceeded = 0;
		
		int amountOfPositiveMovements = 0;
		
		for(int i = 0; i < weeklyMovement.size(); i++) {
			if(weeklyMovement.get(i) >= 0) {
				amountOfPositiveMovements++;
				if(weeklyMovement.get(i) < threshold) {
					amountNotExceeded++;
				}
			}
		}
		
		if(amountOfPositiveMovements == 0) {
			System.out.println("Sorry, there were no positive movements in the past " + weeks + " weeks for " + ticker + ".\n");
			System.exit(0);
		}
		
		positiveSuccessRate = (double) amountNotExceeded / amountOfPositiveMovements;
		positiveSuccessRate *= 100;
		positiveSuccessRate = Double.parseDouble(df.format(positiveSuccessRate));
	
		return positiveSuccessRate;
	}
	
	
	//create method for negative movement successRate (staying within -X%)
//	public double getNegativeSuccessRate(){
//		
//	}
	
	
	//create method for positive movement failureRate (staying within +X%)
//	public double getPositiveFailureRate() {
//		
//	}
	
	
	//create method for negative movement failureRate (staying within -X%)
//	public double getNegativeFailureRate(){
//		
//	}
	
	/*
	 * Equation to consider when implementing what price to buy the option at or to get a break even point
	 * This is for a Call Credit Spread or Bear Call Spread
	 * a = credit
	 * b = spread
	 * c = (b-a) = max loss or net loss
	 * x = past successRate of staying within a % movement
	 * y = past failureRate of staying within a % movement
	 * 
	 * Let's use Apple as an example, which usually have a 250 (2.50x100) spread for a credit spread close to the current price
	 * b = 250
	 * Let's also assume past successRate within the last 4 weeks of Apple staying within a specified +/- % (will have to do + later for this spread)
	 * is 75%. This means the failureRate is 25%
	 * x = .75
	 * y = .25
	 * 
	 * The equation to get the intersection, or the point at which profit = loss or break-even point, then is:
	 * x*a = y*(b-a)
	 * or, in this case:
	 * .75a = .25(250-a)
	 * Solving...
	 * .75a = (.25*250) - (.25*a)
	 * 1a = .25*250
	 * a = 62.5
	 * In this case, 62.5 would be the break-even point. In other words, this would be the price of the credit at which we would break even at,
	 * assuming that the past volatility is similar to future volatility, if the success rate was 75% and the spread 250.
	 * 
	 */

}
