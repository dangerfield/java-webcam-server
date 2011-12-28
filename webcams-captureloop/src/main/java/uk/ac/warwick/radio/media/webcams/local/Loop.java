package uk.ac.warwick.radio.media.webcams.local;

import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;

import java.io.File;

public class Loop {
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        File config = new File(args[0]);
        if (!config.canRead()) {
            System.err.println("Config not readable");
            System.exit(1);
        }

        XmlBeanFactory factory = new XmlBeanFactory(new FileSystemResource(config));
        factory.getBean("containers");
    }

}
