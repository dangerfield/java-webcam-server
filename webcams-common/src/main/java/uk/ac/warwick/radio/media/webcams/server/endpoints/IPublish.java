package uk.ac.warwick.radio.media.webcams.server.endpoints;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import uk.ac.warwick.radio.media.webcams.MultipleImages;
@Path("/publish")
public interface IPublish {
  
  @POST
  @Path("/")
  @Consumes("multipart/related;type=text/xml")
  public Response massUpdate (MultipleImages webcams);
}
