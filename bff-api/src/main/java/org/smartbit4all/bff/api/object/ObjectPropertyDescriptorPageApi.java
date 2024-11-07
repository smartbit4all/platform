package org.smartbit4all.bff.api.object;

import java.util.UUID;
import org.smartbit4all.api.config.PlatformViewNames;
import org.smartbit4all.api.object.bean.ObjectPropertyDescriptor;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.annotation.ViewApi;
import org.smartbit4all.api.view.bean.UiActionRequest;

@ViewApi(value = PlatformViewNames.OBJECT_PROPERTY_DESCRIPTOR_PAGE)
public interface ObjectPropertyDescriptorPageApi extends PageApi<ObjectPropertyDescriptor> {

  String PARAM_MODEL = "model";

  String CALLBACK_SAVE = "save";

  String SAVE_ACTION = "SAVE";

  @ActionHandler(SAVE_ACTION)
  void save(UUID viewUuid, UiActionRequest request);
}
