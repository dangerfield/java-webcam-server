package uk.ac.warwick.radio.media.webcams;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;


@XmlRootElement(name = "webcams")
public class Webcams {
    protected Collection<Webcam> webcams;

    public Webcams() {
    }

    public Webcams(Collection<Webcam> webcams) {
        this.webcams = webcams;
    }

    public Collection<Webcam> getWebcam() {
        return this.webcams;
    }

    public void setWebcam(Collection<Webcam> webcams) {
        this.webcams = webcams;
    }

    public boolean contains(String webcam) {
        return this.webcams.contains(webcam);
    }
}
