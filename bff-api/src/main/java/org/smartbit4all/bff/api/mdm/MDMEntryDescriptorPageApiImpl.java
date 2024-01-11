package org.smartbit4all.bff.api.mdm;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.VectorDBApi;
import org.smartbit4all.api.collection.bean.VectorCollectionDescriptor;
import org.smartbit4all.api.formdefinition.bean.SmartFormWidgetType;
import org.smartbit4all.api.formdefinition.bean.SmartLayoutDefinition;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.mdm.MDMDefinitionOption;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.object.bean.LangString;
import org.smartbit4all.api.value.bean.GenericValue;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.ComponentConstraint;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewConstraint;
import org.smartbit4all.bff.api.mdm.bean.MDMEntryDescriptorPageModel;
import org.smartbit4all.core.object.ObjectLayoutBuilder;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import com.google.common.base.Strings;

public class MDMEntryDescriptorPageApiImpl
    extends PageApiImpl<MDMEntryDescriptorPageModel>
    implements MDMEntryDescriptorPageApi {

  private static final String LAYOUT = "layout";

  @Autowired
  MasterDataManagementApi masterDataManagementApi;
  @Autowired
  VectorDBApi vectorDBApi;
  @Autowired
  InvocationApi invocationApi;


  private static final Logger log =
      LoggerFactory.getLogger(MDMEntryDescriptorPageApiImpl.class);

  public MDMEntryDescriptorPageApiImpl() {
    super(MDMEntryDescriptorPageModel.class);
  }

  protected class PageContext {

    private View view;
    MDMEntryDescriptor entryDescriptor;
    MDMDefinition definition;
    Boolean isNewEntry;
    InvocationRequest refreashActionsCallback;

    PageContext loadByView() {
      ObjectMapHelper parameters = parameters(view);
      String definitionName = parameters.get(PARAM_MDM_DEFINITION, String.class);
      String entryDescriptorName = parameters.get(PARAM_MDM_ENTRY_DESCRIPTOR, String.class);
      definition = masterDataManagementApi.getDefinition(definitionName);
      entryDescriptor = Strings.isNullOrEmpty(entryDescriptorName) ? new MDMEntryDescriptor()
          : masterDataManagementApi.getEntryDescriptor(definitionName, entryDescriptorName);
      isNewEntry = Strings.isNullOrEmpty(entryDescriptorName);
      refreashActionsCallback = objectApi.asType(InvocationRequest.class,
          view.getCallbacks().get(CALLBACK_REFRESH_ACTIONS));
      return this;
    }
  }

  protected PageContext getContextByView(View view) {
    PageContext result = new PageContext();
    result.view = view;
    return result.loadByView();
  }

  protected PageContext getContextByView(UUID viewUuid) {
    PageContext result = new PageContext();
    result.view = viewApi.getView(viewUuid);
    return result.loadByView();
  }

  @Override
  public MDMEntryDescriptorPageModel initModel(View view) {
    UiActions.add(view, ACTION_SAVE, new UiAction().code(DEFAULT_CLOSE));
    view.putLayoutsItem(LAYOUT, getLayout());
    view.constraint(getViewConstraint(view.getUuid()));
    return new MDMEntryDescriptorPageModel();
  }

  private SmartLayoutDefinition getLayout() {
    return new SmartLayoutDefinition().widgets(Arrays.asList(
        new SmartWidgetDefinition().key(MDMEntryDescriptorPageModel.NAME)
            .label(MDMEntryDescriptorPageModel.NAME).type(SmartFormWidgetType.TEXT_FIELD),
        new SmartWidgetDefinition().key(MDMEntryDescriptorPageModel.CODE)
            .label(MDMEntryDescriptorPageModel.CODE).type(SmartFormWidgetType.TEXT_FIELD),
        new SmartWidgetDefinition()
            .key(ObjectLayoutBuilder.widgetKey(MDMEntryDescriptorPageModel.VECTOR_COLLECTION,
                VectorCollectionDescriptor.VECTOR_COLLECTION_NAME))
            .label(VectorCollectionDescriptor.VECTOR_COLLECTION_NAME)
            .type(SmartFormWidgetType.TEXT_FIELD),
        new SmartWidgetDefinition()
            .key(ObjectLayoutBuilder.widgetKey(MDMEntryDescriptorPageModel.VECTOR_COLLECTION,
                VectorCollectionDescriptor.VECTOR_D_B_CONNECTION))
            .label(VectorCollectionDescriptor.VECTOR_D_B_CONNECTION)
            .type(SmartFormWidgetType.TEXT_FIELD),
        new SmartWidgetDefinition()
            .key(ObjectLayoutBuilder.widgetKey(MDMEntryDescriptorPageModel.VECTOR_COLLECTION,
                VectorCollectionDescriptor.EMBEDDING_CONNECTION))
            .label(VectorCollectionDescriptor.EMBEDDING_CONNECTION)
            .type(SmartFormWidgetType.TEXT_FIELD)));
  }

  private ViewConstraint getViewConstraint(UUID viewUuid) {
    ViewConstraint viewConstraint = new ViewConstraint();
    PageContext ctx = getContextByView(viewUuid);
    if (ObjectUtils.isEmpty(vectorDBApi.getContributionApis())) {
      viewConstraint.addComponentConstraintsItem(
          new ComponentConstraint().dataName(ObjectLayoutBuilder.widgetKey(
              MDMEntryDescriptorPageModel.VECTOR_COLLECTION, StringConstant.DOUBLE_ASTERISK))
              .enabled(false).visible(false).mandatory(false));
    }
    if (!Boolean.TRUE.equals(ctx.isNewEntry)) {
      viewConstraint.addComponentConstraintsItem(
          new ComponentConstraint().dataName(MDMEntryDescriptorPageModel.CODE).enabled(false)
              .visible(false).mandatory(false));
    }
    return viewConstraint;
  }

  @Override
  public void saveEntry(UUID viewUuid, UiActionRequest request) {
    PageContext ctx = getContextByView(viewUuid);
    MDMEntryDescriptorPageModel clientModel = extractClientModel(request);
    String code =
        Boolean.TRUE.equals(ctx.isNewEntry) ? clientModel.getCode() : ctx.entryDescriptor.getName();
    String name = clientModel.getName();

    VectorCollectionDescriptor vectorCollectionDescriptor =
        Boolean.TRUE.equals(ctx.isNewEntry) ? clientModel.getVectorCollection()
            : ctx.entryDescriptor.getVectorCollection();

    if (Boolean.TRUE.equals(ctx.isNewEntry)) {
      MDMDefinitionOption option = new MDMDefinitionOption(ctx.definition);
      option.addDefaultDescriptor(GenericValue.class, code).name(code)
          .displayNameForm(new LangString().defaultValue(name))
          .displayNameList(new LangString().defaultValue(name))
          .listPageGridViews(Collections.emptyList())
          .isValueSet(Boolean.TRUE)
          .vectorCollection(vectorCollectionDescriptor);
      masterDataManagementApi.addNewEntries(option);
    } else {
      MDMEntryDescriptor entryDescriptorToEdit =
          ctx.entryDescriptor
              .displayNameForm(new LangString().defaultValue(name))
              .displayNameList(new LangString().defaultValue(name))
              .vectorCollection(vectorCollectionDescriptor);
      masterDataManagementApi.modifyEntry(ctx.definition.getName(), entryDescriptorToEdit);
    }
    if (ctx.refreashActionsCallback != null) {
      try {
        invocationApi.invoke(ctx.refreashActionsCallback);
      } catch (ApiNotFoundException e) {
        // TODO
        log.error(e.getMessage(), e);
      }
    }
    viewApi.closeView(viewUuid);
  }
}
