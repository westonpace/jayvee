# Your first JayVee Application (the Grayscaler)

In this tutorial we are going to be making a system which turns images from color 
images to grayscale images.  For lack of creativity we&apos;ll call it a grayscaler.  This
is perhaps the simplest of image transformations, typically one takes the average of 
the different colors:

<math mathvariant='italic'>
    <mrow>
        <mtext>gray</mtext>
        <mo>=</mo>
        <mtext>(red</mtext>
        <mo>+</mo>
        <mtext>green</mtext>
        <mo>+</mo>
        <mtext>blue)</mtext>
        <mo>/</mo>
        <mn>3</mn>
    </mrow>
</math>

Of course, even with something as simple as this, there is more than one way to do it.  
You could take advantage of the fact that the human eye perceives some colors more strongly
than others and compute a more natural looking gray:

<math mathvariant='italic'>
    <mrow>
        <mtext>gray</mtext>
        <mo>=</mo>
        <mtext>red</mtext>
        <mo>*</mo>
        <mtext>0.3</mtext>
        <mo>+</mo>
        <mtext>green</mtext>
        <mo>*</mo>
        <mtext>.059</mtext>
        <mo>+</mo>
        <mtext>blue</mtext>
        <mo>*</mo>
        <mtext>.11</mtext>
    </mrow>
</math>

You could convert the image to an HSL format and simply take the luminosity:

<math mathvariant='italic'>
    <mrow>
        <mtext>gray</mtext>
        <mo>=</mo>
        <mtext>0.5</mtext>
        <mo>*</mo>
        <mtext mathvariant='normal'>max</mtext>
        <mtext>(</mtext>
        <mtext>red</mtext>
        <mtext>,</mtext>
        <mtext>green</mtext>
        <mtext>,</mtext>
        <mtext>blue</mtext>
        <mtext>)</mtext>
        <mo>+</mo>
        <mtext>0.5</mtext>
        <mo>*</mo>
        <mtext mathvariant='normal'>min</mtext>
        <mtext>(</mtext>
        <mtext>red</mtext>
        <mtext>,</mtext>
        <mtext>green</mtext>
        <mtext>,</mtext>
        <mtext>blue</mtext>
        <mtext>)</mtext>
    </mrow>
</math>

You could even convert the image to an image that only uses 4 different shades of gray 
(or 50).  In this tutorial we don&apos;t care how you do the actual conversion.  Since we&apos;re 
boring we&apos;ll be using the basic averaging method but you&apos;re welcome to use any method you 
want.

---

![Logo going from color to gray](../../images/grayscale_transform.png)

---

## An Overview of the Pieces

JayVee works by connecting pieces together which each perform some kind of individual task.
For this demo we&apos;ll need the following pieces:

---

### Source of Image Files

<img src="../../images/binary-input.svg" style="width:100%;" alt="binary data appearing from the ether" />

In order to conver images into grayscale we need, you guessed it, images.  JayVee supports
receiving images in a number of different ways, from the filesystem, a webcam, the internet,
pre-canned datasets, videos, etc.  For this tutorial our images will come from the filesystem.

### Image Reader

<img src="../../images/binary-to-rgb.svg" style="width:100%;" alt="binary data going to rgb pixels" />

Image files are merely a collection of bytes stored off somewhere.  To really make use of
them we need to parse those bytes into an array of pixels, more commonly known as an image.
An image reader can take in a stream of bytes and produce images or streams of pixels.

### Grayscaler

<img src="../../images/rgb-to-gray.svg" style="width:100%;" alt="rgb pixels going to gray pixels" />

This is the part we&apos;ll write ourselves.  We need something which takes in one image, a color
image, and spits out another image, a gray image.  We&apos;ll go into quite a bit of detail around
how we produce this piece.

---

### Image Writer

<img src="../../images/gray-to-binary.svg" style="width:100%;" alt="gray pixels going to binary" />

Once we have a destination, we need to turn that image back into a stream of bits.  The image
writer conveniently does this, exactly the opposite of the image reader.  As you can tell our
lack of creativity extends into naming as well.

### Image Destination

<img src="../../images/binary-output.svg" style="width:100%;" alt="binary fading into the ether" />

Just like we need images, we also need a place to put the images once we&apos;re done.  Again,
there are a number of choices.  We could display the images on screen in a gallery, in a
slideshow, or as a video stream.  We could pipe the bytes off into the ether.  Or, we can
simply write them back out to the disk in a new location.  As we&apos;ve already established the
fact that we&apos;re boring, we&apos;ll simply be writing them to disk in this tutorial.

### And More!

Just kidding, there isn&apos;t more in this tutorial, but there was a blank spot here.

---

## Workers (They do the Work)

The thing that makes JayVee different than most other vision librarys out there is JayVee&apos;s
workflow system.  In this system a set of workers are connected to each other through buffers.
Each worker has sources (which it receives data from) and sinks (which it sends data to).
To see an example, lets look at our first two pieces, the [DirectoryScanner][dir-scanner], 
and the [ImageIOImageReader][image-reader].

  [dir-scanner]: ../../../jayvee-core/0.1/apidocs/com/github/westonpace/jayvee/image/io/DirectoryScanner.html
  [image-reader]: ../../../jayvee-core/0.1/apidocs/com/github/westonpace/jayvee/image/io/jse/ImageIOImageReader.html

The directory scanner is our source of image files mentioned above.  It walks through a
directory, grabs every file, and passes the file on.  The directory scanner is a worker.
It sends its files forwards with the help of a sink.  In particular, we see it has a sink
of input streams named generatedInputStreams:

    public class DirectoryScanner {
    
        //...
        
        public Sink<InputStream> generatedInputStreams;
        
        //...
    
    }

The next step in our chain is the image reader.  It takes in input streams and turns them
into images.  In particular, the ImageIOImageReader uses Java&apos;s ImageIO class to do the 
reading of images.  Once again, the image reader is a worker.  However, instead of a just
a sink, the image reader has both a source and a sink:

    public class ImageIOImageReader {
    
        //...
        
        public Source<InputStream> inputStreamsToRead;
        public Sink<Image> generatedImages;
        
        //...
        
    }

Every worker in JayVee is going to have at least one source or sink.  Most workers are 
going to have at least one of each.  These workers with both are the ones that are actually
doing the work.  They are taking in images and creating image pyramids, taking in
features to track and creating tracked features, or taking in streams of pixels and spitting
out locations of faces.

Also, you&apos;ll notice that the sink on the directory scanner and the source on the image reader
are the same type (InputStream).  Doesn&apos;t this make you want to just connect the two pieces
together?

## Creating your own worker

Alright, it&apos;s time to get this demo started.  We&apos;re going to create a worker of our
own now.  This worker will be the worker that actually converts the RGB image into a 
grayscale image.  Being our typical creative selves we are going to call the class 
`GrayscaleFilter` but you can call it whatever you want. 

In order to create worker we need to implement the Worker interface.  Actually, since we
don&apos;t need any special initialization or teardown we can just extend the StandardWorker
abstract class (which provides basic setup and teardown).  That means we only have to
worry about:

    public abstract class StandardWorker {
    
        //...
        
        public abstract void iterate();
        
        //...
    
    }
    
The `iterate` method will be called repeatedly until we&apos;re finished.  With all workers this
method does the same thing.  It reads in some input from its sources, processes that input,
and sends the resulting objects to its sinks.  Which reminds me, we&apos;ll also need sources 
and sinks.  The sources or sinks can be public properties or <strike>private properties with
getter and setter methods</strike> (not for the 0.1 release).  For this example, we&apos;re going
to use public properties.

Our grayscale filter will be taking in images, transforming them, and outputting images so
putting everything together we get:

    public class GrayscaleFilter extends StandardWorker {
    
        public Source<Image> inputImages;
        public Sink<Image> outputImages;
    
        private void convertToGrayscale(Image colorImage) {
            //We'll leave this part up to you
        }
    
        public void iterate() {
            Image colorImage = inputImages.pop();
            Image grayImage = convertToGrayscale(colorImage);
            outputImages.push(grayImage);
        }
    
    }
    
Simple, right?
<!---
## Closing the loop (a minor detour)
-->
Before we talk about how we&apos;re going to connect all these workers together lets go ahead and
talk about the rest of the components, e.g. writing the images back out.  To write our images
we&apos;re going to go ahead and use the [ImageIOImageWriter][img-writer].  This component needs 
3 different pieces of input to operate:

    public class ImageIOImageWriter extends StandardWorker {
    
        //...
        
        public Source<OutputStream> outputStreamsToWriteTo;
        public Source<Image> imagesToWrite;
        public Source<String> imageFormats;
        
        //...
        
    }
    
The `imagesToWrite` should be easy enough to supply, that is simply the output of our filter.
The output streams to write to controls where our output will end up.  Again, to be boring,
we are simply going to write our images back onto the filesystem.  To do this we will use
the [IncrementingFileCreator][file-creator].  This worker creates output streams in a given
directory.  Each output stream will have a unique number sandwiched between a standard preifx
and suffix.  In other words, it will create files like "img-0.png, img-1.png, ..." if we 
supply "img" as the prefix and ".png" as the suffix.  

  [img-writer]: ../../../jayvee-core/0.1/apidocs/com/github/westonpace/jayvee/image/io/jse/ImageIOImageWriter.html
  [file-creator]: ../../../jayvee-core/0.1/apidocs/com/github/westonpace/jayvee/image/io/IncrementingFileCreator.html

The final input to the image writer is
the image format.  This is required by the ImageIO library and tells the library what the
file format should be.  Sample values are things like "png", "bmp", and "jpg".  For simplicity,
in this tutorial, we are just going to write all of our images out as "bmp".  To do this we
will supply the image write with the ConstantSource class which takes a value and spits that
value out into the buffer over and over again like a broken record.

## Building the system

Now we have all of our pieces.  It&apos;s time to connect them all together.  There are a number
of ways to do this.  <strike>You can use the GUI builder to configure and connect your system.  You
can use your favorite IoC container like Spring or Guice.</strike> (not in 0.1)  Finally, 
you can also build the system programatically with the SystemBuilder.  For this tutorial we
will use the system builder.

To use the system builder we need to follow these steps:

1. Create a SystemBuilder
2. Instantiate the workers
3. Configure the workers
4. Connect the workers
5. Build the system

The first step is simple enough.  Lets go ahead and make the main method for our application
and get started:

    public class MainClass {

        public static void main(String [] args) {
            SystemBuilder systemBuilder = new SystemBuilder();
        }

    }

### Instantiating the workers

Rather than new up the workers ourselves, we let the system builder instantiate the workers
for us.  This is required because the system builder is responsible for pre-processing and
post-processing the generated workers. (Note: In 0.2 this step has been removed in favor
of standard instantiation, so just bear with me)

    public class MainClass {

        public static void main(String [] args) {
            SystemBuilder systemBuilder = new SystemBuilder();
            
            DirectoryScanner directoryScanner = builder.buildWorker(DirectoryScanner.class);
            ImageIOImageReader imageReader = builder.buildWorker(ImageIOImageReader.class);
            ToGrayscale toGrayscale = builder.buildWorker(ToGrayscale.class);
            IncrementingFileCreator directoryWriter = builder.buildWorker(IncrementingFileCreator.class);
            ImageIOImageWriter imageWriter = builder.buildWorker(ImageIOImageWriter.class);
            @SuppressWarnings("unchecked")
            ConstantSource<String> formatSource = builder.buildWorker(ConstantSource.class);
            
        }

    }
    
### Configuring the workers

Once instantiated the workers are just like any other Java object and so we&apos;ll configure
them by setting the various properties.  Feel free to adjust this configuration to suit your
particular preferences:

    public class MainClass {

        public static void main(String [] args) {
            //...
            ConstantSource<String> formatSource = builder.buildWorker(ConstantSource.class);
            
            directoryScanner.setDirectory("/input-images");
            directoryWriter.setDirectory("/output-images");
            directoryWriter.setPrefix("img");
            directoryWriter.setSuffix(".bmp");
            formatSource.setValue("bmp");
            
        }
        
    }

### Connecting the workers

The system builder can magically connect two workers for us.  All we need to do is supply
it with the sink of one worker and the source of another workers.  It will take care of
worrying about multiple sources and sinks, how to synchronize access, whether a worker is
request only or not, all kinds of stuff that we don&apos;t really care about.  The one thing we
may want to specify however is the size of the buffer between the two workers.

One of the dangerous things about images is that they tend to be large.  Typically we&apos;re
used to working with compressed images and so we don&apos;t notice these things.  If you have a
1 megapixel image (e.g. 1024x1024), it has 3 bands, and we represent each value as a double
then you&apos;re looking at `1024*1024*3*8 = 24 megabytes`.  Even smart phones have 8 megapixel
cameras these days, and an 8 megapixel image is going to be 160 megabytes.  If we don&apos;t 
specify a max buffer size, then it will default to something rather large (over 100) and
if that buffer fills up with 8 megapixel images then we&apos;d have over 16 gigabytes worth of
images in memory.  Or more accurately, we wouldn&apos;t because the application would crash first.
  That is why we specify buffers of max size 1 when working with images buffers below:
  
    public class MainClass {

        public static void main(String [] args) {
            //...
            directoryWriter.setSuffix(".bmp");
            
            builder.connect(directoryScanner.generatedInputStreams, imageReader.inputStreamsToRead, 10);
            builder.connect(imageReader.generatedImages, toGrayscale.inputImages, 1);
            builder.connect(toGrayscale.outputImages, imageWriter.imagesToWrite, 1);
            builder.connect(directoryWriter.generatedOutputStreams, imageWriter.outputStreamsToWriteTo, 10);
            builder.connect(formatSource.outputSink, imageWriter.imageFormats, 10);
      
         }
         
    }
  
### Building and running the system

At this point we are ready to build the system.  The builder knows about all the workers.
All the workers have been configured.  All the workers have been connected.  The last part
is easy:

    public class MainClass {

        public static void main(String [] args) {
            //...
            builder.connect(formatSource.outputSink, imageWriter.imageFormats, 10);
    
            WorkflowSystem system = builder.build();
            system.start();
            system.join();
         }
         
     }

That&apos;s it.  Go ahead, test it out, see what results you get.  If you run into any bugs make
sure to file them.

## Next steps

By this point you should understand the basic JayVee workflow.  You&apos;ve created your own workers
and used existing workers to build a system.  You&apos;ve run that system to see some input.  If
you&apos;re interested in learning more about the system you should <strike>check out our next 
tutorial.</strike> (not yet written)