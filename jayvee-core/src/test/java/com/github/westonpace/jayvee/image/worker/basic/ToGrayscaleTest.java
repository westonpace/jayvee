package com.github.westonpace.jayvee.image.worker.basic;

import org.junit.Assert;
import org.junit.Test;

import com.github.westonpace.jayvee.image.Image;
import com.github.westonpace.jayvee.image.StandardImage;
import com.github.westonpace.jayvee.image.worker.basic.ToGrayscale;
import com.github.westonpace.jayvee.test.ArrayListBuffer;
import com.github.westonpace.jayvee.test.TestBase;

public class ToGrayscaleTest extends TestBase {

	/**
	 * Take an image that has been grayscaled by GIMP and compare it to an image we
	 * grayscale ourselves, make sure it matches.
	 */
	@Test
	public void test() {
		Image colorInput = imageStreamToImage(getLogoBmpStream());
		
		ArrayListBuffer<Image> inputImages = newBuffer(colorInput);
		ArrayListBuffer<Image> outputImages = new ArrayListBuffer<Image>();
		
		ToGrayscale toGrayscale = new ToGrayscale();
		toGrayscale.inputImages = inputImages;
		toGrayscale.outputImages = outputImages;
		
		toGrayscale.init();
		toGrayscale.iterate();
		
		Image grayedImage = outputImages.pop();
		Assert.assertEquals(1, grayedImage.getNumBands());
		
		Image groundTruthGray = imageStreamToImage(getLogoBmpGrayStream());
		
		Assert.assertEquals(groundTruthGray.getWidth(), grayedImage.getWidth());
		Assert.assertEquals(groundTruthGray.getHeight(), grayedImage.getHeight());
		
		for(int x = 0; x < groundTruthGray.getWidth(); x++) {
			for(int y = 0; y < groundTruthGray.getHeight(); y++) {
				//We may have to round to the nearest value as the GIMP ground truth will
				//not have fractional values
				Assert.assertEquals("Incorrect pixel value at " + x + "," + y, groundTruthGray.get(x, y, 0), Math.round(grayedImage.get(x,y,0)), 0.0);
			}
		}
	}
	

	/**
	 * Test the grayscale algorithm on some known values
	 */
	@Test
	public void testKnowValues() {
		
		double [] values = new double [] {1, 2, 3,   4, 4, 4,
				                          9, 3, 1,   0, 0, 0};
		Image image = new StandardImage(2, 2, 3, values);
	
		ArrayListBuffer<Image> inputImages = newBuffer(image);
		ArrayListBuffer<Image> outputImages = new ArrayListBuffer<Image>();
		
		ToGrayscale toGrayscale = new ToGrayscale();
		toGrayscale.inputImages = inputImages;
		toGrayscale.outputImages = outputImages;
		
		toGrayscale.init();
		toGrayscale.iterate();

		Image result = outputImages.pop();
		
		Assert.assertEquals(2, result.getWidth());
		Assert.assertEquals(2, result.getHeight());
		Assert.assertEquals(1, result.getNumBands());
		
		Assert.assertEquals(2, result.get(0, 0, 0), 0.00001);
		Assert.assertEquals(4, result.get(1, 0, 0), 0.00001);
		Assert.assertEquals(4.3333333, result.get(0, 1, 0), 0.00001);
		Assert.assertEquals(0, result.get(1, 1, 0), 0.00001);
	}
}
