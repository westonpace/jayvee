package com.pace.jayvee.util;

import java.awt.image.BufferedImage;

import com.pace.jayvee.image.Image;

public class J2SEUtils {
		
	public static BufferedImage bufferedImageFromImage(Image image) {
		BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		for(int y = 0; y < image.getHeight(); y++) {
			for(int x = 0; x < image.getWidth(); x++) {
				if(image.getNumBands() == 1) {
					int pixel = (int)Math.round(image.get(x, y, 0));
					int val = pixel;
					val = pixel + (val << 8);
					val = pixel + (val << 8);
					result.setRGB(x, y, (int)val);
				} else {
					int val = (int) Math.round(image.get(x, y, 0));
					val = (int) Math.round(image.get(x, y, 1)) + (val << 8);
					val = (int) Math.round(image.get(x, y, 2)) + (val << 8);
					result.setRGB(x, y, val);
				}
			}
		}
		return result;
	}	

}
