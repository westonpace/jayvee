package com.github.westonpace.jayvee.image.io.jse;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import com.github.westonpace.jayvee.image.Image;
import com.github.westonpace.jayvee.image.io.jse.ImageIOImageReader;
import com.github.westonpace.jayvee.test.ArrayListBuffer;
import com.github.westonpace.jayvee.test.TestBase;

public class ImageIOImageReaderTest extends TestBase {

	/**
	 * Test loading the logo in a variety of formats and ensure the same data is loaded
	 */
	@Test
	public void testBasicRead() {
		ArrayListBuffer<InputStream> inputImages = new ArrayListBuffer<InputStream>();
		inputImages.add(getLogoPngStream());
		inputImages.add(getLogoPngGrayStream());
		inputImages.add(getLogoBmpStream());
		inputImages.add(getLogoBmpGrayStream());
		
		ImageIOImageReader reader = new ImageIOImageReader();
		ArrayListBuffer<Image> outputImages = new ArrayListBuffer<Image>();
		reader.generatedImages = outputImages;
		reader.inputStreamsToRead = inputImages;
		
		reader.iterate();
		reader.iterate();
		reader.iterate();
		reader.iterate();
		
		Assert.assertEquals(4, outputImages.size());
		
		Image colorPng = outputImages.pop();
		Image grayPng = outputImages.pop();
		Image colorBmp = outputImages.pop();
		Image grayBmp = outputImages.pop();
		
		//All images should be the same dimensions
		Assert.assertEquals(colorPng.getWidth(), grayPng.getWidth());
		Assert.assertEquals(grayPng.getWidth(), colorBmp.getWidth());
		Assert.assertEquals(colorBmp.getWidth(), grayBmp.getWidth());
		Assert.assertEquals(colorPng.getHeight(), grayPng.getHeight());
		Assert.assertEquals(grayPng.getHeight(), colorBmp.getHeight());
		Assert.assertEquals(colorBmp.getHeight(), grayBmp.getHeight());
		
		//The grays should be 1 band, the colors 3 bands
		Assert.assertEquals(1, grayPng.getNumBands());
		Assert.assertEquals(1, grayBmp.getNumBands());
		Assert.assertEquals(3, colorPng.getNumBands());
		Assert.assertEquals(3, colorBmp.getNumBands());

		//The gray images should be identical and the color images should be identical
		for(int y = 0; y < colorPng.getHeight(); y++) {
			for(int x = 0; x < colorPng.getWidth(); x++) {
				Assert.assertEquals(grayPng.get(x, y, 0), grayBmp.get(x, y, 0), 0.0);
				Assert.assertEquals(colorPng.get(x, y, 0), colorBmp.get(x, y, 0), 0.0);
				Assert.assertEquals(colorPng.get(x, y, 1), colorBmp.get(x, y, 1), 0.0);
				Assert.assertEquals(colorPng.get(x, y, 2), colorBmp.get(x, y, 2), 0.0);
			}
		}
	}
	
	/**
	 * Test images which have known values (the 3x3 grids)
	 */
	@Test
	public void testKnownImages() {
		ArrayListBuffer<InputStream> inputImages = new ArrayListBuffer<InputStream>();
		inputImages.add(get3by3GrayStream());
		inputImages.add(get3by3Stream());
		
		ImageIOImageReader reader = new ImageIOImageReader();
		ArrayListBuffer<Image> outputImages = new ArrayListBuffer<Image>();
		reader.generatedImages = outputImages;
		reader.inputStreamsToRead = inputImages;
		
		reader.iterate();
		reader.iterate();
		
		Assert.assertEquals(2, outputImages.size());
		
		Image gray = outputImages.pop();
		Image color = outputImages.pop();
		
		double [] values = new double [] {0, 1, 2, 
				                          3, 4, 5, 
				                          6, 7, 8};
		
		int index = 0;
		for(int y = 0; y < 3; y++) {
			for(int x = 0; x < 3; x++) {
				Assert.assertEquals(values[index], gray.get(x, y, 0), 0.0);
				Assert.assertEquals(values[index], color.get(x, y, 0), 0.0);
				Assert.assertEquals(values[index], color.get(x, y, 1), 0.0);
				Assert.assertEquals(values[index], color.get(x, y, 2), 0.0);
				index++;
			}
		}
	}
}
