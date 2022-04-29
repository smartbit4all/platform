package org.smartbit4all.api.value;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.value.bean.Value;

public class ValueApiImpl implements ValueApi {
  Map<String, FieldPossibleValueProvider> providersByFieldId = new HashMap<>();

  public ValueApiImpl(List<FieldPossibleValueProvider> providers) {
    providers.forEach(p -> providersByFieldId.put(p.getFieldId(), p));
  }

  @Override
  public List<Value> getPossibleValues(URI uri) throws Exception {
    String fieldId = ValueUris.getSource(uri);
    FieldPossibleValueProvider provider = providersByFieldId.get(fieldId);
    if (provider != null) {
      return provider.getPossibleValues(uri);
    } else {
      return Collections.emptyList();
    }
  }
}
