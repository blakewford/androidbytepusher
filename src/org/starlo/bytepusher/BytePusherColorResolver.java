package org.starlo.bytepusher;

//Try to push into Mark's library
public class BytePusherColorResolver {
	
	public static int getRGBAtCoordinate(int x, int y, char[] data){
		int color = 0;
		int datum = data[(y*256)+x];
		if ( datum < 216 ) {
			int blue = datum % 6;
			int green = ((datum - blue) / 6) % 6;
			int red = ((datum - blue - (6 * green)) / 36) % 6;
		
			color = (red*0x33 << 16) + (green*0x33 <<8) + (blue*0x33 );
		}
		
		return color;
	}
	
}
