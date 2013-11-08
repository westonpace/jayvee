package com.github.westonpace.jayvee.image.io.jse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Assert;
import org.junit.Test;

import com.github.westonpace.jayvee.image.Image;
import com.github.westonpace.jayvee.image.StandardImage;
import com.github.westonpace.jayvee.image.io.jse.ImageIOImageWriter;
import com.github.westonpace.jayvee.test.ArrayListBuffer;
import com.github.westonpace.jayvee.test.TestBase;
import com.github.westonpace.jayvee.util.InvalidParameterException;

public class ImageIOImageWriterTest extends TestBase {
	
	private byte [] readInputStreamIntoByteArray(InputStream input) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[1024*1024*4];

		while ((nRead = input.read(data, 0, data.length)) != -1) {
		  buffer.write(data, 0, nRead);
		}

		buffer.flush();

		return buffer.toByteArray();
	}
	
	/**
	 * Writes an image, reads it back in, ensures it is the same
	 */
	private void testImage(InputStream imageInputStream, String format) throws IOException {
		ArrayListBuffer<String> formats = new ArrayListBuffer<String>();
		ArrayListBuffer<Image> inputImages = new ArrayListBuffer<Image>();
		ArrayListBuffer<OutputStream> outputStreams = new ArrayListBuffer<OutputStream>();

		byte [] originalBytes = readInputStreamIntoByteArray(imageInputStream);
		Image image = imageStreamToImage(new ByteArrayInputStream(originalBytes));
		
		inputImages.push(image);
		formats.push(format);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStreams.push(outputStream);

		ImageIOImageWriter writer = new ImageIOImageWriter();
		writer.imageFormats = formats;
		writer.imagesToWrite = inputImages;
		writer.outputStreamsToWriteTo = outputStreams;
		
		writer.iterate();

		byte [] writtenBytes = outputStream.toByteArray();

		Image rereadImage = imageStreamToImage(new ByteArrayInputStream(writtenBytes));
		
		Assert.assertEquals(image.getWidth(), rereadImage.getWidth());
		Assert.assertEquals(image.getHeight(), rereadImage.getHeight());
		Assert.assertEquals(image.getNumBands(), rereadImage.getNumBands());
		
		for(int y = 0; y < image.getHeight(); y++) {
			for(int x = 0; x < image.getWidth(); x++) {
				for(int b = 0; b < image.getNumBands(); b++) {
					Assert.assertEquals(image.get(x, y, b), rereadImage.get(x, y, b), 0.0);
				}
			}
		}
	}
	
	/**
	 * Read in an image, write it out with the image writer, read it back in, make sure
	 * the image is the same
	 */
	@Test
	public void testLogoImages() throws IOException {
		testImage(getLogoPngStream(), "png");
		testImage(getLogoPngGrayStream(), "png");
		testImage(getLogoBmpStream(), "bmp");
		testImage(getLogoBmpGrayStream(), "bmp");
	}

	@Test(expected=InvalidParameterException.class)
	public void testNegativeGray() throws IOException {
		double [] pixels = new double[] {-3, 0, 0, 0};
		StandardImage image = new StandardImage(2, 2, 1, pixels);
		
		ImageIOImageWriter writer = new ImageIOImageWriter();
		ArrayListBuffer<Image> images = new ArrayListBuffer<Image>();
		ArrayListBuffer<String> formats = new ArrayListBuffer<String>();
		ArrayListBuffer<OutputStream> outputStreams = new ArrayListBuffer<OutputStream>();
		
		writer.imageFormats = formats;
		writer.imagesToWrite = images;
		writer.outputStreamsToWriteTo = outputStreams;
		
		formats.push("png");
		images.push(image);
		outputStreams.push(new ByteArrayOutputStream());
		
		writer.iterate();
	}

	@Test(expected=InvalidParameterException.class)
	public void testNegativeColor() throws IOException {
		double [] pixels = new double[] {0, 0, 0, -3, 0, 0, 0, 0, 0, 0, 0, 0};
		StandardImage image = new StandardImage(2, 2, 3, pixels);
		
		ImageIOImageWriter writer = new ImageIOImageWriter();
		ArrayListBuffer<Image> images = new ArrayListBuffer<Image>();
		ArrayListBuffer<String> formats = new ArrayListBuffer<String>();
		ArrayListBuffer<OutputStream> outputStreams = new ArrayListBuffer<OutputStream>();
		
		writer.imageFormats = formats;
		writer.imagesToWrite = images;
		writer.outputStreamsToWriteTo = outputStreams;
		
		formats.push("png");
		images.push(image);
		outputStreams.push(new ByteArrayOutputStream());
		
		writer.iterate();
	}

}
