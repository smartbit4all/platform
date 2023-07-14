package org.smartbit4all.bff.api.org;

import java.util.UUID;
import java.util.stream.Collectors;
import org.smartbit4all.api.groupselector.bean.GroupEditingModel;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.springframework.beans.factory.annotation.Autowired;

public class GroupEditorPageApiImpl extends PageApiImpl<GroupEditingModel>
    implements GroupEditorPageApi {

  @Autowired
  OrgApi orgApi;

  public GroupEditorPageApiImpl() {
    super(GroupEditingModel.class);
  }

  @Override
  public GroupEditingModel initModel(View view) {
    view.actions(ADMIN_ACTIONS);

    GroupEditingModel pageModel = new GroupEditingModel();

    Group groupToEdit = orgApi.getGroup(view.getObjectUri());

    pageModel.group(groupToEdit).possibleGroups(orgApi.getAllGroups()).childGroups(groupToEdit
        .getChildren().stream().map(u -> orgApi.getGroup(u)).collect(Collectors.toList()));

    return pageModel;
  }

  @Override
  public void saveGroup(UUID viewUuid, UiActionRequest request) {

    GroupEditingModel pageModel =
        objectApi.asType(GroupEditingModel.class, request.getParams().get("formModel"));


    if (orgApi.getAllGroups().stream().map(Group::getUri).collect(Collectors.toList())
        .contains(pageModel.getGroup().getUri())) {
      orgApi.updateGroup(pageModel.getGroup());
    } else {
      orgApi.saveGroup(pageModel.getGroup());
    }

    pageModel.getPossibleGroups()
        .forEach(g -> orgApi.removeSubGroup(pageModel.getGroup().getUri(), g.getUri()));

    pageModel.getChildGroups().stream().forEach(g -> orgApi.addChildGroup(pageModel.getGroup(), g));

  }

  @Override
  public void cancelGroupEdit(UUID viewUuid, UiActionRequest request) {
    viewApi.showView(new View().viewName(OrgViewNames.GROUP_DASHBOARD_PAGE));

  }

}
