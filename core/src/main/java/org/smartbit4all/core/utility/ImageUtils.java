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

  /**
   * Resize the image with the specified width and height.
   * 
   * If either width or height is a negative number then a value is substituted to maintain the
   * aspect ratio of the original image dimensions. If both width and height are negative, then the
   * original image dimensions are used.
   * 
   * @param image
   * @param width
   * @param height
   * @return
   * @throws IOException
   */
  public static BinaryData resizeImage(BinaryData image, int width, int height) throws IOException {
    BufferedImage bi = ImageIO.read(image.inputStream());
    return resizeImageInternal(width, height, bi);
  }

  private static BinaryData resizeImageInternal(int width, int height, BufferedImage bi)
      throws IOException {
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

  public static BinaryData resizeImageForWidth(BinaryData image, int width) throws IOException {
    return resizeImage(image, width, -1);
  }

  public static BinaryData resizeImageForHeight(BinaryData image, int height) throws IOException {
    return resizeImage(image, -1, height);
  }

  public static BinaryData shrink(BinaryData image, int width) throws IOException {
    BufferedImage bi = ImageIO.read(image.inputStream());
    int originalWidth = bi.getWidth();
    if (originalWidth > width) {
      return resizeImageInternal(width, -1, bi);
    }
    return null;
  }

}
