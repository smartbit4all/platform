package org.smartbit4all.api.value;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;

public final class Values {

  public static final Comparator<Value> ALPHABETIC_ORDER =
      Comparator.comparing(Value::getDisplayValue);

  public static final Comparator<Value> CASE_INSENSITIVE_ORDER =
      (v1, v2) -> String.CASE_INSENSITIVE_ORDER.compare(v1.getDisplayValue(), v2.getDisplayValue());

  private Values() {}

  /**
   * Collects all element from values stream, using {@link ObjectNode#getObjectUri()} as
   * {@link Value#objectUri(URI)} and {@link ObjectNode#getValueAsString(String...)} with paths as
   * {@link Value#displayValue(String)}
   *
   * @param values
   * @param paths
   * @return
   */
  public static final List<Value> values(Stream<ObjectNode> values, String... paths) {
    return valuesStream(values, paths)
        .collect(Collectors.toList());
  }

  public static Stream<Value> valuesStream(Stream<ObjectNode> values, String... paths) {
    return values
        .map(n -> new Value()
            .objectUri(n.getObjectUri())
            .displayValue(n.getValueAsString(paths)));
  }

  /**
   * Given a list of selected URIs from a list of possible values, updates the selected URIs to
   * match the version numbers found among the values.
   *
   * <p>
   * For example: if the values are:
   *
   * <pre>
   * {@code
   *    [
   *        {displayValue: 'a', objectUri: 'schema:/path1.v5'},
   *        {displayValue: 'b', objectUri: 'schema:/path2.v7'}
   *    ]
   * }</pre>
   *
   * <p>
   * and the selection is
   *
   * <pre>{@code ['schema:/path1.v0']}</pre>
   *
   * , this method will return the list of
   *
   * <pre>{@code ['schema:/path1.v5']}</pre>
   *
   * @param objectApi an instance of {@link ObjectApi}, not null
   * @param values the values to be searched, not null
   * @param selections the selected URIs to be matched with the value URIs, not null
   * @return the list of URIs with version number matching their counterpart among he values
   */
  public static List<URI> findSelectedUris(ObjectApi objectApi, List<Value> values,
      List<URI> selections) {
    return selections.stream()
        .filter(Objects::nonNull)
        .map(uri -> values.stream()
            .map(Value::getObjectUri)
            .filter(valueUri -> objectApi.getLatestUri(uri)
                .equals(objectApi.getLatestUri(valueUri)))
            .findFirst()
            .orElse(null))
        .collect(toList());
  }

  /**
   * Collects all element from values stream, using {@link ObjectNode#getObjectUri()} as
   * {@link Value#objectUri(URI)} without version tag and
   * {@link ObjectNode#getValueAsString(String...)} with paths as
   * {@link Value#displayValue(String)}.
   *
   * @param values
   * @param paths
   * @return
   */
  public static final List<Value> valuesWithoutVersion(Stream<ObjectNode> values, String... paths) {
    return values
        .map(n -> new Value()
            .objectUri(n.getValue(URI.class, "uri"))
            .displayValue(n.getValueAsString(paths)))
        .collect(Collectors.toList());
  }

  public static final List<Value> findSelectedValues(ObjectApi objectApi, List<Value> allValues,
      List<URI> uris, String... displayValuePath) {
    Map<URI, Value> allTemplates = allValues.stream()
        .collect(toMap(
            v -> objectApi.getLatestUri(v.getObjectUri()),
            v -> v));

    return uris.stream()
        .map(u -> allTemplates.computeIfAbsent(objectApi.getLatestUri(u), uri -> new Value()
            .objectUri(uri)
            .displayValue(objectApi.load(uri).getValue(String.class, displayValuePath))))
        .collect(Collectors.toList());
  }

  public static final List<Value> completeValues(ObjectApi objectApi, List<Value> allValues,
      List<URI> uris, String... displayValuePath) {
    List<URI> allUris = allValues.stream()
        .map(Value::getObjectUri)
        .map(objectApi::getLatestUri)
        .collect(toList());
    uris.forEach(uri -> {
      URI latestUri = objectApi.getLatestUri(uri);
      if (!allUris.contains(latestUri)) {
        allValues.add(loadValue(objectApi, uri, displayValuePath));
      }
    });
    return allValues;
  }


  private static Value loadValue(ObjectApi objectApi, URI u, String... paths) {
    Value v;
    v = new Value()
        .objectUri(u)
        .displayValue(objectApi.load(u).getValue(String.class, paths));
    return v;
  }

}
