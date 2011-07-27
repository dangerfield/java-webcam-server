package uk.ac.warwick.radio.media.webcams.server.endpoints;

import java.io.IOException;


import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import uk.ac.warwick.radio.media.webcams.server.ViewException;

@Path("/view")
public interface IView {
  @GET
  @Path("/{webcam}/full/")
  @Produces("image/jpeg")
  public Response getWebcamFull(@PathParam("webcam") String id, @DefaultValue("false") @QueryParam("overlay") boolean overlay) throws ViewException, IOException;
  @GET
  @Path("/{webcam}/big/")
  @Produces("image/jpeg")
  public Response getWebcamBig(@PathParam("webcam") String id, @DefaultValue("false") @QueryParam("overlay") boolean overlay, @DefaultValue("true") @QueryParam("crop") boolean crop) throws ViewException, IOException;
  @GET
  @Path("/{webcam}/{x}/{y}/")
  @Produces("image/jpeg")
  public Response getWebcamCustom(@PathParam("webcam") String id, @PathParam("x") int x, @PathParam("y") int y, @DefaultValue("false") @QueryParam("overlay") boolean overlay, @DefaultValue("true") @QueryParam("crop") boolean crop) throws ViewException, IOException;
  @GET
  @Path("/{webcam}/")
  @Produces("image/jpeg")
  public Response getWebcamSmall(@PathParam("webcam") String id, @DefaultValue("false") @QueryParam("overlay") boolean overlay, @DefaultValue("true") @QueryParam("crop") boolean crop) throws ViewException, IOException;
  @GET
  @Path("/")
  @Produces("text/xml")
  public Response getIndex();
}
