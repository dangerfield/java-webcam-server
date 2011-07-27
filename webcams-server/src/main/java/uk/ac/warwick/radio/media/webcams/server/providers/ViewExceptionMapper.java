package uk.ac.warwick.radio.media.webcams.server.providers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import uk.ac.warwick.radio.media.webcams.server.ViewException;

public class ViewExceptionMapper implements ExceptionMapper<ViewException> {
  Response error404;
  public ViewExceptionMapper(){
    error404 = Response.status(Response.Status.NOT_FOUND).header("Vary",
    "Accept-Encoding").build();
  }
  @Override
  public Response toResponse(ViewException exception) {
    return error404;
  }

}
