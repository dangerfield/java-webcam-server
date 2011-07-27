package uk.ac.warwick.radio.media.webcams.server;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.warwick.radio.media.webcams.Dates;
import uk.ac.warwick.radio.media.webcams.Image;
import uk.ac.warwick.radio.media.webcams.Webcams;
import uk.ac.warwick.radio.media.webcams.server.util.GenericHibernateDao;

@Repository
@Transactional
public class HistoryDao extends GenericHibernateDao implements IHistoryDao {

  @Override
  public Webcams getAvailableCamerasBetween(Date start, Date end) {

    return new Webcams();
    
//    start = DateUtils.round(start, Calendar.MINUTE);
//    end = DateUtils.round(end, Calendar.MINUTE);
//
//    List<Webcam> list = getSessionFactory().getCurrentSession().createQuery(
//        "SELECT Webcam FROM Webcam join Image WHERE time BETWEEN ? AND ?").setDate(0, start).setDate(1, end).list();
//    return new Webcams(list);
  }

  @Override
  public Dates getAvailableImagesFromCameraBetween(String camera, Date start,
      Date end) {

    return new Dates();
//    start = DateUtils.round(start, Calendar.MINUTE);
//    end = DateUtils.round(end, Calendar.MINUTE);
//    List<Date> list = getSessionFactory().getCurrentSession().createQuery("SELECT time FROM image").
//    List<Date> list = getSessionFactory().getCurrentSession().createCriteria(Image.class)
//    .add(Restrictions.and(Restrictions.eq("camera.id", camera),Restrictions.between("time", start, end)))
//    .setMaxResults(50)
//    .list();
//
//    return new Dates(list);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Image nearestImage(String camera, final Date time) throws ArchiveException {

    Date start = DateUtils.addMinutes(DateUtils.round(time, Calendar.MINUTE),
        -2);
    Date end = DateUtils.addMinutes(DateUtils.round(time, Calendar.MINUTE), +2);

    
    List<Image> closeMatches = getSessionFactory().getCurrentSession().createCriteria(Image.class)
    .add(Restrictions.eq("camera.id", camera)).add(Restrictions.between("time", start, end))
    .list();

    if(closeMatches.size() == 0)
      throw new ArchiveException("No images found");
    
    if(closeMatches.size() > 1)
    Collections.sort(closeMatches, new Comparator<Image>() {

      @Override
      public int compare(Image arg0, Image arg1) {
        long t0 = distance(arg0, time);
        long t1 = distance(arg1, time);
        if (t0 > t1)
          return 1;
        if (t0 < t1)
          return -1;
        return 0;
      }

      protected long distance(Date arg0, Date arg1) {
        return Math.abs(arg0.getTime() - arg1.getTime());
      }

      protected long distance(Image arg0, Date arg1) {
        return distance(arg0.getTime(), arg1);
      }
    });
    return closeMatches.get(0);

  }

  @Override
  public void saveImage(Image image) {
    
    getSessionFactory().getCurrentSession().saveOrUpdate(image.getCamera());
    getSessionFactory().getCurrentSession().save(image);
  }

}
