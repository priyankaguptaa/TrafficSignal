package com.priyanka.traffic;
import java.util.ArrayList;

public class ClearWaitingCars extends Thread {
	
	TrafficController tc1, tc2, tc3, tc4;
	// 4 traffic controllers labeled tc1, tc2, tc3, and tc4, are referenced, one for each direction 
	Signal signal1, signal2, signal3, signal4;
	// 4 signals labeled signal1, signal2, signal3, and signal4, are referenced, one for each direction 
	long timeStamp, tc1LeftCleared, tc1StraightCleared, tc2LeftCleared, tc2StraightCleared, tc3LeftCleared, tc3StraightCleared, tc4LeftCleared, tc4StraightCleared = 0; 
	/* 8 variables that represent the number of vehicles cleared left and straight for each direction are
	created. The values of the variables start at 0, and every time a car is cleared, the value increases
	by one. (logic is in the method run() below)*/
	int timesSlept = 0;
	int overSleep = 0;
	int underSleep = 0;

	public ClearWaitingCars (TrafficController tc1, TrafficController tc2, TrafficController tc3, TrafficController tc4,
							Signal signal1, Signal signal2, Signal signal3, Signal signal4) {
		this.tc1 = tc1;
		this.tc2 = tc2;
		this.tc3 = tc3;
		this.tc4 = tc4;
		this.signal1 = signal1;
		this.signal2 = signal2;
		this.signal3 = signal3;
		this.signal4 = signal4;
	}
	
	void adjustCurrentLeftExitRate(TrafficController tc) {
		tc.currentExitLeftRateFrac += tc.exitLeftRateFrac;
		tc.currentExitLeftRate = tc.exitLeftRate + (int)(tc.currentExitLeftRateFrac/1);
		tc.currentExitLeftRateFrac = tc.currentExitLeftRateFrac%1;
	}
	
	void adjustCurrentStraightExitRate(TrafficController tc) {
		tc.currentExitStraightRateFrac += tc.exitStraightRateFrac;
		tc.currentExitStraightRate = tc.exitStraightRate + (int)(tc.currentExitStraightRateFrac/1);
		tc.currentExitStraightRateFrac = tc.currentExitStraightRateFrac%1;
	}

	public void run() {
		long startTime; 
		long pairTime = System.nanoTime();
		int timeSlept = 0;
		long sleepTime = 0;
		timeStamp = pairTime;
		Car car;
		while (Constants.runTraffic) {
			startTime = System.nanoTime();
			ArrayList<Car> cars;			
			if ((signal1.state & 0x000110) > 0) { 		// straight signal is green/yellow
				cars = tc1.carsWaitingStraight;
				adjustCurrentStraightExitRate(tc1);
				synchronized (cars) {
					for (int i=0; i<tc1.currentExitStraightRate; i++) {
						if ((cars.size() > 0 ) && (cars.get(0) !=null)) {
							car = cars.get(0);
							car.departureTime = startTime;
							car.delay = (startTime - car.arrivalTime)/Constants.timeMultiplier;
							tc1.cars.add(car);
							cars.remove(car);
							tc1StraightCleared++;
						}
					}
				}
			} /* If the straight signal is green or yellow, find the delay time of the first element in the array of cars waiting to go straight and 
			divide by the time multiplier. add the car to the traffic controller array list and remove the car from the cars striahgt waiting list.
			repeat until all cars have been cleared */
			
			if ((signal2.state & 0x000110) > 0) { 		// straight signal is green/yellow
				cars = tc2.carsWaitingStraight;
				adjustCurrentStraightExitRate(tc2);
				synchronized (cars) {
					for (int i=0; i< tc2.currentExitStraightRate; i++) {
						if ((cars.size() > 0) && (cars.get(0) != null)) {
							car = cars.get(0);
							car.departureTime = startTime;
							car.delay = (startTime - car.arrivalTime)/Constants.timeMultiplier;
							tc2.cars.add(car);
							cars.remove(car);
							tc2StraightCleared++;
						}
					}
				}
			}/* If the straight signal is green or yellow, find the delay time of the first element in the array of cars waiting to go straight and 
			divide by the time multiplier. add the car to the traffic controller 2 array list and remove the car from the cars straight waiting list.
			repeat until all cars have been cleared */				

			if ((signal3.state & 0x000110) > 0) { 		// straight signal is green/yellow
				cars = tc3.carsWaitingStraight;
				adjustCurrentStraightExitRate(tc3);
				synchronized (cars) {
					for (int i=0; i<tc3.currentExitStraightRate; i++) {
						if ((cars.size() > 0 ) && (cars.get(0) !=null)) {
							car = cars.get(0);
							car.departureTime = startTime;
							car.delay = (startTime - car.arrivalTime)/Constants.timeMultiplier;
							tc3.cars.add(car);
							cars.remove(car);
							tc3StraightCleared++;
						}
					}
				}
			}/* If the straight signal is green or yellow, find the delay time of the first element in the array of cars waiting to go straight and 
			divide by the time multiplier. add the car to the traffic controller 3 array list and remove the car from the cars straight waiting list.
			repeat until all cars have been cleared */
			
			if ((signal4.state & 0x000110) > 0) { 		// straight signal is green/yellow
				cars = tc4.carsWaitingStraight;
				adjustCurrentStraightExitRate(tc4);
				synchronized (cars) {
					for (int i=0; i< tc4.currentExitStraightRate; i++) {
						if ((cars.size() > 0) && (cars.get(0) != null)) {
							car = cars.get(0);
							car.departureTime = startTime;
							car.delay = (startTime - car.arrivalTime)/Constants.timeMultiplier;
							tc4.cars.add(car);
							cars.remove(car);
							tc4StraightCleared++;
						}
					}
				}
			}/* If the straight signal is green or yellow, find the delay time of the first element in the array of cars waiting to go straight and 
			divide by the time multiplier. add the car to the traffic controller 4 array list and remove the car from the cars straight waiting list.
			repeat until all cars have been cleared */
			
			
			if ((signal1.state & 0x110000) > 0) { 		// left signal is green/yellow		// left signal is green/yellow
				cars = tc1.carsWaitingLeft;
				adjustCurrentLeftExitRate(tc1);
				synchronized (cars) {
					for (int i=0; i<tc1.currentExitLeftRate; i++) {
						if ((cars.size() > 0) && (cars.get(0) != null)) {
							car = cars.get(0);
							car.departureTime = startTime;
							car.delay = (startTime - car.arrivalTime)/Constants.timeMultiplier;
							tc1.cars.add(car);
							cars.remove(car);
							tc1LeftCleared++;
						}
					}
				}
			}/* If the left signal is green or yellow, find the delay time of the first element in the array of cars waiting to go left and 
			divide by the time multiplier. add the car to the traffic controller 1 array list and remove the car from the cars left waiting list.
			repeat until all cars have been cleared */
			
			if ((signal2.state & 0x110000) > 0) { 		// left signal is green/yellow		// left signal is green/yellow
				cars = tc2.carsWaitingLeft;
				adjustCurrentLeftExitRate(tc2);
				synchronized (cars) {
					for (int i=0; i<tc2.currentExitLeftRate; i++) {
						if ((cars.size() > 0) && (cars.get(0) != null)) {
							car = cars.get(0);
							car.departureTime = startTime;
							car.delay = (startTime - car.arrivalTime)/Constants.timeMultiplier;
							tc2.cars.add(car);
							cars.remove(car);
							tc2LeftCleared++;
						}
					}
				}
			}/* If the left signal is green or yellow, find the delay time of the first element in the array of cars waiting to go left and 
			divide by the time multiplier. add the car to the traffic controller 2 array list and remove the car from the cars left waiting list.
			repeat until all cars have been cleared */

			if ((signal3.state & 0x110000) > 0) { 		// left signal is green/yellow
				cars = tc3.carsWaitingLeft;
				adjustCurrentLeftExitRate(tc3);
				synchronized (cars) {
					for (int i=0; i<tc3.currentExitLeftRate; i++) {
						if ((cars.size() > 0) && (cars.get(0) != null)) {
							car = cars.get(0);
							car.departureTime = startTime;
							car.delay = (startTime - car.arrivalTime)/Constants.timeMultiplier;
							tc3.cars.add(car);
							cars.remove(car);
							tc3LeftCleared++;
						}
					}
				}
			}/* If the left signal is green or yellow, find the delay time of the first element in the array of cars waiting to go left and 
			divide by the time multiplier. add the car to the traffic controller 3 array list and remove the car from the cars left waiting list.
			repeat until all cars have been cleared */
			
			if ((signal4.state & 0x110000) > 0) { 		// left signal is green/yellow
				cars = tc4.carsWaitingLeft;
				adjustCurrentLeftExitRate(tc4);
				synchronized (cars) {
					for (int i=0; i<tc4.currentExitLeftRate; i++) {
						if ((cars.size() > 0) && (cars.get(0) != null)) {
							car = cars.get(0);
							car.departureTime = startTime;
							car.delay = (startTime - car.arrivalTime)/Constants.timeMultiplier;
							tc4.cars.add(car);
							cars.remove(car);
							tc4LeftCleared++;
						}
					}
				}
			}/* If the left signal is green or yellow, find the delay time of the first element in the array of cars waiting to go left and 
			divide by the time multiplier. add the car to the traffic controller 4 array list and remove the car from the cars left waiting list.
			repeat until all cars have been cleared */
			
			while (timeSlept <= timesSlept) {
				try {
					sleepTime = Constants.SLEEPTIME;
					if (sleepTime < 1000000) {
						Thread.sleep(0,(int)sleepTime);
						
					}else {
						Thread.sleep(sleepTime/1000000, (int) sleepTime%1000000);
					}
					underSleep++;
					timeSlept = (int)(( System.nanoTime() - timeStamp)/Constants.timeMultiplier);
					if ( (timesSlept > 0 ) && (timeSlept <= timesSlept) ) overSleep++;
				} catch (Exception e) {TrafficSignal.print(3,"Exception in Clear Queue Thread");}
			}
			timesSlept++;
		}
		pairTime  = System.nanoTime() - pairTime;
		TrafficSignal.print(3,"Clearing Thread Ran for " + pairTime/Constants.timeMultiplier + " seconds, Slept " + timesSlept + " times");
		TrafficSignal.print(3,"Cars Cleared: " + Constants.direction[tc1.direction] + ":Total - "+ (tc1LeftCleared + tc1StraightCleared) + "   :Left - "+ tc1LeftCleared + "  :Straight - "+ tc1StraightCleared);
		TrafficSignal.print(3,"Cars Still Waiting: " + Constants.direction[tc1.direction] + ":Left - "+ tc1.carsWaitingLeft.size() + "  :Straight - "+ tc1.carsWaitingStraight.size());
		TrafficSignal.print(3,"Cars " + Constants.direction[tc1.direction] + ":Total - "+ (tc1.left +tc1.straight) + "   :Left - "+ tc1.left + "  :Straight - "+ tc1.straight);
		TrafficSignal.print(3,"Cars Cleared: " + Constants.direction[tc2.direction] +  ":Total - "+ (tc2LeftCleared + tc2StraightCleared) + "   :Left - "+ tc2LeftCleared + "  :Straight - "+ tc2StraightCleared);
		TrafficSignal.print(3,"Cars Still Waiting: " + Constants.direction[tc2.direction] + ":Left - "+ tc2.carsWaitingLeft.size() + "  :Straight - "+ tc2.carsWaitingStraight.size());
		TrafficSignal.print(3,"Cars " + Constants.direction[tc2.direction] + ":Total - "+ (tc2.left +tc2.straight) + "   :Left - "+ tc2.left + "  :Straight - "+ tc2.straight);
		TrafficSignal.print(3,"Cars Cleared: " + Constants.direction[tc3.direction] +  ":Total - "+ (tc3LeftCleared + tc3StraightCleared) + "   :Left - "+ tc3LeftCleared + "  :Straight - "+ tc3StraightCleared);
		TrafficSignal.print(3,"Cars Still Waiting: " + Constants.direction[tc3.direction] + ":Left - "+ tc3.carsWaitingLeft.size() + "  :Straight - "+ tc3.carsWaitingStraight.size());
		TrafficSignal.print(3,"Cars " + Constants.direction[tc3.direction] + ":Total - "+ (tc3.left +tc3.straight) + "   :Left - "+ tc3.left + "  :Straight - "+ tc3.straight);
		TrafficSignal.print(3,"Cars Cleared: " + Constants.direction[tc4.direction] +  ":Total - "+ (tc4LeftCleared + tc4StraightCleared) + "   :Left - "+ tc4LeftCleared + "  :Straight - "+ tc4StraightCleared);
		TrafficSignal.print(3,"Cars Still Waiting: " + Constants.direction[tc4.direction] + ":Left - "+ tc4.carsWaitingLeft.size() + "  :Straight - "+ tc4.carsWaitingStraight.size());
		TrafficSignal.print(3,"Cars " + Constants.direction[tc4.direction] + ":Total - "+ (tc4.left +tc4.straight) + "   :Left - "+ tc4.left + "  :Straight - "+ tc4.straight);
		TrafficSignal.print(3,"[Terminating Thread]: ClearWaitingCar");
	}		
}
