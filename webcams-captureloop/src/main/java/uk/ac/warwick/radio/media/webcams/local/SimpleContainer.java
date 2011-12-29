package uk.ac.warwick.radio.media.webcams.local;

import com.google.common.util.concurrent.Monitor;
import com.google.common.util.concurrent.Uninterruptibles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.radio.media.webcams.Image;
import uk.ac.warwick.radio.media.webcams.MultipleImages;
import uk.ac.warwick.radio.media.webcams.server.endpoints.IPublish;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class SimpleContainer implements ICameraContainer {
    final Logger logger = LoggerFactory.getLogger(SimpleContainer.class);

    protected IPublish client;
    protected ArrayList<Timer> timers = new ArrayList<Timer>();
    protected Properties properties;
    protected SubmitThread submission = new SubmitThread();

    public SimpleContainer(Properties properties, IPublish client,
                           Collection<LocalWebcam> cameras) {

        this.properties = properties;

        this.client = client;

        for (LocalWebcam camera : cameras)
            this.addCamera(camera);

        this.submission.start();

    }

    protected void addCamera(LocalWebcam camera) {
        Timer timer = new Timer();

        timer.schedule(new CaptureTask(camera), Integer.parseInt(properties
                .getProperty("initialDelay", "1000")), Integer.parseInt(properties
                .getProperty("frameRate", "5000")));

        timers.add(timer);
    }

    protected class Container {
        protected Map<String, Image> data = Collections.synchronizedMap(new HashMap<String, Image>());
        protected final Monitor monitor = new Monitor();
        protected final Monitor.Guard notEmpty = new Monitor.Guard(monitor) {
            @Override
            public boolean isSatisfied() {
                return data.size() > 0;
            }
        };

        public Collection<Image> pull() {
            monitor.enter();
            try {
                Collection<Image> old = new ArrayList(data.values());
                data.clear();
                return old;
            } finally {
                monitor.leave();
            }

        }

        public void put(Image upload) {
            monitor.enter();
            try {
                if (data.containsKey(upload.getCamera().getId())) {
                    logger.warn("Frame dropped on camera {}", upload.getCamera().getId());
                }
                data.put(upload.getCamera().getId(), upload);
            } finally {
                monitor.leave();
            }
        }

        public void blockUntilNotEmpty() {
            monitor.enter();
            monitor.waitForUninterruptibly(notEmpty);
            monitor.leave();
        }
    }

    protected Container data = new Container();

    protected class SubmitThread extends Thread {
        @Override
        public void run() {
            while (true) {
                data.blockUntilNotEmpty();

                Uninterruptibles.sleepUninterruptibly(Long.parseLong(properties.getProperty(
                        "maxUploadDelay", "1000")), TimeUnit.MILLISECONDS);


                try {
                    Response response = client.massUpdate(new MultipleImages(data
                            .pull()));
                    if (response.getStatus() != 200) {
                        logger.error(
                                "Error in uploading frames. Received response \"{}\" from server",
                                response.getStatus());
                    }
                } catch (Throwable e) {
                    logger.error("Error in uploading frames. Reason unknown.");
                    logger.debug("Error in uploading frames. Thrown Exception:", e);
                }
            }
        }
    }

    protected class CaptureTask extends TimerTask {
        LocalWebcam camera;

        public CaptureTask(LocalWebcam camera) {
            this.camera = camera;
        }

        @Override
        public void run() {
            try {
                data.put(this.camera.getImage());
            } catch (ImageCaptureException e) {
                logger.error(
                        "Error in capturing image on camera \"{}\". {}.",
                        this.camera.getId(), e.getMessage());
                logger.debug("Error in capturing image on camera \"{}\". Thrown exception:", this.camera.getId(), e);
            } catch (Throwable e) {
                logger.error("Error in capturing image on camera \"{}\". Reason unknown.", this.camera.getId());
                logger.debug("Error in capturing image on camera \"{}\". Thrown exception:", this.camera.getId(), e);
            }
        }
    }
}
