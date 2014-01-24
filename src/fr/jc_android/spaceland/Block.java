package fr.jc_android.spaceland;

public abstract class Block implements Entity{
	private static Long blockID = Long.valueOf(0);
	protected Long mID;
	protected int mX;
	protected int mY;
	public enum BlockType{
		AIR,
		BED_ROCK
	}
	public Block(int x, int y){
		synchronized (Block.blockID) {
			mID = ++Block.blockID;	
		}
		mX=x;
		mY=y;
	}
	@Override
	public abstract String toString();
	public abstract BlockType getType();
	@Override
	public Long getID() {
		return mID;
	}
	@Override
	public abstract boolean save(String path);
	public int getX() {
		return mX;
	}
	public int getY() {
		return mY;
	}
	
}
