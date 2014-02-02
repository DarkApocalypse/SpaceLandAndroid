package fr.jc_android.spaceland;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class GalaxyClickListener implements OnClickListener {
	MainActivity mAct;
	Galaxy mGalaxy;
	public GalaxyClickListener(MainActivity mainActivity, Galaxy g) {
		mGalaxy = g;
		mAct = mainActivity;
	}

	@Override
	public void onClick(View v) {
		Log.i("[GalaxyClickListener]", "Click on "+mGalaxy.getName());
	}

}
