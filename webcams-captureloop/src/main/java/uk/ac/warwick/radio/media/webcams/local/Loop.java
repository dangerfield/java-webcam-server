package uk.ac.warwick.radio.media.webcams.local;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;

import java.io.File;

public class Loop {
    /**
     * @param args
     */
    public static void main(String[] args) {
        ;

        File config = new File(args[0]);
        if (!config.canRead()) {
            System.err.println("Config not readable");
            System.exit(1);
        }
        File logging = new File(config.getParent(), "log4j.properties");
        if (!config.canRead()) {
            System.err.println("Logging settings not readable");
            System.exit(1);
        }
        PropertyConfigurator.configure(logging.getAbsolutePath());

        XmlBeanFactory factory = new XmlBeanFactory(new FileSystemResource(config));
        factory.getBean("containers");
    }

}
