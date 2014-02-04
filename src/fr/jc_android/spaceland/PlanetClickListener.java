package fr.jc_android.spaceland;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class PlanetClickListener implements OnClickListener {
	MainActivity mAct;
	Planet mPlanet;
	public PlanetClickListener(MainActivity mainActivity, Planet p) {
		mAct = mainActivity;
		mPlanet = p;
	}

	@Override
	public void onClick(View v) {
		Log.i("[PlanetClickListener]","click on "+mPlanet.getName());
		Long[] l = mAct.mPlayer.getLocation();
		l[3] = mPlanet.getID();
		mAct.mPlayer.setLocation(new Long[]{l[3],l[2],l[1],l[0]});
		mAct.loadLayout(R.layout.ingame);
	}

}
