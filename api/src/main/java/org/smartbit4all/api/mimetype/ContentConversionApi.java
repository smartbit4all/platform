package org.smartbit4all.api.mimetype;

import java.util.List;
import java.util.Map;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.binarydata.BinaryDataObject;
import org.smartbit4all.api.contribution.PrimaryApi;
import org.smartbit4all.api.invocation.bean.ServiceConnection;

/**
 * The content conversion can convert from one mime type to another. This is a generic
 * infrastructure to get text from documents or text from an audio or pdf to images or so.
 * 
 * @author Peter Boros
 */
public interface ContentConversionApi extends PrimaryApi<ContentConversionContributionApi> {

  /**
   * Some contributions are depending on a {@link ServiceConnection} registry entry that defines the
   * API access.
   */
  static final String MDM_CONVERSION_SERVICES = "ContentConversionServices";

  /**
   * The conversion is based on the mime types. If we need to have special content types then we
   * create special mime types.
   * 
   * @param fromMimeType The from mime type.
   * @param toMimeType The target mime type.
   * @return
   */
  boolean isConversionAvailable(String fromMimeType, String toMimeType);

  /**
   * Returns the currently available conversions from the mime type we have set.
   * 
   * @param fromMimeType The mime type we would like to convert.
   * @return The list of target mime types.
   */
  List<String> getAvailableConversionTargets(String fromMimeType);

  /**
   * The conversion operation itself. The result of the conversion is saved as
   * {@link BinaryDataObject} and we get back the URI of the saved content.
   * 
   * @param binaryContentData The original binary content data as attachment. It must have a correct
   *        mime type to be able to start the proper converter.
   * @param toMimeType The target mime type.
   * @param logicalSchema The logical schema to save the result into.
   * @return The {@link BinaryContentData} with the local content URI of the saved result.
   */
  BinaryContentData convert(BinaryContentData binaryContentData, String toMimeType,
      String logicalSchema, Map<String, Object> parameters);

}
