package fr.jc_android.spaceland;

import android.app.Application;
import android.util.Log;

public class App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("[spaceland.App]","onCreate");
	}

}
