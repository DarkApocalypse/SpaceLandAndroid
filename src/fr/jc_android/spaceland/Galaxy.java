package fr.jc_android.spaceland;

import java.io.FileOutputStream;


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
}
