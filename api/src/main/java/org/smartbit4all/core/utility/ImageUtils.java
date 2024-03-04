package org.smartbit4all.core.utility;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
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
   * Resize the image with the specified width and height.<br/>
   * <b>Caution:</b> this method reads the input stream of the given {@code BinaryData} image and
   * then writes a new BinaryData with a .png formatted image
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
    try (InputStream is = image.inputStream()) {
      BufferedImage bi = ImageIO.read(is);
      return resizeImageInternal(width, height, bi);
    }
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
    // hint: bdos.data() can only be called when the stream is closed, thus try-with-resource can
    // not be used here.
    try {
      ImageIO.write(resizedBufferedImage, "png", bdos);
    } finally {
      bdos.close();
    }
    return bdos.data();
  }

  /**
   * <b>Caution:</b> this method reads the input stream of the given {@code BinaryData} image and
   * then writes a new BinaryData with a .png formatted image
   * 
   * @param image
   * @param width
   * @return
   * @throws IOException
   */
  public static BinaryData resizeImageForWidth(BinaryData image, int width) throws IOException {
    return resizeImage(image, width, -1);
  }

  /**
   * <b>Caution:</b> this method reads the input stream of the given {@code BinaryData} image and
   * then writes a new BinaryData with a .png formatted image
   * 
   * @param image
   * @param height
   * @return
   * @throws IOException
   */
  public static BinaryData resizeImageForHeight(BinaryData image, int height) throws IOException {
    return resizeImage(image, -1, height);
  }

  /**
   * <b>Caution:</b> this method reads the input stream of the given {@code BinaryData} image and
   * then writes a new BinaryData with a .png formatted image
   * 
   * @param image
   * @param width
   * @return
   * @throws IOException
   */
  public static BinaryData shrink(BinaryData image, int width) throws IOException {
    try (InputStream is = image.inputStream()) {
      BufferedImage bi = ImageIO.read(is);
      int originalWidth = bi.getWidth();
      if (originalWidth > width) {
        return resizeImageInternal(width, -1, bi);
      }
      return null;
    }
  }

  /**
   * Returns the width and height of the specified image. <br/>
   * <b>Caution:</b> this method reads the input stream of the given {@code BinaryData} image
   * 
   * @param image a {@code BinaryData} image to be examined, not null
   * @return a {@link Dimension} containing the width and height of the image
   * @throws IOException if the image file does not exist, cannot be opened or otherwise
   *         unavailable, or its format is not recognised by the standard Java ImageReader
   *         implementations
   */
  public static Dimension getDimension(BinaryData image) throws IOException {
    try (InputStream is = image.inputStream()) {
      BufferedImage bi = ImageIO.read(is);
      return new Dimension(bi.getWidth(), bi.getHeight());
    }
  }

}
