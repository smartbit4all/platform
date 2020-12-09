package org.smartbit4all.api.value;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.value.bean.Value;

public interface ValueSource {

  String getSourceId();
  
  List<String> getProvidedObjectCodes();
  
  List<Value> getPossibleValues(URI objectUri);
  
}
