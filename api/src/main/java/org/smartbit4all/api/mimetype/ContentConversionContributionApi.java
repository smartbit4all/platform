package org.smartbit4all.api.mimetype;

import java.net.URI;
import java.util.List;
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

  public URI convert(BinaryContentData content, String logicalSchema);

  public URI convert(BinaryContentData content,
      String logicalSchema, ServiceConnection serviceConnection);

}
