package org.smartbit4all.api.mimetype;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.contribution.PrimaryApiImpl;
import org.smartbit4all.api.invocation.bean.ServiceConnection;

public class ContentConversionApiImpl extends PrimaryApiImpl<ContentConversionContributionApi>
    implements ContentConversionApi {

  public ContentConversionApiImpl() {
    super(ContentConversionContributionApi.class);
  }

  @Override
  public boolean isConversionAvailable(String from, String to) {
    return getContributionApis().values().stream()
        .anyMatch(
            api -> Objects.equals(api.getFromMimeType(), from)
                && Objects.equals(api.getToMimeType(), to));
  }

  @Override
  public List<String> getAvailableConversionTargets(String fromMimeType) {
    return getContributionApis().values().stream()
        .filter(api -> Objects.equals(api.getFromMimeType(), fromMimeType))
        .map(api -> api.getToMimeType())
        .collect(Collectors.toList());
  }

  @Override
  public URI convert(BinaryContentData binaryContentData, String toMimeType, String logicalSchema) {
    return getContributionApis().values().stream()
        .filter(api -> Objects.equals(binaryContentData.getMimeType(), api.getFromMimeType())
            && Objects.equals(toMimeType, api.getToMimeType()))
        .findFirst()
        .map(api -> api.convert(binaryContentData, logicalSchema))
        .orElse(null);
  }

  @Override
  public URI convert(BinaryContentData binaryContentData, String toMimeType, String logicalSchema,
      ServiceConnection serviceConnection) {
    return getContributionApis().values().stream()
        .filter(api -> Objects.equals(binaryContentData.getMimeType(), api.getFromMimeType())
            && Objects.equals(toMimeType, api.getToMimeType()))
        .findFirst()
        .map(api -> api.convert(binaryContentData, logicalSchema, serviceConnection))
        .orElse(null);
  }



}
