package fr.jc_android.spaceland;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Universe implements Entity{
	public static class UniverseParameters{
		protected int mPlanetMinSize = 10;
		protected int mPlanetMaxSize = 100;
		protected int mSolarMinPlanets = 0;
		protected int mSolarMaxPlanets = 10;
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
				Long id = new Long(files[i].getName().replaceAll("[^0-9]", ""));
				if(id-1 > universeID)
					universeID=id;
			}
			mID=++universeID;
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
		File dir = new File(path);
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.startsWith("universe") && filename.endsWith("universe");
			}
		});
		for(int i=0;i<files.length;i++){
			JSONTokener jsont = new JSONTokener(files[i].getAbsolutePath());
			try {
				JSONObject json = (JSONObject) jsont.nextValue();
				if(json==null || !(json instanceof JSONObject))
					throw new Exception("Invalid JSON Object");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	public void generate(UniverseParameters up, String path){
		int nbsGalaxies = up.getGalaxies();
		mGalaxies = new Long[nbsGalaxies];
		for(int i=0;i<nbsGalaxies;i++){
			if(mUL!=null){
				mUL.onUniverseProgress(i, nbsGalaxies);
			}
			int nbsSolars = up.getSolars(nbsGalaxies);
			Galaxy g = new Galaxy(nbsSolars);
			for(int j=0;j<nbsSolars;j++){
				if(mUL!=null){
					mUL.onGalaxyProgress(j, nbsSolars);
				}
				int nbsPlanets = up.getPlanets(nbsSolars);
				Solar s = new Solar(nbsPlanets);
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
								b = new BedRock(x,y);
								p.add(b);
							}
							else{
								b = new Air(x,y);
							}
							b.save(path);
						}
					}
					p.save(path);
					s.add(p,k);
				}
				g.add(s,j);
			}
			this.add(g,i);
		}
	}
	public void setListener(UniverseListener ul) {
		mUL = ul;
	}
	public UniverseListener getListener() {
		return mUL;
	}
}
