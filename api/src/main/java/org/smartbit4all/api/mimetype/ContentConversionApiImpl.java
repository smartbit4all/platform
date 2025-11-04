package org.smartbit4all.api.mimetype;

import static java.lang.Long.MAX_VALUE;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.binarydata.BinaryDataObject;
import org.smartbit4all.api.contribution.PrimaryApiImpl;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.bean.UserActivityLog;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

public class ContentConversionApiImpl extends PrimaryApiImpl<ContentConversionContributionApi>
    implements ContentConversionApi {

  private static final String EXCEPTION_CONVERSION_FAIL =
      ContentConversionContributionApi.EXCEPTION_CONVERSION_FAIL;

  @Autowired(required = false)
  SessionApi sessionApi;
  @Autowired
  MimeTypeApi mimeTypeApi;
  @Autowired
  ObjectApi objectApi;

  public ContentConversionApiImpl() {
    super(ContentConversionContributionApi.class);
  }

  @Override
  public boolean isConversionAvailable(String fromMimeType, String toMimeType) {
    return getContributionApis().values().stream()
        .anyMatch(
            api -> api.getAcceptedMimeTypes().contains(fromMimeType)
                && api.getTargetMimeTypes().contains(toMimeType)
                && api.isAvailable());
  }

  @Override
  public List<String> getAvailableConversionTargets(String fromMimeType) {
    return getContributionApis().values().stream()
        .filter(api -> api.getAcceptedMimeTypes().contains(fromMimeType))
        .flatMap(api -> api.getTargetMimeTypes().stream())
        .distinct()
        .collect(Collectors.toList());
  }


  @Override
  public List<String> getConversionPath(String fromMimeType, String toMimeType) {
    List<String> path = aStar(fromMimeType, toMimeType, it -> 1L, (s1, s2) -> 1L);
    // The return value of the A* algorithm includes the starting vertex, which we do not need, so
    // we remove it
    if (!ObjectUtils.isEmpty(path)) {
      path.remove(0);
    }
    return path;
  }

  public List<String> aStar(String start, String goal, ToLongFunction<String> h,
      BiFunction<String, String, Long> d) {
    Map<String, Set<String>> neighborMap = new HashMap<>();
    for (ContentConversionContributionApi api : getContributionApis().values()) {
      for (String acceptedMimeType : api.getAcceptedMimeTypes()) {
        for (String targetMimeType : api.getTargetMimeTypes()) {
          Set<String> list = neighborMap.get(acceptedMimeType);
          if (list == null) {
            neighborMap.put(acceptedMimeType, new HashSet<>(Set.of(targetMimeType)));
          } else {
            list.add(targetMimeType);
          }
        }
      }
    }

    Set<String> openSet = new HashSet<>();
    openSet.add(start);

    Map<String, String> cameFrom = new HashMap<>();

    Map<String, Long> gScore = new HashMap<>();
    gScore.put(start, 0L);

    Map<String, Long> fScore = new HashMap<>();
    fScore.put(start, h.applyAsLong(start));

    String current = null;
    Long tentaitiveGScore = null;
    while (!openSet.isEmpty()) {
      current =
          openSet.stream()
              .min((s1, s2) -> Long.compare(fScore.getOrDefault(s1, MAX_VALUE),
                  fScore.getOrDefault(s2, MAX_VALUE)))
              .orElse(null);
      if (Objects.equals(current, goal)) {
        return reconstructPath(cameFrom, current);
      }
      openSet.remove(current);
      String current2 = current;
      neighborMap.values().forEach(v -> v.remove(current2));
      for (String neighbor : neighborMap.get(current)) {
        Long temp = gScore.getOrDefault(current, MAX_VALUE);
        tentaitiveGScore = temp == MAX_VALUE ? MAX_VALUE
            : gScore.getOrDefault(current, MAX_VALUE) + d.apply(current, neighbor);
        if (tentaitiveGScore < gScore.getOrDefault(neighbor, MAX_VALUE))
          cameFrom.put(neighbor, current);
        gScore.put(neighbor, tentaitiveGScore);
        temp = gScore.getOrDefault(neighbor, MAX_VALUE);
        fScore.put(neighbor,
            temp == MAX_VALUE ? MAX_VALUE : gScore.get(neighbor) + h.applyAsLong(neighbor));
        openSet.add(neighbor);
      }
    }
    return Collections.emptyList();
  }

  private List<String> reconstructPath(Map<String, String> cameFrom, String current) {
    List<String> totalPath = new LinkedList<>();
    totalPath.add(current);
    Set<String> cameFromKeys = cameFrom.keySet();
    while (cameFromKeys.contains(current)) {
      current = cameFrom.remove(current);
      totalPath.addFirst(current);
    }
    return totalPath;
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
  public final String getConverterApiName(String fromMimeType,
      String toMimeType) {
    ContentConversionContributionApi converterApi = getConverterApi(fromMimeType, toMimeType);
    return converterApi != null ? converterApi.getApiName() : null;
  }

  @Override
  public boolean isMultiOutput(String apiName) {
    ContentConversionContributionApi api =
        getContributionApi(apiName);
    return api.isMultiOutput();
  }

  @Override
  public BinaryContentData convert(BinaryContentData binaryContentData, String toMimeType,
      String logicalSchema, Map<String, Object> parameters) {
    return convert(binaryContentData, toMimeType, logicalSchema, parameters,
        getConverterApiName(binaryContentData.getMimeType(), toMimeType));
  }

  @Override
  public BinaryContentData convert(BinaryContentData binaryContentData, String toMimeType,
      String logicalSchema, Map<String, Object> parameters, String apiName) {
    Objects.requireNonNull(binaryContentData);
    Objects.requireNonNull(toMimeType);
    Objects.requireNonNull(logicalSchema);

    if (!isConversionAvailable(binaryContentData.getMimeType(), toMimeType)) {
      throw new IllegalArgumentException(
          "The conversion of " + binaryContentData + " to " + toMimeType + " is not available.");
    }
    ContentConversionContributionApi api =
        getContributionApi(apiName);
    if (api != null) {
      URI dataUri = api.convert(binaryContentData,
          toMimeType, logicalSchema, parameters);
      BinaryDataObject dataObject =
          objectApi.loadLatest(dataUri).getObject(BinaryDataObject.class);
      UserActivityLog activityLog = sessionApi != null ? sessionApi.createActivityLog() : null;
      return new BinaryContentData()
          .dataUri(dataUri)
          .fileName(mimeTypeApi.ensureFileExtension(binaryContentData.getFileName(), toMimeType))
          .created(activityLog)
          .updated(activityLog)
          .mimeType(toMimeType)
          .extension(mimeTypeApi.getExtension(toMimeType))
          .size(dataObject.getBinaryData().length())
          .contentHash(dataObject.getBinaryData().hashIfPresent());
    }
    return null;
  }

  @Override
  public List<BinaryContentData> convertToMultipleFiles(BinaryContentData binaryContentData,
      String toMimeType,
      String logicalSchema, Map<String, Object> parameters, String apiName) {
    Objects.requireNonNull(binaryContentData);
    Objects.requireNonNull(toMimeType);
    Objects.requireNonNull(logicalSchema);

    if (!isConversionAvailable(binaryContentData.getMimeType(), toMimeType)) {
      throw new IllegalArgumentException(
          "The conversion of " + binaryContentData + " to " + toMimeType + " is not available.");
    }
    ContentConversionContributionApi api =
        getContributionApi(apiName);
    if (api != null) {
      List<URI> dataUris = api.convertToMultipleFiles(binaryContentData,
          toMimeType, logicalSchema, parameters);


      return dataUris.stream().map(uri -> {

        BinaryDataObject dataObject =
            objectApi.loadLatest(uri).getObject(BinaryDataObject.class);
        UserActivityLog activityLog = sessionApi != null ? sessionApi.createActivityLog() : null;
        return new BinaryContentData()
            .dataUri(uri)
            .fileName(mimeTypeApi.ensureFileExtension(binaryContentData.getFileName(), toMimeType))
            .created(activityLog)
            .updated(activityLog)
            .mimeType(toMimeType)
            .extension(mimeTypeApi.getExtension(toMimeType))
            .size(dataObject.getBinaryData().length())
            .contentHash(dataObject.getBinaryData().hashIfPresent());
      }).collect(Collectors.toList());

    }
    return Collections.emptyList();
  }

}
