package uk.ac.warwick.radio.media.webcams;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "webcam")
public class Webcam implements Serializable, Cloneable{
  /**
   * 
   */
  private static final long serialVersionUID = 273557213017141375L;
  protected String id;
  protected String name;


  public Webcam() {}
  public Webcam(String id) {
    this.setId(id);
  }
  public Webcam(String id, String name) {
    this.setId(id);
    this.setName(name);
  }

  @XmlAttribute
  public String getId() {
    return id;
  }

  @XmlAttribute
  public String getName() {
    return name;
  }
 
  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }
  @Override
  protected Object clone() throws CloneNotSupportedException {
    // TODO Auto-generated method stub
    return super.clone();
  }
  
}
