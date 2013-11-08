package com.github.westonpace.jayvee.test.demo_applications;

import java.io.IOException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.SimpleLayout;

import com.github.westonpace.jayvee.image.io.DirectoryScanner;
import com.github.westonpace.jayvee.image.io.IncrementingFileCreator;
import com.github.westonpace.jayvee.image.io.jse.ImageIOImageReader;
import com.github.westonpace.jayvee.image.io.jse.ImageIOImageWriter;
import com.github.westonpace.jayvee.image.worker.basic.ToGrayscale;
import com.github.westonpace.jayvee.util.ConstantSource;
import com.github.westonpace.jayvee.workflow.SystemBuilder;
import com.github.westonpace.jayvee.workflow.WorkflowSystem;

public class Grayscaler {

	public static void main(String [] args) throws InterruptedException, IOException {
		LogManager.getRootLogger().addAppender(new ConsoleAppender(new SimpleLayout()));
		
		SystemBuilder builder = new SystemBuilder();
		DirectoryScanner directoryScanner = builder.buildWorker(DirectoryScanner.class);
		ImageIOImageReader imageReader = builder.buildWorker(ImageIOImageReader.class);
		ToGrayscale toGrayscale = builder.buildWorker(ToGrayscale.class);
		IncrementingFileCreator directoryWriter = builder.buildWorker(IncrementingFileCreator.class);
		ImageIOImageWriter imageWriter = builder.buildWorker(ImageIOImageWriter.class);
		@SuppressWarnings("unchecked")
		ConstantSource<String> formatSource = builder.buildWorker(ConstantSource.class);
		
		formatSource.setValue("bmp");
		directoryScanner.setDirectory("/temppy");
		directoryWriter.setDirectory("/temppy2");
		directoryWriter.setPrefix("img");
		directoryWriter.setSuffix(".bmp");
		
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
