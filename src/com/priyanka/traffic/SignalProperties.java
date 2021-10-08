package com.priyanka.traffic;

public class SignalProperties {
	double straightgreentime, straightyellowtime, leftgreentime, leftyellowtime, redtime, enterstraight, enterleft;
	int exitstraight, exitleft;
	
	public SignalProperties copy() { 
		SignalProperties tmp = new SignalProperties() ;
		tmp.straightgreentime = this.straightgreentime;
		tmp.straightyellowtime = this.straightyellowtime;
		tmp.leftgreentime = this.leftgreentime;
		tmp.leftyellowtime = this.leftyellowtime;
		tmp.redtime = this.redtime;
		tmp.enterstraight = this.enterstraight;
		tmp.enterleft = this.enterleft;
		tmp.exitstraight = this.exitstraight;
		tmp.exitleft = this.exitleft;
		return tmp;				
	} 
	

}



/* each direction has a straight green time, straight yellow time, left green time, left yellow time, red time, straight entry rate, left entry rate, straight exit
rate, and left exit rate*/