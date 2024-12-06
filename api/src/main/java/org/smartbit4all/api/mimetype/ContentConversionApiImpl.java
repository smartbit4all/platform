package org.smartbit4all.api.mimetype;

import static java.util.stream.Collectors.toList;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.contribution.PrimaryApiImpl;
import org.smartbit4all.api.session.SessionApi;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;

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
    if (fromMimeType.equals(toMimeType)) {
      return Collections.emptyList();
    }
    MutableValueGraph<String, ContentConversionContributionApi> conversionGraph =
        ValueGraphBuilder.directed().allowsSelfLoops(true).build();
    for (ContentConversionContributionApi api : getContributionApis().values()) {
      for (String acceptedMimeType : api.getAcceptedMimeTypes()) {
        for (String targetMimeType : api.getTargetMimeTypes()) {
          conversionGraph.putEdgeValue(acceptedMimeType, targetMimeType, api);
        }
      }
    }
    // Now we shell find the shortest path on the conversion graph and execute it.
    Set<String> alreadyVisited = new HashSet<>();
    alreadyVisited.add(fromMimeType);
    Set<EndpointPair<String>> incidentEdges = conversionGraph.incidentEdges(fromMimeType);
    List<EndpointPair<String>> shortestPath =
        pathRecursive(conversionGraph, incidentEdges, fromMimeType, toMimeType, alreadyVisited);
    return shortestPath.stream().map(ep -> ep.target()).collect(toList());
  }

  private final List<EndpointPair<String>> pathRecursive(
      MutableValueGraph<String, ContentConversionContributionApi> conversionGraph,
      Set<EndpointPair<String>> incidentEdges,
      String fromMimeType, String toMimeType, Set<String> alreadyVisited) {
    // If the toMimeType is included then we arrived and we can return the last EnpointPair as
    // result.
    if (fromMimeType.equals(toMimeType)) {
      return Collections.emptyList();
    }
    List<EndpointPair<String>> result = new ArrayList<>();
    Optional<EndpointPair<String>> toOption =
        incidentEdges.stream()
            .filter(ep -> ep.source().equals(fromMimeType) && ep.target().equals(toMimeType))
            .findFirst();
    if (toOption.isPresent()) {
      result.add(toOption.get());
      return result;
    }
    // Go further to find the toMimeType.
    return incidentEdges.stream().filter(ep -> !alreadyVisited.contains(ep.target())).map(ep -> {
      alreadyVisited.add(ep.target());
      List<EndpointPair<String>> pathRecursive =
          pathRecursive(conversionGraph, conversionGraph.incidentEdges(ep.target()),
              ep.target(), toMimeType, alreadyVisited);
      if (!pathRecursive.isEmpty()) {
        List<EndpointPair<String>> tmp = new ArrayList<>();
        tmp.add(ep);
        tmp.addAll(pathRecursive);
        pathRecursive = tmp;
      }
      return pathRecursive;
    }).filter(l -> !l.isEmpty()).findFirst().orElse(Collections.emptyList());
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
      String logicalSchema, Map<String, Object> parameters) {
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
          toMimeType, logicalSchema, parameters);
      return new BinaryContentData()
          .created(sessionApi != null ? sessionApi.createActivityLog() : null).dataUri(dataUri)
          .extension(mimeTypeApi.getExtension(toMimeType))
          .mimeType(toMimeType)
          .fileName(mimeTypeApi.ensureFileExtension(binaryContentData.getFileName(), toMimeType));
    }
    return null;
  }

}
