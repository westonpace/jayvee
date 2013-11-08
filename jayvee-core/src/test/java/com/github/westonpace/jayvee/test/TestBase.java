package com.github.westonpace.jayvee.test;

import java.io.InputStream;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.junit.BeforeClass;

import com.github.westonpace.jayvee.image.Image;
import com.github.westonpace.jayvee.image.io.jse.ImageIOImageReader;

public class TestBase {

	private static boolean loggingSquelched = false;
	
	@BeforeClass
	public static void squelchLogging() {
		if(!loggingSquelched) {
			LogManager.getRootLogger().addAppender(new ConsoleAppender());
			LogManager.getRootLogger().setLevel(Level.ERROR);
			loggingSquelched = true;
		}
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
	
}
