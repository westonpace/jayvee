package com.github.westonpace.jayvee.image;

import org.junit.Assert;
import org.junit.Test;

import com.github.westonpace.jayvee.image.StandardImage;
import com.github.westonpace.jayvee.image.OutOfBoundsAccessStrategy.ExtendEdgesStrategy;
import com.github.westonpace.jayvee.test.TestBase;

public class OutOfBoundsAccessStrategyTest extends TestBase {

	@Test
	public void testExtendImage() {
		double [] values = new double []{1, 2, 3, 4, 5, 6, 7, 8, 9};
		StandardImage image = new StandardImage(3, 3, 1, values);
		
		double [] extendedValues = new double[] {1, 1, 1, 2, 3, 3, 3,
				                                 1, 1, 1, 2, 3, 3, 3,
				                                 4, 4, 4, 5, 6, 6, 6,
				                                 7, 7, 7, 8, 9, 9, 9,
				                                 7, 7, 7, 8, 9, 9, 9};
		
		ExtendEdgesStrategy extendEdgesStrategy = new ExtendEdgesStrategy();
		int index = 0;
		for(int y = -1; y < 4; y++) {
			for(int x = -2; x < 5; x++) {
				Assert.assertEquals(extendedValues[index], image.get(x, y, 0, extendEdgesStrategy), 0.0);
				index++;
			}
		}
	}
	
}
