package fr.jc_android.spaceland;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.jc_android.spaceland.Block.BlockType;
import fr.jc_android.spaceland.MainActivity.InGameMode;
import fr.jc_android.spaceland.Universe.UniverseParameters;

public class Player implements SpaceObject {
	protected Universe mUniverse;
	protected Galaxy mGalaxy;
	protected Solar mSolar;
	protected Planet mPlanet;
	protected MainActivity mAct;
	protected UniverseParameters mUp;
	protected int mX;
	protected int mY;
	public Player(MainActivity act){
		mAct = act;
	}
	@Override
	public void setLocation(Long[] location) {
		int offset = 0;
		if(location.length>0){
			mPlanet = Planet.load(location[offset], mAct.getCurrentPath());
			offset++;
		}
		if(location.length>1){
			mSolar = Solar.load(location[offset], mAct.getCurrentPath());
			offset++;
		}
		if(location.length>2){
			mGalaxy = Galaxy.load(location[offset], mAct.getCurrentPath());
			offset++;
		}
		if(location.length>3){
			mUniverse = Universe.load(location[offset], mAct.getCurrentPath());
			offset++;
		}
	}
	@Override
	public Long[] getLocation() {
		Long[] l = new Long[]{(long) 0,(long) 0,(long) 0,(long) 0};
		try{
			if(mUniverse!=null)
				l[0] = mUniverse.getID();
			if(mGalaxy!=null)
				l[1] = mGalaxy.getID();
			if(mSolar!=null)
				l[2] = mSolar.getID();
			if(mPlanet!=null)
				l[3] = mPlanet.getID();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return l;
	}
	@Override
	public String getPath() {
		return mAct.getCurrentPath()+"/player.json";
	}
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		Long[] location = getLocation();
		sb.append("\"class\":\""+this.getClass().getName()+"\",");
		sb.append("\"mUp\":"+mUp.toString()+",");
		sb.append("\"mLocation\":[");
		for(int i=0;i<location.length;i++){
			sb.append((i>0 ? ",":"")+location[i].longValue());
		}
		sb.append("]}");
		return sb.toString();
	}
	@Override
	public void save() {
		try {
			File f = new File(getPath());
			if(f.exists()){
				f.delete();
			}
			FileOutputStream fos = new FileOutputStream(f);
			String s = toString();
			for(int i = 0;i<s.length();i++){
				fos.write(s.charAt(i));
			}
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static Player load(String path, MainActivity act) {
		JSONObject json;
		try {
			FileInputStream fis = new FileInputStream(path);
			StringBuilder sb = new StringBuilder();
			while(fis.available()!=0){
				sb.append((char)fis.read());
			}
			fis.close();
			json = new JSONObject(sb.toString());
			Player p = new Player(act);
			JSONArray location = json.getJSONArray("mLocation");
			Long[] l = new Long[location.length()];
			for(int i=0;i<location.length();i++){
				l[i] = Long.valueOf(location.getString(3-i));
			}
			p.setLocation(l);
			JSONObject jsonUP = json.getJSONObject("mUp");
			UniverseParameters up = new UniverseParameters();
			up.setPlanetMinSize(jsonUP.getInt("mPlanetMinSize"));
			up.setPlanetMaxSize(jsonUP.getInt("mPlanetMaxSize"));
			up.setSolarMinPlanets(jsonUP.getInt("mSolarMinPlanets"));
			up.setSolarMaxPlanets(jsonUP.getInt("mSolarMaxPlanets"));
			up.setGalaxyMinSolars(jsonUP.getInt("mGalaxyMinSolars"));
			up.setGalaxyMaxSolars(jsonUP.getInt("mGalaxyMaxSolars"));
			up.setUniverseMinGalaxy(jsonUP.getInt("mUniverseMinGalaxy"));
			up.setUniverseMaxGalaxy(jsonUP.getInt("mUniverseMaxGalaxy"));
			p.setUniverseParameters(up);
			return p;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	public void setUniverseParameters(UniverseParameters up){
		mUp = up;
	}
	public UniverseParameters getUniverseParameters(){
		return mUp;
	}
	public InGameMode getIGM(){
		if(mPlanet!=null)
			return InGameMode.PLANET;
		if(mSolar!=null)
			return InGameMode.SOLAR;
		if(mGalaxy!=null)
			return InGameMode.GALAXY;
		return InGameMode.UNIVERSE;
	}
	public Universe getUniverse() {
		return mUniverse;
	}
	public Galaxy getGalaxy() {
		return mGalaxy;
	}
	public Solar getSolar() {
		return mSolar;
	}
	public Planet getPlanet() {
		return mPlanet;
	}
	public int getX(){
		return mX;
	}
	public int getY(){
		return mY;
	}
	public void setX(int i) {
		while(mX<0)
			mX+=getPlanet().getSize();
		mX = i % getPlanet().getSize();
	}
	public void setY(int i) {
		if(i > 0 && i < getPlanet().getSize()){
			mY = i;
		}
	}
	public BlockType blockAt(int x, int y) {
		return getPlanet().getBlockType(mX+x, mY+y);
	}
}
