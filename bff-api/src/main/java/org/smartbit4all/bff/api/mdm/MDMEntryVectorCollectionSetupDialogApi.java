package org.smartbit4all.bff.api.mdm;

import java.util.UUID;
import org.smartbit4all.api.collection.bean.VectorCollectionDescriptor;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.bean.UiActionRequest;

public interface MDMEntryVectorCollectionSetupDialogApi
    extends PageApi<VectorCollectionDescriptor> {

  String ACTION_SAVE = "SAVE";

  @ActionHandler(ACTION_SAVE)
  void save(UUID viewUuid, UiActionRequest request);

}
