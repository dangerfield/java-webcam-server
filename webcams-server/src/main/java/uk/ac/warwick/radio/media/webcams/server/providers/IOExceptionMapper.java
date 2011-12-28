package uk.ac.warwick.radio.media.webcams.server.providers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.io.IOException;

public class IOExceptionMapper implements ExceptionMapper<IOException> {
    Response error404;

    public IOExceptionMapper() {
        error404 = Response.status(Response.Status.NOT_FOUND).header("Vary",
                "Accept-Encoding").build();
    }

    @Override
    public Response toResponse(IOException exception) {
        return error404;
    }

}
