package uk.ac.warwick.radio.media.webcams.local.types;

import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.params.HttpParams;
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
    static protected HttpClient client = new DefaultHttpClient();

    static {
        HttpParams params = client.getParams();
        params.setParameter("http.socket.timeout", 3000);
        params.setParameter("http.connection.timeout", 3000);
        params.setParameter("http.connection-manager.timeout", 3000);
        params.setParameter("http.useragent", "WEBCAMLOOP");
        params.setParameter("http.method.retry-handler", new DefaultHttpRequestRetryHandler(1, false));
    }

    public IPCamera(String identifier, String name, String imageURL) {
        super(identifier, name);
        this.imageURL = imageURL;
        this.httpget = new HttpGet(imageURL);
    }

    @Override
    public Image getData() throws ImageCaptureException {

        try {
            final HttpResponse responce = client.execute(httpget);
            if (responce.getStatusLine().getStatusCode() != 200)
                throw new ImageCaptureException();

            return new Image(ByteStreams.toByteArray(new InputSupplier<InputStream>() {
                @Override
                public InputStream getInput() throws IOException {
                    return responce.getEntity().getContent();
                }
            }));
        } catch (ClientProtocolException e) {
            throw new ImageCaptureException(e);
        } catch (IOException e) {
            throw new ImageCaptureException(e);
        }
    }

}
