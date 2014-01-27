package fr.jc_android.spaceland;

public class Block{
	private static Long blockID = Long.valueOf(0);
	protected Long mID;
	protected int mX;
	protected int mY;
	protected BlockType mType;
	public enum BlockType{
		AIR(true),
		BED_ROCK();
		private boolean mTraversable;
		private Object[] mTools;
		private BlockType(){
			mTraversable=false;
		}
		private BlockType(boolean traversable){
			mTraversable=traversable;
		}
		private BlockType(boolean traversable, Object[] tools){
			mTraversable=traversable;
			mTools = tools.clone();
		}
		public boolean isTraversable(){
			return mTraversable;
		}
		public Object[] tools(){
			return mTools;
		}
	}
	public Block(int x, int y, BlockType type){
		synchronized (Block.blockID) {
			mID = ++Block.blockID;	
		}
		mX=x;
		mY=y;
		mType = type;
	}
	@Override
	public String toString(){
		return String.valueOf(mType.ordinal());
	};
	public BlockType getType(){
		return mType;
	}
	public int getX() {
		return mX;
	}
	public int getY() {
		return mY;
	}
	public static Block genBlock(int x, int y, BlockType type){
		return new Block(x,y,type);
	}
}
