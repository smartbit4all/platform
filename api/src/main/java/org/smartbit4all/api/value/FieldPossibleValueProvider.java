package org.smartbit4all.api.value;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.value.bean.Value;

public interface FieldPossibleValueProvider {
  String getFieldId();

  List<Value> getPossibleValues(URI uri) throws Exception;
}
