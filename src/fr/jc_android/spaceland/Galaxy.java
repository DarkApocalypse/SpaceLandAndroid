package fr.jc_android.spaceland;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Galaxy implements Entity{
	private static Long galaxyID = Long.valueOf(0);
	protected Long mID;
	protected char mX=0;
	protected char mY=0;
	protected Long[] mSolars;
	protected String mName;
	public Galaxy(int size){
		synchronized (galaxyID) {
			mID = ++galaxyID;
		}
		mSolars = new Long[size];
		mName = "Galaxy "+Long.toHexString(mID);
	}
	public Galaxy(Long id) {
		synchronized (galaxyID) {
			galaxyID = Long.valueOf(id.longValue()-1);
			mID = ++galaxyID;
		}
		mName = "Galaxy "+Long.toHexString(mID);
	}
	public void add(Solar s,int index) {
		mSolars[index] = s.getID();		
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("{\"class\":\""+this.getClass().getName()+"\",");
		sb.append("\"mX\":"+(int)mX+",");
		sb.append("\"mY\":"+(int)mY+",");
		sb.append("\"mName\":\""+mName+"\",");
		sb.append("\"mSolars\":[");
		for(int i=0;i<mSolars.length;i++){
			sb.append((i>0?",":"")+mSolars[i]);
		}
		sb.append("]}");
		return sb.toString();
	}
	@Override
	public Long getID() {
		return mID;
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
	public String getName(){
		return mName;
	}
	public void setName(String name){
		mName = name.replaceAll("\"", "");
	}
	@Override
	public boolean save(String path) {
		try {
			FileOutputStream fos = new FileOutputStream(path+"/galaxy_"+mID+".json");
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
	public static Galaxy load(Long id, String path){
		JSONObject json;
		try {
			FileInputStream fis = new FileInputStream(path+"/galaxy_"+id+".json");
			StringBuilder sb = new StringBuilder();
			while(fis.available()!=0){
				sb.append((char)fis.read());
			}
			fis.close();
			json = new JSONObject(sb.toString());
			Galaxy g = new Galaxy(id);
			JSONArray solars = json.getJSONArray("mSolars");
			g.mSolars = new Long[solars.length()];
			for(int i=0;i<solars.length();i++){
				g.mSolars[i] = Long.valueOf(solars.getLong(i));
			}
			g.mX = (char)json.getInt("mX");
			g.mY = (char)json.getInt("mY");
			return g;
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
