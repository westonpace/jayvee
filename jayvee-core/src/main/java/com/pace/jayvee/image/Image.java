package com.pace.jayvee.image;

import com.pace.jayvee.image.Interpolator.BilinearInterpolator;
import com.pace.jayvee.util.Sinkerator;

/**
 * <p>
 * An image is a 2 dimensional array of pixels.  The pixels are laid out in a grid
 * with the horizontal direction being the x dimension and the vertical direction
 * being the y dimension.
 * </p>
 * <h3>Bands</h3>
 * <p>
 * A pixel is an array of doubles.  For example, a color pixel would be an array 
 * of size 3, one double for red, one double for green, and one double for blue.  
 * A grayscale image would have a pixel which is just a single double.  The different
 * values in a pixel are referred to as bands.  For example, a color image has a red
 * band, a green band, and a blue band where a gray image has only the gray band. 
 * </p>
 * <h3>Interpolation</h3>
 * <p>
 * Image values can be accessed by integer coordinates as well as double coordinates.
 * If you access the image with double coordinates then the pixels closest to the point
 * you desire will be interpolated to provide the resulting value.
 * </p>
 * <h3>Pixel Order</h3>
 * <p>
 * Images can also return the values as a single array packed according to some kind
 * of pixel order.  If you had a 3x3 image like so:
 * </p><p>
 *      1 2 3
 *      4 5 6
 *      7 8 9
 * </p><p>
 * where each item is a pixel of (A,B,C).  Then if you get an iterator with row major
 * order (x first, then y) you will receive:
 * </p><p>
 *     1A 1B 1C 2A 2B 2C 3A 3B 3C 4A ...
 * </p><p>
 * and if you get an iterator with column major order (y first, then x) you will receive:
 * </p><p>
 *     1A 1B 1C 4A 4B 4C 7A 7B 7C 2A ...
 * </p>
 */
public abstract class Image {

	/**
	 * The default interpolator is used if no interpolator is supplied
	 */
	private static final Interpolator DEFAULT_INTERPOLATOR = new BilinearInterpolator();
	
	public enum PixelOrder {
		RowMajor, ColumnMajor;
	}
	
	/**
	 * Returns the width of the image
	 * @return The width of the image, in pixels
	 */
	public abstract int getWidth();
	
	/**
	 * Returns the height of the image
	 * @return The height of the image, in pixels
	 */
	public abstract int getHeight();
	
	/**
	 * Returns the number of bands in the image.  The number of bands tells
	 * how many values are in each pixel.
	 * @return The number of bands in the image
	 */
	public abstract int getNumBands();
	
	/**
	 * Gets the value at the given coordinates and band.  For example, to get the
	 * green value of an rgb image at the pixel located at 2,7 you would access
	 * 2,7,1 (1 is the index of the green band).  This method is guaranteed not to
	 * interpolate.
	 * 
	 * @param x The x coordinate of the desired value
	 * @param y The y coordinate of the desired value
	 * @param b The band you wish to access
	 * @return The value represented by those coordinates
	 */
	public abstract double get(int x, int y, int b);

	/**
	 * Sets the value represented by the given coordinates and band.  For example, to
	 * set the blue value of an rgb image at the pixel located at 7,2 you would access
	 * 7,2,2 (2 is the index of the blue band).
	 * @param x The x coordinate of the pixel to set
	 * @param y The y coordinate of the pixel to set
	 * @param b The band of the pixel to set
	 * @param value The value to set
	 */
	public abstract void set(int x, int y, int b, double value);

	/**
	 * Returns the value at the given coordinates and band.  This method is will
	 * typically obtain the value through interpolation since the x and y coordinates
	 * are supplied as doubles.
	 * @param x The x coordinate of the pixel to get
	 * @param y The y coordinate of the pixel to get
	 * @param b The band you wish to access
	 * @return The interpolated value represented by those coordinates
	 */
	public double get(double x, double y, int b) {
		return get(x, y, b, (Interpolator) DEFAULT_INTERPOLATOR);
	}
	
	/**
	 * Returns the value at the given coordinates and band.  This method is will
	 * obtain the value by interpolating the neighbors with the given interpolator.
	 * @param x The x coordinate of the pixel to get
	 * @param y The y coordinate of the pixel to get
	 * @param b The band you wish to access
	 * @param interpolator The interpolator algorithm to use to obtain the value from the neighbors
	 * @return The interpolated value represented by those coordinates
	 */
	public double get(double x, double y, int b, Interpolator interpolator) {
		return interpolator.interpolate(x, y, b, this);
	}
	
	/**
	 * Returns an iterable of the pixels in the given row order.  See the comments
	 * at the class level for more details on the pixel order.  The pixel bands will be
	 * interleaved so for each pixel you will get each band and then you will get the
	 * next pixel.  For example, in an RGB image the values will be R1B1G1R2B2...
	 * 
	 * @param rowOrder The pixel order of the resulting iterator
	 * @return An iterable of band-interleaved pixel values in the supplied order
	 */
	public abstract Iterable<Double> getPixels(PixelOrder rowOrder);

	/**
	 * Returns an iterable of pixels in the RowMajor pixel order.
	 * @return An iterable of band-interleaved pixel values in row-major order
	 */
	public Iterable<Double> getPixels() {
		return getPixels(PixelOrder.RowMajor);
	}
	
	/**
	 * Returns a iteratee which will allow you to write the pixel values out.  Each call
	 * to putNext will replace the pixel value poitned at with the given value and move
	 * on to the next value.
	 * @return An iteratee of band-interleaved pixels that writes into the pixel values
	 */
	public abstract Sinkerator<Double> getPixelWriter(PixelOrder pixelOrder);
	
	/**
	 * Returns a iteratee of pixels in RowMajor pixel order.
	 * @return An iteratee of band-interleaved pixels in row-major order.
	 */
	public Sinkerator<Double> getPixelWriter() {
		return getPixelWriter(PixelOrder.RowMajor);
	}
	
}
