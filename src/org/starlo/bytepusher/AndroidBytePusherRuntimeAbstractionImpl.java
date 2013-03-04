package org.starlo.bytepusher;

import java.io.IOException;
import java.io.InputStream;
import java.util.TimerTask;

import coder36.BytePusherVM;
import coder36.FrameRate;
import android.content.Context;
import android.content.res.AssetManager;

public class AndroidBytePusherRuntimeAbstractionImpl implements BytePusherRuntimeAbstraction {

	public final static String ROMS_ASSETS_FOLDER = "roms";
	
	private TimerTask mTask;
	private AssetManager mAssetManager;
	private FrameRate mFrameRate;
	private BytePusherVM mVirtualMachine;
	private boolean mPausedFlag;
	
	AndroidBytePusherRuntimeAbstractionImpl(Context context, BytePusherVM virtualMachine){
		mTask = new AndroidFrameTask();
		mAssetManager = context.getAssets();
		mFrameRate = new FrameRate();
		mVirtualMachine = virtualMachine;
		mPausedFlag = false;
	}
	
	@Override
	public TimerTask getFrameTask(){
		return mTask;
	}

	@Override
	public String[] getRoms() {
		try {
			return mAssetManager.list(ROMS_ASSETS_FOLDER);
		} catch (IOException e) {
			return null;
		}
	}
	
	@Override
	public void loadRom(String rom) {
		try {
			InputStream stream = mAssetManager.open(ROMS_ASSETS_FOLDER+"/"+rom);
			mVirtualMachine.load(stream);
			stream.close();
		}catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void onPause(){
		mPausedFlag = true;
	}
	
	private class AndroidFrameTask extends TimerTask {
		public void run() {
			mFrameRate.update("gpu");
			if ( !mPausedFlag ) 
				mVirtualMachine.run();		
		}
	}
}
