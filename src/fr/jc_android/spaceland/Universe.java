package fr.jc_android.spaceland;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.provider.ContactsContract.DeletedContacts;
import fr.jc_android.spaceland.Block.BlockType;

public class Universe implements Entity{
	public static class UniverseParameters{
		protected int mPlanetMinSize = 10;
		protected int mPlanetMaxSize = 100;
		protected int mSolarMinPlanets = 2;
		protected int mSolarMaxPlanets = 32;
		protected int mGalaxyMinSolars = 10;
		protected int mGalaxyMaxSolars = 20;
		protected int mUniverseMinGalaxy = 5;
		protected int mUniverseMaxGalaxy = 10;
		public UniverseParameters(){
		}
		public int getPlanetMinSize() {
			return mPlanetMinSize;
		}
		public void setPlanetMinSize(int planetMinSize) {
			mPlanetMinSize = planetMinSize;
		}
		public int getPlanetMaxSize() {
			return mPlanetMaxSize;
		}
		public void setPlanetMaxSize(int planetMaxSize) {
			mPlanetMaxSize = planetMaxSize;
		}
		public int getSolarMinPlanets() {
			return mSolarMinPlanets;
		}
		public void setSolarMinPlanets(int solarMinPlanets) {
			mSolarMinPlanets = solarMinPlanets;
		}
		public int getSolarMaxPlanets() {
			return mSolarMaxPlanets;
		}
		public void setSolarMaxPlanets(int solarMaxPlanets) {
			mSolarMaxPlanets = solarMaxPlanets;
		}
		public int getGalaxyMinSolars() {
			return mGalaxyMinSolars;
		}
		public void setGalaxyMinSolars(int galaxyMinSolars) {
			mGalaxyMinSolars = galaxyMinSolars;
		}
		public int getGalaxyMaxSolars() {
			return mGalaxyMaxSolars;
		}
		public void setGalaxyMaxSolars(int galaxyMaxSolars) {
			mGalaxyMaxSolars = galaxyMaxSolars;
		}
		public int getUniverseMinGalaxy() {
			return mUniverseMinGalaxy;
		}
		public void setUniverseMinGalaxy(int universeMinGalaxy) {
			mUniverseMinGalaxy = universeMinGalaxy;
		}
		public int getUniverseMaxGalaxy() {
			return mUniverseMaxGalaxy;
		}
		public void setUniverseMaxGalaxy(int universeMaxGalaxy) {
			mUniverseMaxGalaxy = universeMaxGalaxy;
		}
		public int getGalaxies() {
			return mUniverseMinGalaxy + (int)(Math.random() * (mUniverseMaxGalaxy-mUniverseMinGalaxy));
		}
		public int getSolars(int nbsGalaxies) {
			return mGalaxyMinSolars + (int)(Math.random() * (mGalaxyMaxSolars-mGalaxyMinSolars));
		}
		public int getPlanets(int nbsSolars) {
			return mSolarMinPlanets + (int)(Math.random() * (mSolarMaxPlanets-mSolarMinPlanets));
		}
		public int getPlanetSize(int nbsPlanets) {
			return mPlanetMinSize + (int)(Math.random() * (mPlanetMaxSize - mPlanetMinSize));
		}
		public String toString(){
			StringBuilder sb = new StringBuilder();
			sb.append("{");
			sb.append("\"class\":\""+this.getClass().getName()+"\",");
			sb.append("\"mPlanetMinSize\":"+mPlanetMinSize+",");
			sb.append("\"mPlanetMaxSize\":"+mPlanetMaxSize+",");
			sb.append("\"mSolarMinPlanets\":"+mSolarMinPlanets+",");
			sb.append("\"mSolarMaxPlanets\":"+mSolarMaxPlanets+",");
			sb.append("\"mGalaxyMinSolars\":"+mGalaxyMinSolars+",");
			sb.append("\"mGalaxyMaxSolars\":"+mGalaxyMaxSolars+",");
			sb.append("\"mUniverseMinGalaxy\":"+mUniverseMinGalaxy+",");
			sb.append("\"mUniverseMaxGalaxy\":"+mUniverseMaxGalaxy);
			sb.append("}");
			return sb.toString();
		}
	}

	private static Long universeID = Long.valueOf(0);
	protected Long mID;
	protected Long[] mGalaxies;
	private UniverseListener mUL;
	public Universe(String path){
		synchronized (universeID) {
			File[] files = (new File(path)).listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					return filename.startsWith("universe_") && filename.endsWith(".json");
				}
			});
			for(int i =0;i<files.length;i++){
				Long id = Long.valueOf(files[i].getName().replaceAll("[^0-9]", ""));
				if(id-1 > universeID)
					universeID=id;
			}
			mID=++universeID;
		}
	}
	public Universe(Long id){
		synchronized (universeID) {
			universeID = Long.valueOf(id.longValue()-1);
			mID = ++universeID;
		}
	}
	protected void add(Galaxy g, int index) {
		mGalaxies[index] = g.getID();
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"class\":\""+this.getClass().getName()+"\",");
		sb.append("\"mGalaxies\":[");
		for(int i=0;i<mGalaxies.length;i++){
			sb.append((i>0 ? ", " : "")+mGalaxies[i].toString());
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
			FileOutputStream fos = new FileOutputStream(path+"/universe_"+mID+".json");
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
	public static Universe load(Long id, String path){
		JSONObject json;
		try {
			FileInputStream fis = new FileInputStream(path+"/universe_"+id+".json");
			StringBuilder sb = new StringBuilder();
			while(fis.available()!=0){
				sb.append((char)fis.read());
			}
			fis.close();
			json = new JSONObject(sb.toString());
			Universe u = new Universe(id);
			JSONArray galaxies = json.getJSONArray("mGalaxies");
			u.mGalaxies = new Long[galaxies.length()];
			for(int i=0;i<galaxies.length();i++){
				u.mGalaxies[i] = Long.valueOf(galaxies.getLong(i));
			}
			return u;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	public void generate(UniverseParameters up, String path){
		//TODO: GEN random location of space object
		int nbsGalaxies = up.getGalaxies();
		mGalaxies = new Long[nbsGalaxies];
		boolean[] places = new boolean[100*100];
		for(int i=0;i<nbsGalaxies;i++){
			if(mUL!=null){
				mUL.onUniverseProgress(i, nbsGalaxies);
			}
			int nbsSolars = up.getSolars(nbsGalaxies);
			Galaxy g = new Galaxy(nbsSolars);
			char _x = (char) Math.floor(Math.random() * 100);
			char _y = (char) Math.floor(Math.random() * 100);
			while(places[_x+100*_y]){
				_x = (char) Math.floor(Math.random() * 100);
				_y = (char) Math.floor(Math.random() * 100);
			}
			g.setX(_x);
			g.setY(_y);
			boolean[] places2 = new boolean[100*100];
			for(int j=0;j<nbsSolars;j++){
				if(mUL!=null){
					mUL.onGalaxyProgress(j, nbsSolars);
				}
				int nbsPlanets = up.getPlanets(nbsSolars);
				Solar s = new Solar(nbsPlanets);
				char _x2 = (char) Math.floor(Math.random() * 100);
				char _y2 = (char) Math.floor(Math.random() * 100);
				while(places2[_x+100*_y]){
					_x2 = (char) Math.floor(Math.random() * 100);
					_y2 = (char) Math.floor(Math.random() * 100);
				}
				s.setX(_x2);
				s.setY(_y2);
				for(int k=0;k<nbsPlanets;k++){
					if(mUL!=null){
						mUL.onSolarProgress(k, nbsPlanets);
					}
					int planetSize = up.getPlanetSize(nbsPlanets);
					Planet p = new Planet(planetSize);
					for(int y = 0;y<planetSize;y++){
						for(int x=0;x<planetSize;x++){
							if(mUL!=null){
								mUL.onPlanetProgress(x + y*planetSize, planetSize*planetSize);
							}
							Block b;
							if(y < 3 + Math.floor(Math.random() * planetSize / 10.0)){
								b = Block.genBlock(x,y,BlockType.BED_ROCK);
								p.add(b);
							}
							else{
								b = Block.genBlock(x,y,BlockType.AIR);
							}
						}
					}
					p.save(path);
					s.add(p,k);
				}
				s.save(path);
				g.add(s,j);
			}
			g.save(path);
			this.add(g,i);
		}
		save(path);
	}
	public void setListener(UniverseListener ul) {
		mUL = ul;
	}
	public UniverseListener getListener() {
		return mUL;
	}
	public int length() {
		return mGalaxies.length;
	}
	public Galaxy getGalaxy(int i, String path){
		return Galaxy.load(mGalaxies[i], path);
	}
}
