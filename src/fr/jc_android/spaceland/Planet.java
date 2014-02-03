package fr.jc_android.spaceland;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.jc_android.spaceland.Block.BlockType;

public class Planet implements Entity{
	private static Long planetID = Long.valueOf(0);
	protected Long mID;
	protected int mSize;
	protected ArrayList<Long> mEntities;
	protected int[] mBlocks;
	protected char mR;
	protected String mName;
	public Planet(int planetSize){
		synchronized (planetID) {
			mID = ++planetID;	
		}
		mSize = planetSize;
		mBlocks = new int[mSize*mSize];
		mEntities = new ArrayList<Long>();
		mName = "Planet "+Long.toHexString(mID);
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("{\"class\":\""+this.getClass().getName()+"\",");
		sb.append("\"mID\":"+mID+"\",");
		sb.append("\"mSize\":"+mSize+",");
		sb.append("\"mR\":"+(int)mR+",");
		sb.append("\"mName\":\""+mName+"\",");
		sb.append("\"mEntities\":[");
		for(int i=0;i<mEntities.size();i++){
			Long e = mEntities.get(i);
			sb.append((i>0?",":"")+e);
		}
		sb.append("],\"mBlocks\":[");
		for(int i=0;i<mBlocks.length;i++){
			sb.append((i>0?",":"")+String.valueOf(mBlocks[i]));
		}
		sb.append("]}");
		return sb.toString();
	}
	@Override
	public Long getID() {
		return mID;
	}
	public char getR() {
		return mR;
	}
	public void setR(char r) {
		mR=r;
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
			FileOutputStream fos = new FileOutputStream(path+"/planet_"+mID+".json");
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
	public void add(Block b) {
		mBlocks[b.getX()+b.getY()*mSize] = b.getType().ordinal();
	}
	public static Planet load(Long id, String path){
		StringBuilder content = new StringBuilder();
		Planet p = null;
		FileInputStream fis;
		try {
			fis = new FileInputStream(path+"/planet_"+id.longValue()+".json");
			fis.close();
			JSONObject json = new JSONObject(content.toString());
			p = new Planet(json.getInt("mSize"));
			JSONArray mBlock = json.getJSONArray("mBlock");
			for(int x=0;x<p.getSize();x++){
				for(int y=0;y<p.getSize();y++){
					p.add(Block.genBlock(x, y, BlockType.values()[mBlock.getInt(y*p.getSize()+x)]));
				}
			}
			JSONArray mEntities = json.getJSONArray("mEntities");
			for(int i=0;i<mEntities.length();i++){
				p.addEntity(mEntities.getLong(i));
			}
			p.mR = (char)json.getInt("mR");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch(NullPointerException e){
			e.printStackTrace();
		}
		return p;
	}
	private void addEntity(long id) {
		mEntities.add(id);
	}
	public int getSize() {
		return mSize;
	}
}
