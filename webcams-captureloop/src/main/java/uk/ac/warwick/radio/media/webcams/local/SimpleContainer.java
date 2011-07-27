package uk.ac.warwick.radio.media.webcams.local;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.ws.rs.core.Response;

import uk.ac.warwick.radio.media.webcams.Image;
import uk.ac.warwick.radio.media.webcams.MultipleImages;
import uk.ac.warwick.radio.media.webcams.server.endpoints.IPublish;

public class SimpleContainer implements ICameraContainer {
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

  protected static class Container {
    protected HashMap<String, Image> data = new HashMap<String, Image>();
    protected Object notifyObject = new Object();

    public synchronized Collection<Image> pull() {
      Collection<Image> old = data.values();
      data = new HashMap<String, Image>();
      return old;
    }

    public synchronized void put(Image upload) {
      if (data.containsKey(upload.getCamera().getId())) {
        System.err.printf("[WARN] %tc - %s - frame dropped%n", Calendar
            .getInstance(), upload.getCamera().getId());
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
            System.err
                .printf(
                    "[ERROR] %tc - error in uploading frames. recieved response %d from server%n",
                    Calendar.getInstance(), response.getStatus());
          }
        } catch (org.apache.cxf.interceptor.Fault e) {
          System.err.printf(
              "[ERROR] %tc - error in uploading frames. connection problem%n",
              Calendar.getInstance());
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
        System.err
            .printf(
                "[ERROR] %tc - %s - error in capturing image. Capture exception was thrown%n",
                Calendar.getInstance(), this.camera.getId());
        e.printStackTrace();
      }
    }
  }
}
