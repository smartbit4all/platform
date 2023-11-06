package org.smartbit4all.bff.api.mdm;

import static java.util.Collections.emptyList;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.grid.bean.GridView;
import org.smartbit4all.api.mdm.MDMDefinitionOption;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.object.bean.LangString;
import org.smartbit4all.api.value.bean.GenericValue;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionButtonDescriptor;
import org.smartbit4all.api.view.bean.UiActionButtonType;
import org.smartbit4all.api.view.bean.UiActionDescriptor;
import org.smartbit4all.api.view.bean.UiActionDialogDescriptor;
import org.smartbit4all.api.view.bean.UiActionInputType;
import org.smartbit4all.api.view.bean.UiActionRequest;

public class MDMAdminValuesPageApiImpl extends MDMAdminPageApiImpl
    implements MDMAdminValuesPageApi {

  private static final String VALUE_SET_CODE = "valueSetCode";
  private static final String VALUE_SET_NAME = "valueSetName";

  @Override
  protected boolean filterDescriptor(MDMEntryDescriptor entryDescriptor) {
    return Boolean.TRUE.equals(entryDescriptor.getIsValueSet());
  }

  @Override
  protected String getListViewName() {
    return MDM_LIST_VALUES;
  }

  @Override
  protected UiActionDescriptor getUiActionDescriptor(MDMEntryDescriptor e) {
    return super.getUiActionDescriptor(e)
        .type(UiActionButtonType.RAISED);
  }

  @Override
  protected void refreshUiActions(PageContext context) {
    super.refreshUiActions(context);
    // Add a new value action as last.
    context.view.addActionsItem(new UiAction()
        .code(ACTION_ADD_NEW_ENTRY)
        .inputType(UiActionInputType.TEXTFIELD).input2Type(UiActionInputType.TEXTFIELD)
        .descriptor(new UiActionDescriptor()
            .title(localeSettingApi.get(MasterDataManagementApi.SCHEMA, ACTION_ADD_NEW_ENTRY))
            .color("secondary")
            .type(UiActionButtonType.RAISED)
            .inputDialog(new UiActionDialogDescriptor()
                .title(localeSettingApi.get(MasterDataManagementApi.SCHEMA, VALUE_SET_NAME))
                .actionButton(nextButton())
                .cancelButton(cancelButton()))
            .input2Dialog(new UiActionDialogDescriptor()
                .title(localeSettingApi.get(MasterDataManagementApi.SCHEMA, VALUE_SET_CODE))
                .actionButton(finishButton())
                .cancelButton(cancelButton()))));
  }

  private UiActionButtonDescriptor cancelButton() {
    return new UiActionButtonDescriptor()
        .caption(localeSettingApi.get(MasterDataManagementApi.SCHEMA, "cancel"))
        .color("");
  }

  private UiActionButtonDescriptor nextButton() {
    return new UiActionButtonDescriptor()
        .caption(localeSettingApi.get(MasterDataManagementApi.SCHEMA, "next"))
        .color("primary");
  }

  private UiActionButtonDescriptor finishButton() {
    return new UiActionButtonDescriptor()
        .caption(localeSettingApi.get(MasterDataManagementApi.SCHEMA, "finish"))
        .color("accent");
  }

  @Override
  public void performNewEntry(UUID viewUuid, UiActionRequest request) {
    String name = actionRequestHelper(request).require(UiActions.INPUT, String.class);
    String code = actionRequestHelper(request).require(UiActions.INPUT2, String.class);
    PageContext context = getContextByViewUUID(viewUuid);
    MDMDefinition definition = new MDMDefinition().name(context.definition.getName());
    MDMDefinitionOption o = new MDMDefinitionOption(definition);
    o.addDefaultDescriptor(GenericValue.class, code).name(code)
        .displayNameForm(new LangString().defaultValue(name))
        .displayNameList(new LangString().defaultValue(name))
        .listPageGridViews(createDefaultListPageGridViews())
        .isValueSet(Boolean.TRUE);

    masterDataManagementApi.addNewEntries(o);
    PageContext refreshedContext = getContextByViewUUID(viewUuid);
    refreshUiActions(refreshedContext);
  }

  protected List<GridView> createDefaultListPageGridViews() {
    return emptyList();
  }

}
