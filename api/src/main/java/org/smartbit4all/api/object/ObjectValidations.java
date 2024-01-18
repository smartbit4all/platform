package org.smartbit4all.api.object;

import java.util.Comparator;
import org.smartbit4all.api.object.bean.ObjectValidationItem;
import org.smartbit4all.api.object.bean.ObjectValidationSeverity;

public final class ObjectValidations {

  private ObjectValidations() {}

  public static Comparator<ObjectValidationItem> bySeverity() {
    return (a, b) -> {
      if (a == null && b == null) {
        return 0;
      }
      
      if (a== null) {
        return 1;
      }
      
      if (b == null) {
        return -1;
      }
      
      final ObjectValidationSeverity sA = a.getSeverity();
      final ObjectValidationSeverity sB = b.getSeverity();
      if (sA == null && sB == null) {
        return 0;
      }
      
      if (sA == null) {
        return 1;
      }
      
      if (sB == null) {
        return -1;
      }
      
      return sB.ordinal() - sA.ordinal();
    }
  }

}
