package fr.jc_android.spaceland;

public class Utils {
	public static Long[] reverse(Long[] e){
		Long[] l = new Long[e.length];
		for(int i=0;i<e.length;i++){
			l[e.length-1-i] = e[i];
		}
		return l;
	}
}
