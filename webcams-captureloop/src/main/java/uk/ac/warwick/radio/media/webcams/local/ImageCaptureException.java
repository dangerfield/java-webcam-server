package uk.ac.warwick.radio.media.webcams.local;

public class ImageCaptureException extends Exception {

  protected Exception wrappedException;
  public ImageCaptureException(Exception e) {
    wrappedException = e;
  }
  public ImageCaptureException() {}

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  
}
