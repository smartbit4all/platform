package org.smartbit4all.ui.common.data.storage.history;

import java.net.URI;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.storage.bean.ObjectHistory;
import org.smartbit4all.api.storage.bean.ObjectHistoryEntry;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObjectEditingImpl;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.ObservableObjectImpl;
import org.smartbit4all.domain.data.storage.history.ObjectHistoryApi;
import org.smartbit4all.ui.api.data.storage.history.ObjectHistoryViewModel;
import org.smartbit4all.ui.api.navigation.UINavigationApi;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.navigation.model.NavigationTargetType;
import org.springframework.beans.factory.annotation.Autowired;

public class ObjectHistoryViewModelImpl extends ObjectEditingImpl
    implements ObjectHistoryViewModel {

  private ObjectHistoryApi historyApi;

  @Autowired
  private UINavigationApi uiNavigationApi;

  private ObservableObjectImpl objectHistory;
  private ApiObjectRef objectHistoryRef;
  private Map<Class<?>, ApiBeanDescriptor> objectHistoryDescriptor;

  public ObjectHistoryViewModelImpl(ObjectHistoryApi historyApi,
      Map<Class<?>, ApiBeanDescriptor> objectHistoryDescriptor) {
    this.historyApi = historyApi;
    this.objectHistoryDescriptor = objectHistoryDescriptor;

    initObjectHistory();
  }

  @Override
  public void executeCommand(String commandPath, String command, Object... params) {
    switch (command) {
      case SET_OBJECT:
        setObjectHistoryRef((URI) params[0]);
        break;
      case OPEN_VERSION:
        openVersion((URI) params[0], (String) params[1]);
        break;
      default:
        super.executeCommand(commandPath, command, params);
        break;
    }
  }

  @Override
  public ObservableObject objectHistory() {
    return objectHistory;
  }

  private void initObjectHistory() {
    objectHistory = new ObservableObjectImpl();
    objectHistoryRef = new ApiObjectRef(null, new ObjectHistory(), objectHistoryDescriptor);
    objectHistory.setRef(objectHistoryRef);
  }

  private void setObjectHistoryRef(URI objectUri) {
    List<ObjectHistoryEntry> historyEntries = historyApi.getObjectHistory(objectUri);
    ObjectHistory objectHistory = new ObjectHistory().objectHistoryEntries(historyEntries);
    objectHistoryRef.setObject(objectHistory);
  }

  private void openVersion(URI versionUri, String viewName) {
    uiNavigationApi.navigateTo(new NavigationTarget()
        .viewName(viewName)
        .type(NavigationTargetType.DIALOG)
        .putParametersItem("entry", versionUri)
        .putParametersItem("history", Boolean.TRUE));
  }

  @Override
  public void setHistoryEntries(List<ObjectHistoryEntry> entries) {
    ObjectHistory objectHistory = new ObjectHistory().objectHistoryEntries(entries);
    objectHistoryRef.setObject(objectHistory);
  }
}
