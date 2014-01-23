package fr.jc_android.spaceland;

public interface Entity {
	public Long getID();
	public boolean save(String path);
	/*public Entity fromJSON(String path);*/
}
