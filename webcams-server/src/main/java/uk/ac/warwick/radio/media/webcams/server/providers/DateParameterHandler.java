package uk.ac.warwick.radio.media.webcams.server.providers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.cxf.jaxrs.ext.ParameterHandler;


public class DateParameterHandler implements ParameterHandler<Date> {

  @Override
  public Date fromString(String arg0){
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    try {
      return format.parse(arg0);
    } catch (ParseException e) {e.printStackTrace();}
    return null;
  }
}
