/**
 * 
 */
package com.priyanka.traffic;
import java.util.ArrayList;

public class SignalPair {
	Signal signal1,signal2;
	int numStates = 0;
	int currentState = 0;
	TrafficController tc1, tc2;
	volatile boolean activeState = false;
	ArrayList<SignalPairState> states = new ArrayList<SignalPairState>();
	long signalTime = 0;
	long[] path1Times, path2Times;
	int[] s1Path1States = {0x100000, 0x010000, 0x001000, 0x001000, 0x001000, 0x001000}; // signal 1 left
	int[] s2Path1States = {0x000001, 0x000001, 0x000001, 0x000100, 0x000010, 0x000001}; //signal 2 straight
	int[] s1Path2States = {0x000100, 0x000010, 0x000001, 0x000001, 0x000001, 0x000001}; //signal 1 straight
	int[] s2Path2States = {0x001000, 0x001000, 0x001000, 0x100000, 0x010000, 0x001000 }; //signal 2 left
	
	public SignalPair (Signal signal1, Signal signal2, TrafficController tc1, TrafficController tc2) {
		this.signal1 = signal1;
		this.signal2 = signal2;			
		this.tc1 = tc1;
		this.tc2 = tc2;
		setupSignal();
	} //signal pairs are north/south and east/west so within each of those pairs, there is a signal1 and a signal2
	
	
	public void setupSignal() {
		fixSignalTime();
		long[] arr1 = {signal1.leftGreenTime,signal1.leftYellowTime,signal1.redTime,signal2.greenTime,signal2.yellowTime,signal2.redTime};
		path1Times = arr1.clone(); //path 1 is signal 1 left + signal 2 straight
		long[] arr2 = {signal1.greenTime,signal1.yellowTime,signal1.redTime,signal2.leftGreenTime,signal2.leftYellowTime,signal2.redTime};
		path2Times = arr2.clone(); //path 2 is signal 1 straight + signal 2 left
		defineStates();
		numStates = states.size()-1;
		TrafficSignal.print(3,"Reset and Starting Traffic controllers "+Constants.direction[tc1.direction]+" & "+Constants.direction[tc2.direction]);		
	}//path 1 and path 2 are adjusted to be the same time in the method fixSignalTime
	 //path 1 and path 2 add up to be a Signal Pair, signal 1 and signal 2 also add up to be a Signal Pair but they have different parts
	
	
	void fixSignalTime() {
		long totalTime1 = signal2.leftGreenTime + signal2.leftYellowTime + signal1.greenTime + signal1.yellowTime; //total green time of path 1
		long totalTime2 = signal1.leftGreenTime + signal1.leftYellowTime + signal2.greenTime + signal2.yellowTime; //total green time of path 2
		if (totalTime1 > totalTime2 ) {
			signal2.greenTime = (totalTime1 - totalTime2)/2 + signal2.greenTime;
			signal1.leftGreenTime = ((totalTime1-totalTime2) - (totalTime1 - totalTime2)/2) + signal1.leftGreenTime;
					
		}else if (totalTime2 > totalTime1 ) {
			signal1.greenTime = signal1.greenTime + (totalTime2 - totalTime1)/2;
			signal2.leftGreenTime = signal2.leftGreenTime + ((totalTime2-totalTime1) - (totalTime2 - totalTime1)/2);

		} 
	} /* if one path has a greater time than the other, add extra time to the shorter path (time is split equally between left and straight) to make 
	  the signal paths even */

	void defineStates() {
		long totalTime1, totalTime2 , stateTime , totalStateTime;
		totalTime1 = totalTime2 = stateTime = totalStateTime = 0;
		for (int i=0; i< path1Times.length;i++) {  //CHECK to see that both paths end at same time
			totalTime1 += path1Times[i];
			totalTime2 += path2Times[i];
		}
		if (totalTime1 == totalTime2) TrafficSignal.print(3,"Total Signal Time " + ((double)totalTime1) / Constants.timeMultiplier + " secs"); else 
								TrafficSignal.print(1,"ERROR: Signal Times are not same " +  totalTime1 / Constants.timeMultiplier + ":" +  totalTime2 / Constants.timeMultiplier); 
		totalTime1 = totalTime2 = 0;		
		for (int i=0; i < path1Times.length; i++) {
			totalTime1 += path1Times[i]; 
			//a value (red, green, or yellow light time) is taken from the array list that stores the cycle times of the paths 
			for (int j=0; j< path2Times.length; j++) {
				totalTime2 += path2Times[j];
				while (totalTime1 <= totalTime2) {
					stateTime = totalTime1 - totalStateTime;
					totalStateTime += stateTime;
					if (stateTime > 0) {
						states.add(new SignalPairState(signal1,signal2,s1Path1States[i] | s1Path2States[j], s2Path1States[i] | s2Path2States[j],stateTime));
						TrafficSignal.print(3,"State " + states.size() + " : " + ((double)stateTime)/Constants.timeMultiplier + "s, Direction:" +  Constants.direction[signal1.direction] + " - "+ signal1.signalStateString(s1Path1States[i] | s1Path2States[j]) 
						+ " || Direction:" +  Constants.direction[signal2.direction] + " - "+ signal2.signalStateString(s2Path1States[i] | s2Path2States[j]));
					}
					//if path 2 has gone through more time than path 1 (because states are longer, etc.), add another path 1 state from the array list 
					if (i== (path1Times.length-1)) break; else totalTime1 += path1Times[++i];		 
					//repeat if needed
				}				
				stateTime = totalTime2 - totalStateTime;
				totalStateTime += stateTime;				
				if (stateTime > 0) {
					states.add(new SignalPairState(signal1,signal2,s1Path1States[i] | s1Path2States[j], s2Path1States[i] | s2Path2States[j],stateTime));
					TrafficSignal.print(3,"State " + states.size() + " : " + stateTime/Constants.timeMultiplier + "s, Direction:" +  Constants.direction[signal1.direction] + " - "+ signal1.signalStateString(s1Path1States[i] | s1Path2States[j]) 
					+ " || Direction:" +  Constants.direction[signal2.direction] + " - "+ signal2.signalStateString(s2Path1States[i] | s2Path2States[j]));
				}
				//if path 1 has gone through more time than path 1 (because states are longer, etc.), add another path 2 state from the array list to the total time 2
			}
			if (i== (path1Times.length-1)) break;
		}			
	}
	
	void startThreads() {
		this.tc1.start();
		this.tc2.start();		
	}
	
	long changeState() {
		SignalPairState state = states.get(currentState);
		long stateTime = state.stateTime;
		state.signal1.state = state.s1State;
		state.signal2.state = state.s2State;
		activeState = (currentState == (numStates)) ? false : true;
		signalTime = (currentState == numStates) ? 0 : signalTime + stateTime;
		currentState = (currentState == numStates) ? 0 : currentState+1;
		//after every state, a signal pair runs this method to either switch to the next signal pair or switch to the next state in the array list
		//when a signal pair (north/south or east/west) is finished with all of the 9 states, switch to the next pair
		//if the signal pair is not done with all of the 9 states, switch to the next state in the array list
		return stateTime;
		//return the amount of time in which the signal was in a particular state (there are 9 state times total)
	}
	
}
