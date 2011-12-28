package uk.ac.warwick.radio.media.webcams.server;

import uk.ac.warwick.radio.media.webcams.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

public class Imaging {
    protected Image image;
    protected BufferedImage imageData;
    protected SimpleDateFormat dateFormat = new SimpleDateFormat("E yyyy/MM/dd hh:mm:ss a zzz");

    public Imaging(Image image) throws IOException {
        ImageIO.setUseCache(false); // Stops disk use
        this.image = image;
        InputStream inputStream = new ByteArrayInputStream(image.getRaw());
        this.imageData = ImageIO.read(inputStream);
        inputStream.close();
    }


    public byte[] get() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(this.imageData, "JPG", outputStream);
        byte[] jpegdata = outputStream.toByteArray();
        outputStream.close();
        return jpegdata;
    }

    public Imaging scale(float scale) {

        int x = Math.round(this.imageData.getWidth() * scale);
        int y = Math.round(this.imageData.getHeight() * scale);

        BufferedImage newImageData = new BufferedImage(x, y, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = newImageData.createGraphics();

        AffineTransform transform = AffineTransform.getScaleInstance(scale, scale);
        graphics.drawRenderedImage(this.imageData, transform);
        graphics.dispose();
        this.imageData = newImageData;

        return this;
    }

    public Imaging scaleProportionallyToWithin(int x, int y) {
        return scale((float) Math.min((double) x / this.imageData.getWidth(), (double) y / this.imageData.getHeight()));
    }

    public Imaging scaleAndCropTo(int x, int y) {
        BufferedImage newImageData = new BufferedImage(x, y, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = newImageData.createGraphics();

        float scale = (float) Math.max((double) x / this.imageData.getWidth(), (double) y / this.imageData.getHeight());

        int excessXpixels = Math.round(scale * this.imageData.getWidth()) - x;
        int excessYpixels = Math.round(scale * this.imageData.getHeight()) - y;

        graphics.translate(Math.round(-excessXpixels / 2), Math.round(-excessYpixels / 2));

        AffineTransform transform = AffineTransform.getScaleInstance(scale, scale);
        graphics.drawRenderedImage(this.imageData, transform);
        graphics.dispose();

        this.imageData = newImageData;

        return this;
    }

    public Imaging overlay() {

        int x = this.imageData.getWidth();
        int y = this.imageData.getHeight() + 15;

        BufferedImage newImageData = new BufferedImage(x, y, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = newImageData.createGraphics();

        graphics.setColor(Color.black);
        graphics.fillRect(0, 0, x, y);

        graphics.drawImage(this.imageData, 0, 15, null);

        graphics.setColor(Color.white);
        graphics.setFont(new Font("SansSerif", Font.PLAIN, 10));
        graphics.drawString(image.getCamera().getName(), 1, 11);
        String timestamp = dateFormat.format(image.getTime().getTime());
        graphics.drawString(timestamp, x - graphics.getFontMetrics().stringWidth(timestamp) - 1, 11);
        graphics.dispose();
        this.imageData = newImageData;

        return this;
    }


}
