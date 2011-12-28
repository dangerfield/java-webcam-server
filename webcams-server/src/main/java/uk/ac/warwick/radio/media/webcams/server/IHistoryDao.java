package uk.ac.warwick.radio.media.webcams.server;

import uk.ac.warwick.radio.media.webcams.Dates;
import uk.ac.warwick.radio.media.webcams.Image;
import uk.ac.warwick.radio.media.webcams.Webcams;

import java.util.Date;

public interface IHistoryDao {
    public Image nearestImage(String camera, Date time) throws ArchiveException;

    public Dates getAvailableImagesFromCameraBetween(String camera, Date start,
                                                     Date end);

    public Webcams getAvailableCamerasBetween(Date start, Date end);

    public void saveImage(Image image);
}