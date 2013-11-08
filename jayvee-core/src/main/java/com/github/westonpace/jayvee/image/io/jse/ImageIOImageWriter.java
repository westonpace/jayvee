package com.github.westonpace.jayvee.image.io.jse;

import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import com.github.westonpace.jayvee.image.Image;
import com.github.westonpace.jayvee.util.J2SEUtils;
import com.github.westonpace.jayvee.workflow.InputBuffer;
import com.github.westonpace.jayvee.workflow.Source;
import com.github.westonpace.jayvee.workflow.Worker;

/**
 * Uses the standard Java ImageIO library to write out an image to an output
 * stream.  The format (e.g. png) of the resulting image is specified by another source.
 * 
 * Like the ImageIOImageReader this class is not the most optimized class.  It currently
 * creates a buffered image first and then passes that into the library.  This has the
 * unfortunate side effect of creating the image twice in memory (as well as a performance
 * hit).  But it works.
 * 
 * The images coming in can be one band or three bands.  Three band images will be 
 * interpreted as RGB images.
 * 
 * The image values should be scaled in the 0 to 255 range.  Negative values and values 
 * over 255 will cause an error.
 * 
 * Image values will be rounded to the nearest integer.
 * 
 * TODO: Improve image writing performance and memory usage
 */
public class ImageIOImageWriter implements Worker {

	/**
	 * This source takes in the images that will actually be written.  They must be
	 * scaled from 0 to 255 and be 1 or 3 bands.
	 */
	@InputBuffer
	public Source<Image> imagesToWrite;
	/**
	 * This source takes in the ouptut streams the images will be written to.  They
	 * will be closed after reading.  These streams should not be shared anywhere else
	 * as output streams are stateful and not thread safe.
	 */
	@InputBuffer
	public Source<OutputStream> outputStreamsToWriteTo;
	/**
	 * This source supplies the format of the output image files.  Values should be 
	 * Java ImageIO format names (e.g. "png" or "jpeg").  The image format has no effect
	 * on the output filename, only on how the image data is serialized.
	 */
	@InputBuffer
	public Source<String> imageFormats;
	
	public void writeImage(Image image, String format, OutputStream streamToWriteTo) {
		try {
			ImageIO.write(J2SEUtils.bufferedImageFromImage(image), format, streamToWriteTo);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public void iterate() {
		Image imageToWrite = imagesToWrite.pop();
		OutputStream streamToWriteTo = outputStreamsToWriteTo.pop();
		String format = imageFormats.pop();
		writeImage(imageToWrite, format, streamToWriteTo);
	}

	@Override
	public void init() {
		
	}
	
}
