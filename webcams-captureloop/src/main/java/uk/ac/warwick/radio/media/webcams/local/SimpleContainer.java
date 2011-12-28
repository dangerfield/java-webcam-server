package uk.ac.warwick.radio.media.webcams.local;

import java.util.*;

import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.radio.media.webcams.Image;
import uk.ac.warwick.radio.media.webcams.MultipleImages;
import uk.ac.warwick.radio.media.webcams.server.endpoints.IPublish;

public class SimpleContainer implements ICameraContainer {
    final Logger logger = LoggerFactory.getLogger(SimpleContainer.class);

  protected IPublish client;
  protected ArrayList<Timer> timers = new ArrayList<Timer>();
  protected Properties properties;
  protected SubmitThread submission = new SubmitThread();

  public SimpleContainer(Properties properties, IPublish client,
      Collection<LocalWebcam> cameras) {

    this.properties = properties;

    this.client = client;

    for (LocalWebcam camera : cameras)
      this.addCamera(camera);

    this.submission.start();

  }

  protected void addCamera(LocalWebcam camera) {
    Timer timer = new Timer();
    
    timer.schedule(new CaptureTask(camera), Integer.parseInt(properties
        .getProperty("initialDelay", "1000")), Integer.parseInt(properties
            .getProperty("frameRate", "5000")));
    
    timers.add(timer);
  }

  protected class Container {
    protected Map<String, Image> data = initMap();
    protected Object notifyObject = new Object();

    protected Map<String, Image> initMap() {
        return Collections.synchronizedMap(new HashMap<String, Image>());
    }
    public synchronized Collection<Image> pull() {
      Collection<Image> old = data.values();
      data = initMap();
      return old;
    }

    public synchronized void put(Image upload) {
      if (data.containsKey(upload.getCamera().getId())) {
        logger.warn("{} frame dropped", upload.getCamera().getId());
      }
      data.put(upload.getCamera().getId(), upload);
      synchronized (notifyObject) {
        notifyObject.notify();
      }
    }

    public void blockUntilNotEmpty() {
      synchronized (notifyObject) {
        while (data.isEmpty()) {
          try {
            notifyObject.wait();
          } catch (InterruptedException e) {
          }
        }
      }
    }
  }

  protected Container data = new Container();

  protected class SubmitThread extends Thread {
    @Override
    public void run() {
      while (true) {
        data.blockUntilNotEmpty();
        try {
          Thread.sleep(Integer.parseInt(properties.getProperty(
              "maxUploadDelay", "1000")));
        } catch (InterruptedException e) {
        }
        try {         
          Response response = client.massUpdate(new MultipleImages(data
              .pull()));
          if (response.getStatus() != 200) {
            logger.error(
                    "Error in uploading frames. Recieved response {} from server",
                    response.getStatus());
          }
        } catch (org.apache.cxf.interceptor.Fault e) {
            logger.error("Error in uploading frames. connection problem");
        }
      }
    }
  }

  protected class CaptureTask extends TimerTask {
    LocalWebcam camera;

    public CaptureTask(LocalWebcam camera) {
      this.camera = camera;
    }

    @Override
    public void run() {
      try {
        data.put(this.camera.getImage());
      } catch (ImageCaptureException e) {
          logger.error(
                "Error in capturing image on camera {}. Capture exception was thrown",
                this.camera.getId());
        e.printStackTrace();
      } catch (Throwable e) {
          logger.error("Error in capturing image on camera {}. Exception was thrown",
                  this.camera.getId());
      }
    }
  }
}
