package fr.jc_android.spacelandAlt;

import fr.jc_android.gameframework.AbstractAndroidGame;
import fr.jc_android.gameframework.Screen;

public class MainActivity extends AbstractAndroidGame {

	@Override
	public Screen getInitScreen() {
		//TODO: integrer getResources().openRawResource(id)
		return new LoadingScreen(this);
	}

}
