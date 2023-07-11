package org.smartbit4all.bff.api.mdm;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.mdm.bean.MDMEntryEditingObject;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.ViewApi;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.springframework.beans.factory.annotation.Autowired;

public class MDMEntryEditPageApiImpl extends PageApiImpl<Object>
    implements MDMEntryEditPageApi {

  private static final Logger log = LoggerFactory.getLogger(MDMEntryEditPageApiImpl.class);

  @Autowired
  private ViewApi viewApi;

  @Autowired
  private MDMEntryListPageApi listPageApi;

  public MDMEntryEditPageApiImpl() {
    super(Object.class);
  }

  @Override
  public MDMEntryEditingObject initModel(View view) {
    // The page always get an initiated model. So if the init model is called it is a mistake.
    log.error("The master data management page api should be used with prepared model.");
    return new MDMEntryEditingObject();
  }

  @Override
  public void performSave(UUID viewUuid, UiActionRequest request) {
    View view = viewApi.getView(viewUuid);
    UUID parentUUID =
        extractParam(UUID.class, MDMEntryListPageApi.PARAM_MDM_LIST_VIEW, view.getParameters());
    BranchedObjectEntry branchedObjectEntry = extractParamUnChecked(BranchedObjectEntry.class,
        MDMEntryListPageApi.PARAM_BRANCHED_OBJECT_ENTRY, view.getParameters());
    view.setModel(request.getParams().get("model"));
    if (branchedObjectEntry == null) {
      // It is was a new object editing. So we call the create new object of the list page.
      listPageApi.saveNewObject(parentUUID, view.getModel());

      viewApi.closeView(viewUuid);
    } else {
      listPageApi.saveModificationObject(parentUUID, view.getModel(),
          branchedObjectEntry);
      // We have the branched object entry so it will be a modification rather.

      viewApi.closeView(viewUuid);
    }
    log.error("The master data management page api need an event handler to execute save.");
  }

  @Override
  public void performCancel(UUID viewUuid, UiActionRequest request) {
    viewApi.closeView(viewUuid);
  }

}
