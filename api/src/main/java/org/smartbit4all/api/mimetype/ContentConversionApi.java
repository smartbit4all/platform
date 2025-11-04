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
   * Returns the currently available conversions from the mime type we have set.
   * 
   * @param fromMimeType The mime type we would like to convert.
   * @param toMimeType The mime type we would like to have as a result of the conversion chain.
   * @return The list of mime types to go through. If empty then there is no way to convert. If it
   *         has only one item then only one conversion is enough.
   */
  List<String> getConversionPath(String fromMimeType, String toMimeType);

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

  BinaryContentData convert(BinaryContentData binaryContentData, String toMimeType,
      String logicalSchema, Map<String, Object> parameters, String apiName);

  /**
   * Converts a single input file into multiple output files based on the specified target MIME
   * type.
   * <p>
   * This method supports conversions where one source file produces multiple outputs, such as
   * converting a PDF document into individual image files (one per page), or an archive file (e.g.,
   * ZIP) into its extracted contents.
   * </p>
   * 
   * @param binaryContentData The original binary content data as attachment. It must have a correct
   *        mime type to be able to start the proper converter.
   * @param toMimeType The target mime type.
   * @param logicalSchema The logical schema to save the result into.
   * @return The {@link BinaryContentData} with the local content URI of the saved result.
   */
  List<BinaryContentData> convertToMultipleFiles(BinaryContentData binaryContentData,
      String toMimeType,
      String logicalSchema, Map<String, Object> parameters, String apiName);

  /**
   * Return if the conversion returns multiple output converted files.
   */
  boolean isMultiOutput(String apiName);

  String getConverterApiName(String fromMimeType, String toMimeType);

}
