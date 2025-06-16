package org.smartbit4all.bff.api.utils;

import java.util.UUID;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.value.bean.GenericValue;
import org.smartbit4all.api.value.bean.KeyValuePair;

public interface BffUtilsApi {

  void showMapEntryEditor(UUID viewUuid, String gridId, KeyValuePair pageModel,
      boolean keyEditable, InvocationRequest saveRequest);

  void showMapEntryEditor(UUID viewUuid, String gridId, GenericValue pageModel,
      boolean keyEditable, InvocationRequest saveRequest);

  <T> T getValueFromGridRow(UUID viewUuid, String widgetId, String nodeId, String key,
      Class<T> clazz);
}
