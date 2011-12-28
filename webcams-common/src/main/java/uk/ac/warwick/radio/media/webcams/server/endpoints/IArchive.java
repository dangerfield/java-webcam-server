package uk.ac.warwick.radio.media.webcams.server.endpoints;

import uk.ac.warwick.radio.media.webcams.Dates;
import uk.ac.warwick.radio.media.webcams.Webcams;
import uk.ac.warwick.radio.media.webcams.server.ArchiveException;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Date;

@Path("/archive")
public interface IArchive {
    @GET
    @Path("/{webcam}/{time}/full/")
    @Produces("image/jpeg")
    public Response getWebcamFull(@PathParam("time") Date time, @PathParam("webcam") String id, @DefaultValue("false") @QueryParam("overlay") boolean overlay) throws IOException, ArchiveException;

    @GET
    @Path("/{webcam}/{time}/big/")
    @Produces("image/jpeg")
    public Response getWebcamBig(@PathParam("time") Date time, @PathParam("webcam") String id, @DefaultValue("false") @QueryParam("overlay") boolean overlay, @DefaultValue("true") @QueryParam("crop") boolean crop) throws IOException, ArchiveException;

    @GET
    @Path("/{webcam}/{time}/{x}/{y}/")
    @Produces("image/jpeg")
    public Response getWebcamCustom(@PathParam("time") Date time, @PathParam("webcam") String id, @PathParam("x") int x, @PathParam("y") int y, @DefaultValue("false") @QueryParam("overlay") boolean overlay, @DefaultValue("true") @QueryParam("crop") boolean crop) throws IOException, ArchiveException;

    @GET
    @Path("/{webcam}/{time}/")
    @Produces("image/jpeg")
    public Response getWebcamSmall(@PathParam("time") Date time, @PathParam("webcam") String id, @DefaultValue("false") @QueryParam("overlay") boolean overlay, @DefaultValue("true") @QueryParam("crop") boolean crop) throws IOException, ArchiveException;

    @GET
    @Path("/{webcam}/between/")
    @Produces("text/xml")
    public Dates getAvailableImagesFromCameraBetween(@PathParam("webcam") String id, @QueryParam("start") Date start, @QueryParam("end") Date end) throws ArchiveException;

    @GET
    @Path("/between/")
    @Produces("text/xml")
    public Webcams getAvailableCamerasBetween(@QueryParam("start") Date start, @QueryParam("end") Date end) throws ArchiveException;
}
