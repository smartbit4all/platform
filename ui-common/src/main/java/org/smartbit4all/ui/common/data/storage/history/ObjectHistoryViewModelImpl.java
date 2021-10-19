package org.smartbit4all.ui.common.data.storage.history;

import java.net.URI;
import java.util.Map;
import org.smartbit4all.api.storage.bean.ObjectHistory;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObjectEditingImpl;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.ObservableObjectImpl;
import org.smartbit4all.domain.data.storage.history.ObjectHistoryApi;
import org.smartbit4all.ui.api.data.storage.history.ObjectHistoryViewModel;

public class ObjectHistoryViewModelImpl extends ObjectEditingImpl
    implements ObjectHistoryViewModel {

  private ObjectHistoryApi historyApi;

  private ObservableObjectImpl objectHistory;
  private ApiObjectRef objectHistoryRef;
//  private ObjectHistory objectHistoryWrapper;
  private Map<Class<?>, ApiBeanDescriptor> objectHistoryDescriptor;

  public ObjectHistoryViewModelImpl(ObjectHistoryApi historyApi,
      Map<Class<?>, ApiBeanDescriptor> objectHistoryDescriptor) {
    this.historyApi = historyApi;
    this.objectHistoryDescriptor = objectHistoryDescriptor;

    initObjectHistory();
  }

  @Override
  public ObservableObject objectHistory() {
    return objectHistory;
  }

  @Override
  public void setObjectHistoryRef(URI objectUri, String scheme) {
    ObjectHistory objectHistory = historyApi.getObjectHistory(objectUri, scheme);
    objectHistoryRef.setObject(objectHistory);
  }

  private void initObjectHistory() {
    objectHistory = new ObservableObjectImpl();
    objectHistoryRef = new ApiObjectRef(null, new ObjectHistory(), objectHistoryDescriptor);
    objectHistory.setRef(objectHistoryRef);
//    objectHistoryWrapper = objectHistoryRef.getWrapper(ObjectHistory.class);
  }
}
