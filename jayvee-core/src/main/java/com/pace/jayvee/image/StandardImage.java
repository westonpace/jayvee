package com.pace.jayvee.image;

import java.util.Iterator;

import com.pace.jayvee.util.Sinkerator;

/**
 * Our standard image representation stores the pixel values into a single
 * double array. Since images can get quite large and arrays are quite costly to
 * create in Java, it is more desirable to use a single array.
 * 
 * The accessor functions then simply index to the correct position in the array
 * to obtain the pixels we want.
 * 
 * The standard image always stores the underyling data in row-major order
 * starting with the pixel at (0,0). In other words, an image with 3 bands would
 * have the following in the beginning of the array:
 * 
 * (0,0,0) (0,0,1) (0,0,2) (1,0,0) (1,0,1) (1,0,2) (2,0,0) ...
 * 
 * Based on this, in order to get to a pixel at x,y,b we access the array index
 * at:
 * 
 * index = x*numBands + y*width*numBands + b
 * 
 * And the total length of our double array will be:
 * 
 * length = width * height * numBands
 */
public class StandardImage extends Image {

	private int width;
	private int height;
	private int numBands;
	private double[] pixels;

	/**
	 * Creates a brand new image. The values array will be initialized to all
	 * 0's
	 * 
	 * @param width
	 *            The desired width of the image
	 * @param height
	 *            The desired height of the image
	 * @param numBands
	 *            The number of bands in the image
	 */
	public StandardImage(int width, int height, int numBands) {
		this.pixels = new double[height * width * numBands];
		this.width = width;
		this.height = height;
		this.numBands = numBands;
	}

	/**
	 * Creates an image from existing values. This constructor does not copy the
	 * data passed in, but rather references it. This means that further
	 * modification to the data array will modify the image itself.
	 * 
	 * The pixels array must be of correct length (width * height * numBands) or
	 * an error will be thrown.
	 * 
	 * @param width
	 *            The desired width of the image
	 * @param height
	 *            The desired height of the image
	 * @param numBands
	 *            The number of bands in the image
	 * @param pixels
	 *            The initial data for the image
	 */
	public StandardImage(int width, int height, int numBands, double[] pixels) {
		if (pixels.length != (width * height * numBands)) {
			throw new RuntimeException("When constructing a standard image we were given " + pixels.length + " pixels but for a width x height x bands of (" + width + "," + height + "," + numBands
					+ ") we should have " + (width * height * numBands) + " pixels");
		}
		this.width = width;
		this.height = height;
		this.numBands = numBands;
		this.pixels = pixels;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getNumBands() {
		return numBands;
	}

	@Override
	public Iterable<Double> getPixels(PixelOrder pixelOrder) {
		//To iterate through the pixels in row-major order we simply iterate forwards through
		//the array.  To iterate through the pixels in column-major order we will have to jump
		//downwards through the array
		final int stride = (pixelOrder == PixelOrder.RowMajor) ? 1 : getWidth();
		final int length = pixels.length;
		return new Iterable<Double>() {

			@Override
			public Iterator<Double> iterator() {
				return new Iterator<Double>() {

					int index = 0;

					@Override
					public boolean hasNext() {
						return index != pixels.length;
					}

					@Override
					public Double next() {
						double result = pixels[index];
						index += stride;
						index %= length;
						return result;
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}

				};
			}

		};
	}

	/**
	 * Returns a iteratee which will allow you to write the pixel values out.
	 * Each call to putNext will replace the pixel value poitned at with the
	 * given value and move on to the next value.
	 * 
	 * @return An iteratee that writes into the pixel values
	 */
	public Sinkerator<Double> getPixelWriter(PixelOrder pixelOrder) {
		//We use the same trick to iterate in the proper pixelOrder as above in getPixels()
		final int stride = (pixelOrder == PixelOrder.RowMajor) ? 1 : getWidth();
		final int length = pixels.length;
		return new Sinkerator<Double>() {

			private int index = 0;

			private void incrementIndex() {
				index += stride;
				index %= length;
			}

			@Override
			public void putNext(Double value) {
				pixels[index] = value;
				incrementIndex();
			}

			@Override
			public void skipNext() {
				incrementIndex();
			}

			@Override
			public boolean hasNext() {
				return index == length;
			}
		};
	}

	@Override
	public double get(int x, int y, int b) {
		return pixels[y * width * numBands + x * numBands + b];
	}

	@Override
	public void set(int x, int y, int b, double value) {
		pixels[y * width * numBands + x * numBands + b] = value;
	}

}
