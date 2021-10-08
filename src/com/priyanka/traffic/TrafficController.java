package com.priyanka.traffic;
import java.util.ArrayList;

public class TrafficController extends Thread {	
	int carEnterRate, leftEnterRate, exitLeftRate, exitStraightRate, currentExitLeftRate, currentExitStraightRate, direction;
	int left=0;
	int straight=0;
	double satrate=0; //saturation rate for each direction for left and straight	
	/* The entry rates and exit rates are non integers; however, the program cannot create a fraction of a 
	 * car. Therefore, the program continuously adds up the fractional part of the entry and exit 
	 * rates. When the value becomes greater than 1, the program creates a car and subtracts 1 from the 
	 * sum. */	
	double carEnterRateFrac=0; 
	//the fractional part of the arrival rate of cars going straight for particular direction
	double leftEnterRateFrac=0; 
	//the fractional part of the arrival rate of cars going left for a particular direction
	double exitLeftRateFrac=0; 
	//the fractional part of the exit rate of cars turning left for a particular direction
	double currentExitLeftRateFrac=0; 
	//the SUM of the fractional parts of the left exit rate for a particular direction(should be less than 1)
	double exitStraightRateFrac=0;
	//the fractional part of the exit rate of cars turning straight for a particular direction
	double currentExitStraightRateFrac=0;
	//the SUM of the fractional parts of the straight exit rate for a particular direction (less than 1)	
	volatile ArrayList<Car> cars = new ArrayList<Car>();
	//an array list stores the arrival time and exit time for every car
	volatile ArrayList<Car> carsWaitingLeft = new ArrayList<Car>();
	//an array list stores data about the cars waiting to turn left
	volatile ArrayList<Car> carsWaitingStraight = new ArrayList<Car>();
	//an array list stores data about the cars waiting to go straight
	double[] delayArray;
	//an array list stores the delay for ever car
	Car car;
	
	public TrafficController (double leftEnterRate, double carEnterRate, double exitLeftRate, double exitStraightRate, int direction) {
		this.satrate = ((double)TrafficSignal.props.satrate)/3600;
		exitLeftRate = exitLeftRate * this.satrate;
		exitStraightRate = exitStraightRate * this.satrate;
		/* the standard saturation rate of 1900 vehicles that pass through per lane per hour of green 
		 * is divided by 3600 to get the vehicles that pass through per second. This value is then 
		 * multiplied by the number of lanes for left or straight to get the actual saturation rate. */		
		this.carEnterRate = (int) (carEnterRate/1); 
		//the carEnterRate variable is reassigned the value of the integer part of the straight enter rate
		this.carEnterRateFrac = carEnterRate%1;
		//the carEnterRateFrac variable is reassigned the value of the fractional part of the straight enter rate
		this.leftEnterRate = (int) (leftEnterRate/1);
		//the leftEnterRate variable is reassigned the value of the integer part of the left enter rate
		this.leftEnterRateFrac = leftEnterRate%1;
		//the leftEnterRateFrac variable is reassigned the value of the fractional part of the left enter rate
		this.exitLeftRateFrac= exitLeftRate%1;
		//the exitLeftRateFrac variable is reassigned the value of the fractional part of the left exit rate
		this.exitStraightRateFrac = exitStraightRate%1;
		//the exitLeftRateFrac variable is reassigned the value of the fractional part of the straight exit rate
		this.exitLeftRate = (int) (exitLeftRate/1);
		//the exitLeftRate variable is reassigned the value of the integer part of the left exit rate
		this.exitStraightRate = (int) (exitStraightRate/1);
		//the exitStraightRate variable is reassigned the value of the integer part of the straight exit rate
		this.direction = direction;
		TrafficSignal.print(3,"Cars from " +Constants.direction[direction] + " SatRate - " + this.satrate + " St Rate - " + exitStraightRate + " Left Rate - " + exitLeftRate );
	}
	
	
	public void run() {
		//TrafficSignal.print(3,"Cars " + Constants.direction[direction] + ":Left - "+ exitLeftRate + "  :Straight - "+ exitStraightRate);
		long startTime = System.nanoTime(); 
		long timeStamp; 
		int timesSlept=0;
		int totalTimesSlept=0;
		int timesRun = 0;
		int overSleep = 0;
		long sleepTime = 0;
		int currentEnterRate=0;
		int currentLeftEnterRate=0;
		double currentEnterRateFrac=0;
		double currentLeftEnterRateFrac=0;
		while (Constants.runTraffic) {
			currentEnterRateFrac += carEnterRateFrac;
			//keep adding up the fractional part of the straight entry rate
			currentEnterRate = carEnterRate + (int)(currentEnterRateFrac/1);
			currentEnterRateFrac = currentEnterRateFrac%1;
			//if the sum of the fractional part is more than 1, subtract 1 and combine it with the integer part of the straight entry rate
			currentLeftEnterRateFrac += leftEnterRateFrac;
			//keep adding up the fractional part of the left entry rate
			currentLeftEnterRate = leftEnterRate + (int)(currentLeftEnterRateFrac/1);
			currentLeftEnterRateFrac = currentLeftEnterRateFrac%1;
			//if the sum of the fractional part is more than 1, subtract 1 and combine it with the intefer part of the straight entry rate
			timeStamp = System.nanoTime();
			//time stamp when the new car is created
			
			for (int i=0; i<currentEnterRate; i++) {
				car = new Car();
				car.arrivalTime = timeStamp;
				car.direction = direction;
				car.exitDirection = Constants.STRAIGHT;	
				straight++;
				synchronized (carsWaitingStraight) {
					carsWaitingStraight.add(car);
				} /* Create a car that goes straight at the pace of the straight entry rate
				     Give it an arrival time by time stamping it and give it a direction it came from
				     Add the car to the array list of the cars that are waiting to go straight */
			}
			
			for (int i=0; i<currentLeftEnterRate; i++) {
				car = new Car();
				car.arrivalTime = timeStamp;
				car.direction = direction;
				car.exitDirection = Constants.LEFT;
				left++;
				synchronized (carsWaitingLeft) {
					carsWaitingLeft.add(car);
				}/* Create a car that goes left at the pace of the left entry rate
			     Give it an arrival time by time stamping it and give it a direction it came from
			     Add the car to the array list of the cars that are waiting to go left */
			}
			while (timesSlept <= timesRun) {
				try {
					sleepTime = Constants.SLEEPTIME;
					if (sleepTime < 1000000) {
						Thread.sleep(0,(int)sleepTime);
						
					}else {
						Thread.sleep(sleepTime/1000000, (int) sleepTime%1000000);
					}
					timesSlept = (int)( ( System.nanoTime() - startTime)/Constants.timeMultiplier);
					totalTimesSlept++;
					if ((timesSlept > 0) && (timesSlept <= timesRun)) overSleep++;
				} catch (Exception e) {TrafficSignal.print(3,"Exception in traffic controller Thread");}
			} 
			timesRun++;
			/* If the program sleeps less than 999,999 nanoseconds, do not add to timesRun but let the program sleep again */	
		}
		TrafficSignal.print(3,"Delay Time for "+ cars.size() + " cars from " +Constants.direction[direction] + " is - Total Avg: " + Math.round(calculateDelay()*100/100) + " - Left: "+ Math.round(calculateDelayLeft()*100)/100 + " - Straight: "+ Math.round(calculateDelayStraight()*100)/100 + ", Standard Deviation is "+ calculateSD(delayArray));
		TrafficSignal.print(3,"[Terminating Thread]: TrafficController - "+Constants.direction[direction] + ", Ran for " + timesSlept + ", Cars Created " + timesRun +  ", Slept for " + totalTimesSlept + "s, Overslept " + overSleep + " times");		
	}
	
	double calculateDelay() {
		double delayTime = 0;
		delayArray = new double[cars.size()];
		int size = cars.size();
		for (int i=0; i<size;i++)	{
			delayTime += cars.get(i).delay;
			delayArray[i] = cars.get(i).delay;
			if (Constants.runCompleted) TrafficSignal.print(5, ""+cars.get(i).delay);
	
		}		
		return (delayTime)/size;
	} //delay times for each car from the array list are added up and divided by the number of cars to find the average delay
	
	double calculateDelayLeft() {
		double delayTime = 0;
		delayArray = new double[cars.size()];
		int size = cars.size();
		int count = 0;
		for (int i=0; i<size;i++)	{
			if (cars.get(i).exitDirection == Constants.LEFT) {
				delayTime += cars.get(i).delay;
				count++;
			}
			delayArray[i] = cars.get(i).delay;
			TrafficSignal.print(5, ""+cars.get(i).delay);
			
		}		
		return (delayTime)/count;
	} //calculates average delay for cars turning left
	
	double calculateDelayStraight() {
		double delayTime = 0;
		delayArray = new double[cars.size()];
		int size = cars.size();
		int count = 0;
		for (int i=0; i<size;i++)	{
			if (cars.get(i).exitDirection == Constants.STRAIGHT) {
				delayTime += cars.get(i).delay;
				count++;
			}
			delayArray[i] = cars.get(i).delay;
			TrafficSignal.print(5, ""+cars.get(i).delay);
		}		
		return (delayTime)/count;
	}//calculates average delay for cars going straight
	
	public  double calculateSD(double numArray[])
    {
        double sum = 0.0, standardDeviation = 0.0;
        int length = numArray.length;
        int count10=0, count20=0, count30=0, count40=0, count_10=0, count_20=0, count_30=0, count_40=0;
        for(double num : numArray) {
            sum += num;
        }
        double mean = sum/length;
        for(double num: numArray) {
            standardDeviation += Math.pow(num - mean, 2);
            
            if ((num-mean) < -3*mean/4) {
            	count_40++;               	          
            }else if ((num-mean) < -2*mean/4) {
            	count_30++;            	
            }else if ((num-mean) < -mean/4) {
            	count_20++;   
            }else if ((num-mean) < 0) {
            	count_10++;            	
            }else if ((num-mean) > 3*mean/4) {
            	count40++;            	
            }else if ((num-mean) > 2*mean/4) {
            	count30++;            	
            }else if ((num-mean) > mean/4) {
            	count20++;            	
            }else if ((num-mean) > 0) {
            	count10++;            	
            }
        } //calculates standard deviation from the average delay
		TrafficSignal.print(3,"Counts < -"+Math.round(3*mean/4)+" = "+count_40+", < -"+Math.round(2*mean/4)+" = "+count_30+", < -"+Math.round(mean/4)+" = "+count_20+", < 0 = "+count_10+", > 0 = "+count10+", > "+Math.round(mean/4)+" = "+count20+", > "+Math.round(2*mean/4)+" = "+count30+", > "+Math.round(3*mean/4)+" = "+count40);
        return Math.sqrt(standardDeviation/length);
    }

}
