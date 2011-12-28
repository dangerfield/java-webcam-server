package uk.ac.warwick.radio.media.webcams.local.types;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import uk.ac.warwick.radio.media.webcams.Image;
import uk.ac.warwick.radio.media.webcams.local.ImageCaptureException;
import uk.ac.warwick.radio.media.webcams.local.LocalWebcam;

import java.io.IOException;
import java.io.InputStream;

public class IPCamera extends LocalWebcam {

    /**
     *
     */
    private static final long serialVersionUID = -1343179666638835136L;
    protected String imageURL;
    protected HttpGet httpget;
    protected HttpClient client = new DefaultHttpClient();

    public IPCamera(String identifier, String name, String imageURL) {
        super(identifier, name);
        this.imageURL = imageURL;
        this.httpget = new HttpGet(imageURL);
    }

    @Override
    public Image getData() throws ImageCaptureException {

        try {
            HttpResponse responce = client.execute(httpget);
            if (responce.getStatusLine().getStatusCode() != 200)
                throw new ImageCaptureException();

            InputStream stream = responce.getEntity().getContent();
            byte[] image = IOUtils.toByteArray(stream);
            stream.close();
            return new Image(image);
        } catch (ClientProtocolException e) {
            throw new ImageCaptureException(e);
        } catch (IOException e) {
            throw new ImageCaptureException(e);
        }
    }

}
