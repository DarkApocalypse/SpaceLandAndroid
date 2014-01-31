package fr.jc_android.spaceland;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import fr.jc_android.spaceland.Universe.UniverseParameters;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnTouchListener, OnClickListener, OnItemSelectedListener, OnItemClickListener {
	protected int mCurrentLayout = 0;
	protected Menu mMenu;
	protected int selectedPos;
	protected Thread thread;
	protected boolean isCreating;
	protected Universe mUniverse;
	protected Galaxy mGalaxy;
	protected Solar mSolar;
	protected Planet mPlanet;
	protected Player mPlayer;
	protected String mPath;
	protected UniverseParameters mUp = new UniverseParameters();
	protected enum InGameMode{
		UNIVERSE,
		GALAXY,
		SOLAR,
		PLANET
	}
	protected class ToastByThread implements Runnable{
		protected String mText;
		protected int mDuration;
		public ToastByThread(String text, int duration){
			mText = text;
			mDuration = duration;
		}
		@Override
		public void run(){
			Toast.makeText(MainActivity.this, mText, mDuration).show();
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		if(thread==null)
			loadLayout(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		if(mMenu==null){
			getMenuInflater().inflate(R.menu.main, menu);
			mMenu = menu;
		}
		switch(mCurrentLayout){
			case R.layout.activity_main:{
				menu.findItem(R.id.MenuSave).setVisible(false);
				menu.findItem(R.id.MenuSaveSettings).setVisible(false);
			}break;
			case R.layout.creategame:{
				menu.findItem(R.id.MenuSave).setVisible(true);
				menu.findItem(R.id.MenuSaveSettings).setVisible(true);
			}break;
			case R.layout.loadgame:{
				menu.findItem(R.id.MenuSave).setVisible(true);
			}break;
			default:{
				menu.findItem(R.id.MenuSave).setVisible(false);
				menu.findItem(R.id.MenuSaveSettings).setVisible(false);
			}break;
		}
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()){
			case R.id.MenuSave:{
				Log.i("[ACTIVITY]", "Saving...");
				Log.i("[ACTIVITY]", "Saved!");
			}break;
			case R.id.MenuQuit:{
				Log.i("[ACTIVITY]", "Quit...");
				this.finish();
			}break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.i("[ACTITIVY]","onConfigurationChanged");
		super.onConfigurationChanged(newConfig);
		return;
	}

	//LoadListener
	protected void loadLayout(int layout){
		Button b;
		mCurrentLayout = layout;
		setContentView(layout);
		switch(layout){
			case R.layout.activity_main:{
				b = (Button)findViewById(R.id.createGame);
				b.setOnClickListener(this);
				if(isCreating)
					b.setEnabled(true);
				b = (Button)findViewById(R.id.LoadGame);
				b.setOnClickListener(this);
				b = (Button)findViewById(R.id.Settings);
				b.setOnClickListener(this);
			}break;
			case R.layout.creategame:{
				b = (Button)findViewById(R.id.createGame_back);
				b.setOnClickListener(this);
				b = (Button)findViewById(R.id.bp_createGameRun);
				b.setOnClickListener(this);
			}break;
			case R.layout.loadgame:{
				b = (Button)findViewById(R.id.loadGame_back);
				b.setOnClickListener(this);
				b = (Button)findViewById(R.id.bp_loadGameRun);
				b.setOnClickListener(this);
				b.setEnabled(false);
				b = (Button)findViewById(R.id.bp_loadDelete);
				b.setOnClickListener(this);
				b.setEnabled(false);
				File saveDir = new File(getPath()+"/saves/");
				if(saveDir.exists()){
					ListView lv = (ListView)findViewById(R.id.listGames);
					String[] files = saveDir.list();
					ArrayList<String> saves = new ArrayList<String>();
					Log.i("[LOAD GAME]","List of save:");
					for(int i =0;i<files.length;i++){
						if(!files[i].startsWith("save_"))
							continue;
						String save = files[i].substring(files[i].lastIndexOf('/')+1).replaceAll("[^0-9]", "");
						Log.i("[LOAD GAME]","(save)"+files[i]);
						Calendar c = Calendar.getInstance();
						c.setTimeInMillis(Long.parseLong(save)*1000);
						save = c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND)+" "+c.get(Calendar.DATE)+"/"+(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.YEAR);
						saves.add(save);
					}
					ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,saves);
					lv.setAdapter(aa);
					lv.setOnItemSelectedListener(this);
					lv.setOnItemClickListener(this);
					lv.clearChoices();
				}
			}break;
			case R.layout.settings:{
				b = (Button)findViewById(R.id.settings_back);
				b.setOnClickListener(this);
				SeekBar sb = (SeekBar)findViewById(R.id.settings_universeBar);
				sb.setProgress(mUp.getUniverseMaxGalaxy());
				sb = (SeekBar)findViewById(R.id.settings_galaxyBar);
				sb.setProgress(mUp.getGalaxyMaxSolars());
				sb = (SeekBar)findViewById(R.id.settings_solarBar);
				sb.setProgress(mUp.getSolarMaxPlanets());
				sb = (SeekBar)findViewById(R.id.settings_planetBar);
				sb.setProgress(mUp.getPlanetMaxSize());
				b.setOnClickListener(this);
			}break;
			case R.layout.ingame:{
				ImageView i;
				i = (ImageView)findViewById(R.id.imageUniverse);
				i.setVisibility(View.INVISIBLE);
				i = (ImageView)findViewById(R.id.imageGalaxy);
				i.setVisibility(View.INVISIBLE);
				i = (ImageView)findViewById(R.id.imageSolar);
				i.setVisibility(View.INVISIBLE);
				switch(mPlayer.getIGM()){
					case UNIVERSE:{
						i = (ImageView)findViewById(R.id.imageUniverse);
						i.setVisibility(View.VISIBLE);
						//TODO: Draw universe
					}break;
					case GALAXY:{
						i = (ImageView)findViewById(R.id.imageGalaxy);
						i.setVisibility(View.VISIBLE);
						//TODO: Draw galaxy
					}break;
					case SOLAR:{
						i = (ImageView)findViewById(R.id.imageSolar);
						i.setVisibility(View.VISIBLE);
						//TODO: Draw solar system
					}break;
					case PLANET:{
						Log.i("[ACTIVITY]","TODO");
						View g = findViewById(R.id.gameView);
						g.setVisibility(View.VISIBLE);
						//TODO: Draw planet
					}break;
				}
			}break;
			default:{
				Log.i("[ACTIVITY]","Unknow layout");
			}break;
		}
		if(mMenu!=null)
			onCreateOptionsMenu(mMenu);
	}
	//View listener
	@Override
	public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            Log.i("[ACTIVITY]","Touch coordinates : " + String.valueOf(event.getX()) + "x" + String.valueOf(event.getY()));
        }
        return true;
    }
	@Override
	public void onClick(View v){
		Button b;
		switch(v.getId()){
			case R.id.createGame:{
				Log.i("[BUTTON]","New game");
				loadLayout(R.layout.creategame);
			}break;
			case R.id.LoadGame:{
				Log.i("[BUTTON]","Load game");
				loadLayout(R.layout.loadgame);
			}break;
			case R.id.Settings:{
				Log.i("[BUTTON]","Settings");
				loadLayout(R.layout.settings);
			}break;
			//CreateGame layout
			case R.id.bp_createGameRun:{
				b = (Button)findViewById(R.id.bp_createGameRun);
				b.setEnabled(false);
				thread = new Thread(new Runnable() {
					@Override
					public void run() {
						Calendar c = Calendar.getInstance();
						File saveDir = new File(getPath()+"/saves/save_"+(c.getTimeInMillis() / 1000));
						if(!saveDir.exists())
							saveDir.mkdirs();
						Log.i("[NEW GAME]","save to "+saveDir.getAbsolutePath());
						mPath = saveDir.getAbsolutePath();
						Universe universe = new Universe(saveDir.getAbsolutePath());
						universe.setListener(new UniverseListener(){
							public void onUniverseProgress(int indexGalaxy, int nbsGalaxies){
								ProgressBar pb = (ProgressBar)findViewById(R.id.progress_universe_create);
								if(pb!=null){
									pb.setMax(nbsGalaxies);
									pb.setProgress(indexGalaxy);
								}
							}

							@Override
							public void onGalaxyProgress(int indexSolar, int nbsSolars) {
								ProgressBar pb = (ProgressBar)findViewById(R.id.progress_galaxy_create);
								if(pb!=null){
									pb.setMax(nbsSolars);
									pb.setProgress(indexSolar);
								}
							}

							@Override
							public void onSolarProgress(int indexPlanet, int nbsPlanets) {
								ProgressBar pb = (ProgressBar)findViewById(R.id.progress_solar_create);
								if(pb!=null){
									pb.setMax(nbsPlanets);
									pb.setProgress(indexPlanet);
								}
							}

							@Override
							public void onPlanetProgress(int indexBlock, int nbsBlocks) {
								ProgressBar pb = (ProgressBar)findViewById(R.id.progress_planet_create);
								if(pb!=null){
									pb.setMax(nbsBlocks);
									pb.setProgress(indexBlock);
								}
								
							}
						});
						universe.generate(mUp, saveDir.getAbsolutePath());
						Player p = new Player(MainActivity.this);
						Long[] location = new Long[4];
						location[0] = Long.valueOf(0);
						location[1] = Long.valueOf(0);
						location[2] = Long.valueOf(0);
						location[3] = universe.getID();
						p.setLocation(location);
						p.save();
						MainActivity.this.runOnUiThread(new MainActivity.ToastByThread(
							String.format(getResources().getString(R.string.Good_createGame),saveDir.getName()),
							Toast.LENGTH_SHORT
						));
						MainActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Button b = (Button)findViewById(R.id.bp_createGameRun);
								if(b!=null){
									isCreating=false;
									b.setEnabled(true);
								}
							}
						});
					}
				});
				thread.setPriority(Thread.MIN_PRIORITY);
				isCreating = true;
				thread.start();
			}break;
			case R.id.createGame_back:{
				loadLayout(R.layout.activity_main);
			}break;
			//LoadGame layout
			case R.id.loadGame_back:{
				loadLayout(R.layout.activity_main);
			}break;
			case R.id.bp_loadGameRun:{
				Log.i("[LOAD GAME","Load ");
				ListView ls = (ListView)findViewById(R.id.listGames);
				if(ls!=null){
					String tv = (String)ls.getItemAtPosition(selectedPos);
					Log.i("[LOAD GAME","pos:"+selectedPos);
					if(tv!=null){
						Log.i("[LOAD GAME","Load "+tv);String[] s = tv.split("[ :/]");
						Calendar c = Calendar.getInstance();
						c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(s[0]));
						c.set(Calendar.MINUTE, Integer.parseInt(s[1]));
						c.set(Calendar.SECOND, Integer.parseInt(s[2]));
						c.set(Calendar.DATE, Integer.parseInt(s[3]));
						c.set(Calendar.MONTH, Integer.parseInt(s[4])-1);
						c.set(Calendar.YEAR, Integer.parseInt(s[5]));
						File save = new File(getPath()+"/saves/save_"+(c.getTimeInMillis() / 1000)+"/");
						Log.i("[LOAD GAME]","Loading file("+save.getAbsolutePath()+"...");
						if(save.exists()){
							String files[] = save.list();
							Player p = null;
							for(int i=0;i<files.length;i++){
								if(files[i].equalsIgnoreCase("player.json")){
									mPath = save.getAbsolutePath();
									p = Player.load(save.getAbsolutePath()+"/"+files[i], this);
									mPlayer = p;
									loadLayout(R.layout.ingame);
									break;
								}
							}
							if(p==null){
								Log.i("[LOAD GAME]","Can't find player.json");
							}
						}
						else{
							Log.i("[LOAD GAME]","Can't find save folder");
						}
					}
					else{
						Log.i("[LOAD GAME","tv null");
					}
				}
				else{
					Log.i("[LOAD GAME","ls null");
				}
			}break;
			case R.id.bp_loadDelete:{
				Log.i("[DELETE GAME]","Delete ");
				ListView ls = (ListView)findViewById(R.id.listGames);
				if(ls!=null){
					String tv = (String)ls.getItemAtPosition(selectedPos);
					Log.i("[DELETE GAME","pos:"+selectedPos);
					if(tv!=null){
						Log.i("[DELETE GAME]","Delete "+tv);
						String[] s = tv.split("[ :/]");
						Calendar c = Calendar.getInstance();
						c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(s[0]));
						c.set(Calendar.MINUTE, Integer.parseInt(s[1]));
						c.set(Calendar.SECOND, Integer.parseInt(s[2]));
						c.set(Calendar.DATE, Integer.parseInt(s[3]));
						c.set(Calendar.MONTH, Integer.parseInt(s[4]));
						c.set(Calendar.YEAR, Integer.parseInt(s[5]));
						File save = new File(getPath()+"/saves/save_"+(c.getTimeInMillis() / 1000));
						Log.i("[DELETE GAME]","Deleting file("+save.getAbsolutePath()+")...");
						if(save.exists()){
							File[] files = save.listFiles();
							for(int i=0;i<files.length;i++){
								if(files[i].isFile()){
									files[i].delete();
								}
							}
							save.delete();
							Toast.makeText(this, String.format(getResources().getString(R.string.Good_loadDelete),save.getName()), Toast.LENGTH_SHORT).show();
							loadLayout(R.layout.loadgame);
						}
						else{
							Toast.makeText(this, getResources().getString(R.string.Error_loadDelete), Toast.LENGTH_SHORT).show();
						}
					}
					else{
						Log.i("[DELETE GAME]","tv null");
					}
				}
				else{
					Log.i("[DELETE GAME]","ls null");
				}
			}break;
			//Settings layout
			case R.id.settings_back:{
				loadLayout(R.layout.activity_main);
			}break;
			case R.id.bp_settingsSave:{
				SeekBar sb;
				String text = getResources().getString(R.string.settings_SaveText);
				sb = (SeekBar)findViewById(R.id.settings_universeBar);
				mUp.setUniverseMaxGalaxy(sb.getProgress());
				text = text.replace("_1d", String.valueOf(sb.getProgress()));
				sb = (SeekBar)findViewById(R.id.settings_galaxyBar);
				mUp.setGalaxyMaxSolars(sb.getProgress());
				text = text.replace("_2d", String.valueOf(sb.getProgress()));
				sb = (SeekBar)findViewById(R.id.settings_solarBar);
				mUp.setSolarMaxPlanets(sb.getProgress());
				text = text.replace("_3d", String.valueOf(sb.getProgress()));
				sb = (SeekBar)findViewById(R.id.settings_planetBar);
				mUp.setPlanetMaxSize(sb.getProgress());
				text = text.replace("_4d", String.valueOf(sb.getProgress()));
				Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
			}break;
			default:{
				Log.i("[ONCLICK]","Unknow button");
			}break;
		}
		return ;
	}
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}

	private String getPath() {
		if(isExternalStorageWritable()){
			File save = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/SpaceLand/");
			if(!save.exists()){
				save.mkdirs();
			}
			return save.getAbsolutePath();
		}
		
		return getDir("SpaceLand", Context.MODE_PRIVATE).getAbsolutePath();
	}
	
	protected String getCurrentPath(){
		return mPath;
	}

	@Override
 	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Log.i("[onItemSeleted]", "Item selected: "+arg0.getItemAtPosition(arg0.getSelectedItemPosition()));
		Button b = (Button)findViewById(R.id.bp_loadGameRun);
		if(b!=null){
			b.setEnabled(true);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		Button b = (Button)findViewById(R.id.bp_loadGameRun);
		if(b!=null){
			b.setEnabled(false);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Log.i("[onItemClick]", "Item selected: "+arg0.getItemAtPosition(arg2));
		Button b = (Button)findViewById(R.id.bp_loadGameRun);
		if(b!=null){
			b.setEnabled(true);
		}
		b = (Button)findViewById(R.id.bp_loadDelete);
		if(b!=null){
			b.setEnabled(true);
		}
		int length = arg0.getCount();
		for(int i=0;i<length;i++){
			TextView tv = (TextView)arg0.getChildAt(i);
			if(i==arg2){
				tv.setTextColor(getResources().getColor(android.R.color.darker_gray));
				((ListView)tv.getParent()).setSelection(arg2);
				selectedPos = arg2;
			}
			else{
				tv.setTextColor(getResources().getColor(android.R.color.black));
			}
		}
	}

	@Override
	protected void onDestroy() {
		if(thread!=null){
			try {
				thread.wait(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try{
				thread.interrupt();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		super.onDestroy();
	}
	
}
