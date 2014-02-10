package fr.jc_android.spaceland;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import fr.jc_android.spaceland.Block.BlockType;
import fr.jc_android.spaceland.Universe.UniverseParameters;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.ColorFilter;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.TouchDelegate;
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
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnTouchListener, OnClickListener, OnItemSelectedListener, OnItemClickListener {
	protected int mCurrentLayout = 0;
	protected Menu mMenu;
	protected int selectedPos;
	protected Thread thread;
	protected boolean isCreating;
	protected Player mPlayer;
	protected String mPath;
	protected MediaPlayer mMedia;
	protected UniverseParameters mUp = new UniverseParameters();
	protected double mZoom = 10.0;
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
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    	switch(mCurrentLayout){
	    		case R.layout.creategame:
	    		case R.layout.loadgame:
	    		case R.layout.settings:{
	    			loadLayout(R.layout.activity_main);
	    		}break;
	    		case R.layout.ingame:{
	    			if(mPlayer!=null){
	    				switch(mPlayer.getIGM()){
							case GALAXY:{
								Long[] location = mPlayer.getLocation();
								location = Utils.reverse(location);
								location[0]=Long.valueOf(0);
								location[1]=Long.valueOf(0);
								location[2]=Long.valueOf(0);
								mPlayer.setLocation(location);
			    				loadLayout(R.layout.ingame);
							}break;
							case PLANET:{
								Long[] location = mPlayer.getLocation();
								Log.i("[onKeyDown]","Planet onKeyDown location:"+location);
								location = Utils.reverse(location);
								location[0] = Long.valueOf(0);
								mPlayer.setLocation(location);
			    				loadLayout(R.layout.ingame);
							}break;
							case SOLAR:{
								Long[] location = mPlayer.getLocation();
								location = Utils.reverse(location);
								location[0]=Long.valueOf(0);
								location[1]=Long.valueOf(0);
								mPlayer.setLocation(location);
			    				loadLayout(R.layout.ingame);
							}break;
							case UNIVERSE:
							default:{
								mPlayer.save();
			    				loadLayout(R.layout.activity_main);
							}break;
	    				}
	    			}
	    			else{
		    			loadLayout(R.layout.activity_main);
	    			}
	    		}break;
	    		case R.layout.activity_main:
	    		default:{
	    			
	    		}
	    		break;
	    	}
	    	return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	//LoadListener
	protected void loadLayout(int layout){
		Button b;
		int oldLayout = mCurrentLayout;
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
				b =  (Button)findViewById(R.id.bp_settingsSave);
				b.setOnClickListener(this);
			}break;
			case R.layout.ingame:{
				if(oldLayout!=mCurrentLayout){
					Log.i("[INGAME]","load music:");
					startMusic(R.raw.doc_neptune);
				}
				ImageView imgView;
				imgView = (ImageView)findViewById(R.id.imageUniverse);
				imgView.setVisibility(View.GONE);
				imgView = (ImageView)findViewById(R.id.imageGalaxy);
				imgView.setVisibility(View.GONE);
				imgView = (ImageView)findViewById(R.id.imageSolar);
				imgView.setVisibility(View.GONE);
				Log.i("R.layout.ingame", "Universe: "+(mPlayer.getUniverse()!=null ? mPlayer.getUniverse().getID() : "null"));
				Log.i("R.layout.ingame", "Galaxy: "+(mPlayer.getGalaxy()!=null ? mPlayer.getGalaxy().getID() : "null"));
				Log.i("R.layout.ingame", "solar: "+(mPlayer.getSolar()!=null ? mPlayer.getSolar().getID() : "null"));
				Log.i("R.layout.ingame", "Planet: "+(mPlayer.getPlanet()!=null ? mPlayer.getPlanet().getID() : "null"));
				if(mPlayer.mUniverse==null){
					loadLayout(R.layout.activity_main);
				}
				switch(mPlayer.getIGM()){
					case UNIVERSE:{
						imgView = (ImageView)findViewById(R.id.imageUniverse);
						imgView.setVisibility(View.VISIBLE);
						RelativeLayout rl = (RelativeLayout)findViewById(R.id.gameLayout);
						int length = mPlayer.getUniverse().length();
						double stepX = (double)getWidth() / (1.5 * length);
						double stepY = (double)getHeight() / (1.5 *length);
						for(int i=0;i<length;i++){
							Galaxy g = mPlayer.getUniverse().getGalaxy(i, getCurrentPath());
							ImageView imgGalaxy = new ImageView(this);
							imgGalaxy.setImageResource(R.drawable.star);
							if((g.getID()%3)==0){	//RED
								imgGalaxy.setColorFilter(0xFFFF0000,Mode.MULTIPLY);
							}
							else if((g.getID()%3)==1){	//GREEN
								imgGalaxy.setColorFilter(0xFF00FF00,Mode.MULTIPLY);
							}
							else if((g.getID()%3)==2){	//BLUE
								imgGalaxy.setColorFilter(0xFF0000FF,Mode.MULTIPLY);
							}
							imgGalaxy.setClickable(true);
							imgGalaxy.setOnClickListener(new GalaxyClickListener(this,g));
							int x = (int)((double)g.getX() / 100.0 * length);
							int y = (int)((double)g.getY() / 100.0 * length);
							RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
									RelativeLayout.LayoutParams.MATCH_PARENT,
									RelativeLayout.LayoutParams.MATCH_PARENT);
							lp.leftMargin =(int)(x * stepX);
							lp.topMargin = (int)(y * stepY);
							lp.width = (int)stepX;
							lp.height = (int)stepY;
							rl.addView(imgGalaxy,lp);
						}
					}break;
					case GALAXY:{
						imgView = (ImageView)findViewById(R.id.imageGalaxy);
						imgView.setVisibility(View.VISIBLE);
						RelativeLayout rl = (RelativeLayout)findViewById(R.id.gameLayout);
						int length = mPlayer.getGalaxy().length();
						double stepX = (double)getWidth() / (1.5 * length);
						double stepY = (double)getHeight() / (1.5 *length);
						for(int i=0;i<length;i++){
							Solar s = mPlayer.getGalaxy().getSolar(i, getCurrentPath());
							ImageView imgSolar = new ImageView(this);
							imgSolar.setImageResource(R.drawable.star);
							if((s.getID()%3)==0){	//RED
								imgSolar.setColorFilter(0xFFFF0000,Mode.MULTIPLY);
							}
							else if((s.getID()%3)==1){	//GREEN
								imgSolar.setColorFilter(0xFF00FF00,Mode.MULTIPLY);
							}
							else if((s.getID()%3)==2){	//BLUE
								imgSolar.setColorFilter(0xFF0000FF,Mode.MULTIPLY);
							}
							imgSolar.setClickable(true);
							imgSolar.setOnClickListener(new SolarClickListener(this,s));
							int x = (int)((double)s.getX() / 100.0 * length);
							int y = (int)((double)s.getY() / 100.0 * length);
							RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
									RelativeLayout.LayoutParams.MATCH_PARENT,
									RelativeLayout.LayoutParams.MATCH_PARENT);
							lp.leftMargin =(int)(x * stepX);
							lp.topMargin = (int)(y * stepY);
							lp.width = (int)stepX;
							lp.height = (int)stepY;
							rl.addView(imgSolar,lp);
							Log.i("[]",((int)s.getX())+","+((int)s.getY()));
						}
					}break;
					case SOLAR:{
						imgView = (ImageView)findViewById(R.id.imageSolar);
						imgView.setVisibility(View.VISIBLE);
						RelativeLayout rl = (RelativeLayout)findViewById(R.id.gameLayout);
						int length = mPlayer.getSolar().length();
						double centerX = (double)getWidth() / 2;
						double centerY = (double)getHeight() / 2;
						double stepR = Math.min(centerX, centerY) / (1.5 * length);
						for(int i=0;i<length;i++){
							Planet p = mPlayer.getSolar().getPlanet(i, getCurrentPath());
							ImageView imgPlanet = new ImageView(this);
							imgPlanet.setImageResource(R.drawable.star);
							if((p.getID()%3)==0){	//RED
								imgPlanet.setColorFilter(0xFFFF0000,Mode.MULTIPLY);
							}
							else if((p.getID()%3)==1){	//GREEN
								imgPlanet.setColorFilter(0xFF00FF00,Mode.MULTIPLY);
							}
							else if((p.getID()%3)==2){	//BLUE
								imgPlanet.setColorFilter(0xFF0000FF,Mode.MULTIPLY);
							}
							imgPlanet.setClickable(true);
							imgPlanet.setOnClickListener(new PlanetClickListener(this,p));
							int x = (int)(Math.cos(((double)i / (double)length) * 2 * Math.PI) * (i*stepR));
							int y = (int)(Math.sin(((double)i / (double)length) * 2 * Math.PI) * (i*stepR));
							RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
									RelativeLayout.LayoutParams.MATCH_PARENT,
									RelativeLayout.LayoutParams.MATCH_PARENT);
							lp.leftMargin =(int)(x + centerX);
							lp.topMargin = (int)(y + centerY);
							lp.width = (int)stepR;
							lp.height = (int)stepR;
							rl.addView(imgPlanet,lp);
							Log.i("[]","R:"+((int)p.getR())+" "+stepR);
						}
					}break;
					case PLANET:{
						View p = findViewById(R.id.gameView);
						p.setVisibility(View.VISIBLE);
						p.setOnClickListener(new OnClickListener() {
							protected long lastClicked;
							@Override
							public void onClick(View v) {
								if(lastClicked+300 > Calendar.getInstance().getTimeInMillis()){
									lastClicked = 0;
									Log.i("[]","zoom In");
									if(mZoom > 2.5){
										mZoom = mZoom / 2.0;
										drawPlanetArea();
									}
								}
								else{
									lastClicked = Calendar.getInstance().getTimeInMillis();
								}
							}
						});
						p.setOnLongClickListener(new View.OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {
								Log.i("[]","zoom Out");
								if(mZoom < 40.0){
									mZoom = mZoom * 2.0;
									drawPlanetArea();
								}
								return false;
							}
						});
						p.setOnTouchListener(new View.OnTouchListener() {
							private boolean mInited=false;
							private float mX;
							private float mY;
							@Override
							public boolean onTouch(View v, MotionEvent event) {
								Log.i("[PLANET OnTouch]",event.getX()+":"+event.getY());
								if(!mInited || event.getAction()==MotionEvent.ACTION_DOWN){
									mX = event.getX();
									mY = event.getY();
									mInited=true;
									return true;
								}
								float delta = (mX-event.getX()); 
								if(delta!=0){
									int move=(delta>0 ? 1 : -1);
									if(mPlayer.blockAt(move,0).isTraversable()){
										mPlayer.setX(mPlayer.getX() + move);
										drawPlanetArea();
									}
									return true;
								}
								return false;
							}
						});
						drawPlanetArea();
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
						p.setUniverseParameters(mUp);
						Log.i("[NEW GAME]","Player: "+p);
						p.save();
						mPlayer = p;
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
								loadLayout(R.layout.ingame);
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
									mUp = p.getUniverseParameters();
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
						c.set(Calendar.MONTH, Integer.parseInt(s[4])-1);
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
				thread.join(500);;
			} catch (Exception e) {
				e.printStackTrace();
			}
			try{
				thread.interrupt();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if(mMedia!=null){
			mMedia.stop();
			mMedia.release();
		}
		super.onDestroy();
	}
	public void startMusic(int resId){
		if(mMedia!=null){
			mMedia.stop();
			mMedia.release();
		}
		AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
		mMedia = MediaPlayer.create(this, resId);
		if(mMedia!=null){
			mMedia.setLooping(true);
			float vol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
			mMedia.setVolume(vol, vol);
			mMedia.setOnErrorListener(new OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					Log.i("MediaPlayer.OnErrorListener","Error:"+what+":"+extra);
					return false;
				}
			});
			mMedia.start();
		}
	}
	protected void drawPlanetArea(){
		Log.i("[ACTIVITY]","drawPlanetArea");
		int spawnX = mPlayer.getX();
		int spawnY = mPlayer.getY();
		Log.i("[Planet]","Spawn at "+spawnX+":"+spawnY);
		RelativeLayout rl = (RelativeLayout)findViewById(R.id.gameLayout);
		int i=0;
		while(i<rl.getChildCount()){
			View v = rl.getChildAt(i);
			if(v instanceof ImageView && v.getId()!=R.id.imageUniverse && v.getId()!=R.id.imageGalaxy && v.getId()!=R.id.imageSolar && v.getId()!=R.id.gameView){
				rl.removeView(v);
				continue;
			}
			i++;
		}
		Log.i("[PlanetSpawn]","Display:"+getWidth()+" "+getHeight());
		double stepX = (double)getWidth() / mZoom;
		double stepY = (double)getHeight() / mZoom;
		Log.i("[drawPlanetArea]",stepX+" "+stepY);
		int offsetX = spawnX-(int)Math.floor(mZoom / 2);
		int offsetY = spawnY-(int)Math.floor(mZoom / 2);
		for(int x=(spawnX-(int)Math.floor(mZoom / 2));x<(spawnX+(int)Math.ceil(mZoom / 2));x++){
			for(int y=(spawnY-(int)Math.floor(mZoom / 2));y<(spawnY+(int)Math.ceil(mZoom / 2));y++){
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
				BlockType bt = mPlayer.getPlanet().getBlockType(x, y);
				ImageView imgBlock = new ImageView(this);
				imgBlock.setImageResource(R.drawable.star);
				if(bt==BlockType.AIR){
					imgBlock.setColorFilter(0xFFccccFF, Mode.MULTIPLY);
				}
				else if(bt==BlockType.BED_ROCK){
					imgBlock.setColorFilter(0xFF555555, Mode.MULTIPLY);
				}
				lp.leftMargin =(int)((x-offsetX) * stepX);
				lp.topMargin = getHeight() - (int)stepY - (int)((y-offsetY) * stepY);
				lp.width = (int)stepX;
				lp.height = (int)stepY;
				Log.i("[PlanetSpawn]","["+x+":"+y+"]"+bt.name()+":"+lp.leftMargin+","+lp.topMargin);
				rl.addView(imgBlock,lp);
			}
		}
	}
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public int getHeight(){
		Display display = getWindowManager().getDefaultDisplay();
		if(android.os.Build.VERSION.SDK_INT<13){
			return display.getHeight();
		}
		else{
			Point p= new Point();
			display.getSize(p);
			return p.y;
		}
	}
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public int getWidth(){
		Display display = getWindowManager().getDefaultDisplay();
		if(android.os.Build.VERSION.SDK_INT<13){
			return display.getWidth();
		}
		else{
			Point p= new Point();
			display.getSize(p);
			return p.x;
		}
	}
}
