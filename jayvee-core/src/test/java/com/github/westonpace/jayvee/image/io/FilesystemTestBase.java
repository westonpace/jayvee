package com.github.westonpace.jayvee.image.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;

import org.junit.After;
import org.junit.Before;

import com.github.westonpace.jayvee.test.TestBase;

/**
 * A base class for test cases which need to use the file system
 */
public class FilesystemTestBase extends TestBase {

	protected File testDirectory;
	
	@Before
	public void setupTestDirectory() throws IOException {
		testDirectory = File.createTempFile("temp", Long.toString(System.nanoTime()));
		testDirectory.delete();
		testDirectory.mkdir();
	}
	
	protected void deleteDirectory(File directory) {
		for(File file : directory.listFiles()) {
			if(file.isDirectory()) {
				deleteDirectory(file);
			}
			file.delete();
		}
	}
	
	@After
	public void cleanup() {
		deleteDirectory(testDirectory);
	}
	
	protected void createTempFile(String filePrefix, String content) throws IOException {
		File file = File.createTempFile(filePrefix, null, testDirectory);
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		PrintWriter printWriter = new PrintWriter(fileOutputStream);
		printWriter.print(content);
		printWriter.close();
		fileOutputStream.close();
	}
	
	protected String loadStreamAsString(InputStream inputStream) throws IOException {
		StringBuilder result = new StringBuilder();
		Reader reader = new InputStreamReader(inputStream);
		while(true) {
			int next = reader.read();
			if(next < 0) {
				return result.toString();
			} else {
				result.append((char)next);
			}
		}
	}

}
