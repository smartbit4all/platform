package org.smartbit4all.bff.api.org;

import java.util.UUID;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.bff.api.groupselector.bean.GroupEditingModel;

public interface GroupEditorPageApi extends PageApi<GroupEditingModel> {


  public static final String SAVE_GROUP = "SAVE_GROUP";
  public static final String CANCEL = "CANCEL";

  @ActionHandler(SAVE_GROUP)
  void saveGroup(UUID viewUuid, UiActionRequest request);

  @ActionHandler(CANCEL)
  void cancelGroupEdit(UUID viewUuid, UiActionRequest request);

}
