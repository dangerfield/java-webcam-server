package uk.ac.warwick.radio.media.webcams.server;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.radio.media.webcams.Image;
import uk.ac.warwick.radio.media.webcams.MultipleImages;
import uk.ac.warwick.radio.media.webcams.Webcam;
import uk.ac.warwick.radio.media.webcams.Webcams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class RemoteWebcams {
    final Logger logger = LoggerFactory.getLogger(RemoteWebcams.class);
    protected Cache<String, Image> remoteWebcams = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .removalListener(new RemovalListener<String, Image>() {
                @Override
                public void onRemoval(RemovalNotification<String, Image> objectObjectRemovalNotification) {
                    if (!objectObjectRemovalNotification.getCause().equals(RemovalCause.REPLACED))
                        logger.info("Camera {} has been removed from the live view.", objectObjectRemovalNotification.getValue().getCamera().getId());
                }
            }).build();

    protected Persister persister;

    public Persister getPersister() {
        return persister;
    }

    public void setPersister(Persister persister) {
        this.persister = persister;
    }

    public boolean contains(String image) {
        return remoteWebcams.asMap().containsKey(image);
    }

    public Image get(String image) {
        return remoteWebcams.getIfPresent(image);
    }

    public Collection<Image> getCollection() {
        return remoteWebcams.asMap().values();
    }

    public Webcams getWebcams() {
        ArrayList<Webcam> webcams = new ArrayList<Webcam>(Long.valueOf(remoteWebcams.size()).intValue());
        for (Image image : remoteWebcams.asMap().values())
            webcams.add(image.getCamera());
        return new Webcams(webcams);
    }

    public synchronized void set(Image image) {
        if (!contains(image.getCamera().getId()))
            logger.info("Camera {} has been added to live view.", image.getCamera().getId());
        remoteWebcams.put(image.getCamera().getId(), image);
        if (persister != null)
            persister.trySave(image);

    }

    public void set(MultipleImages webcams) {
        for (Image webcam : webcams.getWebcam())
            set(webcam);
    }

    public void remove(String image) {
        remoteWebcams.invalidate(image);
    }

    public void remove(Image image) {
        remove(image.getCamera().getId());
    }


}
