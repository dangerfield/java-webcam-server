package uk.ac.warwick.radio.media.webcams.server.endpoints;

import org.apache.cxf.resource.URIResolver;
import org.springframework.core.io.ClassPathResource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: william
 * Date: 29/12/11
 * Time: 14:43
 * To change this template use File | Settings | File Templates.
 */
@Path("/")
public class Root {
    @GET
    @Path("/")
    @Produces("text/html")
    public InputStream getIndex() throws IOException {
        return new ClassPathResource("index.html").getInputStream();
    }
}
