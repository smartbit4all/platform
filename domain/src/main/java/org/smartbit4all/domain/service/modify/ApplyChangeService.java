package org.smartbit4all.domain.service.modify;

import java.util.Map;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.core.object.ObjectChange;

public interface ApplyChangeService {

  /**
   * Applies the given changes of the object on the database layer using the given
   * {@link ApplyChangeObjectConfig}.
   */
  void applyChange(ObjectChange objectChange, Object object, ApplyChangeObjectConfig configuration)
      throws Exception;

  /**
   * Applies the given changes of the object on the database layer using the default
   * {@link ApplyChangeObjectConfig} for the object.
   */
  void applyChange(ObjectChange objectChange, Object object) throws Exception;

  void createBean(Object newBean, Map<Class<?>, ApiBeanDescriptor> descriptor,
      ApplyChangeObjectConfig configuration) throws Exception;

  void createBean(Object newBean, Map<Class<?>, ApiBeanDescriptor> descriptor) throws Exception;

  void updateBean(Object oldBean, Object newBean, Map<Class<?>, ApiBeanDescriptor> descriptor,
      ApplyChangeObjectConfig configuration) throws Exception;

  void updateBean(Object oldBean, Object newBean, Map<Class<?>, ApiBeanDescriptor> descriptor)
      throws Exception;

}
