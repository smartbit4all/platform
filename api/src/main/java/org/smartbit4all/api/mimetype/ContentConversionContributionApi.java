package org.smartbit4all.api.mimetype;

import java.net.URI;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.contribution.ContributionApi;
import org.smartbit4all.api.invocation.bean.ServiceConnection;

public interface ContentConversionContributionApi extends ContributionApi {

  /**
   * Returns the input data's mimetype.
   */
  public String getFromMimeType();

  /**
   * Returns the output data's mimetype.
   */
  public String getToMimeType();


  public URI convert(BinaryContentData content, String logicalSchema);

  public URI convert(BinaryContentData content, ServiceConnection serviceConnection,
      String logicalSchema);

}
