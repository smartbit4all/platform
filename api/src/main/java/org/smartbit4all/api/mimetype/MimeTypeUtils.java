package org.smartbit4all.api.mimetype;

public class MimeTypeUtils {


  /**
   * Changes the extension in the filename
   * 
   * @param fileName the orignal filename
   * @param newExtension the new extension without '.'
   * @param suffix optional text, that can be added before the extension e.g.
   *        [filename][suffix].[extension]
   * @return
   */
  public static String changeExtension(String fileName, String newExtension, String suffix) {
    String pureName = fileName.substring(0, fileName.lastIndexOf('.'));
    if (suffix != null) {
      pureName += suffix;
    }
    return pureName + "." + newExtension;
  }
}
