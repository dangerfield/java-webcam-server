package uk.ac.warwick.radio.media.webcams;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;

@XmlRootElement(name = "images")
public class MultipleImages {
    protected Collection<Image> webcams;

    public MultipleImages() {
    }

    public MultipleImages(Collection<Image> webcams) {
        this.webcams = webcams;
    }

    public Collection<Image> getWebcam() {
        return this.webcams;
    }

    public void setWebcam(Collection<Image> webcams) {
        this.webcams = webcams;
    }

    public boolean contains(String webcam) {
        return this.webcams.contains(webcam);
    }
}
