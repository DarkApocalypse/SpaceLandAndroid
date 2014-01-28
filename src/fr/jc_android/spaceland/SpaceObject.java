package fr.jc_android.spaceland;

public interface SpaceObject {
	public Long[] setLocation(Long[] location);
	public Long[] getLocation();
	public String getPath();
	public void save();
	public String toString();
}