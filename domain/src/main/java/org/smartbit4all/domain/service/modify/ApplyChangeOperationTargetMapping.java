package org.smartbit4all.domain.service.modify;

import java.util.HashMap;
import java.util.Map;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertySet;

public final class ApplyChangeOperationTargetMapping {

  private final DataRow targetRow;

  private final Map<Property<?>, PropertySet> propertyMapping = new HashMap<>();

  public ApplyChangeOperationTargetMapping(DataRow targetRow) {
    super();
    this.targetRow = targetRow;
  }

  public final DataRow getTargetRow() {
    return targetRow;
  }

  public final Map<Property<?>, PropertySet> getPropertyMapping() {
    return propertyMapping;
  }

}
