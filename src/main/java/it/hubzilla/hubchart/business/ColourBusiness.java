package it.hubzilla.hubchart.business;

public class ColourBusiness {

	public static String getColourShade(String startColour, String endColour,
			Double value, Double total) {
		int start[] = getRGB(startColour);
		int end[] = getRGB(endColour);
		int out[] = new int[3];
		for (int i=0; i<3; i++) {
			Double v = (new Double(start[i]-end[i]))*(value/total)+new Double(end[i]);
			out[i] = Math.round(v.floatValue());
		}
		String result = String.format("#%02x%02x%02x", out[0], out[1], out[2]);
		return result;
	}
	
	public static int[] getRGB(String rgb){
		if (rgb.startsWith("#")) rgb = rgb.substring(1);
	    int[] ret = new int[3];
	    for(int i=0; i<3; i++){
	    	String hex = rgb.substring(i*2, i*2+2);
	    	ret[i] = Integer.parseInt(hex,16);
	    }
	    return ret;
	}
	
}
