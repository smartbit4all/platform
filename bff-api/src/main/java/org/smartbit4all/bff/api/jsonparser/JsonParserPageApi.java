package org.smartbit4all.bff.api.jsonparser;

import java.util.UUID;
import org.smartbit4all.api.config.PlatformViewNames;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.annotation.ViewApi;
import org.smartbit4all.api.view.bean.UiActionRequest;

@ViewApi(PlatformViewNames.JSON_PARSER_DIALOG)
public interface JsonParserPageApi extends PageApi<Object> {

  String PARAM_JSON = "paramJson";

  String PARAM_OBJECT = "paramObject";

  String PARAM_OBJECT_CLASS = "objectClass";

  String CALLBACK_SAVE = "saveCallback";

  String ACTION_SAVE = "SAVE";

  @ActionHandler(ACTION_SAVE)
  void save(UUID viewUuid, UiActionRequest request);

}
