package com.pace.jayvee.image.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.pace.jayvee.util.InvalidParameterException;
import com.pace.jayvee.workflow.OutputBuffer;
import com.pace.jayvee.workflow.Sink;
import com.pace.jayvee.workflow.Worker;

/**
 * The directory scanner reads through each file in a given directory and opens up an
 * input stream to that file which it passes onto its output buffer.  This is often used
 * when the input images for a particular system are all in one directory and each image
 * needs to be processed once.
 * 
 * Currently the directory scanner is quite dumb and assumes every file is an image.  A
 * more sophisticated directory scanner would at least take in some kind of filter or, even
 * better, be able to determine if the underlying file truly is an image file before it
 * passes the stream on.
 * 
 * The directory scanner also does not currently recurse into subdirectories.  It will 
 * simply skip over them.
 * 
 * TODO: Enhance this class
 */
public class DirectoryScanner implements Worker {

	private static final Logger logger = Logger.getLogger(DirectoryScanner.class);
	
	/**
	 * This sink will receive the input streams opened for files that this directory
	 * scans.  The input streams hold an active reference to a file and therefore
	 * this stream should only go to a single destination (e.g. if the second destination
	 * closes the stream while the first is still reading it you are bound to have trouble.
	 */
	@OutputBuffer
	public Sink<InputStream> generatedInputStreams;
	//Parameters
	private File directory;
	//Instance state
	private File[] files;
	private int fileIndex;
		
	/**
	 * Sets the directory that this scanner should scan.  Directories should be in the
	 * format /foo/bar/dir/blah for both Windows and Linux.  If specifying the drive is
	 * necessary on Windows then it should be /C/foo/bar/dir/blah where C is the drive
	 * letter.
	 * 
	 * Paths should be absolute for maximum portability.  Relative paths will be interpreted
	 * relative to the users current directory when they execute the application.
	 * 
	 * @param directoryName The path of the directory that should be scanned by this scanner
	 */
	public void setDirectory(String directoryName) {
		this.directory = new File(directoryName);
	}

	/**
	 * During initialization the directory scanner checks to make sure the path specified
	 * is indeed a directory that can be read.  It then reads out a list of files which
	 * will be read as this worker iterates.
	 */
	public void init() {
		
		if(!directory.exists()) {
			throw new InvalidParameterException(directory + " does not exist");
		}
		if(!directory.isDirectory()) {
			throw new InvalidParameterException(directory + " is not a directory");
		}
		if(!directory.canRead()) {
			throw new InvalidParameterException(directory + " is not readable");
		}
		
		files = directory.listFiles();
		fileIndex = 0;
	}
	
	//Plucks the next file from the files array or returns null if there are no more
	private File nextFile() {
		if(fileIndex < files.length) {
			File result = files[fileIndex];
			fileIndex++;
			//Skip it if its a subdirectory
			if(result.isFile()) {
				return result;
			} else {
				return nextFile();
			}
		} else {
			return null;
		}
	}
	
	@Override
	public void iterate() {
		File file = nextFile();
		if(file != null) {
			try {
				logger.debug("Generating input stream from: " + file.getAbsolutePath());
				generatedInputStreams.push(new FileInputStream(file));
			} catch (FileNotFoundException ex) {
				logger.warn("Skipping file: " + file.getAbsolutePath() + " [FileNotFoundException]");
				logger.trace("Stack trace", ex);
			}
		} else {
			generatedInputStreams.end();
		}
	}
	
}
