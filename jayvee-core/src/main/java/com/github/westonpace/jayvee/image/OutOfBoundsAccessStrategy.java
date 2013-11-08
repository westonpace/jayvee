package com.github.westonpace.jayvee.image;

/**
 * Defines what to do when a user attempts to access a pixel value that is outside
 * of the legal range.  Several common out of bounds strategies exist and they have
 * different advantages for different situations.
 */
public interface OutOfBoundsAccessStrategy {

	/**
	 * Retrieves a value from an image when the value does not lay on the actual
	 * image itself.
	 * @param x The x coordinate of the desired pixel
	 * @param y The y coordinate of the desired pixel
	 * @param b The band containing the desired pixel
	 * @param img The image to access
	 * @return A pixel value that represents the given coordinates
	 */
	public double get(int x, int y, int b, Image img);
	
	/**
	 * <p>
	 * Accesses pixels beyond the edge of an image by assuming the edgemost
	 * pixels simply extend into infinity.  As an example (note how the corners work):
	 * </p><pre><code>
	 * 0 1 2
	 * 3 4 5
	 * 6 7 8
	 * </code></pre>
	 * <p>
	 * Input Image
	 * </p>
	 * <pre><code>
	 * 0 0 0 1 2 2 2
	 * 0 0 0 1 2 2 2
	 * 0 0 0 1 2 2 2
	 * 3 3 3 4 5 5 5
	 * 6 6 6 7 8 8 8
	 * 6 6 6 7 8 8 8
	 * 6 6 6 7 8 8 8
	 * </code></pre>
	 * <p>
	 * Extended Image
	 * </p>
	 */
	public class ExtendEdgesStrategy implements OutOfBoundsAccessStrategy {

		@Override
		public double get(int x, int y, int b, Image img) {
			//Set x to 0 if it is below 0
			int trueX = Math.max(x, 0);
			//Set x to the width of the image - 1 if it is >= the width
			trueX = Math.min(img.getWidth()-1, trueX);
			//Set y to 0 if it is below 0
			int trueY = Math.max(y, 0);
			//Set y to the height of the image - 1 if it is >= the height
			trueY = Math.min(img.getHeight()-1, trueY);
			return img.get(trueX, trueY, b);
		}
		
	}
	
}
