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
	
	
	
	/*
	 * HAVE TO ADD VALIDATION TO CHECK WHETHER THERE'S A DAY IN THE WEEK WHERE THE MARKET IS CLOSED. IF THERE IS,
	 * THE DATA WILL BE OFF. FOR EXAMPLE IF THE MARKET IS CLOSED ON A FRIDAY AND THE CURRENT DAY IS A FRIDAY, IT WILL GRAB 
	 * THURSDAY'S CLOSING PRICE AND SUBTRACT IT FROM LAST FRIDAY'S CLOSING PRICE INSTEAD OF FRIDAY TO MONDAY
	 * 
	 * I HAVE TO ACCOUNT FOR THIS SOMEHOW BY CHECKING IF THERE WERE ANY DAYS WHERE THE MARKET WAS CLOSED 
	 * 
	 * MAYBE I CAN USE THIS WEBSITE https://www.nasdaqtrader.com/Trader.aspx?id=Calendar AND PARSE IT TO ADJUST ACCORDINGLY
	 * 
	 */
	/**
	 * A method that returns an ArrayList<Double> containing the past specified number of weekly movements of a specified ticker
	 * 
	 * @param ticker
	 * @param weeks
	 * @return
	 * @throws IOException
	 * @throws ParseException 
	 */
	public ArrayList<Double> getWeeklyMovementArray(String ticker, int weeks) throws IOException, ParseException {
		HistoricalData hd = new HistoricalData();
		double weeklyMovement = 0;
		ArrayList<Double> allWeeklyMovement = new ArrayList<>(); //ArrayList of all the weekly movements
		
		double[] prices; //array containing the last X prices
		double lastDayOfWeekClose = 0; //last day of the week close. Friday most of the time unless the market is closed
		double firstDayOfWeekClose = 0; //first day of the week. Monday most of the time unless the market is closed
		double weeklyDifference = 0; //Friday - Monday difference

		LocalDate currentDate = LocalDate.now(); //current date
		DayOfWeek currentDayOfWeek = currentDate.getDayOfWeek(); //current day of week (ENUM DayOfWeek object)
		String currentDayOfWeekString = currentDayOfWeek.toString(); //string version of day of week
		
		/* offset is used to get full weeks. For example if current date is Friday that's fine we can do Fri - Mon
		 * but if the current date is thursday then we have to have an offset so it starts counting the weeks starting
		 * from last friday and last monday since current week has no friday
		 */
		int offset = 0;	
		
		/*
		 * openDays is so that we can account for days where the market is closed. If today is friday but the market
		 * is closed, then we have to use last week's data and so on. 
		 * 
		 * If today is Wednesday but the market was closed on last week Thursday, then we have to get the friday minus monday
		 * so we can't just increment by 4, we'd have to increment by 3 for that one week.
		 * 
		 * add extra validation for each for loop that checks the amount of closed days during that fri - mon week
		 * depending on how many there are, subtract that amount from 4... the prices[i+4] logic should be turned into a
		 * prices[i+openMarketDays] logic
		 * 
		 * maybe can use https://www.nasdaqtrader.com/Trader.aspx?id=Calendar to see all the closed days. Early closes are okay.
		 * maybe we can hard code it for 2019 for now and fix it later?
		 * 
		 * 2019 closed days --->
		 * 010119	012119	021819	041919	052719	070419	090219	112819	122519
		 * 
		 */
		int closedMarketDays = 0;
		
		

		int days = weeks * 5;

		/*
		 * ---------------------------------------------------------------------------------------------------------------------------
		 * the variables fridayClose and mondayClose won't always be friday and monday but rather the last and first day of the week
		 * rename them
		 * ---------------------------------------------------------------------------------------------------------------------------
		 */
		switch(currentDayOfWeekString) {
		
		//if it's sunday, the array will have Friday as date at index 0 since Sunday (and Saturday) are not trading days
		case "SUNDAY": offset = 0; 
		//instantiate the LocalDate[] objects after we get the max amount of days we need to cover the amount of weeks the user is asking for
		prices = hd.getAdjClosePrice(ticker, days + offset); 
		//sometimes we can't get the amount of weeks requested because of the way the historical data class is set up
		//it's due to some rows not having the adjusted closing price (dividends?) which reduces the length of the array
		//this is why prices.length is used in the for loop.
		//this message lets the user know we were only able to get X amount of rows
		if(days + offset > prices.length) {
			weeks = prices.length / 5;
			System.out.println("We were only able to retrieve the last " + weeks + " weeks of data. \n");
		}
		for(int i = offset; i < prices.length; i += 5 ) { //increment by 5 at end to get next week
			lastDayOfWeekClose = prices[i]; //get Friday (or last day of week) close price... which will be at the offset index
			if(i + 4 >= prices.length) { //break out of loop if we reach scenario mentioned in above if statement
				break;
			}
			//instead of i+4 it should be i+marketDaysOpen. Add validation here inside the for loop?
			firstDayOfWeekClose = prices[i+4]; //get Monday close price... which will be 4 indices ahead of Friday index
			weeklyDifference = lastDayOfWeekClose - firstDayOfWeekClose; //difference between last - first day of week close (typically fri - mon)
			weeklyMovement = (weeklyDifference / firstDayOfWeekClose) * 100; //the movement amount in a percentage
			allWeeklyMovement.add(weeklyMovement); //add the weekly movement to the array list containing all weekly movements
			
		}
		break;
		
		case "SATURDAY": offset = 0; //see above
		prices = hd.getAdjClosePrice(ticker, days + offset);
		//******************************************************************************************************************************
		LocalDate[] dates = hd.getDates(ticker, days + offset);
		//******************************************************************************************************************************
		if(days + offset > prices.length) {
			weeks = prices.length / 5;
			System.out.println("We were only able to retrieve the last " + weeks + " weeks of data. \n");
		}
		for(int i = offset; i < prices.length; i += (5-closedMarketDays)) { 
			lastDayOfWeekClose = prices[i]; 
			closedMarketDays = 0; //reset //******************
			//******************************************************************************************************************************
			
			if(dates[i].getDayOfWeek().toString().equals("FRIDAY")) { //if last open day of week is Friday
				if(dates[i+4].getDayOfWeek().toString().equals("MONDAY")) { //and if first open day of week is Monday
					//no closed market days this week
					firstDayOfWeekClose = prices[i+4];
					weeklyDifference = lastDayOfWeekClose - firstDayOfWeekClose;
					weeklyMovement = (weeklyDifference / firstDayOfWeekClose) * 100;
					allWeeklyMovement.add(weeklyMovement);
				}
				else if(!dates[i+4].getDayOfWeek().toString().equals("MONDAY")) { //if first open day of week is not Monday
					//get the next Friday's index, which should be at most 4 out
					int indexOfOpenMarketDayAfterLastFriday = 0;
					for(int x = 0; i < 4; i++) {
						if(dates[x].getDayOfWeek().toString().equals("FRIDAY")) {
							indexOfOpenMarketDayAfterLastFriday = x - 1; //get the index before the previous Friday, which is the first open day of week
						}
					}
					firstDayOfWeekClose = prices[i + indexOfOpenMarketDayAfterLastFriday];
				}
				
			}
			else if(!dates[i].getDayOfWeek().toString().equals("FRIDAY")) { //if last open day of week is NOT Friday
				if(dates[i].getDayOfWeek().toString().equals("THURSDAY")) {
					
				}
				if(dates[i].getDayOfWeek().toString().equals("WEDNESDAY")) {
					
				}
				if(dates[i].getDayOfWeek().toString().equals("TUESDAY")) {
					
				}
				if(dates[i].getDayOfWeek().toString().equals("MONDAY")) {
					closedMarketDays = 4;
				}
			}
			
			
			//******************************************************************************************************************************
//			if(i + 4 >= prices.length) { 
//				break;
//			}
//			firstDayOfWeekClose = prices[i+4]; 
//			weeklyDifference = lastDayOfWeekClose - firstDayOfWeekClose; 
//			weeklyMovement = (weeklyDifference / firstDayOfWeekClose) * 100; 
//			allWeeklyMovement.add(weeklyMovement); 
			
			
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
			lastDayOfWeekClose = prices[i]; //get Friday close price... which will be at the offset index
			if(i + 4 >= prices.length) {
				break;
			}
			firstDayOfWeekClose = prices[i+4]; //get Monday close price... which will be 4 indices ahead of Friday index
			weeklyDifference = lastDayOfWeekClose - firstDayOfWeekClose; //difference between Friday and Monday close
			weeklyMovement = (weeklyDifference / firstDayOfWeekClose) * 100; //the movement amount in a percentage
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
			lastDayOfWeekClose = prices[i]; //get Friday close price... which will be at the offset index
			if(i + 4 >= prices.length) {
				break;
			}
			firstDayOfWeekClose = prices[i+4]; //get Monday close price... which will be 4 indices ahead of Friday index
			weeklyDifference = lastDayOfWeekClose - firstDayOfWeekClose; //difference between Friday and Monday close
			weeklyMovement = (weeklyDifference / firstDayOfWeekClose) * 100; //the movement amount in a percentage
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
			lastDayOfWeekClose = prices[i]; //get Friday close price... which will be at the offset index
			if(i + 4 >= prices.length) {
				break;
			}
			firstDayOfWeekClose = prices[i+4]; //get Monday close price... which will be 4 indices ahead of Friday index
			weeklyDifference = lastDayOfWeekClose - firstDayOfWeekClose; //difference between Friday and Monday close
			weeklyMovement = (weeklyDifference / firstDayOfWeekClose) * 100; //the movement amount in a percentage
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
			lastDayOfWeekClose = prices[i]; //get Friday close price... which will be at the offset index
			if(i + 4 >= prices.length) {
				break;
			}
			firstDayOfWeekClose = prices[i+4]; //get Monday close price... which will be 4 indices ahead of Friday index
			weeklyDifference = lastDayOfWeekClose - firstDayOfWeekClose; //difference between Friday and Monday close
			weeklyMovement = (weeklyDifference / firstDayOfWeekClose) * 100; //the movement amount in a percentage
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
			lastDayOfWeekClose = prices[i]; //get Friday close price... which will be at the offset index
			if(i + 4 >= prices.length) {
				break;
			}
			firstDayOfWeekClose = prices[i+4]; //get Monday close price... which will be 4 indices ahead of Friday index
			weeklyDifference = lastDayOfWeekClose - firstDayOfWeekClose; //difference between Friday and Monday close
			weeklyMovement = (weeklyDifference / firstDayOfWeekClose) * 100; //the movement amount in a percentage
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
		ArrayList<Double> weeklyMovementArray = getWeeklyMovementArray(ticker, weeks);
		
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
	 * @throws ParseException 
	 */
	public double getFailureRateWithinThreshold(String ticker, int weeks, double threshold) throws IOException, ParseException { 
		threshold = Math.abs(threshold);
		
		double failureRate = 0;
		
		int amountExceeded = 0;
		
		ArrayList<Double> weeklyMovementArray = getWeeklyMovementArray(ticker, weeks);
		
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
	 * @throws ParseException 
	 */
	public double getSuccessRateWithinThreshold(String ticker, int weeks, double threshold) throws IOException, ParseException { 
		threshold = Math.abs(threshold);
		
		double successRate = 0;

		successRate = 100 - getFailureRateWithinThreshold(ticker, weeks, threshold);
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
	 * @throws ParseException 
	 */
	public double getPositiveMovementSuccessRateWithinThreshold(String ticker, int weeks, double threshold) throws IOException, ParseException {
		if(threshold == 0) {
			System.out.println("Please enter a non-zero threshold percentage");
			
			System.exit(0);
		}
		else if(threshold < 0) {
			System.out.println("You entered a negative threshold so it will be converted to a positive one");
			threshold *= -1;
		}
		
		double positiveSuccessRate = 0;
	
		ArrayList<Double> weeklyMovement = getWeeklyMovementArray(ticker, weeks);
		
		int amountNotExceeded = 0;
		
		int amountOfPositiveMovements = 0;
		
		for(int i = 0; i < weeklyMovement.size(); i++) {
			if(weeklyMovement.get(i) >= 0) { //is it okay to include 0 as a positive #? how likely is it that 0% movement will occur in a week?
				amountOfPositiveMovements++;
				if(weeklyMovement.get(i) <= threshold) {
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
	
	//create method for positive movement failureRate (staying within +X% / +N)
	public double getPositiveMovementFailureRateWithinThreshold(String ticker, int weeks, double threshold) throws IOException, ParseException {
		double positiveFailureRate = 0;
		
		positiveFailureRate = 100 - getPositiveMovementSuccessRateWithinThreshold(ticker, weeks, threshold);
		positiveFailureRate = Double.parseDouble(df.format(positiveFailureRate));
		
		return positiveFailureRate;
	}
	
	
	//create method for negative movement successRate (staying within -X% / -N)
	public double getNegativeMovementSuccessRateWithinThreshold(String ticker, int weeks, double threshold) throws IOException, ParseException{
		if(threshold == 0) {
			System.out.println("Please enter a non-zero threshold percentage");
			
			System.exit(0);
		}
		else if(threshold > 0) {
			System.out.println("You entered a positive threshold so it will be converted to a negative one");
			threshold *= -1;
		}
		
		double negativeSuccessRate = 0;
		
		ArrayList<Double> weeklyMovement = getWeeklyMovementArray(ticker, weeks);
		
		int amountNotExceeded = 0;
		
		int amountOfNegativeMovements = 0;
		
		for(int i = 0; i < weeklyMovement.size(); i++) {
			if(weeklyMovement.get(i) <= 0) { //is it okay to include 0 as a negative #? how likely is it that 0% movement will occur in a week?
				amountOfNegativeMovements++;
				if(weeklyMovement.get(i) >= threshold) { //>= since amount will not be exceeded if it's between 0 and -X% threshold
					amountNotExceeded++;
				}
			}
		}
		
		if(amountOfNegativeMovements == 0) {
			System.out.println("Sorry, there were no negative movements in the past " + weeks + " weeks for " + ticker + ".\n");
			System.exit(0);
		}
		
		negativeSuccessRate = (double) amountNotExceeded / amountOfNegativeMovements;
		negativeSuccessRate *= 100;
		negativeSuccessRate = Double.parseDouble(df.format(negativeSuccessRate));
		
		
		return negativeSuccessRate;
	}
	
	
	//create method for negative movement failureRate (staying within -X% / -N)
	public double getNegativeMovementFailureRateWithinThreshold(String ticker, int weeks, double threshold) throws IOException, ParseException{
		double negativeFailureRate = 0;
		
		negativeFailureRate = 100 - getNegativeMovementSuccessRateWithinThreshold(ticker, weeks, threshold);
		negativeFailureRate = Double.parseDouble(df.format(negativeFailureRate));
		
		
		return negativeFailureRate;
	}
	
	
	/*
	 * Will need to create positive/negative success/failure rate for Exceeding threshold? 6 new methods.
	 */
	
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
	
	
	//implement a standard deviation method
//	public double getStandardDeviation(String ticker, int weeks) {
//		
//	}

}
