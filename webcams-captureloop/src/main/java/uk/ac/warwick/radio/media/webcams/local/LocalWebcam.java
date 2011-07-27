package uk.ac.warwick.radio.media.webcams.local;

import uk.ac.warwick.radio.media.webcams.Image;
import uk.ac.warwick.radio.media.webcams.Webcam;

abstract public class LocalWebcam extends Webcam {
	
  /**
   * 
   */
  private static final long serialVersionUID = 8363100818999786339L;

  public abstract Image getData() throws ImageCaptureException;

  public LocalWebcam() {
    super();
    // TODO Auto-generated constructor stub
  }

  public LocalWebcam(String identifier, String name) {
    super(identifier, name);
    // TODO Auto-generated constructor stub
  }

  public LocalWebcam(String identifier) {
    super(identifier);
    // TODO Auto-generated constructor stub
  }

  public Webcam getWebcam() {
    return new Webcam(this.getId(), this.getName());
  }
  
  public Image getImage() throws ImageCaptureException {
    Image image = this.getData();
    image.setCamera(this.getWebcam());
    return image;
  }

}
