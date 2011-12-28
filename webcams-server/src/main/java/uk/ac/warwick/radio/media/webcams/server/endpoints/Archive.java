package uk.ac.warwick.radio.media.webcams.server.endpoints;

import uk.ac.warwick.radio.media.webcams.Dates;
import uk.ac.warwick.radio.media.webcams.Webcams;
import uk.ac.warwick.radio.media.webcams.server.ArchiveException;
import uk.ac.warwick.radio.media.webcams.server.IHistoryDao;
import uk.ac.warwick.radio.media.webcams.server.Imaging;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Date;

public class Archive implements IArchive {
    CacheControl caching = new CacheControl();

    protected IHistoryDao dao;

    @Override
    public Dates getAvailableImagesFromCameraBetween(String id, Date start,
                                                     Date end) {
        return dao.getAvailableImagesFromCameraBetween(id, start, end);
    }

    @Override
    public Webcams getAvailableCamerasBetween(Date start, Date end) {
        return dao.getAvailableCamerasBetween(start, end);
    }

    public Archive(IHistoryDao dao) {
        this.dao = dao;
        /* Reverse proxy caches should cache images for a bit */
        caching.setSMaxAge(5);
        caching.setMustRevalidate(true);
        caching.setNoTransform(false);
        /* Browsers should not cache webcam images */
        caching.setMaxAge(0);
    }

    @Override
    public Response getWebcamBig(Date time, String id, boolean overlay,
                                 boolean crop) throws IOException, ArchiveException {
        return getWebcamCustom(time, id, 640, 480, overlay, crop);
    }

    @Override
    public Response getWebcamCustom(Date time, String id, int x, int y,
                                    boolean overlay, boolean crop) throws IOException, ArchiveException {
        Imaging image = new Imaging(dao.nearestImage(id, time));

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
    public Response getWebcamFull(Date time, String id, boolean overlay)
            throws IOException, ArchiveException {
        Imaging image = new Imaging(dao.nearestImage(id, time));
        if (overlay)
            image.overlay();

        return Response.ok(image.get()).header("Vary", "Accept-Encoding")
                .cacheControl(caching).build();

    }

    @Override
    public Response getWebcamSmall(Date time, String id, boolean overlay,
                                   boolean crop) throws IOException, ArchiveException {
        return getWebcamCustom(time, id, 320, 240, overlay, crop);
    }

//  public Response getZip(String[] id, Date start, Date end, int x, int y,
//      boolean overlay, boolean crop) throws IOException {
//    String outFilename = "BOB";
//    ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(outFilename));
//    Iterator<Image> itr = dao.getImageIteratorFor(id, start, end);
//    while (itr.hasNext()) {
//      zip.putNextEntry(new ZipEntry("F"));
//
//      Imaging image = new Imaging(itr.next());
//
//      if (crop)
//        image.scaleAndCropTo(x, y);
//      else
//        image.scaleProportionallyToWithin(x, y);
//
//      if (overlay)
//        image.overlay();
//      zip.write(image.get());
//      zip.closeEntry();
//    }
//    zip.
//    return Response.ok(zip).build();
//  }

}
