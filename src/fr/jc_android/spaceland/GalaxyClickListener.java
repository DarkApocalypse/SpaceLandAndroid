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
		Long[] l = mAct.mPlayer.getLocation();
		l[1] = mGalaxy.getID();
		mAct.mPlayer.setLocation(new Long[]{l[3],l[2],l[1],l[0]});
		mAct.loadLayout(R.layout.ingame);
	}

}
