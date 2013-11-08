package com.github.westonpace.jayvee.image;

import org.junit.Assert;
import org.junit.Test;

import com.github.westonpace.jayvee.image.StandardImage;
import com.github.westonpace.jayvee.image.Interpolator.BilinearInterpolator;
import com.github.westonpace.jayvee.test.TestBase;

public class InterpolatorTest extends TestBase {

	/**
	 * (1 + 2)/2 = 1.5
	 * (3 + 4)/2 = 3.5
	 * (1.5 + 3.5)/2 = 2.5
	 * 
	 * (1*0.75 + 2*0.25) = 1.25
	 * (3*0.75 + 4*0.25) = 3.25
	 * (1.25*0.25 + 3.25*0.75) = 2.75
	 */
	@Test
	public void testBilinearInterpolation() {
		double[] data = new double[] { 1, 2, 3, 4 };
		StandardImage image = new StandardImage(2, 2, 1, data);
		
		BilinearInterpolator interpolator = new BilinearInterpolator();
		double result = interpolator.interpolate(0.5, 0.5, 0, image);
		
		Assert.assertEquals(2.5, result, 0.00001);
		
		//Throw 2 bands worth of data in there just to try and confuse things
		data = new double[] {0, 1, 0, 2, 0, 3, 0, 4};
		image = new StandardImage(2, 2, 2, data);
		
		result = interpolator.interpolate(0.25, 0.75, 1, image);
		Assert.assertEquals(2.75, result, 0.00001);
	}

}
