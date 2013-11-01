package com.pace.jayvee.test.demo_applications;

import java.io.IOException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.SimpleLayout;
import org.junit.Test;

import com.pace.jayvee.image.color.ToGrayscale;
import com.pace.jayvee.image.io.DirectoryScanner;
import com.pace.jayvee.image.io.IncrementingDirectoryWriter;
import com.pace.jayvee.image.io.jse.ImageIOImageReader;
import com.pace.jayvee.image.io.jse.ImageIOImageWriter;
import com.pace.jayvee.util.ConstantSource;
import com.pace.jayvee.workflow.system.SystemBuilder;
import com.pace.jayvee.workflow.system.WorkflowSystem;

public class Grayscaler {

	@Test
	public void testGrayscaler() throws InterruptedException, IOException {
		LogManager.getRootLogger().addAppender(new ConsoleAppender(new SimpleLayout()));
		
		SystemBuilder builder = new SystemBuilder();
		DirectoryScanner directoryScanner = builder.buildWorker(DirectoryScanner.class);
		ImageIOImageReader imageReader = builder.buildWorker(ImageIOImageReader.class);
		ToGrayscale toGrayscale = builder.buildWorker(ToGrayscale.class);
		IncrementingDirectoryWriter directoryWriter = builder.buildWorker(IncrementingDirectoryWriter.class);
		ImageIOImageWriter imageWriter = builder.buildWorker(ImageIOImageWriter.class);
		ConstantSource<String> formatSource = builder.buildWorker(ConstantSource.class);
		
		formatSource.setValue("png");
		directoryScanner.setDirectory("/temppy");
		directoryWriter.setDirectory("/temppy2");
		directoryWriter.setPrefix("img");
		directoryWriter.setSuffix(".png");
		
		builder.connect(directoryScanner.generatedInputStreams, imageReader.inputStreamsToRead, 10);
		builder.connect(imageReader.generatedImages, toGrayscale.inputImages, 10);
		builder.connect(toGrayscale.outputImages, imageWriter.imagesToWrite, 10);
		builder.connect(directoryWriter.generatedOutputStreams, imageWriter.outputStreamsToWriteTo, 1);
		builder.connect(formatSource.outputSink, imageWriter.imageFormats, 1);
		
		System.out.println("Connect");
		System.in.read();
		
		WorkflowSystem system = builder.build();
		system.start();
		system.join();
	}
	
}
