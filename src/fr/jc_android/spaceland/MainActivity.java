package fr.jc_android.spaceland;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import fr.jc_android.spaceland.Universe.UniverseParameters;

import android.os.Bundle;
import android.app.Activity;
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
import android.widget.ListView;
import android.widget.ProgressBar;
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
						Date d = new Date();
						d.setTime(Long.parseLong(save)*1000);
						save = d.getHours()+":"+d.getMinutes()+":"+d.getSeconds()+" "+d.getDate()+"/"+(d.getMonth()+1)+"/"+(d.getYear()+1900);
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
						File saveDir = new File(getPath()+"/saves/save_"+((new Date()).getTime() / 1000));
						if(!saveDir.exists())
							saveDir.mkdirs();
						Log.i("[NEW GAME]","save to "+saveDir.getAbsolutePath());
						UniverseParameters up = new UniverseParameters();
						Universe universe = new Universe(saveDir.getAbsolutePath());
						universe.setListener(new UniverseListener(){
							public void onUniverseProgress(int indexGalaxy, int nbsGalaxies){
								ProgressBar pb = (ProgressBar)findViewById(R.id.progress_universe_create);
								if(pb!=null){
									pb.setMax(nbsGalaxies);
									pb.setProgress(indexGalaxy)
;								}
							}

							@Override
							public void onGalaxyProgress(int indexSolar, int nbsSolars) {
								ProgressBar pb = (ProgressBar)findViewById(R.id.progress_galaxy_create);
								if(pb!=null){
									pb.setMax(nbsSolars);
									pb.setProgress(indexSolar)
;								}
							}

							@Override
							public void onSolarProgress(int indexPlanet, int nbsPlanets) {
								ProgressBar pb = (ProgressBar)findViewById(R.id.progress_solar_create);
								if(pb!=null){
									pb.setMax(nbsPlanets);
									pb.setProgress(indexPlanet)
;								}
							}

							@Override
							public void onPlanetProgress(int indexBlock, int nbsBlocks) {
								ProgressBar pb = (ProgressBar)findViewById(R.id.progress_planet_create);
								if(pb!=null){
									pb.setMax(nbsBlocks);
									pb.setProgress(indexBlock)
;								}
								
							}
						});
						universe.generate(up, saveDir.getAbsolutePath());
						universe.save(saveDir.getAbsolutePath());
						MainActivity.this.runOnUiThread(new MainActivity.ToastByThread(
							String.format(getResources().getString(R.string.Good_createGame),saveDir.getName()),
							Toast.LENGTH_SHORT
						));
						Button b = (Button)findViewById(R.id.bp_createGameRun);
						if(b!=null){
							isCreating=false;
							b.setEnabled(true);
						}
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
						Log.i("[LOAD GAME","Load "+tv);
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
				Log.i("[LOAD GAME]","Delete ");
				ListView ls = (ListView)findViewById(R.id.listGames);
				if(ls!=null){
					String tv = (String)ls.getItemAtPosition(selectedPos);
					Log.i("[LOAD GAME","pos:"+selectedPos);
					if(tv!=null){
						Log.i("[DELETE GAME]","Delete "+tv);
						Date d = new Date();
						String[] s = tv.split("[ :/]");
						d.setHours(Integer.parseInt(s[0]));
						d.setMinutes(Integer.parseInt(s[1]));
						d.setSeconds(Integer.parseInt(s[2]));
						d.setDate(Integer.parseInt(s[3]));
						d.setMonth(Integer.parseInt(s[4])-1);
						d.setYear(Integer.parseInt(s[5])-1900);
						File save = new File(getPath()+"/saves/save_"+(d.getTime() / 1000)+"");
						Log.i("[DELETE GAME]","Deleting file("+save.getAbsolutePath()+"...");
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
			default:{
				Log.i("[ONCLICK]","Unknow button");
			}break;
		}
		return ;
	}

	private String getPath() {
		return getDir("SpaceLand", MODE_WORLD_READABLE).getAbsolutePath();
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
