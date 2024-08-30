package org.smartbit4all.bff.api.utils;

import java.util.UUID;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.value.bean.KeyValuePair;

public interface BffUtilsApi {

  void showMapEntryEditor(UUID viewUuid, String gridId, KeyValuePair pageModel,
      boolean keyEditable, InvocationRequest saveRequest);

}
