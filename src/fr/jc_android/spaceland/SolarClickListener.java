package fr.jc_android.spaceland;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class SolarClickListener implements OnClickListener {
	Solar mSolar;
	MainActivity mAct;
	public SolarClickListener(MainActivity mainActivity, Solar s) {
		mSolar=s;
		mAct=mainActivity;
	}

	@Override
	public void onClick(View v) {
		Log.i("[GalaxyClickListener]", "Click on "+mSolar.getName());
		Long[] l = mAct.mPlayer.getLocation();
		l[2] = mSolar.getID();
		mAct.mPlayer.setLocation(new Long[]{l[3],l[2],l[1],l[0]});
		mAct.loadLayout(R.layout.ingame);
	}

}
