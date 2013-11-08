package com.github.westonpace.jayvee.image.io.jse;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.github.westonpace.jayvee.image.Image;
import com.github.westonpace.jayvee.image.StandardImage;
import com.github.westonpace.jayvee.workflow.InputBuffer;
import com.github.westonpace.jayvee.workflow.OutputBuffer;
import com.github.westonpace.jayvee.workflow.Sink;
import com.github.westonpace.jayvee.workflow.Source;
import com.github.westonpace.jayvee.workflow.StandardWorker;

/**
 * Uses Java's ImageIO library to read in images from
 * an input stream.  Currently it is not the most ideal algorithm.  It first
 * loads images into a BufferedImage and then copies the data from the buffered image
 * into the actual StandardImage.  This has the unfortunate side effect of loading the 
 * image into memory twice.  Ideally an image reader would just spit out the StandardImage
 * directly from the input.
 * 
 * TODO: Skip BufferedImage
 * 
 * The input streams given can currently only be images that result in the ImageIO library
 * creating a TYPE_3BYTE_BGR data buffer that stores its data in bytes.  This works for
 * PNG images and JPG images and may work for others
 * 
 * TODO: Support all image formats
 */
public class ImageIOImageReader extends StandardWorker {

	/**
	 * This sink receives the images that are read in by this reader.  This reader
	 * is not a streaming reader.  It will read the entire image into memory and then
	 * place it into this sink.  Images can be quite large (8*width*height*numBands bytes)
	 * so use caution when sizing this buffer (and all future buffers that contain images).
	 */
	@OutputBuffer
	public Sink<Image> generatedImages;
	/**
	 * This input source provides input streams for image files.  If an input stream
	 * doesn't contain an image file then it will result in an error.  As input streams are
	 * not thread safe, care should be taken to ensure that the input streams coming into
	 * this worker don't go anywhere else.  This worker will read through the entire stream
	 * and then close the stream when finished.
	 */
	@InputBuffer
	public Source<InputStream> inputStreamsToRead;
	
	public Image readImage(InputStream input) {
		try {
			BufferedImage image = ImageIO.read(input);
			input.close();
			return decodeImage(image);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private double [] loadDataFromIntRgb(int width, int height, DataBufferInt dataBuffer) {
		double[] pixels = new double[width*height*3];
		int [] ints = dataBuffer.getData();
		int index = 0;
		for(int i = 0; i < ints.length; i++) {
			int rgb = ints[i];
			pixels[index] = ((rgb & 0xFF0000) >> 16);
			pixels[index+1] = ((rgb & 0xFF00) >> 8);
			pixels[index+2] = (rgb & 0xFF);
			index+=3 ;
		}
		return pixels;
	}

	private double [] loadDataFromOneByteGray(int width, int height, DataBufferByte dataBuffer) {
		double[] pixels = new double[width*height];
		byte [] bytes = dataBuffer.getData();
		for(int i = 0; i < bytes.length; i++) {
			byte gray = bytes[i];
			pixels[i] = gray & 0xFF;
		}
		return pixels;
	}

	private double [] loadDataFromTwoByteAlphaGray(int width, int height, DataBufferByte dataBuffer) {
		double[] pixels = new double[width*height];
		byte [] bytes = dataBuffer.getData();
		int index = 0;
		for(int i = 0; i < bytes.length; i+=2) {
			byte gray = bytes[i];
			pixels[index] = gray & 0xFF;
			index++;
		}
		return pixels;
	}

	private double [] loadDataFromThreeByteBgr(int width, int height, DataBufferByte dataBuffer) {
		double[] pixels = new double[width*height*3];
		byte [] bytes = dataBuffer.getData();
		for(int i = 0; i < bytes.length; i+=3) {
			byte blue = bytes[i];
			byte green = bytes[i+1];
			byte red = bytes[i+2];
			pixels[i] = red & 0xFF;
			pixels[i+1] = green & 0xFF;
			pixels[i+2] = blue & 0xFF;
		}
		return pixels;
	}
	
	private double [] loadDataFromFourByteAbgr(int width, int height, DataBufferByte dataBuffer) {
		double[] pixels = new double[width*height*3];
		byte [] bytes = dataBuffer.getData();
		int index = 0;
		for(int i = 0; i < bytes.length; i+=4) {
			byte blue = bytes[i+1];
			byte green = bytes[i+2];
			byte red = bytes[i+3];
			pixels[index] = red & 0xFF;
			pixels[index+1] = green & 0xFF;
			pixels[index+2] = blue & 0xFF;
			index+=3;
		}
		return pixels;
	}
	
	private StandardImage decodeImage(BufferedImage source) {
		Raster raster = source.getRaster();
		DataBuffer buffer = raster.getDataBuffer();
		int width = source.getWidth();
		int height = source.getHeight();
		if(buffer.getNumBanks() != 1) {
			throw new RuntimeException("Oops");
		}
		//Clearly not the best algorithm but it is reasonably fast and works
		if(source.getType() == BufferedImage.TYPE_3BYTE_BGR) {
			double [] pixels = loadDataFromThreeByteBgr(width, height, (DataBufferByte)buffer);
			return new StandardImage(width, height, 3, pixels);
		} else if (source.getType() == BufferedImage.TYPE_4BYTE_ABGR) {
			double [] pixels = loadDataFromFourByteAbgr(width, height, (DataBufferByte)buffer);
			return new StandardImage(width, height, 3, pixels);
		} else if (source.getType() == BufferedImage.TYPE_CUSTOM) { 
			//This is 2 byte buffer?!
			double [] pixels = loadDataFromTwoByteAlphaGray(width, height, (DataBufferByte)buffer);
			return new StandardImage(width, height, 1, pixels);
		} else if (source.getType() == BufferedImage.TYPE_INT_RGB) {
			double [] pixels = loadDataFromIntRgb(width, height, (DataBufferInt)buffer);
			return new StandardImage(width, height, 3, pixels);
		} else if (source.getType() == BufferedImage.TYPE_BYTE_GRAY) {
			double [] pixels = loadDataFromOneByteGray(width, height, (DataBufferByte)buffer);
			return new StandardImage(width, height, 1, pixels);
		} else {
			throw new RuntimeException("Image type not implemented: " + source.getType());
		}
	}
	
	@Override
	public void iterate() {
		InputStream inputStream = inputStreamsToRead.pop();
		Image image = readImage(inputStream);
		generatedImages.push(image);
	}

	@Override
	public void init() {
		//No initialization needed
	}
	
}
