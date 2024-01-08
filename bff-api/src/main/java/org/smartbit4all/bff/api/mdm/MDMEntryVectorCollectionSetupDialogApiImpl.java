package org.smartbit4all.bff.api.mdm;

import java.util.Arrays;
import java.util.UUID;
import org.smartbit4all.api.collection.bean.VectorCollectionDescriptor;
import org.smartbit4all.api.formdefinition.bean.SmartFormWidgetType;
import org.smartbit4all.api.formdefinition.bean.SmartLayoutDefinition;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.springframework.beans.factory.annotation.Autowired;

public class MDMEntryVectorCollectionSetupDialogApiImpl
    extends PageApiImpl<VectorCollectionDescriptor>
    implements MDMEntryVectorCollectionSetupDialogApi {

  private static final String LAYOUT = "layout";
  @Autowired
  MasterDataManagementApi masterDataManagementApi;

  public MDMEntryVectorCollectionSetupDialogApiImpl() {
    super(VectorCollectionDescriptor.class);
  }

  @Override
  public VectorCollectionDescriptor initModel(View view) {
    UiActions.add(view, ACTION_SAVE, DEFAULT_CLOSE);
    view.putLayoutsItem(LAYOUT, new SmartLayoutDefinition().widgets(Arrays.asList(
        new SmartWidgetDefinition().key(VectorCollectionDescriptor.VECTOR_COLLECTION_NAME)
            .label(VectorCollectionDescriptor.VECTOR_COLLECTION_NAME)
            .type(SmartFormWidgetType.TEXT_FIELD),
        new SmartWidgetDefinition().key(VectorCollectionDescriptor.VECTOR_D_B_CONNECTION)
            .label(VectorCollectionDescriptor.VECTOR_D_B_CONNECTION)
            .type(SmartFormWidgetType.TEXT_FIELD),
        new SmartWidgetDefinition().key(VectorCollectionDescriptor.EMBEDDING_CONNECTION)
            .label(VectorCollectionDescriptor.EMBEDDING_CONNECTION)
            .type(SmartFormWidgetType.TEXT_FIELD))));
    return new VectorCollectionDescriptor();
  }

  @Override
  public void save(UUID viewUuid, UiActionRequest request) {
    VectorCollectionDescriptor vectorCollectionDescriptor = extractClientModel(request);
    String defintion =
        parameters(viewUuid).get(MDMEntryListPageApi.PARAM_MDM_DEFINITION, String.class);
    String entry =
        parameters(viewUuid).get(MDMEntryListPageApi.PARAM_ENTRY_DESCRIPTOR, String.class);
    masterDataManagementApi.saveVectorCollectionDescriptor(defintion, entry,
        vectorCollectionDescriptor);
    viewApi.closeView(viewUuid);
  }
}
