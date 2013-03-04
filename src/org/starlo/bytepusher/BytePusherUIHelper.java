package org.starlo.bytepusher;

import java.util.Timer;
import java.util.TimerTask;
import org.starlo.bytepusher.BytePusherRuntimeAbstraction;

//Try to push to Mark's library
public class BytePusherUIHelper {
	
	public void setUpVm(BytePusherRuntimeAbstraction abstraction) {				
		// load first rom
		String [] l = abstraction.getRoms();
		if (l != null && l.length != 0 ) {
			abstraction.loadRom(l[0]);
		}
		// startup vm
		setFrameTask(abstraction.getFrameTask());
	}
	
	private void setFrameTask(TimerTask task) {
		new Timer().schedule(task, 0, 1000/BytePusherRuntimeAbstraction.CLOCK_FREQUENCY);
	}
}
