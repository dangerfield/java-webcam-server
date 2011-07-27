package uk.ac.warwick.radio.media.webcams.server.endpoints;

import javax.ws.rs.core.Response;

import uk.ac.warwick.radio.media.webcams.MultipleImages;
import uk.ac.warwick.radio.media.webcams.server.RemoteWebcams;

public class Publish implements IPublish {

  protected RemoteWebcams webcams;
  public Publish(RemoteWebcams webcams){
    this.webcams = webcams;
  }
  @Override
  public Response massUpdate(MultipleImages images) {
    this.webcams.set(images);
    return Response.ok().build();
  }
}
