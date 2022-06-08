package org.smartbit4all.core.utility;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataOutputStream;

/**
 * Same useful utility functions for handling images in java. Based on the {@link ImageIO} library
 * mainly.
 * 
 * @author Peter Boros
 */
public class ImageUtils {

  private ImageUtils() {
    super();
  }

  public static BinaryData resizeImage(BinaryData image, int width, int height) throws IOException {
    BufferedImage bi = ImageIO.read(image.inputStream());
    Image resizedImage = bi.getScaledInstance(width, height, Image.SCALE_SMOOTH);

    BufferedImage resizedBufferedImage = new BufferedImage(resizedImage.getWidth(null),
        resizedImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
    Graphics g = resizedBufferedImage.getGraphics();
    g.drawImage(resizedImage, 0, 0, null);
    g.dispose();

    BinaryDataOutputStream bdos = new BinaryDataOutputStream();
    try {
      ImageIO.write(resizedBufferedImage, "png", bdos);
    } finally {
      bdos.close();
    }
    return bdos.data();
  }


}
