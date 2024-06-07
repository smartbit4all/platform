package org.smartbit4all.api.mimetype;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.activation.MimetypesFileTypeMap;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeTypeUtils;
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
  public static final String PPTX_MIMETYPE = MediaType.OOXML_PRESENTATION.toString();

  public static final String DOC_MIMETYPE = MediaType.MICROSOFT_WORD.toString();
  public static final String XLS_MIMETYPE = MediaType.MICROSOFT_EXCEL.toString();
  public static final String PPT_MIMETYPE = MediaType.MICROSOFT_POWERPOINT.toString();

  public static final String ZIP_MIMETYPE = MediaType.ZIP.toString();

  public static final String PDF_EXT = "pdf";
  public static final String DOCX_EXT = "docx";
  public static final String DOC_EXT = "doc";

  public static final String TXT_EXT = "txt";
  public static final String HTML_EXT = "html";

  public static final String PNG_EXT = "png";
  public static final String JPEG_EXT = "jpg";
  public static final String GIF_EXT = "gif";

  public static final String XLSX_EXT = "xlsx";
  public static final String XLS_EXT = "xls";

  public static final String PPT_EXT = "ppt";
  public static final String PPTX_EXT = "pptx";

  public static final String ZIP_EXT = "zip";


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

    extensionsByMimeType.put(XLSX_MIMETYPE, XLSX_EXT);
    extensionsByMimeType.put(DOC_MIMETYPE, DOC_EXT);
    extensionsByMimeType.put(XLS_MIMETYPE, XLS_EXT);
    extensionsByMimeType.put(PPT_MIMETYPE, PPT_EXT);
    extensionsByMimeType.put(PPTX_MIMETYPE, PPTX_EXT);
    extensionsByMimeType.put(ZIP_MIMETYPE, ZIP_EXT);
  }

  public String getMimeType(String filename) {
    return getMimeTypeFromBuiltIns(filename).orElseGet(() -> fileTypeMap.getContentType(filename));
  }

  /**
   * Retrieves the MIME type from built-in extensions based on the provided filename.
   *
   * @param filename the name of the file to determine the MIME type for
   * @return an {@link Optional} containing the MIME type if found, otherwise an empty
   *         {@link Optional}
   */
  private Optional<String> getMimeTypeFromBuiltIns(final String filename) {
    if (!Strings.isNullOrEmpty(filename)) {

      final int lastDot = filename.lastIndexOf('.');
      if (lastDot > 0) {

        final String ext = filename.substring(lastDot + 1);
        if (!Strings.isNullOrEmpty(ext)) {

          return extensionsByMimeType.entrySet().stream()
              .filter(e -> ext.toLowerCase().equals(e.getValue()))
              .findAny()
              .map(Map.Entry::getKey)
              .filter(it -> !Strings.isNullOrEmpty(it));
        }
      }
    }
    return Optional.empty();
  }

  /**
   * Retrieves the file extension for the given MIME type.
   *
   * @param mimeType the MIME type to get the file extension for
   * @return the file extension associated with the given MIME type
   */
  public String getExtension(String mimeType) {
    String extension = extensionsByMimeType.get(mimeType);
    if (extension == null) {
      extension = MimeTypeUtils.parseMimeType(mimeType).getSubtype();
    }
    return extension;
  }

  /**
   * Retrieves the file extension from the provided filename.
   *
   * @param fileName the name of the file to extract the extension from
   * @return the file extension if found, otherwise null
   */
  public String getExtensionFromFileName(String fileName) {
    if (fileName == null) {
      return null;
    }
    int dotIndex = fileName.lastIndexOf(StringConstant.DOT);
    if (dotIndex < 0) {
      return null;
    }
    return fileName.substring(dotIndex + 1);
  }

  /**
   * Ensures the filename has the correct extension based on the provided MIME type.
   *
   * @param filename the name of the file to ensure the extension for
   * @param mimeType the MIME type to determine the correct extension
   * @return the filename with the correct extension
   */
  public String ensureFileExtension(String filename, String mimeType) {
    String extension = getExtension(mimeType);
    String suffix = StringConstant.DOT + extension;
    if (filename == null) {
      return "unnamed" + suffix;
    }
    int dotIndex = filename.lastIndexOf(StringConstant.DOT);
    if (dotIndex < 0) {
      // The dot was not found.
      return filename + suffix;
    } else {
      return filename.substring(0, dotIndex) + suffix;
    }
  }

  /**
   * Ensures the filename has a .pdf extension. If the filename is null or empty, returns
   * "file.pdf". If the filename ends with .docx, replaces the .docx extension with .pdf.
   *
   * @param filename the name of the file to ensure the .pdf extension for
   * @return the filename with the .pdf extension
   */
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
