package uk.ac.warwick.radio.media.webcams.local;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LocalWebcams {
  protected static Map<String, LocalWebcam> localWebcams = Collections.synchronizedMap(new HashMap<String, LocalWebcam>());
  
  public static boolean contains(String identifier) {
    return localWebcams.containsKey(identifier);
  }

  public static LocalWebcam get(String identifier) {
    return localWebcams.get(identifier);
  }

  public static Collection<LocalWebcam> getWebcams() {
    return localWebcams.values();
  }
  public static void set(String identifier, LocalWebcam webcam) {
    localWebcams.put(identifier, webcam);
  }

}
