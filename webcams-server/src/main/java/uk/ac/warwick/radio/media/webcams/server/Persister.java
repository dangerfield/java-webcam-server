package uk.ac.warwick.radio.media.webcams.server;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import uk.ac.warwick.radio.media.webcams.Image;

public class Persister extends Thread {
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
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }
  }
  }

  public void trySave(Image image) {
    this.queue.offer(image);
  }
}
