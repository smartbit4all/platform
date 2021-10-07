package org.smartbit4all.api.compdata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.smartbit4all.api.compdata.bean.CompositeDataCollection;

/**
 * TODO delete it soon
 * 
 * @deprecated
 */
@Deprecated
public class StaticCompositeDataCollector implements CompositeDataCollector {

  private Map<String, Object> fields;

  public StaticCompositeDataCollector() {
    this.fields = new HashMap<>();
  }

  public void putStaticField(String identifier, String value) {
    fields.put(identifier, value);
  }

  @Override
  public Map<String, Object> collect(
      Collection<String> identifier,
      CompositeDataCollection compositeDataCollection) {

    return fields.entrySet()
        .stream()
        .filter(e -> identifier.contains(e.getKey()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

}
