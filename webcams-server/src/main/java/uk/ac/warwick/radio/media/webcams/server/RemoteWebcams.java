package uk.ac.warwick.radio.media.webcams.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import uk.ac.warwick.radio.media.webcams.Image;
import uk.ac.warwick.radio.media.webcams.MultipleImages;
import uk.ac.warwick.radio.media.webcams.Webcam;
import uk.ac.warwick.radio.media.webcams.Webcams;

public class RemoteWebcams {
  protected Map<String, Image> remoteWebcams = Collections.synchronizedMap(new HashMap<String, Image>());

  protected Persister persister;
  
  public Persister getPersister() {
    return persister;
  }

  public void setPersister(Persister persister) {
    this.persister = persister;
  }

  public boolean contains(String image) {
    return remoteWebcams.containsKey(image);
  }

  public Image get(String image) {
    return remoteWebcams.get(image);
  }

  public Collection<Image> getCollection() {
    return remoteWebcams.values();
  }
  public Webcams getWebcams() {
    ArrayList<Webcam> webcams = new ArrayList<Webcam>(remoteWebcams.size());
    for(Image image : remoteWebcams.values())
      webcams.add(image.getCamera());
    return new Webcams(webcams);
  }
  
  public void set(Image image) {
    remoteWebcams.put(image.getCamera().getId(), image);
    if(persister != null)
      persister.trySave(image);
    
  }
  public void set(MultipleImages webcams){
    for(Image webcam : webcams.getWebcam())
      set(webcam);
  }
  
  public void remove(String image) {
    remoteWebcams.remove(image);
  }
  public void remove(Image image) {
    remove(image.getCamera().getId());
  }
  

}
