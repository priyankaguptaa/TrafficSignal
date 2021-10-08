package com.priyanka.traffic;
import java.io.FileInputStream;

public class Properties {
	
	int timeunit, numcycles, satrate;
	SignalProperties north = new SignalProperties();
	SignalProperties south = new SignalProperties();
	SignalProperties east = new SignalProperties();
	SignalProperties west = new SignalProperties();	
	SignalProperties orgnorth, orgsouth, orgeast, orgwest;
	java.util.Properties props = new java.util.Properties();
	String signalName;
	//instantiates Properties class
	
	
	public Properties() {
		String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		String appConfigPath = rootPath + "config.properties";
		try {
			props.load(new FileInputStream(appConfigPath));
			readProperties();
		} catch (Exception e) {TrafficSignal.print(3,"Exception reading file: " + e);}		
	}// this method tells the program to read the configuration properties from a different file
	
	
	void readProperties() {
		timeunit = getPropertyInt("timeunit");
		numcycles = getPropertyInt("numcycles");
		satrate = getPropertyInt("satrate");
		signalName = props.getProperty("signalname");
		north.enterleft = getPropertyDouble("north.enterleft");
		north.enterstraight = getPropertyDouble("north.enterstraight");
		north.leftgreentime = getPropertyDouble("north.leftgreentime");
		north.leftyellowtime = getPropertyDouble("north.leftyellowtime");
		north.redtime = getPropertyDouble("north.redtime");
		north.straightgreentime = getPropertyDouble("north.straightgreentime");
		north.straightyellowtime = getPropertyDouble("north.straightyellowtime");
		north.exitleft = getPropertyInt("north.exitleft");
		north.exitstraight = getPropertyInt("north.exitstraight");

		south.enterleft = getPropertyDouble("south.enterleft");
		south.enterstraight = getPropertyDouble("south.enterstraight");
		south.leftgreentime = getPropertyDouble("south.leftgreentime");
		south.leftyellowtime = getPropertyDouble("south.leftyellowtime");
		south.redtime = getPropertyDouble("south.redtime");
		south.straightgreentime = getPropertyDouble("south.straightgreentime");
		south.straightyellowtime = getPropertyDouble("south.straightyellowtime");
		south.exitleft = getPropertyInt("south.exitleft");
		south.exitstraight = getPropertyInt("south.exitstraight");
		

		east.enterleft = getPropertyDouble("east.enterleft");
		east.enterstraight = getPropertyDouble("east.enterstraight");
		east.leftgreentime = getPropertyDouble("east.leftgreentime");
		east.leftyellowtime = getPropertyDouble("east.leftyellowtime");
		east.redtime = getPropertyDouble("east.redtime");
		east.straightgreentime = getPropertyDouble("east.straightgreentime");
		east.straightyellowtime = getPropertyDouble("east.straightyellowtime");
		east.exitleft = getPropertyInt("east.exitleft");
		east.exitstraight = getPropertyInt("east.exitstraight");
		

		west.enterleft = getPropertyDouble("west.enterleft");
		west.enterstraight = getPropertyDouble("west.enterstraight");
		west.leftgreentime = getPropertyDouble("west.leftgreentime");
		west.leftyellowtime = getPropertyDouble("west.leftyellowtime");
		west.redtime = getPropertyDouble("west.redtime");
		west.straightgreentime = getPropertyDouble("west.straightgreentime");
		west.straightyellowtime = getPropertyDouble("west.straightyellowtime");
		west.exitleft = getPropertyInt("west.exitleft");
		west.exitstraight = getPropertyInt("west.exitstraight");

		orgnorth = north.copy();
		orgsouth = south.copy();
		orgeast = east.copy();
		orgwest = west.copy();
			//instantiates class Signal Properties for each direction				
	}//the properties from the configuration properties file are read and the values from the file are assigned to these variables
	
	
	int getPropertyInt(String str) {
		return Integer.parseInt(props.getProperty(str)) ;		
	}
	
	double getPropertyDouble(String str) {
		return Double.parseDouble(props.getProperty(str)) ;		
	}
	

}
