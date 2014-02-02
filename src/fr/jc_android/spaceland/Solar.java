package fr.jc_android.spaceland;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Solar implements Entity{
	private static Long solarID = Long.valueOf(0);
	protected Long mID;
	protected Long[] mPlanets;
	protected char mX;
	protected char mY;
	protected String mName;
	public Solar(int nbsPlanets) {
		synchronized (solarID) {
			mID = ++solarID;
		}
		mPlanets = new Long[nbsPlanets];
		mName = "Solar System "+Long.toHexString(mID);
	}
	public Solar(Long id) {
		synchronized (solarID) {
			solarID = Long.valueOf(id.longValue()-1);
			mID = ++solarID;
		}
		mName = "Solar System "+Long.toHexString(mID);
	}
	public void add(Planet p, int k) {
		mPlanets[k] = p.getID();
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("{\"class\":\""+this.getClass().getName()+"\",");
		sb.append("\"mX\":"+(int)mX+",");
		sb.append("\"mY\":"+(int)mY+",");
		sb.append("\"mName\":\""+mName+"\",");
		sb.append("\"mPlanets\":[");
		for(int i=0;i<mPlanets.length;i++){
			sb.append((i>0?",":"")+mPlanets[i]);
		}
		sb.append("]}");
		return sb.toString();
	}
	public char getX() {
		return mX;
	}
	public char getY() {
		return mY;
	}
	public void setX(char x) {
		mX=x;
	}
	public void setY(char y) {
		mY=y;
	}
	@Override
	public Long getID() {
		return mID;
	}
	public String getName(){
		return mName;
	}
	public void setName(String name){
		mName = name.replaceAll("\"", "");
	}
	@Override
	public boolean save(String path) {
		try {
			FileOutputStream fos = new FileOutputStream(path+"/solar_"+mID+".json");
			String s = toString();
			for(int i=0;i<s.length();i++){
				fos.write(s.charAt(i));
			}
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public static Solar load(Long id,String path){
		JSONObject json;
		try {
			FileInputStream fis = new FileInputStream(path+"/solar_"+id+".json");
			StringBuilder sb = new StringBuilder();
			while(fis.available()!=0){
				sb.append((char)fis.read());
			}
			fis.close();
			json = new JSONObject(sb.toString());
			Solar s = new Solar(id);
			JSONArray planets = json.getJSONArray("mPlanets");
			s.mPlanets = new Long[planets.length()];
			for(int i=0;i<planets.length();i++){
				s.mPlanets[i] = Long.valueOf(planets.getLong(i));
			}
			return s;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch(NullPointerException e){
			e.printStackTrace();
		}
		return null;
	}
}
