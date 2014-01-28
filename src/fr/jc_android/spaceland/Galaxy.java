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
	protected Long[] mSolars;
	public Galaxy(int size){
		synchronized (galaxyID) {
			mID = ++galaxyID;
		}
		mSolars = new Long[size];
	}
	public Galaxy(Long id) {
		synchronized (galaxyID) {
			galaxyID = Long.valueOf(id.longValue()-1);
			mID = ++galaxyID;
		}
	}
	public void add(Solar s,int index) {
		mSolars[index] = s.getID();		
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("{\"class\":\""+this.getClass().getName()+"\",");
		sb.append("\"mSolar\":[");
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
			return g;
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
