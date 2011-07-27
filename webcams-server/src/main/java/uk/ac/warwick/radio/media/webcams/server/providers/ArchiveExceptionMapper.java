package uk.ac.warwick.radio.media.webcams.server.providers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import uk.ac.warwick.radio.media.webcams.server.ArchiveException;

public class ArchiveExceptionMapper implements ExceptionMapper<ArchiveException> {
  Response error404;
  public ArchiveExceptionMapper(){
    error404 = Response.status(Response.Status.NOT_FOUND).header("Vary",
    "Accept-Encoding").build();
  }
  @Override
  public Response toResponse(ArchiveException exception) {
    return error404;
  }

}
