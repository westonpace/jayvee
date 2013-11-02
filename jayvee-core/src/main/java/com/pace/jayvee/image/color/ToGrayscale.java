package com.pace.jayvee.image.color;

import com.pace.jayvee.image.Image;
import com.pace.jayvee.image.worker.ImageTransformer;

/**
 * This transformer takes in an image with any number of bands and reduces it to
 * a single band image.  It does this by taking the average value across all of the
 * bands.
 * 
 * For color images this has the effect of turning them into grayscale images.
 */
public class ToGrayscale extends ImageTransformer {

	@Override
	protected void transform(Image input, Image output) {
		for(int y = 0; y < input.getHeight(); y++) {
			for(int x = 0; x < input.getWidth(); x++) {
				//For each pixel, compute the average by summing up the value in
				//each band and dividing by the total number of bands
				double sum = 0;
				for(int b = 0; b < input.getNumBands(); b++) {
					sum += input.get(x, y, b);
				}
				output.set(x, y, 0, sum/input.getNumBands());
			}
		}
	}

	@Override
	public void init() {
		//No initialization necessary
	}

	@Override
	protected int getOutputBands(Image input) {
		//Regardless of how many bands are passed in we always pass out 1 band
		return 1;
	}

}
