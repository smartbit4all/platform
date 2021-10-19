package org.smartbit4all.ui.common.data.storage.history;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.smartbit4all.api.storage.bean.ObjectHistory;
import org.smartbit4all.api.storage.bean.ObjectHistoryEntry;
import org.smartbit4all.api.storage.bean.ObjectVersion;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.domain.data.storage.history.ObjectHistoryApi;
import org.smartbit4all.ui.api.data.storage.history.ObjectHistoryViewModel;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

public class ObjectHistoryViewModelConfig {

  private Map<Class<?>, ApiBeanDescriptor> objectHistoryDescriptor;
  
  public ObjectHistoryViewModelConfig() {
    Set<Class<?>> objectHistoryBeans = new HashSet<>();
    
    objectHistoryBeans.add(ObjectHistory.class);
    objectHistoryBeans.add(ObjectHistoryEntry.class);
    objectHistoryBeans.add(ObjectVersion.class);
    
    objectHistoryDescriptor = ApiBeanDescriptor.of(objectHistoryBeans);
  }
  
  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public ObjectHistoryViewModel objectHistoryViewModel(ObjectHistoryApi historyApi) {
    return new ObjectHistoryViewModelImpl(historyApi, objectHistoryDescriptor);
  }
}
