package org.smartbit4all.api.mimetype;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.contribution.PrimaryApiImpl;

public class ContentConversionApiImpl extends PrimaryApiImpl<ContentConversionContributionApi>
    implements ContentConversionApi {

  public ContentConversionApiImpl() {
    super(ContentConversionContributionApi.class);
  }

  @Override
  public boolean isConversionAvailable(String fromMimeType, String toMimeType) {
    return getContributionApis().values().stream()
        .anyMatch(
            api -> api.getAcceptedMimeTypes().contains(fromMimeType)
                && api.getTargetMimeTypes().contains(toMimeType));
  }

  @Override
  public List<String> getAvailableConversionTargets(String fromMimeType) {
    return getContributionApis().values().stream()
        .filter(api -> api.getAcceptedMimeTypes().contains(fromMimeType))
        .flatMap(api -> api.getTargetMimeTypes().stream())
        .distinct()
        .collect(Collectors.toList());
  }

  private final ContentConversionContributionApi getConverterApi(String fromMimeType,
      String toMimeType) {
    return getContributionApis().values().stream()
        .filter(api -> api.getAcceptedMimeTypes().contains(fromMimeType)
            && api.getTargetMimeTypes().contains(toMimeType))
        .findFirst()
        .orElse(null);
  }

  @Override
  public URI convert(BinaryContentData binaryContentData, String toMimeType, String logicalSchema) {
    ContentConversionContributionApi api =
        getConverterApi(binaryContentData.getMimeType(), toMimeType);
    return api == null ? null
        : api.convert(binaryContentData,
            logicalSchema);
  }

}
