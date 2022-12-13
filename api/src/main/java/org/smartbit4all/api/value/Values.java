package org.smartbit4all.api.value;

import static java.util.stream.Collectors.toMap;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;

public class Values {

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
    return values
        .map(n -> new Value()
            .objectUri(n.getObjectUri())
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
        .map(u -> findOrLoadValue(objectApi, allTemplates, u, displayValuePath))
        .collect(Collectors.toList());
  }

  private static final Value findOrLoadValue(ObjectApi objectApi, Map<URI, Value> allTemplates,
      URI u, String... paths) {
    Value v = allTemplates.get(u);
    if (v == null) {
      v = new Value()
          .objectUri(u)
          .displayValue(objectApi.load(u).getValue(String.class, paths));
    }
    return v;
  }

}
