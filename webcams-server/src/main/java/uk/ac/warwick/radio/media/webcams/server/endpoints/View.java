package uk.ac.warwick.radio.media.webcams.server.endpoints;

import uk.ac.warwick.radio.media.webcams.server.Imaging;
import uk.ac.warwick.radio.media.webcams.server.RemoteWebcams;
import uk.ac.warwick.radio.media.webcams.server.ViewException;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class View implements IView {
    CacheControl caching = new CacheControl();

    protected RemoteWebcams webcams;

    public View(RemoteWebcams webcams) {
        this.webcams = webcams;

        /* Reverse proxy caches should cache images for a bit */
        caching.setSMaxAge(5);
        caching.setMustRevalidate(true);
        caching.setNoTransform(false);
        /* Browsers should not cache webcam images */
        caching.setMaxAge(0);
    }

    @Override
    public Response getWebcamFull(String id, boolean overlay)
            throws ViewException, IOException {
        if (!webcams.contains(id))
            throw new ViewException();
        Imaging image = new Imaging(webcams.get(id));
        if (overlay)
            image.overlay();

        return Response.ok(image.get()).header("Vary", "Accept-Encoding")
                .cacheControl(caching).build();
    }

    @Override
    public Response getWebcamBig(String id, boolean overlay, boolean crop)
            throws ViewException, IOException {
        return getWebcamCustom(id, 640, 480, overlay, crop);
    }

    @Override
    public Response getWebcamCustom(String id, int x, int y, boolean overlay,
                                    boolean crop) throws ViewException, IOException {
        if (!webcams.contains(id))
            throw new ViewException();
        Imaging image = new Imaging(webcams.get(id));

        if (crop)
            image.scaleAndCropTo(x, y);
        else
            image.scaleProportionallyToWithin(x, y);

        if (overlay)
            image.overlay();

        return Response.ok(image.get()).header("Vary", "Accept-Encoding")
                .cacheControl(caching).build();
    }

    @Override
    public Response getWebcamSmall(String id, boolean overlay, boolean crop)
            throws ViewException, IOException {
        return getWebcamCustom(id, 320, 240, overlay, crop);
    }

    @Override
    public Response getIndex() {
        return Response.ok(webcams.getWebcams()).header("Vary", "Accept-Encoding")
                .cacheControl(caching).build();
    }
}
