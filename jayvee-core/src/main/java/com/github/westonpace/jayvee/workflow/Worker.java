package com.github.westonpace.jayvee.workflow;

/**
 * An object that can be iterated to process data from sources into sinks.
 * <p>
 * The Worker is the core of the JayVee architecture (the "work"horse...if you will).  A
 * JayVee system is a collection of workers hooked together by sources and sinks.  For
 * example, if you want to read all the images from a directory, convert those images to
 * grayscale, and write them out to a different directory then each of those tasks will
 * probably be carried out by a worker somewhere.
 * </p><p>
 * That being said, the worker interface is quite simple.  It simply has an iterate method
 * which performs the work.  For example, an RGB->Grayscale worker's iterate method might
 * read in one RGB image from a source, create a new grayscale image, and then push that
 * grayscale image into a sink (there are, of course, implementations which don't create new
 * images and some which don't even read entire images into memory but this is just an example)
 * </p>
 * <h3>Statelessness</h3>
 * <p>
 * In general it is assumed that workers are stateless.  Stateless means the worker doesn't
 * keep track of anything between runs of iterate.  Another way of putting it is that there
 * aren't any instance variables that change from run to run based on the input (there can
 * be instance variables, parameters for example, they should just be constant throughout
 * the life of a worker).
 * </p><p>
 * The main appeal of a stateless worker is that it can be spawned onto multiple threads to
 * work in parallel.  In the graysacle example we provided above you could have 5 threads
 * grayscaling images and process a much faster rate of incoming images.
 * </p><p>
 * Some workers must have state.  For example, a Lucas-Kinade tracker looks at a series
 * of images and tracks a point moving through them.  If we spawned off 5 copies of the
 * tracker and gave a series of 5 images to each one then no single tracker would be able
 * to create the track.
 * </p><p>
 * Workers that must have state MUST be marked with the Stateful annotation.
 * </p>
 * <h3>Order</h3>
 * <p>
 * Order can sometimes be an important consideration for a worker.  In our example above
 * with turning images into grayscale we don't really care if the resulting images get out
 * of order.  However, if we were taking in an video, turning the video to grayscale, and
 * then playing it back out, then we do really care about order.
 * </p><p>
 * Unlike statefulness, order doesn't prevent us from running in parallel, it simply adds
 * overhead onto the buffers to ensure that we maintain the ordering of the components 
 * flowing through it.  Currently, ordering is a system level property and either an entire
 * system is ordered or not.
 * </p><p>
 * TODO: Determine if we need to allow a worker to specify ordering
 * </p><p>
 * A stateful worker will, of course, have an ordered output
 * </p>
 */
public abstract interface Worker {

	/**
	 * Runs through one pass of this worker, retrieving files from sources, transforming
	 * them, and writing the result out to sinks.
	 */
	public abstract void iterate();
	
	/**
	 * Initializes the worker.  This will be called once all sources and sinks have been
	 * connected and all parameters have been set.
	 */
	public abstract void init();
	
	/**
	 * Checks to see if a worker is finished and can no longer iterate.  Most 
	 * subclasses can always return true as they will end automatically when they
	 * try to pull from an input buffer and get an BufferEndedException.  This should only
	 * be needed for something like a source which has realized that its native input has
	 * run dry.  For example, the DirectoryScanner signals this when there are no more files
	 * that it can process.
	 */
	public abstract boolean isEnded();
	
}
