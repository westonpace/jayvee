package com.github.westonpace.jayvee.image.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.junit.Assert;
import org.junit.Test;

import com.github.westonpace.jayvee.image.io.IncrementingFileCreator;
import com.github.westonpace.jayvee.test.ArrayListBuffer;
public class IncrementingFileCreatorTest extends FilesystemTestBase {

	private void writeIntoFileAndClose(String string, OutputStream file) throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(file);
		writer.append(string);
		writer.close();
		file.close();
	}
	
	/**
	 * Uses the fileCreator to create 3 files
	 */
	@Test
	public void testBasicCase() throws IOException {
		//Setup a file creator
		IncrementingFileCreator fileCreator = new IncrementingFileCreator();
		fileCreator.setDirectory(testDirectory.getAbsolutePath());
		fileCreator.setPrefix("AAA");
		fileCreator.setSuffix(".tmp");
		
		ArrayListBuffer<OutputStream> outputBuffer = new ArrayListBuffer<OutputStream>();
		fileCreator.generatedOutputStreams = outputBuffer;
		
		fileCreator.init();
		
		//Iterate 3 times
		for(int i = 0; i < 3; i++) {
			fileCreator.iterate();
		}
		
		//Write into the 3 generated output buffers
		Assert.assertEquals(3, outputBuffer.size());
		
		writeIntoFileAndClose("A", outputBuffer.pop());
		writeIntoFileAndClose("B", outputBuffer.pop());
		writeIntoFileAndClose("C", outputBuffer.pop());
		
		//Make sure the files exist with the correct name and content
		String a = loadStreamAsString(new FileInputStream(new File(testDirectory, "AAA-0.tmp")));
		String b = loadStreamAsString(new FileInputStream(new File(testDirectory, "AAA-1.tmp")));
		String c = loadStreamAsString(new FileInputStream(new File(testDirectory, "AAA-2.tmp")));
		
		Assert.assertEquals("A", a);
		Assert.assertEquals("B", b);
		Assert.assertEquals("C", c);
	}
	
}
