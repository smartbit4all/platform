package org.smartbit4all.bff.api.org;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.bff.api.groupselector.bean.GroupEditingModel;
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
    view.actions(getGroupEditorActions());

    GroupEditingModel pageModel = new GroupEditingModel();
    Group groupToEdit;
    if (view.getObjectUri() != null) {
      groupToEdit = orgApi.getGroup(view.getObjectUri());
    } else {
      groupToEdit = new Group().name("").title("").kindCode("").description("")
          .children(new ArrayList<>());
    }

    pageModel.group(groupToEdit).possibleGroups(orgApi.getAllGroups()).childGroups(groupToEdit
        .getChildren());

    return pageModel;
  }

  @Override
  public void saveGroup(UUID viewUuid, UiActionRequest request) {

    GroupEditingModel pageModel =
        objectApi.asType(GroupEditingModel.class, request.getParams().get("formModel"));

    if (orgApi.getAllGroups().stream().map(Group::getName).collect(Collectors.toList())
        .contains(pageModel.getGroup().getName())) {
      orgApi.updateGroup(pageModel.getGroup());
    } else {
      pageModel.getGroup().name("user-create-group." + viewUuid);
      orgApi.saveGroup(pageModel.getGroup());
    }

    pageModel.getPossibleGroups()
        .forEach(g -> orgApi.removeSubGroup(pageModel.getGroup().getUri(), g.getUri()));

    pageModel.getChildGroups().stream()
        .forEach(g -> orgApi.addChildGroup(pageModel.getGroup(), orgApi.getGroup(g)));

    viewApi.showView(new View().viewName(getGroupListName()));

  }

  @Override
  public void cancelGroupEdit(UUID viewUuid, UiActionRequest request) {
    viewApi.showView(new View().viewName(getGroupListName()));
  }

  protected String getGroupListName() {
    return OrgViewNames.GROUP_LIST_PAGE;
  }

  protected List<UiAction> getGroupEditorActions() {
    return Arrays.asList(new UiAction().code(SAVE_GROUP),
        new UiAction().code(CANCEL_GROUP));
  }

}
