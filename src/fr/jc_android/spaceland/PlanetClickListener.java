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
		//TODO: load planet
	}

}
