package com.github.westonpace.jayvee.image.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.github.westonpace.jayvee.util.InvalidParameterException;
import com.github.westonpace.jayvee.workflow.OnRequestOnly;
import com.github.westonpace.jayvee.workflow.OutputBuffer;
import com.github.westonpace.jayvee.workflow.Sink;
import com.github.westonpace.jayvee.workflow.StandardWorker;

/**
 * Creates output streams to files in a given directory
 * by incrementing a counter.  To get filenames it simply takes the output directory
 * a suffix, and a prefix and increments an index creating:
 * 
 *   ${directory}/${prefix}${index}${suffix}
 *   
 * The file creator does not append any special characters so if you want your file
 * to have an extension make sure to include a . in the suffix. (e.g. ".png")
 */
@OnRequestOnly
public class IncrementingFileCreator extends StandardWorker {

	private static final Logger logger = Logger.getLogger(DirectoryScanner.class);

	/**
	 * <p>
	 * Receives the output streams generated by the directory writer.
	 * </p><p>
	 * Those output streams are references to an active file handle and should not
	 * be shared or unexpected behavior will likely result.
	 * </p>
	 */
	@OutputBuffer
	public Sink<OutputStream> generatedOutputStreams;
	// Parameters
	private File directory;
	private String prefix;
	private String suffix;
	// Instance state
	private int fileIndex;

	/**
	 * Sets the directory the output files will be placed in.  The path should probably be
	 * absolute for maximum portability.  Relative paths will be interpreted relative
	 * to the location of the working directory when the main was executed.
	 * @param directoryName A path to a directory to place output files.
	 */
	public void setDirectory(String directoryName) {
		this.directory = new File(directoryName);
	}

	/**
	 * Sets the prefix to place before the index.  For example, "image-"
	 * @param prefix The ${prefix} part of ${prefix}${index}${suffix}
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * Sets the suffix to place after the index.  For example, ".png"
	 * @param suffix The ${suffix} part of ${prefix}${index}${suffix}
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	/*
	 * During initialization this worker checks the directory parameter and ensures it
	 * exists and is writable.
	 */
	@Override
	public void init() {

		if (!directory.exists()) {
			throw new InvalidParameterException(directory + " does not exist");
		}
		if (!directory.isDirectory()) {
			throw new InvalidParameterException(directory + " is not a directory");
		}
		if (!directory.canWrite()) {
			throw new InvalidParameterException(directory + " is not writable");
		}

		fileIndex = 0;
	}

	private File nextFile() {
		return new File(directory, prefix + "-" + fileIndex + suffix);
	}

	@Override
	public void iterate() {
		File file = nextFile();
		fileIndex++;
		try {
			logger.debug("Generating output stream for " + file.getAbsolutePath());
			generatedOutputStreams.push(new FileOutputStream(file));
		} catch (FileNotFoundException ex) {
			logger.warn("Skipping file: " + file.getAbsolutePath() + " [FileNotFoundException]");
			logger.trace("Stack trace", ex);
		}
	}

}
