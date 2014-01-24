package fr.jc_android.spaceland;

import java.io.FileOutputStream;

public class Solar implements Entity{
	private static Long solarID = Long.valueOf(0);
	protected Long mID;
	protected Long[] mPlanets;
	public Solar(int nbsPlanets) {
		synchronized (solarID) {
			mID = ++solarID;
		}
		mPlanets = new Long[nbsPlanets];
	}
	public void add(Planet p, int k) {
		mPlanets[k] = p.getID();
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("{\"class\":\""+this.getClass().getName()+"\",");
		sb.append("\"mPlanets\":[");
		for(int i=0;i<mPlanets.length;i++){
			sb.append((i>0?",":"")+mPlanets[i]);
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
}
