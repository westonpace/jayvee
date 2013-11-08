package com.github.westonpace.jayvee.image;

import org.junit.Assert;
import org.junit.Test;

import com.github.westonpace.jayvee.image.Image;
import com.github.westonpace.jayvee.image.OutOfBoundsAccessStrategy;
import com.github.westonpace.jayvee.image.StandardImage;
import com.github.westonpace.jayvee.test.TestBase;
import com.github.westonpace.jayvee.util.InvalidParameterException;

public class ImageTest extends TestBase {

	//Note: This test uses StandardImage which makes it not quite a true unit test.  We
	//could write our own mock image implementation thus breaking the coupling but the
	//classes being tested are simple enough we'll just try and get away with it.
	
	@Test(expected=InvalidParameterException.class)
	public void testBeyondMaxWidth() {
		StandardImage image = new StandardImage(2, 2, 2);
		image.get(2, 0, 0);
	}

	@Test(expected=InvalidParameterException.class)
	public void testBeyondMaxHeight() {
		StandardImage image = new StandardImage(2, 2, 2);
		image.get(0, 2, 0);
	}

	@Test(expected=InvalidParameterException.class)
	public void testBeyondMaxBands() {
		StandardImage image = new StandardImage(2, 2, 2);
		image.get(0, 0, 2);
	}

	@Test(expected=InvalidParameterException.class)
	public void testNegativeWidth() {
		StandardImage image = new StandardImage(2, 2, 2);
		image.get(-1, 0, 0);
	}

	@Test(expected=InvalidParameterException.class)
	public void testNegativeHeight() {
		StandardImage image = new StandardImage(2, 2, 2);
		image.get(0, -1, 0);
	}

	@Test(expected=InvalidParameterException.class)
	public void testNegativeBands() {
		StandardImage image = new StandardImage(2, 2, 2);
		image.get(0, 0, -1);
	}
	
	@Test
	public void testOutOfBoundsAccessStrategy() {
		final int [] expected = new int [] {2, 1, 3};
		final StandardImage image = new StandardImage(2, 2, 2);
		
		OutOfBoundsAccessStrategy strategy = new OutOfBoundsAccessStrategy() {
			@Override
			public double get(int x, int y, int b, Image img) {
				Assert.assertEquals(expected[0], x);
				Assert.assertEquals(expected[1], y);
				Assert.assertEquals(expected[2], b);
				Assert.assertSame(image, img);
				return 0;
			}
		};
		
		image.get(2, 1, 3, strategy);
		
		expected[0] = 3;
		expected[1] = -4;
		expected[2] = 7;
		
		image.get(3, -4, 7, strategy);
		
		OutOfBoundsAccessStrategy dontCallMe = new OutOfBoundsAccessStrategy() {
			
			@Override
			public double get(int x, int y, int b, Image img) {
				Assert.fail("This out of bounds access strategy should not be called for in-bounds access");
				return 0;
			}
		};
		
		image.get(0, 0, 0, dontCallMe);
		
		image.get(1, 1, 1, dontCallMe);
	}

}
