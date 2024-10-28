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

  public static final String JSON_MIMETYPE = MediaType.JSON_UTF_8.toString();
  public static final String JAVASCRIPT_MIMETYPE = MediaType.JAVASCRIPT_UTF_8.toString();
  public static final String XML_MIMETYPE = MediaType.XML_UTF_8.toString();
  public static final String URL_ENCODED_MIMETYPE = "application/x-www-form-urlencoded";
  public static final String CSS_MIMETYPE = MediaType.CSS_UTF_8.toString();
  public static final String CSV_MIMETYPE = "text/csv";
  public static final String WEBP_MIMETYPE = "image/webp";
  public static final String BMP_MIMETYPE = "image/bmp";
  public static final String SVG_MIMETYPE = "image/svg+xml";
  public static final String TIFF_MIMETYPE = "image/tiff";
  public static final String ICON_MIMETYPE = "image/x-icon";
  public static final String MP4_MIMETYPE = "video/mp4";
  public static final String MPEG_MIMETYPE = "video/mpeg";
  public static final String WEBM_VIDEO_MIMETYPE = "video/webm";
  public static final String OGG_VIDEO_MIMETYPE = "video/ogg";
  public static final String MP3_MIMETYPE = MediaType.MPEG_AUDIO.toString();
  public static final String OGG_AUDIO_MIMETYPE = "audio/ogg";
  public static final String WAV_MIMETYPE = "audio/wav";
  public static final String WEBM_AUDIO_MIMETYPE = "audio/webm";
  public static final String GZIP_MIMETYPE = "application/gzip";
  public static final String EXCEL_MIMETYPE = "application/vnd.ms-excel";
  public static final String EXCEL_XLSX_MIMETYPE =
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
  public static final String WORD_MIMETYPE = "application/msword";
  public static final String WORD_DOCX_MIMETYPE =
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
  public static final String POWERPOINT_MIMETYPE = "application/vnd.ms-powerpoint";
  public static final String POWERPOINT_PPTX_MIMETYPE =
      "application/vnd.openxmlformats-officedocument.presentationml.presentation";
  public static final String TAR_MIMETYPE = "application/x-tar";
  public static final String SEVEN_ZIP_MIMETYPE = "application/x-7z-compressed";
  public static final String RAR_MIMETYPE = "application/x-rar-compressed";
  public static final String FLASH_MIMETYPE = "application/x-shockwave-flash";
  public static final String BINARY_MIMETYPE = MediaType.OCTET_STREAM.toString();
  public static final String JSON_API_MIMETYPE = "application/vnd.api+json";
  public static final String XUL_MIMETYPE = "application/vnd.mozilla.xul+xml";
  public static final String MULTIPART_FORM_MIMETYPE = MediaType.FORM_DATA.toString();
  public static final String EPUB_MIMETYPE = "application/epub+zip";
  public static final String TTF_MIMETYPE = "font/ttf";
  public static final String WOFF_MIMETYPE = "font/woff";
  public static final String WOFF2_MIMETYPE = "font/woff2";
  public static final String ODT_MIMETYPE = "application/vnd.oasis.opendocument.text";
  public static final String ODS_MIMETYPE = "application/vnd.oasis.opendocument.spreadsheet";
  public static final String ODP_MIMETYPE = "application/vnd.oasis.opendocument.presentation";
  public static final String GLTF_MIMETYPE = "model/gltf+json";

  // extensions
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
  public static final String PLAIN_EXT = "txt";
  public static final String JSON_EXT = "json";
  public static final String JAVASCRIPT_EXT = "js";
  public static final String XML_EXT = "xml";
  public static final String URL_ENCODED_EXT = "urlencoded";
  public static final String CSS_EXT = "css";
  public static final String CSV_EXT = "csv";
  public static final String WEBP_EXT = "webp";
  public static final String BMP_EXT = "bmp";
  public static final String SVG_EXT = "svg";
  public static final String TIFF_EXT = "tiff";
  public static final String ICON_EXT = "ico";
  public static final String MP4_EXT = "mp4";
  public static final String MPEG_EXT = "mpeg";
  public static final String WEBM_VIDEO_EXT = "webm";
  public static final String OGG_VIDEO_EXT = "ogv";
  public static final String MP3_EXT = "mp3";
  public static final String OGG_AUDIO_EXT = "oga";
  public static final String WAV_EXT = "wav";
  public static final String WEBM_AUDIO_EXT = "weba";
  public static final String GZIP_EXT = "gz";
  public static final String EXCEL_EXT = "xls";
  public static final String EXCEL_XLSX_EXT = "xlsx";
  public static final String WORD_EXT = "doc";
  public static final String WORD_DOCX_EXT = "docx";
  public static final String POWERPOINT_EXT = "ppt";
  public static final String POWERPOINT_PPTX_EXT = "pptx";
  public static final String TAR_EXT = "tar";
  public static final String SEVEN_ZIP_EXT = "7z";
  public static final String RAR_EXT = "rar";
  public static final String FLASH_EXT = "swf";
  public static final String BINARY_EXT = "bin";
  public static final String JSON_API_EXT = "json";
  public static final String XUL_EXT = "xul";
  public static final String MULTIPART_FORM_EXT = "multipart";
  public static final String EPUB_EXT = "epub";
  public static final String TTF_EXT = "ttf";
  public static final String WOFF_EXT = "woff";
  public static final String WOFF2_EXT = "woff2";
  public static final String ODT_EXT = "odt";
  public static final String ODS_EXT = "ods";
  public static final String ODP_EXT = "odp";
  public static final String GLTF_EXT = "gltf";



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
