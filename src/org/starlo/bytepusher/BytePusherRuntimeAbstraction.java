package org.starlo.bytepusher;

import java.util.TimerTask;

//Try to push to Mark's library
public interface BytePusherRuntimeAbstraction {

	public final static int CLOCK_FREQUENCY = 60;
	
	TimerTask getFrameTask();	  
	String [] getRoms();
	void loadRom(String rom);
}
