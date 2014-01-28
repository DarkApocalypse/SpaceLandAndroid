package fr.jc_android.spaceland;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Player implements SpaceObject {
	protected Universe mUniverse;
	protected Galaxy mGalaxy;
	protected Solar mSolar;
	protected Planet mPlanet;
	protected MainActivity mAct;

	@Override
	public Long[] setLocation(Long[] location) {
		Long[] l = new Long[4];
		try{
			l[0] = mUniverse.getID();
			l[1] = mGalaxy.getID();
			l[2] = mSolar.getID();
			l[3] = mPlanet.getID();
		}
		catch(Exception e){}
		int offset = 0;
		if(l.length==4){
			mUniverse = Universe.load(location[0], mAct.getCurrentPath());
			offset++;
		}
		if(l.length>=3){
			mGalaxy = Galaxy.load(location[offset], mAct.getCurrentPath());
			offset++;
		}
		if(l.length>=2){
			mSolar = Solar.load(location[offset], mAct.getCurrentPath());
			offset++;
		}
		if(l.length>=1){
			mPlanet = Planet.load(location[offset], mAct.getCurrentPath());
			offset++;
		}
		return l;
	}

	@Override
	public Long[] getLocation() {
		Long[] l = new Long[4];
		try{
			l[0] = mUniverse.getID();
			l[1] = mGalaxy.getID();
			l[2] = mSolar.getID();
			l[3] = mPlanet.getID();
		}
		catch(Exception e){}
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
			
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Player load(String path) {

		JSONObject json;
		try {
			FileInputStream fis = new FileInputStream(path);
			StringBuilder sb = new StringBuilder();
			while(fis.available()!=0){
				sb.append((char)fis.read());
			}
			fis.close();
			json = new JSONObject(sb.toString());
			Player p = new Player();
			JSONArray location = json.getJSONArray("mLocation");
			Long[] l = new Long[location.length()];
			p.setLocation(l);
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

}
