package org.smartbit4all.bff.api.mdm.apikey;

import java.util.UUID;
import org.smartbit4all.api.config.PlatformViewNames;
import org.smartbit4all.api.view.annotation.ViewApi;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.ValueSet;
import org.smartbit4all.bff.api.mdm.MDMEntryEditPageApi;

@ViewApi(value = PlatformViewNames.API_KEY_EDITOR)
public interface ApiKeyEditorPageApi extends MDMEntryEditPageApi {

  public static interface ScopeValueSetProvider {
    ValueSet get();
  }

  void copyToClipboard(UUID viewUuid, UiActionRequest request, String token);

}
