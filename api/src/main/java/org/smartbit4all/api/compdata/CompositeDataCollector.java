package org.smartbit4all.api.compdata;

import java.util.Collection;
import java.util.Map;
import org.smartbit4all.api.compdata.bean.CompositeDataCollection;

public interface CompositeDataCollector {

  public Map<String, Object> collect(
      Collection<String> identifier,
      CompositeDataCollection compositeDataCollection);

}
