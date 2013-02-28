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
import android.widget.Toast;
import android.app.Activity;
import android.content.res.AssetManager;

public class BytePusherActivity extends Activity implements OnClickListener {

	public final static int CLOCK_FREQUENCY = 60;
	public final static String ROMS_ASSETS_FOLDER = "roms";
	
	private BytePusherVM mVirtualMachine;
	private AndroidBytePusherIODriverImpl mIODriver;

	private boolean mPausedFlag;
	private ClockTask mClockTask;
	private FrameRate mFrameRate = new FrameRate();
	private int mCurrentRomIndex = 0;
	private AssetManager mAssetManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_layout);
		mAssetManager = getAssets();
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
		try{
			System.gc();
			loadRom(roms[mCurrentRomIndex]);
		}catch (OutOfMemoryError e){
			mCurrentRomIndex = mCurrentRomIndex > 0 ? mCurrentRomIndex--: roms.length-1;
			Toast.makeText(this, R.string.out_of_memory, Toast.LENGTH_SHORT).show();
		}
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
		
		final int[] keyIds = 
			new int[]{
				R.id.key_0, R.id.key_1, R.id.key_2, R.id.key_3,
				R.id.key_4, R.id.key_5, R.id.key_6, R.id.key_7,
				R.id.key_8, R.id.key_9, R.id.key_A, R.id.key_B,
				R.id.key_C, R.id.key_D, R.id.key_E, R.id.key_F
		};
		
		for(int id: keyIds)
			findViewById(id).setOnTouchListener(mIODriver);
	}
	
	private void loadRom( String rom ) {
		try {
			InputStream fis = mAssetManager.open( "roms/"+rom );
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
			return mAssetManager.list(ROMS_ASSETS_FOLDER);
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
