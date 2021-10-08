package com.priyanka.traffic;

public class RunManager {
	com.priyanka.traffic.Properties props = TrafficSignal.props;
	double[] arr1, arr2;
	double[] varyEntryFactor = {0.1, 0.5, 0.7, 0.9, 1, 1.2, 1.4, 1.6};
	double[] varyGreenTimeFactor = {0.1, 0.5, 0.7, 0.9, 1, 1.2, 1.4, 1.6};
	double[] varyRedTime = {0, 1, 2, 3, 4, 5};	
	String str1, str2;
	
	public void runOnce() {
		TrafficSignal.print(1,"Average Delay: " + singlerun());
	}
	
	public void varyingEntryAndGreen (int type) {
		TrafficSignal.debugLevel = 1;
		int runNumber = 1;
		double delay;	
		switch (type) {
		case 1:
			arr1 = varyGreenTimeFactor;
			arr2 = varyEntryFactor;
			break;
		
		case 2:
			arr1 = varyRedTime;
			arr2 = varyGreenTimeFactor;
			break;
		}
		for (int i=0; i<arr1.length; i++) {
			for (int j=0; j<arr2.length; j++) {
				switch (type) {
				case 1:
					changeEntryAndGreen (arr1[i], arr2[j]);
					break;
				case 2: 
					changeGreenAndRed (arr1[i], arr2[j]);
					break;
				}
				delay = singlerun();
				TrafficSignal.print(1,"Run Number " + runNumber + str1 + str2 +  ", Delay: " + delay);
				runNumber++;
			}
		}
	}
	
	public void changeGreenAndRed (double redTime, double greenTimeFactor) {
		props.north.redtime = redTime;
		props.south.redtime = redTime;
		props.east.redtime = redTime;
		props.west.redtime = redTime;		
		props.north.straightgreentime = props.orgnorth.straightgreentime * greenTimeFactor;
		props.north.leftgreentime = props.orgnorth.leftgreentime * greenTimeFactor;	
		props.south.straightgreentime = props.orgsouth.straightgreentime * greenTimeFactor;
		props.south.leftgreentime = props.orgsouth.leftgreentime * greenTimeFactor;
		props.east.straightgreentime = props.orgeast.straightgreentime * greenTimeFactor;
		props.east.leftgreentime = props.orgeast.leftgreentime * greenTimeFactor;
		props.west.straightgreentime = props.orgwest.straightgreentime * greenTimeFactor;
		props.west.leftgreentime = props.orgwest.leftgreentime * greenTimeFactor;
		
		double totalGreenTime = props.north.straightgreentime + props.north.leftgreentime + props.south.straightgreentime + props.south.leftgreentime + 
				props.east.straightgreentime + props.east.leftgreentime + props.west.straightgreentime + props.west.leftgreentime;
		str1 = ", Total Green Time: " + totalGreenTime + ", Red Time: " + redTime;
		str2 = ", Green Time Factor: " + greenTimeFactor;
		
	}
	
	public void changeEntryAndGreen(double greenTimeFactor, double entryFactor) {
		props.north.straightgreentime = props.orgnorth.straightgreentime * greenTimeFactor;
		props.north.leftgreentime = props.orgnorth.leftgreentime * greenTimeFactor;	
		props.south.straightgreentime = props.orgsouth.straightgreentime * greenTimeFactor;
		props.south.leftgreentime = props.orgsouth.leftgreentime * greenTimeFactor;
		props.east.straightgreentime = props.orgeast.straightgreentime * greenTimeFactor;
		props.east.leftgreentime = props.orgeast.leftgreentime * greenTimeFactor;
		props.west.straightgreentime = props.orgwest.straightgreentime * greenTimeFactor;
		props.west.leftgreentime = props.orgwest.leftgreentime * greenTimeFactor;
		
		props.north.enterstraight = props.orgnorth.enterstraight * entryFactor;
		props.north.enterleft = props.orgnorth.enterleft * entryFactor;
		props.south.enterstraight = props.orgsouth.enterstraight * entryFactor;
		props.south.enterleft = props.orgsouth.enterleft * entryFactor;
		props.east.enterstraight = props.orgeast.enterstraight * entryFactor;
		props.east.enterleft = props.orgeast.enterleft * entryFactor;
		props.west.enterstraight = props.orgwest.enterstraight * entryFactor;
		props.west.enterleft = props.orgwest.enterleft * entryFactor;
			
		double totalGreenTime = props.north.straightgreentime + props.north.leftgreentime + props.south.straightgreentime + props.south.leftgreentime + 
				props.east.straightgreentime + props.east.leftgreentime + props.west.straightgreentime + props.west.leftgreentime;
		str1 = ", Total Green Time: " + totalGreenTime + ", Green Time Factor: " + greenTimeFactor;
		str2 = ", Entry Rate Factor: " + entryFactor;
	}
		
	public double singlerun() {
		Constants.runTraffic = true;
		Constants.runCompleted = false;
		SignalController sc = new SignalController(); 
		// instantiates SignalController
		sc.start();
		while (Constants.runTraffic) { // wait till all cycles completed
			try {
				Thread.sleep(100);
			} catch (Exception e) {TrafficSignal.print(3,"Exception in Single Run");}
		}
		try {
			Thread.sleep(5000); // wait for all threads from previous cycles to finish
		} catch (Exception e) {TrafficSignal.print(3,"Exception in Single Run");}
		Constants.runCompleted = true;
		double delay = calculateAvgDelay(sc); 
		//calculates average delay using method calculateAvgDelay (below)
		return delay;
		
	}
	
	double calculateAvgDelay(SignalController sc) {
		double len1 = sc.northTC.cars.size();
		double len2 = sc.southTC.cars.size();
		double len3 = sc.eastTC.cars.size();
		double len4 = sc.westTC.cars.size();
		return  (sc.northTC.calculateDelay()*len1 + sc.southTC.calculateDelay()*len2 + sc.eastTC.calculateDelay()*len3 + sc.westTC.calculateDelay()*len4
				)/(len1+len2+len3+len4);
		/*calculates average delay using calculateDelay method from TrafficController class (add up delays in array list and divided by total
		number of cars in the array list (size)*/		
	}
	

	void ratios() {
		double totalGreenTime; //out of the entire cycle, time when at least one direction is green
		double northSouthRatio1;
		double northSouthRatio2;
		double eastWestRatio1;
		double eastWestRatio2;
		double northSouthRatio;
		double eastWestRatio;
		double northSouthGreenTime;
		double eastWestGreenTime;
		
		totalGreenTime = props.north.straightgreentime + props.north.leftgreentime + props.south.straightgreentime + props.south.leftgreentime + 
						props.east.straightgreentime + props.east.leftgreentime + props.west.straightgreentime + props.west.leftgreentime;
		//calculates total green time
		northSouthRatio1 = props.north.enterstraight/props.north.exitstraight + props.south.enterleft/props.south.exitleft;
		//within a signal pair, the amount of combined time that north straight and south left should get
		northSouthRatio2 = props.south.enterstraight/props.south.exitstraight + props.north.enterleft/props.north.exitleft;
		//within a signal pair, the amount of combined time that north left and south straight should get
		eastWestRatio1 = props.east.enterstraight/props.east.exitstraight + props.west.enterleft/props.west.exitleft;
		//within a signal pair, the amount of combined time that east straight and west left should get
		eastWestRatio2 = props.west.enterstraight/props.west.exitstraight + props.east.enterleft/props.east.exitleft;
		//within a signal pair, the amount of combined time that east left and west straight should get
		
		northSouthRatio = Math.max(northSouthRatio1, northSouthRatio2);
		//the ratio with the higher green time automatically becomes the ratio for northSouthRatio1 and northSouthRatio2 (so ratios are the same)
		eastWestRatio = Math.max(eastWestRatio1,  eastWestRatio2);
		//the ratio with the higher green time automatically becomes the ratio for eastWestRatio1 and eastWestRatio2 (so ratios are the same)
		
		northSouthGreenTime = totalGreenTime*(northSouthRatio/(northSouthRatio + eastWestRatio));
		/* calculates the green light time that the northSouthRatio1 and northSouthRatio2 should get out of the total green time (green time should be 
		the same for both ratios because the ratios are the same due to previous logic) */
		eastWestGreenTime = totalGreenTime - northSouthGreenTime;
		/* calculates the ratio of green light time that the eastWestRatio1 and eastWestRatio2 should get out of the total green time (green time should be 
		the same for both ratios because the ratios are the same due to previous logic) */
		
		props.north.straightgreentime = northSouthGreenTime*((props.north.enterstraight/props.north.exitstraight)/northSouthRatio1);
		/* calculates the amount of green light time the north straight direction should get out of the combined north straight and south left 
		 ratio based on entry/exit rates */
		props.south.leftgreentime = northSouthGreenTime-props.north.straightgreentime;
		/* calculates the amount of green light time the south left direction should get out of the combined north straight and south left ratio
		based on entry and exit rates */
		props.north.leftgreentime = northSouthGreenTime*((props.north.enterleft/props.north.exitleft)/northSouthRatio2);
		/* calculates the amount of green light time the north left direction should get out of the combined north left and south straight ratio
		based on entry and exit rates */
		props.south.straightgreentime = northSouthGreenTime - props.north.leftgreentime;
		/* calculates the amount of green light time the south straight direction should get out of the combined north left and south straight ratio
		based on entry and exit rates */
		 
		props.east.straightgreentime = eastWestGreenTime*((props.east.enterstraight/props.east.exitstraight)/eastWestRatio1);
		props.west.leftgreentime = eastWestGreenTime - props.east.straightgreentime;
		props.west.straightgreentime = eastWestGreenTime*((props.west.enterstraight/props.west.exitstraight)/eastWestRatio2);
		props.east.leftgreentime = eastWestGreenTime - props.west.straightgreentime;
		//same as north/south pair
	}
	
}
