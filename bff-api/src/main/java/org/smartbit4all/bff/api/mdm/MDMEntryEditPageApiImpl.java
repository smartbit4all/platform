package org.smartbit4all.bff.api.mdm;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.mdm.bean.MDMEntryEditingObject;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.springframework.beans.factory.annotation.Autowired;

public class MDMEntryEditPageApiImpl extends PageApiImpl<Object>
    implements MDMEntryEditPageApi {

  private static final Logger log = LoggerFactory.getLogger(MDMEntryEditPageApiImpl.class);

  @Autowired
  private MDMEntryListPageApi listPageApi;

  public MDMEntryEditPageApiImpl() {
    super(Object.class);
  }

  @Override
  public Object initModel(View view) {
    // The page always get an initiated model. So if the init model is called it is a mistake.
    // log.error("The master data management page api should be used with prepared model.");
    UiAction saveAction = new UiAction().code(ACTION_SAVE).submit(true);
    UiAction cancelAction = new UiAction().code(ACTION_CANCEL);
    view.addActionsItem(saveAction);
    view.addActionsItem(cancelAction);
    return new MDMEntryEditingObject();
  }

  @Override
  public void performSave(UUID viewUuid, UiActionRequest request) {
    View view = viewApi.getView(viewUuid);
    // TODO use view.containerUuid
    UUID parentUUID = parameters(view)
        .get(MDMEntryListPageApi.PARAM_MDM_LIST_VIEW, UUID.class);
    BranchedObjectEntry branchedObjectEntry = parameters(view)
        .get(MDMEntryListPageApi.PARAM_BRANCHED_OBJECT_ENTRY, BranchedObjectEntry.class);
    view.setModel(request.getParams().get(UiActions.MODEL));
    listPageApi.saveObject(parentUUID, view.getObjectUri(), view.getModel(), branchedObjectEntry);
    viewApi.closeView(viewUuid);
  }

  @Override
  public void performCancel(UUID viewUuid, UiActionRequest request) {
    viewApi.closeView(viewUuid);
  }

}
