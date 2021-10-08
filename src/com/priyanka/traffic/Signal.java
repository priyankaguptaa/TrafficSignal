/**
 * 
 */
package com.priyanka.traffic;

/**
 * @author gupta
 *
 */
public class Signal {
	double speedlimit;
	int state = 0x001001; // Left - GYR   Straight GYR  -- GYRGYR
	int direction;
	long greenTime, yellowTime, leftGreenTime, leftYellowTime, redTime;
	//every signal has these values
	
	public Signal (double greenTime, double yellowTime, double leftGreenTime, double leftYellowTime, double redTime, int direction) {
		this.greenTime = (int)(greenTime*Constants.timeMultiplier);
		this.yellowTime  = (int)(yellowTime*Constants.timeMultiplier);
		this.leftGreenTime = (int)(leftGreenTime*Constants.timeMultiplier);
		this.leftYellowTime = (int)(leftYellowTime*Constants.timeMultiplier);
		this.redTime = (int)(redTime*Constants.timeMultiplier);
		this.direction = direction;
	}//multiplies times by time multiplier to convert milliseconds to real seconds
	
	String signalStateString(long tempState) {
		String temp = "Left [GYR] = ";
		String tState = "";
		int status = (int)(tempState & 0x111000);
		switch (status) {		 
			case 0x100000 : tState = "G";
							break;
			case 0x010000 : tState = "Y";
							break;
			case 0x001000 : tState = "R";
							break;		
		}//for the left turning exit direction, the light will either be red, green, or yellow depending on the cases above
		temp += tState + " ||| Straight [GYR] = ";
		status = (int)(tempState & 0x000111);
		switch (status) {		 
			case 0x000100 : tState = "G";
							break;
			case 0x000010 : tState = "Y";
							break;
			case 0x000001 : tState = "R";
							break;		
		}//for the straight exit direction, the light will either be red, green, or yellow depending on the cases above
		temp += tState;
		return temp;
		
	}//there are 9 different states that a signal pair can be in 	
	
	String signalStateString() {
		return signalStateString(state);
	}

}
