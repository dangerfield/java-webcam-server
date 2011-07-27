package uk.ac.warwick.radio.media.webcams.local.types;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.imageio.ImageIO;

import uk.ac.warwick.radio.media.webcams.Image;
import uk.ac.warwick.radio.media.webcams.local.ImageCaptureException;
import uk.ac.warwick.radio.media.webcams.local.LocalWebcam;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IContainerFormat;
import com.xuggle.xuggler.IContainerParameters;
import com.xuggle.xuggler.IError;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

public class XugglerCamera extends LocalWebcam {
    
  /**
   * 
   */
  private static final long serialVersionUID = -1902516093310864732L;
  protected String driverName;
  protected String deviceName;
  protected CameraListener deviceListener;
  protected int channel;
  public XugglerCamera(String identifier, String name, String driverName, String deviceName, int channel) {
    super(identifier, name);
    this.driverName = driverName;
    this.deviceName = deviceName;
    this.deviceListener = new CameraListener();
    this.deviceListener.start();
    this.channel = channel;
  }

  @Override
  public Image getData() throws ImageCaptureException {
    
    synchronized (prepareImage) {
      prepareImage.set(true);
      while(prepareImage.get()){
      try {
        prepareImage.wait();
      } catch (InterruptedException e) {}
      }
    }
    
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try {
      ImageIO.write(this.deviceListener.getMostRecentImage(), "JPG",outputStream);
      byte[] jpegdata = outputStream.toByteArray();
      outputStream.close();
      return new Image(jpegdata);
    } catch (IOException e) {
      throw new ImageCaptureException(e);
    }
  }
  private AtomicBoolean prepareImage = new AtomicBoolean(false);

  protected class CameraListener extends Thread {
    protected BufferedImage mostRecentImage;
    public BufferedImage getMostRecentImage() {
      return mostRecentImage;
    }
    public void run() {
      // Let's make sure that we can actually convert video pixel formats.
      if (!IVideoResampler.isSupported(IVideoResampler.Feature.FEATURE_COLORSPACECONVERSION))
        throw new RuntimeException("you must install the GPL version of Xuggler (with IVideoResampler support) for this demo to work");

      // Create a Xuggler container object
      IContainer container = IContainer.make();

      // Devices, unlike most files, need to have parameters set in order
      // for Xuggler to know how to configure them.  For a webcam, these
      // parameters make sense
      IContainerParameters params = IContainerParameters.make();
      
      // The timebase here is used as the camera frame rate
      params.setTimeBase(IRational.make(30,1));
      params.setTVStandard("PAL");
      // we need to tell the driver what video with and height to use
      params.setVideoWidth(640);
      params.setVideoHeight(480);
      params.setTVChannel(channel);
      // and finally, we set these parameters on the container before opening
      container.setParameters(params);
      
      // Tell Xuggler about the device format
      IContainerFormat format = IContainerFormat.make();
      if (format.setInputFormat(driverName) < 0)
        throw new IllegalArgumentException("couldn't open webcam device: " + driverName);
      
      // Open up the container
      int retval = container.open(deviceName, IContainer.Type.READ, format);
      if (retval < 0)
      {
        // This little trick converts the non friendly integer return value into
        // a slightly more friendly object to get a human-readable error name
        IError error = IError.make(retval);
        throw new IllegalArgumentException("could not open file: " + deviceName + "; Error: " + error.getDescription());
      }      

      // query how many streams the call to open found
      int numStreams = container.getNumStreams();

      // and iterate through the streams to find the first video stream
      int videoStreamId = -1;
      IStreamCoder videoCoder = null;
      for(int i = 0; i < numStreams; i++)
      {
        // Find the stream object
        IStream stream = container.getStream(i);
        // Get the pre-configured decoder that can decode this stream;
        IStreamCoder coder = stream.getStreamCoder();

        if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO)
        {
          videoStreamId = i;
          videoCoder = coder;
          break;
        }
      }
      if (videoStreamId == -1)
        throw new RuntimeException("could not find video stream in container: "+deviceName);

      /*
       * Now we have found the video stream in this file.  Let's open up our decoder so it can
       * do work.
       */
      if (videoCoder.open() < 0)
        throw new RuntimeException("could not open video decoder for container: "+deviceName);

      IVideoResampler resampler = null;
      if (videoCoder.getPixelType() != IPixelFormat.Type.BGR24)
      {
        // if this stream is not in BGR24, we're going to need to
        // convert it.  The VideoResampler does that for us.
        resampler = IVideoResampler.make(videoCoder.getWidth(), videoCoder.getHeight(), IPixelFormat.Type.BGR24,
            videoCoder.getWidth(), videoCoder.getHeight(), videoCoder.getPixelType());
        if (resampler == null)
          throw new RuntimeException("could not create color space resampler for: " + deviceName);
      }

      /*
       * Now, we start walking through the container looking at each packet.
       */
      IConverter converter = null;
      IPacket packet = IPacket.make();
      while(container.readNextPacket(packet) >= 0)
      {
        if(!prepareImage.get())
          continue;
        
        /*
         * Now we have a packet, let's see if it belongs to our video stream
         */
        if (packet.getStreamIndex() == videoStreamId)
        {
          /*
           * We allocate a new picture to get the data out of Xuggler
           */
          IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(),
              videoCoder.getWidth(), videoCoder.getHeight());

          int offset = 0;
          while(offset < packet.getSize())
          {
            /*
             * Now, we decode the video, checking for any errors.
             * 
             */
            int bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);
            if (bytesDecoded < 0)
              throw new RuntimeException("got error decoding video in: " + deviceName);
            offset += bytesDecoded;

            /*
             * Some decoders will consume data in a packet, but will not be able to construct
             * a full video picture yet.  Therefore you should always check if you
             * got a complete picture from the decoder
             */
            if (picture.isComplete())
            {
              
              IVideoPicture newPic = picture;
              /*
               * If the resampler is not null, that means we didn't get the video in BGR24 format and
               * need to convert it into BGR24 format.
               */
              if (resampler != null)
              {
                // we must resample
                newPic = IVideoPicture.make(resampler.getOutputPixelFormat(), picture.getWidth(), picture.getHeight());
                if (resampler.resample(newPic, picture) < 0)
                  throw new RuntimeException("could not resample video from: " + deviceName);
              }
              if (newPic.getPixelType() != IPixelFormat.Type.BGR24)
                throw new RuntimeException("could not decode video as BGR 24 bit data in: " + deviceName);

              if(converter == null)
                converter = ConverterFactory.createConverter(
                  ConverterFactory.XUGGLER_BGR_24, newPic);
              
              mostRecentImage = converter.toImage(newPic);
              
              synchronized (prepareImage) {
                prepareImage.set(false);
                prepareImage.notify();
              }
            }
          }
        }
        else
        {
          /*
           * This packet isn't part of our video stream, so we just silently drop it.
           */
          do {} while(false);
        }

      }
      /*
       * Technically since we're exiting anyway, these will be cleaned up by 
       * the garbage collector... but because we're nice people and want
       * to be invited places for Christmas, we're going to show how to clean up.
       */
      if (videoCoder != null)
      {
        videoCoder.close();
        videoCoder = null;
      }
      if (container !=null)
      {
        container.close();
        container = null;
      }

    }
  }
}
