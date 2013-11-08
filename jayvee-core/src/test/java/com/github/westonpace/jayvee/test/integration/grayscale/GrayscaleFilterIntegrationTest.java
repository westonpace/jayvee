package com.github.westonpace.jayvee.test.integration.grayscale;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;

import com.github.westonpace.jayvee.image.Image;
import com.github.westonpace.jayvee.image.io.DirectoryScanner;
import com.github.westonpace.jayvee.image.io.FilesystemTestBase;
import com.github.westonpace.jayvee.image.io.IncrementingFileCreator;
import com.github.westonpace.jayvee.image.io.jse.ImageIOImageReader;
import com.github.westonpace.jayvee.image.io.jse.ImageIOImageWriter;
import com.github.westonpace.jayvee.image.worker.basic.ToGrayscale;
import com.github.westonpace.jayvee.util.ConstantSource;
import com.github.westonpace.jayvee.workflow.SystemBuilder;
import com.github.westonpace.jayvee.workflow.WorkflowSystem;

/**
 * Creates the following system:
 * 
 *                                     IncrementingFileCreator---V
 * DirectoryScanner --> ImageIOImageReader --> ToGrayscale --> ImageIOImageWriter
 *                                             ConstantSource ---^
 *                                             
 * Runs the system against two input images (logo in bmp and png format) and compares
 * the resulting images against a known ground truth.
 * 
 */
public class GrayscaleFilterIntegrationTest extends FilesystemTestBase {

	private File outputDirectory;
		
	@Override
	protected Level getLoggingLevel() {
		return Level.DEBUG;
	}

	private void copyStream(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[1024];
		int len = input.read(buffer);
		while (len != -1) {
			output.write(buffer, 0, len);
		    len = input.read(buffer);
		}
		input.close();
		output.close();
	}
	
	//Writes our test images to our temporary directory
	private void writeImagesToDirectory() throws IOException {
		File file = new File(testDirectory, "input-one.png");
		copyStream(getLogoPngStream(), new FileOutputStream(file));
		file = new File(testDirectory, "input-two.bmp");
		copyStream(getLogoBmpStream(), new FileOutputStream(file));
		file = new File(testDirectory, "input-three.bmp");
		copyStream(getLogoBmpGrayStream(), new FileOutputStream(file));
	}
	
	private void makeOutputDirectory() {
		outputDirectory = new File(testDirectory, "output");
		outputDirectory.mkdir();
	}
	
	@Test
	public void test() throws IOException {
		makeOutputDirectory();
		writeImagesToDirectory();
		
		SystemBuilder builder = new SystemBuilder();
		//Build the workers
		DirectoryScanner directoryScanner = builder.buildWorker(DirectoryScanner.class);
		ImageIOImageReader imageReader = builder.buildWorker(ImageIOImageReader.class);
		ToGrayscale grayscaler = builder.buildWorker(ToGrayscale.class);
		@SuppressWarnings("unchecked")
		ConstantSource<String> formatSource = (ConstantSource<String>) builder.buildWorker(ConstantSource.class);
		IncrementingFileCreator fileCreator = builder.buildWorker(IncrementingFileCreator.class);
		ImageIOImageWriter imageWriter = builder.buildWorker(ImageIOImageWriter.class);
		
		//Configure parameters
		directoryScanner.setDirectory(testDirectory.getAbsolutePath());
		formatSource.setValue("png");
		fileCreator.setDirectory(outputDirectory.getAbsolutePath());
		fileCreator.setPrefix("output");
		fileCreator.setSuffix(".png");
		
		//Connect workers
		builder.connect(directoryScanner.generatedInputStreams, imageReader.inputStreamsToRead);
		builder.connect(imageReader.generatedImages, grayscaler.inputImages);
		builder.connect(grayscaler.outputImages, imageWriter.imagesToWrite);
		builder.connect(formatSource.outputSink, imageWriter.imageFormats);
		builder.connect(fileCreator.generatedOutputStreams, imageWriter.outputStreamsToWriteTo);
		
		//Build and run the system
		WorkflowSystem workflowSystem = builder.build();
		workflowSystem.start();
		workflowSystem.join();
		
		//Test the output images against the ground truth
		Image groundTruth = imageStreamToImage(getLogoBmpGrayStream());
		
		verifyFile("output-0.png", groundTruth);
		verifyFile("output-1.png", groundTruth);
		verifyFile("output-2.png", groundTruth);
	}
	
	private void verifyFile(String filename, Image groundTruth) throws IOException {
		File file = new File(outputDirectory, filename);
		Assert.assertTrue("The output file " + filename + " was not created", file.exists() && file.canRead());
		
		Image image = imageStreamToImage(new FileInputStream(file));
		
		assertEquals(groundTruth, image, 0.0);
	}
}
