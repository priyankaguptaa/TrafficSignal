package com.priyanka.traffic;

public class SignalPairState {
	Signal signal1, signal2;
	int s1State = 0x000000;
	int s2State = 0x00000;
	long stateTime = 0;	
	//variables are created but not instantiated
	
	public SignalPairState (Signal signal1, Signal signal2, int s1State, int s2State, long stateTime) {
		this.signal1 = signal1;
		this.signal2 = signal2;	
		this.s1State = s1State;
		this.s2State = s2State;
		this.stateTime = stateTime;
	}

}
