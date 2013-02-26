package org.starlo.bytepusher;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import org.starlo.bytepusher.R;

import coder36.BytePusherVM;
import coder36.FrameRate;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.Activity;

public class BytePusherActivity extends Activity implements OnClickListener {

	public final static int CLOCK_FREQUENCY = 60;
	public final static String ROMS_ASSETS_FOLDER = "roms";
	
	private BytePusherVM mVirtualMachine;
	private AndroidBytePusherIODriverImpl mIODriver;

	private boolean mPausedFlag;
	private ClockTask mClockTask;
	private FrameRate mFrameRate = new FrameRate();
	private int mCurrentRomIndex = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_layout);
		setUpVm();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		mClockTask.cancel();
	}
	
	@Override
	public void onClick(View arg0) {
		String[] roms = getRoms();
		mCurrentRomIndex = ++mCurrentRomIndex < roms.length-1 ? mCurrentRomIndex: 0;
		loadRom(roms[mCurrentRomIndex]);
	}
	
	//Mostly code from the desktop version
	private void setUpVm() {
		SurfaceView view = (SurfaceView) findViewById(R.id.display);
		view.setOnClickListener(this);
		// set up bytepusher vm
		mIODriver = new AndroidBytePusherIODriverImpl(view);
		mVirtualMachine = new BytePusherVM( mIODriver );
				
		// load first rom
		String [] l = getRoms();
		if (l != null && l.length != 0 ) {
			loadRom( l[0] );
		}
		
		// startup vm
		setFrequency(CLOCK_FREQUENCY);			
	}
	
	private void loadRom( String rom ) {
		try {
			InputStream fis = getAssets().open( "roms/"+rom );
			mVirtualMachine.load( fis );
			fis.close();
		}
		catch( IOException e ) {
			throw new RuntimeException( e );
		}
	}
	
	private void setFrequency( int f ) {
		// cancel any previous tasks
		if ( mClockTask != null ) mClockTask.cancel();
		// create new task
		mClockTask = new ClockTask();
		new Timer().schedule(mClockTask, 0, 1000/CLOCK_FREQUENCY);
	}
	
	private String [] getRoms() {
		try {
			return getAssets().list(ROMS_ASSETS_FOLDER);
		} catch (IOException e) {
			return null;
		}
	}
    
	private class ClockTask extends TimerTask {
		public void run() {
			mFrameRate.update("gpu");
			if ( !mPausedFlag ) 
				mVirtualMachine.run();		
		}
	
	}
}
