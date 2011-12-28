package uk.ac.warwick.radio.media.webcams.server.endpoints;

import uk.ac.warwick.radio.media.webcams.MultipleImages;
import uk.ac.warwick.radio.media.webcams.server.RemoteWebcams;

import javax.ws.rs.core.Response;

public class Publish implements IPublish {

    protected RemoteWebcams webcams;

    public Publish(RemoteWebcams webcams) {
        this.webcams = webcams;
    }

    @Override
    public Response massUpdate(MultipleImages images) {
        this.webcams.set(images);
        return Response.ok().build();
    }
}
