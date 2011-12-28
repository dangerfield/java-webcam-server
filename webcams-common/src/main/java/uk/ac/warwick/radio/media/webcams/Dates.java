package uk.ac.warwick.radio.media.webcams;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.Date;


@XmlRootElement(name = "dates")
public class Dates {
    protected Collection<Date> dates;

    public Dates() {
    }

    public Dates(Collection<Date> webcams) {
        this.dates = webcams;
    }

    public Collection<Date> getDate() {
        return this.dates;
    }

    public void setDate(Collection<Date> dates) {
        this.dates = dates;
    }

    public boolean contains(String date) {
        return this.dates.contains(date);
    }
}
