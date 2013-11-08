package com.github.westonpace.jayvee.util;

import java.awt.image.BufferedImage;

import com.github.westonpace.jayvee.image.Image;

public class J2SEUtils {
		
	public static BufferedImage bufferedImageFromImage(Image image) {
		int imageType = -1;
		if(image.getNumBands() == 3) {
			imageType = BufferedImage.TYPE_3BYTE_BGR;
		} else if (image.getNumBands() == 1) {
			imageType = BufferedImage.TYPE_BYTE_GRAY;
		} else {
			throw new InvalidParameterException("Don't know how to convert an image with " + image.getNumBands() + " to a BufferedImage");
		}
		BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), imageType);
		for(int y = 0; y < image.getHeight(); y++) {
			for(int x = 0; x < image.getWidth(); x++) {
				if(image.getNumBands() == 1) {
					int pixel = (int)Math.round(image.get(x, y, 0));
					if(pixel < 0 || pixel > 255) {
						throw new InvalidParameterException("Illegal pixel value, must be between 0-255 when writing an image (" + x + "," + y + ") = " + pixel);
					}
					result.getRaster().setSample(x, y, 0, pixel);
				} else {
					int red = (int) Math.round(image.get(x, y, 0));
					int green = (int) Math.round(image.get(x, y, 1));
					int blue = (int) Math.round(image.get(x, y, 2));
					if(red < 0 || green < 0 || blue < 0 || red > 255 || green > 255 || blue > 255) {
						throw new InvalidParameterException("Illegal pixel value, must be between 0-255 when writing an image (" + x + "," + y + ") = (" + red + "," + green + "," + blue + ")");
					}
					int val = red;
					val = green + (val << 8);
					val = blue + (val << 8);
					result.setRGB(x, y, val);
				}
			}
		}
		return result;
	}	

}
