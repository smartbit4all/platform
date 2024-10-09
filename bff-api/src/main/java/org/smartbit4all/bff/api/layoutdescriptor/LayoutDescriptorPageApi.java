package org.smartbit4all.bff.api.layoutdescriptor;

import java.util.UUID;
import org.smartbit4all.api.config.PlatformViewNames;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.annotation.ViewApi;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.ViewConstraint;
import org.smartbit4all.bff.api.mdm.MDMEntryEditPageApi;

@ViewApi(PlatformViewNames.LAYOUT_DESRIPTOR_DIALOG)
public interface LayoutDescriptorPageApi extends MDMEntryEditPageApi {

  String ACTION_EDIT_LAYOUT = "editLayout";

  String ACTION_EDIT_CONSTRAINT = "editConstraint";

  @ActionHandler(ACTION_EDIT_LAYOUT)
  void editLayout(UUID viewUuid, UiActionRequest request);

  void editLayoutCallback(UUID viewUuid, SmartComponentLayoutDefinition layoutDefinition);

  @ActionHandler(ACTION_EDIT_CONSTRAINT)
  void editConstraint(UUID viewUuid, UiActionRequest request);

  void editConstraintCallback(UUID viewUuid, ViewConstraint viewConstraint);

}
