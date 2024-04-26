package org.smartbit4all.api.mimetype;

import java.net.URI;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.contribution.ContributionApi;
import org.smartbit4all.api.invocation.bean.ServiceConnection;

public interface ContentConversionContributionApi extends ContributionApi {

  /**
   * Returns the accepted input data's mime types.
   */
  public List<String> getAcceptedMimeTypes();

  /**
   * Returns the possible output data's mime types.
   */
  public List<String> getTargetMimeTypes();

  /**
   * @return Return true if the given service is available. If a {@link ServiceConnection} is
   *         required to for the functioality then we need to add the
   *         {@link ContentConversionApi#MDM_CONVERSION_SERVICES} MDM Entry. The name should be the
   *         fully qualified name of the contribution api itself.
   */
  boolean isAvailable();

  /**
   * The implementation of the conversion.
   * 
   * @param content The content to convert.
   * @param toMimeType The target mime type.
   * @param logicalSchema The logical schema.
   * @return
   */
  public URI convert(BinaryContentData content, String toMimeType,
      String logicalSchema, Map<String, Object> parameters);

}
