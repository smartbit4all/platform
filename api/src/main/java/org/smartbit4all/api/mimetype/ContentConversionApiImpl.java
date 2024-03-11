package org.smartbit4all.api.mimetype;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.contribution.PrimaryApiImpl;
import org.smartbit4all.api.session.SessionApi;
import org.springframework.beans.factory.annotation.Autowired;

public class ContentConversionApiImpl extends PrimaryApiImpl<ContentConversionContributionApi>
    implements ContentConversionApi {

  @Autowired(required = false)
  SessionApi sessionApi;

  @Autowired
  MimeTypeApi mimeTypeApi;

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
  public BinaryContentData convert(BinaryContentData binaryContentData, String toMimeType,
      String logicalSchema) {
    Objects.requireNonNull(binaryContentData);
    Objects.requireNonNull(toMimeType);
    Objects.requireNonNull(logicalSchema);

    if (!isConversionAvailable(binaryContentData.getMimeType(), toMimeType)) {
      throw new IllegalArgumentException(
          "The conversion of " + binaryContentData + " to " + toMimeType + " is not available.");
    }
    ContentConversionContributionApi api =
        getConverterApi(binaryContentData.getMimeType(), toMimeType);
    if (api != null) {
      URI dataUri = api.convert(binaryContentData,
          toMimeType, logicalSchema);
      BinaryContentData result = new BinaryContentData()
          .created(sessionApi != null ? sessionApi.createActivityLog() : null).dataUri(dataUri)
          .extension(mimeTypeApi.getExtension(toMimeType))
          .fileName(mimeTypeApi.ensureFileExtension(binaryContentData.getFileName(), toMimeType));
      return result;
    }
    return null;
  }

}
