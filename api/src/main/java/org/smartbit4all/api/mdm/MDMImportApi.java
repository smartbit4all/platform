package org.smartbit4all.api.mdm;

import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMErrorLog;
import org.smartbit4all.api.mdm.bean.MDMModificationRequest;

public interface MDMImportApi {

  /*
   * Load data from a MDMModificationRequest object to a MDMEntry object
   * 
   * @param MDMModificationRequest
   */
  <T> MDMErrorLog importData(MDMDefinition definition, MDMEntryDescriptor descriptor,
      MDMModificationRequest modificationRequest, Class<T> clazz);
}
