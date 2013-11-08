package com.github.westonpace.jayvee.image.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import com.github.westonpace.jayvee.image.io.DirectoryScanner;
import com.github.westonpace.jayvee.test.ArrayListBuffer;

public class DirectoryScannerTest extends FilesystemTestBase {

	/**
	 * This test tests the basic case where there is a directory with a few files and
	 * all files are read out
	 */
	@Test
	public void testBasicCase() throws IOException {
		createTempFile("BBB", "B");
		createTempFile("CCC", "C");
		createTempFile("AAA", "A");
		
		DirectoryScanner scanner = new DirectoryScanner();
		scanner.setDirectory(testDirectory.getAbsolutePath());
		scanner.init();
		
		ArrayListBuffer<InputStream> buffer = new ArrayListBuffer<InputStream>();
		
		scanner.generatedInputStreams = buffer;
		
		while(!scanner.isEnded()) {
			scanner.iterate();
		}
		
		Assert.assertEquals(3, buffer.size());
		Assert.assertEquals("A", loadStreamAsString(buffer.pop()));
		Assert.assertEquals("B", loadStreamAsString(buffer.pop()));
		Assert.assertEquals("C", loadStreamAsString(buffer.pop()));
	}
	
	/**
	 * This test ensures that subdirectories in the scanners scanning directory are
	 * harmlessly skipped
	 */
	@Test
	public void ensureDirectoryHarmless() throws IOException {
		createTempFile("BBB", "B");
		File directory = File.createTempFile("CCC", null, testDirectory);
		directory.delete();
		directory.mkdir();
		createTempFile("AAA", "A");

		DirectoryScanner scanner = new DirectoryScanner();
		scanner.setDirectory(testDirectory.getAbsolutePath());
		scanner.init();
		
		ArrayListBuffer<InputStream> buffer = new ArrayListBuffer<InputStream>();
		
		scanner.generatedInputStreams = buffer;
		
		while(!scanner.isEnded()) {
			scanner.iterate();
		}
		
		Assert.assertEquals(2, buffer.size());
		Assert.assertEquals("A", loadStreamAsString(buffer.pop()));
		Assert.assertEquals("B", loadStreamAsString(buffer.pop()));
	}
}
