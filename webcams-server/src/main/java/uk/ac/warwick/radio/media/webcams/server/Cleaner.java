package uk.ac.warwick.radio.media.webcams.server;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import uk.ac.warwick.radio.media.webcams.Image;


public class Cleaner extends Thread {

  RemoteWebcams cameras;
  public Cleaner(RemoteWebcams cameras){
    this.cameras = cameras;
    this.start();
  }
  
  @Override
  public void run() {
    while(true) {
      try {
        Thread.sleep(getDelayTime());
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }    
      Date time = getTimeCutOff();
      for(Iterator<Image> itr = cameras.getCollection().iterator(); itr.hasNext(); ) {
        if (itr.next().getTime().compareTo(time) <= 0)
          itr.remove();
      }
    
    }
  }
  protected static Date getTimeCutOff() {
    Calendar time = Calendar.getInstance();
    time.add(Calendar.HOUR, -1);
    return time.getTime();
  }
  protected Date getOldestUpdate() {
    Date time = new Date();
    for (Image camera : cameras.getCollection())
      if (camera.getTime().compareTo(time) < 0)
        time = camera.getTime();
    return time;
  }
  protected long getDelayTime() {
    Calendar cutOff = Calendar.getInstance();
    cutOff.setTime(getTimeCutOff());
    Calendar oldestUpdate = Calendar.getInstance();
    oldestUpdate.setTime(getOldestUpdate());
    return Math.max(60000, oldestUpdate.getTimeInMillis() - cutOff.getTimeInMillis());
  }

}
