package org.smartbit4all.api.mimetype;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.contribution.PrimaryApi;
import org.smartbit4all.api.invocation.bean.ServiceConnection;

public interface ContentConversionApi extends PrimaryApi<ContentConversionContributionApi> {

  boolean isConversionAvailable(String from, String to);

  List<String> getAvailableConversionTargets(String fromMimeType);

  URI convert(BinaryContentData binaryContentData, String toMimeType, String logicalSchema);

  URI convert(BinaryContentData binaryContentData, String toMimeType, String logicalSchema,
      ServiceConnection serviceConnection);

}
