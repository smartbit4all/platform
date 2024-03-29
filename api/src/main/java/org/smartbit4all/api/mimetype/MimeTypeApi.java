package org.smartbit4all.api.mimetype;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.activation.MimetypesFileTypeMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import com.google.common.base.Strings;
import com.google.common.net.MediaType;

/**
 * Utility class for getting mimeType from filename, and extension from mimeType.
 *
 * @author Andras Pallo
 *
 */
public class MimeTypeApi implements InitializingBean {
  public static final String PDF_MIMETYPE = MediaType.PDF.toString();
  public static final String DOCX_MIMETYPE = MediaType.OOXML_DOCUMENT.toString();

  public static final String TXT_MIMETYPE = MediaType.PLAIN_TEXT_UTF_8.toString();
  public static final String HTML_MIMETYPE = MediaType.HTML_UTF_8.toString();

  public static final String PNG_MIMETYPE = MediaType.PNG.toString();
  public static final String JPEG_MIMETYPE = MediaType.JPEG.toString();
  public static final String GIF_MIMETYPE = MediaType.GIF.toString();

  public static final String XLSX_MIMETYPE = MediaType.OOXML_SHEET.toString();

  public static final String PDF_EXT = MediaType.PDF.subtype();
  public static final String DOCX_EXT = "docx";

  public static final String TXT_EXT = "txt";
  public static final String HTML_EXT = "html";

  public static final String PNG_EXT = "png";
  public static final String JPEG_EXT = "jpg";
  public static final String GIF_EXT = "gif";

  public static final String XLSX_EXT = "xlsx";


  /**
   * Includes the mimeTypes that can be recognized by the {@link MimetypesFileTypeMap}.
   */
  @Value("classpath:mimetypes.txt")
  private Resource mimetypeProperties;

  /**
   * Used for getting mimeTypes from filenames.
   */
  private MimetypesFileTypeMap fileTypeMap;

  /**
   * Includes the mimeType - extension pairs. By default it's not a bijection in many cases,
   * therefore initializing a map is required here.
   */
  private Map<String, String> extensionsByMimeType = new HashMap<>();

  @Override
  public void afterPropertiesSet() throws Exception {
    try {
      this.fileTypeMap = new MimetypesFileTypeMap(mimetypeProperties.getInputStream());
    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to load mimetype property file.", e);
    }

    initExtensionsByMimeType();
  }

  private void initExtensionsByMimeType() {
    extensionsByMimeType.put(PDF_MIMETYPE, "pdf");
    extensionsByMimeType.put(DOCX_MIMETYPE, "docx");

    extensionsByMimeType.put(TXT_MIMETYPE, "txt");
    extensionsByMimeType.put(HTML_MIMETYPE, "html");

    extensionsByMimeType.put(PNG_MIMETYPE, "png");
    extensionsByMimeType.put(JPEG_MIMETYPE, "jpeg");
    extensionsByMimeType.put(GIF_MIMETYPE, "gif");

    extensionsByMimeType.put(XLSX_MIMETYPE, "xlsx");
  }

  public String getMimeType(String filename) {
    return fileTypeMap.getContentType(filename);
  }

  public String getExtension(String mimeType) {
    return extensionsByMimeType.get(mimeType);
  }

  public String ensurePdfFilename(String filename) {
    if (Strings.isNullOrEmpty(filename)) {
      return "file.pdf";
    }

    if (filename.toLowerCase().endsWith(".pdf")) {
      return filename;
    }

    if (filename.toLowerCase().endsWith(".docx")) {
      return filename.substring(0, filename.length() - ".docx".length()) + ".pdf";
    }
    return filename + ".pdf";
  }

}
