package com.github.westonpace.jayvee.test;

import java.io.InputStream;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.SimpleLayout;
import org.junit.Assert;
import org.junit.Before;

import com.github.westonpace.jayvee.image.Image;
import com.github.westonpace.jayvee.image.io.jse.ImageIOImageReader;

public class TestBase {

	@Before
	public void squelchLogging() {
		LogManager.getRootLogger().removeAllAppenders();
		LogManager.getRootLogger().addAppender(new ConsoleAppender(new SimpleLayout()));
		LogManager.getRootLogger().setLevel(getLoggingLevel());
	}
	
	protected Level getLoggingLevel() {
		return Level.ERROR;
	}
	
	private InputStream getTestImageAsStream(String testImageName) {
		return TestBase.class.getResourceAsStream("/com/pace/jayvee/test/test_images/" + testImageName);
	}
	
	protected InputStream getLogoPngStream() {
		return getTestImageAsStream("logo.png");
	}

	protected InputStream getLogoPngGrayStream() {
		return getTestImageAsStream("logo-gray.png");
	}

	protected InputStream getLogoBmpStream() {
		return getTestImageAsStream("logo.bmp");
	}

	protected InputStream getLogoBmpGrayStream() {
		return getTestImageAsStream("logo-gray.bmp");
	}
	
	protected InputStream get3by3GrayStream() {
		return getTestImageAsStream("3by3grid-gray.bmp");
	}

	protected InputStream get3by3Stream() {
		return getTestImageAsStream("3by3grid.bmp");
	}
	
	protected Image imageStreamToImage(InputStream stream) {
		ImageIOImageReader reader = new ImageIOImageReader();
		return reader.readImage(stream);
	}

	protected <T> ArrayListBuffer<T> newBuffer(T ... values) {
		ArrayListBuffer<T> buffer = new ArrayListBuffer<T>();
		for(T value : values) {
			buffer.push(value);
		}
		return buffer;
	}

	protected void assertEquals(Image expected, Image image, double delta) {
		Assert.assertEquals(expected.getWidth(), image.getWidth());
		Assert.assertEquals(expected.getHeight(), image.getHeight());
		Assert.assertEquals(expected.getNumBands(), image.getNumBands());
		
		for(int y = 0; y < expected.getHeight(); y++) {
			for(int x = 0; x < expected.getWidth(); x++) {
				for(int b = 0; b < expected.getNumBands(); b++) {
					Assert.assertEquals("Incorrect pixel value at (" + x + "," + y + "," + b + ")", expected.get(x, y, b), image.get(x, y, b), delta);
				}
			}
		}
	}
	
}
