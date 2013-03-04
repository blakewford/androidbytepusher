package org.starlo.bytepusher;

import org.starlo.bytepusher.R;

import coder36.BytePusherVM;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.app.Activity;

public class BytePusherActivity extends Activity implements OnClickListener {
	
	private int mCurrentRomIndex = 0;
	private BytePusherUIHelper mUIHelper;
	private AndroidBytePusherIODriverImpl mDriver;
	private AndroidBytePusherRuntimeAbstractionImpl mAbstraction; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_layout);
		mUIHelper = new BytePusherUIHelper();
		setup();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		mAbstraction.onPause();
	}
	
	@Override
	public void onClick(View arg0) {
		String[] roms = mAbstraction.getRoms();
		mCurrentRomIndex = ++mCurrentRomIndex < roms.length-1 ? mCurrentRomIndex: 0;
		try{
			System.gc();
			mAbstraction.loadRom(roms[mCurrentRomIndex]);
		}catch (OutOfMemoryError e){
			mCurrentRomIndex = mCurrentRomIndex > 0 ? mCurrentRomIndex--: roms.length-1;
			Toast.makeText(this, R.string.out_of_memory, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void setup() {
		SurfaceView view = (SurfaceView) findViewById(R.id.display);
		mDriver = new AndroidBytePusherIODriverImpl(view);
		mAbstraction = new AndroidBytePusherRuntimeAbstractionImpl(this, new BytePusherVM(mDriver));
		mUIHelper.setUpVm(mAbstraction);
		view.setOnClickListener(this);
		final int[] keyIds = 
			new int[]{
				R.id.key_0, R.id.key_1, R.id.key_2, R.id.key_3,
				R.id.key_4, R.id.key_5, R.id.key_6, R.id.key_7,
				R.id.key_8, R.id.key_9, R.id.key_A, R.id.key_B,
				R.id.key_C, R.id.key_D, R.id.key_E, R.id.key_F
		};
		
		for(int id: keyIds)
			findViewById(id).setOnTouchListener(mDriver);
	}
}
