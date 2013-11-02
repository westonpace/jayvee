package com.pace.jayvee.image;

/**
 * An interpolator defines how an image should retrieve a value from neighboring pixels
 * when the requested values coordinates don't define an exact pixel.  For exmaple, if
 * you have one pixel at 5,7 which is white and another at 5,8 which is black and the
 * pixel at 5,7.5 is requested it is reasonable to assume that pixel would probably be
 * some kind of gray.
 * 
 * Interpolators typically assume the true image is a continuous function and the image
 * we have is a mere sampling of that function.  Given that, if we assume the image is 
 * smooth it makes sense that neighboring pixels could be used to define the value of
 * pixels which were not sampled.
 * 
 * There are a number of ways to interpolate pixels, the typical trade-off is a cost
 * to accuracy tradeoff.  The simplest methods do little math but return imperfect results
 * while the more complex methods involve more math and are more expensive.
 */
public interface Interpolator {
	
	/**
	 * Interpolates a value where the coordinates are not integers by taking the neighbors
	 * of the desired point and making some guesses about what the true value should be.
	 * @param x The x coordinate of the pixel
	 * @param y The y coordinate of the pixel
	 * @param b The band that we want the value for
	 * @param source The source image, used to obtain neighboring pixels
	 * @return An interpolated guess at the true value of the desired pixel
	 */
	public double interpolate(double x, double y, int b, Image source);
	
	/**
	 * Bilinear interpolation is a basic form of interpolation.  It takes the 4 closest
	 * neighbors and then interpolates the top neighbors linearly in the X direction and
	 * the bottom neighbors linearly in the X direction.  It takes the two resulting 
	 * values and interpolates them linearly in the Y direction.
	 * 
	 * Example:
	 *              |-a-|--b---| 
	 *            - TL---------TR
	 *            c |          |
	 *            - |  X       |
	 *            | |          |
	 *            d |          |
	 *            - BL---------BR
	 * 
	 * T = TL * a + TR * b
	 * B = BL * a + BR * b
	 * X = T * c + B * d
	 * 
	 * The result is (ironically) a non-linear function as it is the product of two
	 * linear functions.  Bilinear interpolation can often lead to a number of visual
	 * artifacts (e.g. aliasing, halos around edges, etc.) and is less accurate than
	 * bicubic interpolation.  It is more accurate than nearest neighbor interpolation.
	 */
	public static class BilinearInterpolator implements Interpolator {

		@Override
		public double interpolate(double x, double y, int b, Image source) {
			
			double topLeftNeighbor = source.get((int)x, (int)y, b);
			double topRightNeighbor = source.get((int)x+1, (int)y, b);
			double bottomLeftNeighbor = source.get((int)x, (int)y+1, b);
			double bottomRightNeighbor = source.get((int)x+1, (int)y+1, b);
			
			double xPercent = x - (int)x;
			double yPercent = y - (int)y;
			
			//Assuming all pixels the same size (seems safe)
			double topNeighbor = topLeftNeighbor * xPercent + topRightNeighbor * (1 - xPercent);
			double bottomNeighbor = bottomLeftNeighbor * xPercent + bottomRightNeighbor * (1 - xPercent);
			double value = topNeighbor * yPercent + bottomNeighbor * (1 - yPercent);
			
			return value;
		}
		
	}
	
}