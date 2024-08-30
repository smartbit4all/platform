package org.smartbit4all.bff.api.mdm.oauth;

import org.smartbit4all.api.config.PlatformViewNames;
import org.smartbit4all.api.view.annotation.ViewApi;
import org.smartbit4all.bff.api.mdm.MDMEntryEditPageApi;

@ViewApi(value = PlatformViewNames.DYNAMIC_OAUTH_PROPERTIES_EDITOR)
public interface DynamicOAuthPropertiesEditorPageApi extends MDMEntryEditPageApi {

  static final String GRID_USERPROP_MAPPING = "GRID_USERPROP_MAPPING";
  static final String GRID_ROLE_MAPPING = "GRID_ROLE_MAPPING";
  static final String ACTION_ADD_USERPROP_MAPPING = "ADD_USERPROP_MAPPING";
  static final String ACTION_ADD_ROLE_MAPPING = "ADD_ROLE_MAPPING";



}
