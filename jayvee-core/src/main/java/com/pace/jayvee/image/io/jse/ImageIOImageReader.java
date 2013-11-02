package com.pace.jayvee.image.io.jse;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.pace.jayvee.image.Image;
import com.pace.jayvee.image.StandardImage;
import com.pace.jayvee.workflow.InputBuffer;
import com.pace.jayvee.workflow.OutputBuffer;
import com.pace.jayvee.workflow.Sink;
import com.pace.jayvee.workflow.Source;
import com.pace.jayvee.workflow.Worker;

/**
 * The ImageIOImageReader uses Java's ImageIO library to read in images from
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
public class ImageIOImageReader implements Worker {

	/**
	 * This output sink contains the images that are read in by this reader.  This reader
	 * is not a streaming reader.  It will read the entire image into memory and then
	 * place it into this sink.  Images can be quite large (8*width*height*numBands bytes)
	 * so use caution when sizing this buffer (and all future buffers that contain images).
	 */
	@OutputBuffer
	public Sink<Image> generatedImages;
	/**
	 * This input source should contain input streams for image files.  If an input stream
	 * doesn't contain an image file then it will result in an error.  As input streams are
	 * not thread safe, care should be taken to ensure that the input streams coming into
	 * this worker don't go anywhere else.  This worker will read through the entire stream
	 * and then close the stream when finished.
	 */
	@InputBuffer
	public Source<InputStream> inputStreamsToRead;
	
	private Image readImage(InputStream input) {
		try {
			BufferedImage image = ImageIO.read(input);
			input.close();
			return decodeImage(image);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private StandardImage decodeImage(BufferedImage source) {
		//Clearly not the best algorithm but it is reasonably fast and works
		if(source.getType() != BufferedImage.TYPE_3BYTE_BGR) {
			throw new RuntimeException("Do this someday");
		}
		Raster raster = source.getRaster();
		DataBuffer buffer = raster.getDataBuffer();
		if(buffer.getNumBanks() != 1) {
			throw new RuntimeException("Oops");
		}
		double[] pixels = new double[source.getHeight()*source.getWidth()*3];
		byte [] bytes = ((DataBufferByte)buffer).getData();
		for(int i = 0; i < bytes.length; i+=3) {
			byte blue = bytes[i];
			byte green = bytes[i+1];
			byte red = bytes[i+2];
			pixels[i] = red & 0xFF;
			pixels[i+1] = green & 0xFF;
			pixels[i+2] = blue & 0xFF;
		}
		
		return new StandardImage(source.getWidth(), source.getHeight(), 3, pixels);
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
