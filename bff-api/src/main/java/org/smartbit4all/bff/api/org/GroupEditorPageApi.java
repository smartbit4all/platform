package org.smartbit4all.bff.api.org;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.groupselector.bean.GroupEditingModel;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;

public interface GroupEditorPageApi extends PageApi<GroupEditingModel> {


  public static final String SAVE_GROUP = "SAVE_GROUP";
  public static final String CANCEL_GROUP = "CANCEL_GROUP";

  List<UiAction> ADMIN_ACTIONS =
      Arrays.asList(new UiAction().code(SAVE_GROUP),
          new UiAction().code(CANCEL_GROUP));

  @ActionHandler(SAVE_GROUP)
  void saveGroup(UUID viewUuid, UiActionRequest request);

  @ActionHandler(CANCEL_GROUP)
  void cancelGroupEdit(UUID viewUuid, UiActionRequest request);

}
