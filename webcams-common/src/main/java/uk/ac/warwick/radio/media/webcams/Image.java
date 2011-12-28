package uk.ac.warwick.radio.media.webcams;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

@XmlRootElement(name = "image")
public class Image implements Serializable, Cloneable {
    /**
     *
     */
    private static final long serialVersionUID = 1108263203839773659L;
    protected Webcam camera;
    protected Date time;
    protected byte[] raw;

    public Webcam getCamera() {
        return camera;
    }

    public void setCamera(Webcam camera) {
        this.camera = camera;
    }

    public Image() {
    }

    public Image(byte[] raw) {
        this.setTime(new Date());
        this.setRaw(raw);
    }

    @XmlAttribute
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @XmlMimeType("image/jpeg")
    public byte[] getRaw() {
        return raw;
    }

    public void setRaw(byte[] rawImageData) {
        this.raw = rawImageData;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Image image = (Image) super.clone();
        image.camera = (Webcam) this.camera.clone();
        return image;
    }
}
