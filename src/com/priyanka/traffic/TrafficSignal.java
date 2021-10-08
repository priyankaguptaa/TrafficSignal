/**
 *
 */
package com.priyanka.traffic;

import java.util.Properties;
import java.io.FileInputStream;
/**
 * @author gupta
 */
public class TrafficSignal {
	
	public static com.priyanka.traffic.Properties props = new com.priyanka.traffic.Properties();
	//instantiates Properties class
	public static int debugLevel = 3;
	public static int totalCycles = 0;
	
	public static void main(String[] args) {
		adjustConstants(); 
		//runs adjustConstants method (below)//
		RunManager rm = new RunManager();
		//instantiates RunManager class
		//rm.runOnce();
		rm.varyingEntryAndGreen(1);
		//case 1 is varying entry rate and green time factor
		//case 2 is varying stop time and green time factor
	}
	
	public static long threadLatency()
	{
	  long tempTime = 0;
	  long startTime = 0;
	  startTime = System.nanoTime();
	  for (int i=0; i<1000; i++) {
		  try {
			  Thread.sleep(0,999999);
		  } catch (Exception e) {TrafficSignal.print(3,"Exception in latency Thread");}	  }

	  tempTime = System.nanoTime() - startTime;
	  return Math.round(tempTime/1000);
	}
	/*The function tells the thread to sleep for 999,999 nanoseconds after every traffic cycle. The function then. Because the
	 computer takes time to carry out the command to sleep, the thread sleeps more than 999,999 nanoseconds. The purpose of this
	 thread is to calculate how long the thread slept for. */
	
	public static long nanoLatency()
	{
	  long elapsed = 0;
	  long tempTime = 0;
	  long startTime = 0;
	  for (int i=0; i<1000; i++) {
		  startTime = System.nanoTime();
		  tempTime = System.nanoTime() - startTime;
		  elapsed += tempTime;
	  }	  
	  return Math.round(elapsed/1000);
	} //calculates elapsed time
	
	public static void adjustConstants() {
		//props.props.list(System.out);
		TrafficSignal.print(1, "Starting simulation for signal  : "+ props.signalName);
		Constants.timeLatency = nanoLatency(); //nanoLatency method is called (above)
		TrafficSignal.print(3,"Time Latency is  : "+ Constants.timeLatency);
		long overHead  = threadLatency() - 999999;
		Constants.TIMEUNIT = TrafficSignal.props.timeunit;
		Constants.timeMultiplier = overHead + (1000000)*Constants.TIMEUNIT;
		Constants.SLEEPTIME = Constants.SLEEPTIME * Constants.TIMEUNIT;
		TrafficSignal.print(1, "Time Multiplier is  : "+ Constants.timeMultiplier);
		totalCycles =  TrafficSignal.props.numcycles;
	}
	
	/*timeLatency refers to the time it takes for a computer to carry out a function. In this program,
	the program sleeps one millisecond in program time (5 miliseconds in program time is 1 second because
	of the timeunit) every time a cycle is run, but it also takes another small period of  time, called overHead, to carry
	out the command to sleep. */
	
	public static void print(int level, String msg) {
		if (level <= debugLevel) System.out.println(msg);
	}
}
