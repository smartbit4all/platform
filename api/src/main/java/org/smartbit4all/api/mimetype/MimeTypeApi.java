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
  public static final String TAR_MIMETYPE = "application/x-tar";
  public static final String SEVEN_ZIP_MIMETYPE = "application/x-7z-compressed";
  public static final String RAR_MIMETYPE = "application/x-rar-compressed";
  public static final String FLASH_MIMETYPE = "application/x-shockwave-flash";
  public static final String BINARY_MIMETYPE = MediaType.OCTET_STREAM.toString();
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
  public static final String FLAC_MIMETYPE = "audio/flac";
  public static final String M4A_MIMETYPE = "audio/m4a";
  public static final String GPP2_MIMETYPE = "video/3gpp2";
  public static final String M4V_MIMETYPE = "video/x-m4v";
  public static final String GPP_MIMETYPE = "video/3gpp";
  public static final String MKV_MIMETYPE = "video/x-matroska";

  public static final String JAVA_MIMETYPE = "text/x-java-source";
  public static final String CPP_MIMETYPE = "text/x-c++src";
  public static final String GROOVY_MIMETYPE = "text/x-groovy";
  public static final String IBPK_MIMETYPE = "application/x-ibooks+zip";
  public static final String ATOM_MIMETYPE = "application/atom+xml";
  public static final String RSS_MIMETYPE = "application/rss+xml";
  public static final String AFM_MIMETYPE = "application/x-font-adobe-metric";
  public static final String HDF_MIMETYPE = "application/x-hdf";
  public static final String ASP_MIMETYPE = "application/x-asp";
  public static final String XHTML_MIMETYPE = "application/xhtml+xml";
  public static final String PSD_MIMETYPE = "image/vnd.adobe.photoshop";
  public static final String ANPA_MIMETYPE = "text/vnd.iptc.anpa";
  public static final String IWORK_MIMETYPE = "application/vnd.apple.iwork";
  public static final String NUMBERS_MIMETYPE = "application/vnd.apple.numbers";
  public static final String KEY_MIMETYPE = "application/vnd.apple.keynote";
  public static final String PAGES_MIMETYPE = "application/vnd.apple.pages";
  public static final String RFC822_MIMETYPE = "message/rfc822";
  public static final String MATLAB_MIMETYPE = "application/x-matlab-data";
  public static final String MBOX_MIMETYPE = "application/mbox";
  public static final String PST_MIMETYPE = "application/vnd.ms-outlook-pst";
  public static final String MSPUB_MIMETYPE = "application/x-mspublisher";
  public static final String MSOFFICE_MIMETYPE = "application/x-tika-msoffice";
  public static final String SLDWORKS_MIMETYPE = "application/sldworks";
  public static final String MSWORKS_MIMETYPE = "application/x-tika-msworks-spreadsheet";
  public static final String OLE10_MIMETYPE =
      "application/x-tika-msoffice-embedded; format=ole10_native";
  public static final String MPP_MIMETYPE = "application/vnd.ms-project";
  public static final String OOXML_MIMETYPE = "application/x-tika-ooxml-protected";
  public static final String MSG_MIMETYPE = "application/vnd.ms-outlook";
  public static final String VSD_MIMETYPE = "application/vnd.visio";
  public static final String TNEF_MIMETYPE = "application/vnd.ms-tnef";
  public static final String XLSM_MIMETYPE = "application/vnd.ms-excel.sheet.macroenabled.12";
  public static final String PPTM_MIMETYPE =
      "application/vnd.ms-powerpoint.presentation.macroenabled.12";
  public static final String XLTX_MIMETYPE =
      "application/vnd.openxmlformats-officedocument.spreadsheetml.template";
  public static final String POTX_MIMETYPE =
      "application/vnd.openxmlformats-officedocument.presentationml.template";
  public static final String XLAM_MIMETYPE = "application/vnd.ms-excel.addin.macroenabled.12";
  public static final String DOTM_MIMETYPE = "application/vnd.ms-word.document.macroenabled.12";
  public static final String XLTM_MIMETYPE = "application/vnd.ms-excel.template.macroenabled.12";
  public static final String DOTX_MIMETYPE =
      "application/vnd.openxmlformats-officedocument.wordprocessingml.template";
  public static final String PPSM_MIMETYPE =
      "application/vnd.ms-powerpoint.slideshow.macroenabled.12";
  public static final String PPAM_MIMETYPE = "application/vnd.ms-powerpoint.addin.macroenabled.12";
  public static final String DOT_MIMETYPE = "application/vnd.ms-word.template.macroenabled.12";
  public static final String PPSX_MIMETYPE =
      "application/vnd.openxmlformats-officedocument.presentationml.slideshow";
  public static final String ODG_MIMETYPE =
      "application/x-vnd.oasis.opendocument.graphics-template";
  public static final String SXW_MIMETYPE = "application/vnd.sun.xml.writer";
  public static final String ODM_MIMETYPE = "application/vnd.oasis.opendocument.text-master";
  public static final String OTT_MIMETYPE = "application/vnd.oasis.opendocument.text-template";
  public static final String OTP_MIMETYPE =
      "application/vnd.oasis.opendocument.presentation-template";
  public static final String ODC_MIMETYPE = "application/vnd.oasis.opendocument.chart-template";
  public static final String FODT_MIMETYPE =
      "application/x-vnd.oasis.opendocument.formula-template";
  public static final String BZ_MIMETYPE = "application/x-bzip";
  public static final String BZ2_MIMETYPE = "application/x-bzip2";
  public static final String JAR_MIMETYPE = "application/java-archive";
  public static final String CPIO_MIMETYPE = "application/x-cpio";
  public static final String RTF_MIMETYPE = "application/rtf";
  public static final String FB2_MIMETYPE = "application/x-fictionbook+xml";

  // extensions
  public static final String PDF_EXT = "pdf";
  public static final String DOCX_EXT = "docx";
  public static final String DOC_EXT = "doc";
  public static final String TXT_EXT = "txt";
  public static final String HTML_EXT = "html";
  public static final String PNG_EXT = "png";
  public static final String JPG_EXT = "jpg";
  public static final String JPEG_EXT = "jpeg";
  public static final String GIF_EXT = "gif";
  public static final String XLSX_EXT = "xlsx";
  public static final String XLS_EXT = "xls";
  public static final String PPT_EXT = "ppt";
  public static final String PPTX_EXT = "pptx";
  public static final String ZIP_EXT = "zip";
  public static final String JSON_EXT = "json";
  public static final String JAVASCRIPT_EXT = "js";
  public static final String XML_EXT = "xml";
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
  public static final String OGV_EXT = "ogv";
  public static final String MP3_EXT = "mp3";
  public static final String OGG_EXT = "ogg";
  public static final String WAV_EXT = "wav";
  public static final String WEBM_AUDIO_EXT = "weba";
  public static final String GZIP_EXT = "gz";
  public static final String TAR_EXT = "tar";
  public static final String SEVEN_ZIP_EXT = "7z";
  public static final String RAR_EXT = "rar";
  public static final String FLASH_EXT = "swf";
  public static final String BINARY_EXT = "bin";
  public static final String JSON_API_EXT = "json";
  public static final String XUL_EXT = "xul";
  public static final String EPUB_EXT = "epub";
  public static final String TTF_EXT = "ttf";
  public static final String WOFF_EXT = "woff";
  public static final String WOFF2_EXT = "woff2";
  public static final String ODT_EXT = "odt";
  public static final String ODS_EXT = "ods";
  public static final String ODP_EXT = "odp";
  public static final String GLTF_EXT = "gltf";
  public static final String FLAC_EXT = "flac";
  public static final String M4A_EXT = "m4a";
  public static final String GPP2_EXT = "3g2";
  public static final String M4V_EXT = "m4v";
  public static final String GPP_EXT = "3gp";
  public static final String MKV_EXT = "mkv";

  public static final String JAVA_EXT = "java";
  public static final String CPP_EXT = "cpp";
  public static final String GROOVY_EXT = "groovy";
  public static final String IBPK_EXT = "ibooks";
  public static final String ATOM_EXT = "atom";
  public static final String RSS_EXT = "rss";
  public static final String AFM_EXT = "afm";
  public static final String HDF_EXT = "hdf";
  public static final String ASP_EXT = "asp";
  public static final String XHTML_EXT = "xhtml";
  public static final String PSD_EXT = "psd";
  public static final String ANPA_EXT = "anpa";
  public static final String IWORK_EXT = "iwork";
  public static final String NUMBERS_EXT = "numbers";
  public static final String KEY_EXT = "key";
  public static final String PAGES_EXT = "pages";
  public static final String RFC822_EXT = "eml";
  public static final String MATLAB_EXT = "mat";
  public static final String MBOX_EXT = "mbox";
  public static final String PST_EXT = "pst";
  public static final String MSPUB_EXT = "pub";
  public static final String MSOFFICE_EXT = "mso";
  public static final String SLDWORKS_EXT = "sldprt";
  public static final String MSWORKS_EXT = "wks";
  public static final String OLE10_EXT = "ole10";
  public static final String MPP_EXT = "mpp";
  public static final String OOXML_EXT = "ooxml";
  public static final String MSG_EXT = "msg";
  public static final String VSD_EXT = "vsd";
  public static final String TNEF_EXT = "tnef";
  public static final String XLSM_EXT = "xlsm";
  public static final String PPTM_EXT = "pptm";
  public static final String XLTX_EXT = "xltx";
  public static final String POTX_EXT = "potx";
  public static final String XLAM_EXT = "xlam";
  public static final String DOTM_EXT = "dotm";
  public static final String XLTM_EXT = "xltm";
  public static final String DOTX_EXT = "dotx";
  public static final String PPSM_EXT = "ppsm";
  public static final String PPAM_EXT = "ppam";
  public static final String DOT_EXT = "dot";
  public static final String PPSX_EXT = "ppsx";
  public static final String ODG_EXT = "odg";
  public static final String SXW_EXT = "sxw";
  public static final String ODM_EXT = "odm";
  public static final String OTT_EXT = "ott";
  public static final String OTP_EXT = "otp";
  public static final String ODC_EXT = "odc";
  public static final String FODT_EXT = "fodt";
  public static final String BZ_EXT = "bz";
  public static final String BZ2_EXT = "bz2";
  public static final String JAR_EXT = "jar";
  public static final String CPIO_EXT = "cpio";
  public static final String RTF_EXT = "rtf";
  public static final String FB2_EXT = "fb2";

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
    extensionsByMimeType.put(PDF_MIMETYPE, PDF_EXT);
    extensionsByMimeType.put(DOCX_MIMETYPE, DOCX_EXT);
    extensionsByMimeType.put(DOC_MIMETYPE, DOC_EXT);
    extensionsByMimeType.put(TXT_MIMETYPE, TXT_EXT);
    extensionsByMimeType.put(HTML_MIMETYPE, HTML_EXT);
    extensionsByMimeType.put(PNG_MIMETYPE, PNG_EXT);
    extensionsByMimeType.put(JPEG_MIMETYPE, JPG_EXT);
    extensionsByMimeType.put(JPG_EXT, JPG_EXT);
    extensionsByMimeType.put(GIF_MIMETYPE, GIF_EXT);
    extensionsByMimeType.put(XLSX_MIMETYPE, XLSX_EXT);
    extensionsByMimeType.put(XLS_MIMETYPE, XLS_EXT);
    extensionsByMimeType.put(PPT_MIMETYPE, PPT_EXT);
    extensionsByMimeType.put(PPTX_MIMETYPE, PPTX_EXT);
    extensionsByMimeType.put(ZIP_MIMETYPE, ZIP_EXT);
    extensionsByMimeType.put(JSON_MIMETYPE, JSON_EXT);
    extensionsByMimeType.put(JAVASCRIPT_MIMETYPE, JAVASCRIPT_EXT);
    extensionsByMimeType.put(XML_MIMETYPE, XML_EXT);
    extensionsByMimeType.put(CSS_MIMETYPE, CSS_EXT);
    extensionsByMimeType.put(CSV_MIMETYPE, CSV_EXT);
    extensionsByMimeType.put(WEBP_MIMETYPE, WEBP_EXT);
    extensionsByMimeType.put(BMP_MIMETYPE, BMP_EXT);
    extensionsByMimeType.put(SVG_MIMETYPE, SVG_EXT);
    extensionsByMimeType.put(TIFF_MIMETYPE, TIFF_EXT);
    extensionsByMimeType.put(ICON_MIMETYPE, ICON_EXT);
    extensionsByMimeType.put(MP4_MIMETYPE, MP4_EXT);
    extensionsByMimeType.put(MPEG_MIMETYPE, MPEG_EXT);
    extensionsByMimeType.put(WEBM_VIDEO_MIMETYPE, WEBM_VIDEO_EXT);
    extensionsByMimeType.put(OGG_VIDEO_MIMETYPE, OGV_EXT);
    extensionsByMimeType.put(MP3_MIMETYPE, MP3_EXT);
    extensionsByMimeType.put(OGG_AUDIO_MIMETYPE, OGG_EXT);
    extensionsByMimeType.put(WAV_MIMETYPE, WAV_EXT);
    extensionsByMimeType.put(WEBM_AUDIO_MIMETYPE, WEBM_AUDIO_EXT);
    extensionsByMimeType.put(GZIP_MIMETYPE, GZIP_EXT);
    extensionsByMimeType.put(TAR_MIMETYPE, TAR_EXT);
    extensionsByMimeType.put(SEVEN_ZIP_MIMETYPE, SEVEN_ZIP_EXT);
    extensionsByMimeType.put(RAR_MIMETYPE, RAR_EXT);
    extensionsByMimeType.put(FLASH_MIMETYPE, FLASH_EXT);
    extensionsByMimeType.put(BINARY_MIMETYPE, BINARY_EXT);
    extensionsByMimeType.put(XUL_MIMETYPE, XUL_EXT);
    extensionsByMimeType.put(EPUB_MIMETYPE, EPUB_EXT);
    extensionsByMimeType.put(TTF_MIMETYPE, TTF_EXT);
    extensionsByMimeType.put(WOFF_MIMETYPE, WOFF_EXT);
    extensionsByMimeType.put(WOFF2_MIMETYPE, WOFF2_EXT);
    extensionsByMimeType.put(ODT_MIMETYPE, ODT_EXT);
    extensionsByMimeType.put(ODS_MIMETYPE, ODS_EXT);
    extensionsByMimeType.put(ODP_MIMETYPE, ODP_EXT);
    extensionsByMimeType.put(GLTF_MIMETYPE, GLTF_EXT);
    extensionsByMimeType.put(FLAC_MIMETYPE, FLAC_EXT);
    extensionsByMimeType.put(M4A_EXT, M4A_EXT);
    extensionsByMimeType.put(GPP2_MIMETYPE, GPP2_EXT);
    extensionsByMimeType.put(M4V_MIMETYPE, M4V_EXT);
    extensionsByMimeType.put(GPP_MIMETYPE, GPP_EXT);
    extensionsByMimeType.put(MKV_MIMETYPE, MKV_EXT);

    extensionsByMimeType.put(JAVA_MIMETYPE, JAVA_EXT);
    extensionsByMimeType.put(CPP_MIMETYPE, CPP_EXT);
    extensionsByMimeType.put(GROOVY_MIMETYPE, GROOVY_EXT);
    extensionsByMimeType.put(IBPK_MIMETYPE, IBPK_EXT);
    extensionsByMimeType.put(ATOM_MIMETYPE, ATOM_EXT);
    extensionsByMimeType.put(RSS_MIMETYPE, RSS_EXT);
    extensionsByMimeType.put(AFM_MIMETYPE, AFM_EXT);
    extensionsByMimeType.put(HDF_MIMETYPE, HDF_EXT);
    extensionsByMimeType.put(ASP_MIMETYPE, ASP_EXT);
    extensionsByMimeType.put(XHTML_MIMETYPE, XHTML_EXT);
    extensionsByMimeType.put(PSD_MIMETYPE, PSD_EXT);
    extensionsByMimeType.put(ANPA_MIMETYPE, ANPA_EXT);
    extensionsByMimeType.put(IWORK_MIMETYPE, IWORK_EXT);
    extensionsByMimeType.put(NUMBERS_MIMETYPE, NUMBERS_EXT);
    extensionsByMimeType.put(KEY_MIMETYPE, KEY_EXT);
    extensionsByMimeType.put(PAGES_MIMETYPE, PAGES_EXT);
    extensionsByMimeType.put(RFC822_MIMETYPE, RFC822_EXT);
    extensionsByMimeType.put(MATLAB_MIMETYPE, MATLAB_EXT);
    extensionsByMimeType.put(MBOX_MIMETYPE, MBOX_EXT);
    extensionsByMimeType.put(PST_MIMETYPE, PST_EXT);
    extensionsByMimeType.put(MSPUB_MIMETYPE, MSPUB_EXT);
    extensionsByMimeType.put(MSOFFICE_MIMETYPE, MSOFFICE_EXT);
    extensionsByMimeType.put(SLDWORKS_MIMETYPE, SLDWORKS_EXT);
    extensionsByMimeType.put(MSWORKS_MIMETYPE, MSWORKS_EXT);
    extensionsByMimeType.put(OLE10_MIMETYPE, OLE10_EXT);
    extensionsByMimeType.put(MPP_MIMETYPE, MPP_EXT);
    extensionsByMimeType.put(OOXML_MIMETYPE, OOXML_EXT);
    extensionsByMimeType.put(MSG_MIMETYPE, MSG_EXT);
    extensionsByMimeType.put(VSD_MIMETYPE, VSD_EXT);
    extensionsByMimeType.put(TNEF_MIMETYPE, TNEF_EXT);
    extensionsByMimeType.put(XLSM_MIMETYPE, XLSM_EXT);
    extensionsByMimeType.put(PPTM_MIMETYPE, PPTM_EXT);
    extensionsByMimeType.put(XLTX_MIMETYPE, XLTX_EXT);
    extensionsByMimeType.put(POTX_MIMETYPE, POTX_EXT);
    extensionsByMimeType.put(XLAM_MIMETYPE, XLAM_EXT);
    extensionsByMimeType.put(DOTM_MIMETYPE, DOTM_EXT);
    extensionsByMimeType.put(XLTM_MIMETYPE, XLTM_EXT);
    extensionsByMimeType.put(DOTX_MIMETYPE, DOTX_EXT);
    extensionsByMimeType.put(PPSM_MIMETYPE, PPSM_EXT);
    extensionsByMimeType.put(PPAM_MIMETYPE, PPAM_EXT);
    extensionsByMimeType.put(DOT_MIMETYPE, DOT_EXT);
    extensionsByMimeType.put(PPSX_MIMETYPE, PPSX_EXT);
    extensionsByMimeType.put(ODG_MIMETYPE, ODG_EXT);
    extensionsByMimeType.put(SXW_MIMETYPE, SXW_EXT);
    extensionsByMimeType.put(ODM_MIMETYPE, ODM_EXT);
    extensionsByMimeType.put(OTT_MIMETYPE, OTT_EXT);
    extensionsByMimeType.put(OTP_MIMETYPE, OTP_EXT);
    extensionsByMimeType.put(ODC_MIMETYPE, ODC_EXT);
    extensionsByMimeType.put(FODT_MIMETYPE, FODT_EXT);
    extensionsByMimeType.put(BZ_MIMETYPE, BZ_EXT);
    extensionsByMimeType.put(BZ2_MIMETYPE, BZ2_EXT);
    extensionsByMimeType.put(JAR_MIMETYPE, JAR_EXT);
    extensionsByMimeType.put(CPIO_MIMETYPE, CPIO_EXT);
    extensionsByMimeType.put(RTF_MIMETYPE, RTF_EXT);
    extensionsByMimeType.put(FB2_MIMETYPE, FB2_EXT);
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
