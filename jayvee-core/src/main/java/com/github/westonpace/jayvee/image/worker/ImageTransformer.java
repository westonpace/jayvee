package com.github.westonpace.jayvee.image.worker;

import com.github.westonpace.jayvee.image.Image;
import com.github.westonpace.jayvee.image.StandardImage;
import com.github.westonpace.jayvee.workflow.InputBuffer;
import com.github.westonpace.jayvee.workflow.OutputBuffer;
import com.github.westonpace.jayvee.workflow.Sink;
import com.github.westonpace.jayvee.workflow.Source;
import com.github.westonpace.jayvee.workflow.Worker;

/**
 * Modifies an existing image an turns it into a new image of the
 * same width and height. The number of bands in the output image may differ.  
 * 
 * Common image transformers are things like convolution, color space conversion, edge
 * detection, etc.
 * 
 * This basic image transformer creates a brand new image in memory and then relies
 * on a subclass to do the actual transformation.  Some subclasses may choose to modify
 * the image in place.
 */
public abstract class ImageTransformer implements Worker {

	/**
	 * The images that will be transformed by the transformer.  There is no requirement
	 * on the structure or form of these images.  Subclasses should inspect the images
	 * to make sure they are valid.
	 */
	@InputBuffer
	public Source<Image> inputImages;
	/**
	 * The transformed images.
	 */
	@OutputBuffer
	public Sink<Image> outputImages;
		
	protected abstract void transform(Image input, Image output);
	protected abstract int getOutputBands(Image input);
	
	private Image buildOutputImage(Image input) {
		return new StandardImage(input.getWidth(), input.getHeight(), getOutputBands(input));
	}
	
	@Override
	public void iterate() {
		Image input = inputImages.pop();
		Image output = buildOutputImage(input);
		transform(input, output);
		outputImages.push(output);
	}
	
}
