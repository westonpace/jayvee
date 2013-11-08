package com.github.westonpace.jayvee.image;

import org.junit.Assert;
import org.junit.Test;

import com.github.westonpace.jayvee.image.StandardImage;
import com.github.westonpace.jayvee.image.Image.PixelOrder;
import com.github.westonpace.jayvee.test.TestBase;
import com.github.westonpace.jayvee.util.InvalidParameterException;
import com.github.westonpace.jayvee.util.Sinkerator;

public class StandardImageTest extends TestBase {

	/**
	 * Creates an empty image and confirms that all values are set to 0
	 */
	@Test
	public void testEmptyImage() {
		// RGB
		StandardImage image = new StandardImage(3, 3, 3);
		for (double value : image.getPixels()) {
			Assert.assertEquals(0.0, value, 0.0);
		}
		// Grayscale
		image = new StandardImage(3, 5, 1);
		for (double value : image.getPixels()) {
			Assert.assertEquals(0.0, value, 0.0);
		}
	}

	/**
	 * Tests row-major iteration
	 */
	@Test
	public void testRowMajorOrder() {
		double[] values = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		StandardImage image = new StandardImage(3, 3, 1, values);

		int index = 0;
		for (double value : image.getPixels(PixelOrder.RowMajor)) {
			Assert.assertEquals(values[index], value, 0.0);
			index++;
		}

		values = new double[] { 1, 1, 2, 2, 3, 3, 4, 4 };
		image = new StandardImage(2, 2, 2, values);
		index = 0;
		for (double value : image.getPixels(PixelOrder.RowMajor)) {
			Assert.assertEquals(values[index], value, 0.0);
			index++;
		}
	}

	/**
	 * Tests column-major iteration
	 */
	@Test
	public void testColumnMajorOrder() {
		double[] values = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		double[] expected = new double[] { 1, 4, 7, 2, 5, 8, 3, 6, 9 };
		StandardImage image = new StandardImage(3, 3, 1, values);

		int index = 0;
		for (double value : image.getPixels(PixelOrder.ColumnMajor)) {
			Assert.assertEquals(expected[index], value, 0.0);
			index++;
		}

		values = new double[] { 1, 1, 2, 2, 3, 3, 4, 4 };
		expected = new double[] { 1, 1, 3, 3, 2, 2, 4, 4 };
		image = new StandardImage(2, 2, 2, values);

		index = 0;
		for (double value : image.getPixels(PixelOrder.ColumnMajor)) {
			Assert.assertEquals(expected[index], value, 0.0);
			index++;
		}

		values = new double[] { 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4 };
		expected = new double[] { 1, 1, 1, 3, 3, 3, 2, 2, 2, 4, 4, 4 };
		image = new StandardImage(2, 2, 3, values);

		index = 0;
		for (double value : image.getPixels(PixelOrder.ColumnMajor)) {
			Assert.assertEquals(expected[index], value, 0.0);
			index++;
		}

	}

	@Test
	public void testWritingRowMajorOrder() {
		//Try writing to a 3x3x1 image
		double[] values = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		StandardImage image = new StandardImage(3, 3, 1);

		//Write the values
		Sinkerator<Double> pixelWriter = image.getPixelWriter();
		int index = 0;
		while(pixelWriter.hasNext()) {
			pixelWriter.putNext(values[index]);
			index++;
		}
		
		//Read them back out and make sure they are what we put in
		index = 0;
		for (double value : image.getPixels(PixelOrder.RowMajor)) {
			Assert.assertEquals(values[index], value, 0.0);
			index++;
		}

		//Try writing to a 2x2x2 image
		values = new double[] { 1, 1, 2, 2, 3, 3, 4, 4};
		image = new StandardImage(2, 2, 2);

		//Write the values
		pixelWriter = image.getPixelWriter();
		index = 0;
		while(pixelWriter.hasNext()) {
			pixelWriter.putNext(values[index]);
			index++;
		}
		
		//Read them back out and ensure they are what was expected
		index = 0;
		for (double value : image.getPixels(PixelOrder.RowMajor)) {
			Assert.assertEquals(values[index], value, 0.0);
			index++;
		}

	}

	@Test
	public void testWritingColumnMajorOrder() {
		//Try writing to a 3x3x1 image
		double[] values = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		StandardImage image = new StandardImage(3, 3, 1);

		//Write the values
		Sinkerator<Double> pixelWriter = image.getPixelWriter(PixelOrder.ColumnMajor);
		int index = 0;
		while(pixelWriter.hasNext()) {
			pixelWriter.putNext(values[index]);
			index++;
		}
		
		//Read them back out and make sure they are what we put in
		index = 0;
		for (double value : image.getPixels(PixelOrder.ColumnMajor)) {
			Assert.assertEquals(values[index], value, 0.0);
			index++;
		}

		//Try writing to a 2x2x2 image
		values = new double[] { 1, 1, 2, 2, 3, 3, 4, 4};
		image = new StandardImage(2, 2, 2);

		//Write the values
		pixelWriter = image.getPixelWriter(PixelOrder.ColumnMajor);
		index = 0;
		while(pixelWriter.hasNext()) {
			pixelWriter.putNext(values[index]);
			index++;
		}
		
		//Read them back out and ensure they are what was expected
		index = 0;
		for (double value : image.getPixels(PixelOrder.ColumnMajor)) {
			Assert.assertEquals(values[index], value, 0.0);
			index++;
		}

	}
	
	/**
	 * Tests accessing image values using get(x, y, b)
	 */
	@Test
	public void testImageRandomAccess() {
		double [] values = new double[] {1, 2, 3, 4, 5, 6, 7, 8, 9};
		StandardImage image = new StandardImage(3, 3, 1, values);
		
		int index = 0;
		for(int y = 0; y < 3; y++) {
			for(int x = 0; x < 3; x++) {
				Assert.assertEquals(values[index], image.get(x, y, 0), 0.0);
				index++;
			}
		}

		values = new double[] {1, 1, 2, 2, 3, 3, 4, 4};
		image = new StandardImage(2, 2, 2, values);
		
		index = 0;
		for(int y = 0; y < 2; y++) {
			for(int x = 0; x < 2; x++) {
				for(int b = 0; b < 2; b++) {
					Assert.assertEquals(values[index], image.get(x, y, b), 0.0);
					index++;
				}
			}
		}

	}
	
	/**
	 * Tests writing image values using set(x, y, b, value)
	 */
	@Test
	public void testImageSetAccess() {
		double [] values = new double[] {1, 2, 3, 4, 5, 6, 7, 8, 9};
		StandardImage image = new StandardImage(3, 3, 1);
		
		int index = 0;
		for(int y = 0; y < 3; y++) {
			for(int x = 0; x < 3; x++) {
				image.set(x, y, 0, values[index]);
				index++;
			}
		}
		
		index = 0;
		for(Double value : image.getPixels()) {
			Assert.assertEquals(values[index], value, 0.0);
			index++;
		}

		values = new double[] {1, 1, 2, 2, 3, 3, 4, 4};
		image = new StandardImage(2, 2, 2);
		
		index = 0;
		for(int y = 0; y < 2; y++) {
			for(int x = 0; x < 2; x++) {
				for(int b = 0; b < 2; b++) {
					image.set(x, y, b, values[index]);
					index++;
				}
			}
		}
		
		index = 0;
		for(Double value : image.getPixels()) {
			Assert.assertEquals(values[index], value, 0.0);
			index++;
		}
		
	}
	
	@Test(expected=InvalidParameterException.class)
	public void testNegativeWidth() {
		new StandardImage(-1, 1, 1);
	}

	@Test(expected=InvalidParameterException.class)
	public void testNegativeHeight() {
		new StandardImage(1, -1, 1);
	}

	@Test(expected=InvalidParameterException.class)
	public void testNegativeNumBands() {
		new StandardImage(1, 1, -1);
	}

	@Test(expected=InvalidParameterException.class)
	public void testZeroWidth() {
		new StandardImage(0, 1, 1);
	}

	@Test(expected=InvalidParameterException.class)
	public void testZeroHeight() {
		new StandardImage(1, 0, 1);
	}

	@Test(expected=InvalidParameterException.class)
	public void testZeroNumBands() {
		new StandardImage(1, 1, 0);
	}
	
	@Test(expected=InvalidParameterException.class)
	public void testTooFewValues() {
		new StandardImage(2, 1, 1, new double [] {1});
	}

	@Test(expected=InvalidParameterException.class)
	public void testTooManyValues() {
		new StandardImage(2, 1, 1, new double [] {1, 2, 3});
	}

}
