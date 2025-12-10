package org.smartbit4all.bff.api.mdm.apikey;

import java.util.UUID;
import org.smartbit4all.api.config.PlatformViewNames;
import org.smartbit4all.api.view.annotation.ViewApi;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.ValueSet;
import org.smartbit4all.bff.api.mdm.MDMEntryEditPageApi;

@ViewApi(value = PlatformViewNames.USER_API_KEY_EDITOR)
public interface UserApiKeyEditorPageApi extends MDMEntryEditPageApi {
  public static final String ENABLED_SCOPE_NAMES = "ENABLED_SCOPE_NAMES";

  public static interface ScopeValueSetProvider {
    ValueSet get();
  }

  void copyToClipboard(UUID viewUuid, UiActionRequest request, String token);

}
