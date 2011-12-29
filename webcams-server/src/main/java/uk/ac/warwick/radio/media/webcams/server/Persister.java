package uk.ac.warwick.radio.media.webcams.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.radio.media.webcams.Image;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class Persister extends Thread {
    final Logger logger = LoggerFactory.getLogger(Persister.class);
    protected BlockingQueue<Image> queue;
    protected IHistoryDao dao;

    public Persister(BlockingQueue<Image> queue, IHistoryDao dao) {
        this.queue = queue;
        this.dao = dao;
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Image temp = (Image) ((Image) queue.take()).clone();
                temp.setRaw(new Imaging(temp).scaleAndCropTo(640, 480).get());
                dao.saveImage(temp);
            } catch (Throwable e) {
                logger.debug("Exception in Image Persistence thread: ", e);
            }
        }
    }

    public void trySave(Image image) {
        if (!this.queue.offer(image))
            logger.warn("Image Persistence has run out of space. Write latency is too high or queue size is too small");
    }
}
