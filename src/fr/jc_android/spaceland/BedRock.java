package fr.jc_android.spaceland;

import java.io.FileOutputStream;

public class BedRock extends Block {
	public BedRock(int x, int y) {
		super(x, y);
	}

	@Override
	public BlockType getType() {
		return BlockType.BED_ROCK;
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("{\"class\":\""+this.getClass().getName()+"\",");
		sb.append("\"mX\":"+mX+",\"mY\":"+mY+",\"type\":\""+getType().name()+"\"");
		sb.append("}");
		return sb.toString();
	}
	
	@Override
	public boolean save(String path) {
		try {
			FileOutputStream fos = new FileOutputStream(path+"/blocks_"+mID+".json");
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
