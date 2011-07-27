package uk.ac.warwick.radio.media.webcams.server.util;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class GenericHibernateDao extends HibernateDaoSupport {
  @Autowired
  public void anyMethodName(SessionFactory sessionFactory)
  {
      setSessionFactory(sessionFactory);
  }
}
