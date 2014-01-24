package fr.jc_android.spaceland;

import java.io.FileOutputStream;
import java.util.ArrayList;

public class Planet implements Entity{
	private static Long planetID = Long.valueOf(0);
	protected Long mID;
	protected int mSize;
	protected ArrayList<Long> mEntities;
	protected Long[] mBlocks;
	public Planet(int planetSize){
		synchronized (planetID) {
			mID = ++planetID;	
		}
		mSize = planetSize;
		mBlocks = new Long[mSize*mSize];
		mEntities = new ArrayList<Long>();
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("{\"class\":\""+this.getClass().getName()+"\",");
		sb.append("\"mID\":"+mID+"\",\"mSize\":"+mSize+",\"mEntities\":[");
		for(int i=0;i<mEntities.size();i++){
			Long e = mEntities.get(i);
			sb.append((i>0?",":"")+e);
		}
		sb.append("],\"mBlocks\":[");
		for(int i=0;i<mBlocks.length;i++){
			Long e = mBlocks[i];
			if(e==null){
				e = new Long(0);
			}
			sb.append((i>0?",":"")+e);
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
		mBlocks[b.getX()+b.getY()*mSize] = b.getID();
	}
}
