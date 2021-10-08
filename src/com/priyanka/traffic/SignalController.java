/**
 * 
 */
package com.priyanka.traffic;

import java.util.ArrayList;
import java.util.Properties;

public class SignalController extends Thread {
	volatile Signal north, south, east, west;
	SignalPair NorthSouth, EastWest, currentPair;
	com.priyanka.traffic.Properties props = TrafficSignal.props;
	volatile TrafficController northTC, southTC, eastTC, westTC;
	ClearWaitingCars cwc;
	ArrayList<Double> cycleTimes = new ArrayList<Double>();	
	/*Creates 4 signals (north, south, east, west), 2 signal pairs (north/south and east/west), and 
	 4 traffic controller threads (north, south, east, west). 
	 Defines variable "cwc"
	 Instantiates an ArrayList named cycleTimes that stores the cycle times of the various directions*/	
	long numCycles = 0;
	long pairTime, prevPairTime;
	
	public SignalController() {
		north = new Signal (props.north.straightgreentime,props.north.straightyellowtime, props.north.leftgreentime,props.north.leftyellowtime,
				props.north.redtime, Constants.NORTH);
		south = new Signal (props.south.straightgreentime,props.south.straightyellowtime, props.south.leftgreentime,props.south.leftyellowtime,
				props.south.redtime, Constants.SOUTH);
		east = new Signal (props.east.straightgreentime,props.east.straightyellowtime, props.east.leftgreentime,props.east.leftyellowtime,
				props.east.redtime, Constants.EAST);
		west = new Signal (props.west.straightgreentime,props.west.straightyellowtime, props.west.leftgreentime,props.west.leftyellowtime,
				props.west.redtime, Constants.WEST);		
	/* instantiates signals for the 4 directions and assigns it timing values from the Properties class */		
		northTC = new TrafficController(props.north.enterleft, props.north.enterstraight, props.north.exitleft,props.north.exitstraight,Constants.NORTH);
		southTC = new TrafficController(props.south.enterleft, props.south.enterstraight, props.south.exitleft,props.south.exitstraight,Constants.SOUTH);
		eastTC = new TrafficController(props.east.enterleft,props.east.enterstraight, props.east.exitleft,props.east.exitstraight,Constants.EAST);
		westTC = new TrafficController(props.west.enterleft, props.west.enterstraight, props.west.exitleft,props.west.exitstraight,Constants.WEST);
	/* instantiates traffic controllers for the 4 directions and assigns it entry and exit rates from the Properties class 
	 see TrafficController class for what the traffic controllers do*/
		NorthSouth = new SignalPair(north,south, northTC, southTC);
		EastWest = new SignalPair(east,west, eastTC, westTC);			
	/* instantiates signal pairs of north/south and east/west
	see SignalPair class for what the signal pairs do */		
		cwc = new ClearWaitingCars(northTC,southTC,eastTC,westTC,north,south,east,west);		
	/*instantiates the class ClearWaitingCars class 
	see ClearWaitingCars class to see what it does */		
		currentPair = NorthSouth;		
	}
	
	public void run() {
		pairTime = System.nanoTime();
		long runTime = pairTime;
		NorthSouth.startThreads();
		EastWest.startThreads();
		cwc.start();
		long startTime, elapsed;
		long stateTime = 0;
		while (Constants.runTraffic) {
			startTime = System.nanoTime();
			stateTime = currentPair.changeState();
			try {
				do {
				    elapsed = System.nanoTime() + Constants.timeLatency - startTime;
				} while (elapsed < stateTime);

			} catch (Exception e) {TrafficSignal.print(3,"Exception in signal controller Thread");}

			if (!currentPair.activeState) changePair();
				
		}
		TrafficSignal.print(3,"Average Cycle Time = " + getCycleTimeAvg() + "s for #Cycle "+ cycleTimes.size());
		TrafficSignal.print(3,"[Terminating Thread]: SignalController. Ran for " + ((System.nanoTime() - runTime)/Constants.timeMultiplier));
	}
	
	SignalPair changePair () {
		numCycles++;
		prevPairTime = pairTime;
		pairTime = System.nanoTime();
		cycleTimes.add (((double)((pairTime-prevPairTime))/Constants.timeMultiplier));
		TrafficSignal.print(3,"Last Pair ran for: "+ (pairTime-prevPairTime)/Constants.timeMultiplier +" secs, Cycles = "
		+ Math.round(numCycles/2) + ", Ran = " + cwc.timesSlept + " times" + ", OverSlept = " + cwc.overSleep + " times" + ", UnderSlept = " + (cwc.timesSlept - cwc.underSleep) + " times");
		cwc.timesSlept = 0;
		cwc.overSleep = 0;
		cwc.underSleep = 0;
		cwc.timeStamp = System.nanoTime();
		if (Math.round(numCycles/2) == TrafficSignal.totalCycles) Constants.runTraffic = false;
		currentPair  = (currentPair.equals(NorthSouth)) ? EastWest : NorthSouth;
		return currentPair;
	}
	
	double getCycleTimeAvg() {
		double avg = 0;
		for (int i=0; i<cycleTimes.size();i++) {
			avg+= cycleTimes.get(i).doubleValue();
		}
		return 2*avg/cycleTimes.size();
	}
	
}
