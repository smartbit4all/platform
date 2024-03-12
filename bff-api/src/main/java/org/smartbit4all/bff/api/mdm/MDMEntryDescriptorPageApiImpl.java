package org.smartbit4all.bff.api.mdm;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import org.apache.logging.log4j.util.Strings;
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
import org.smartbit4all.api.mdm.bean.MDMTableColumnDescriptor;
import org.smartbit4all.api.object.bean.LangString;
import org.smartbit4all.api.setting.LocaleSettingApi;
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
  @Autowired
  LocaleSettingApi localeSettingApi;


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
    URI mdmBranch;

    PageContext loadByView() {
      ObjectMapHelper parameters = parameters(view);
      String definitionName = parameters.get(PARAM_MDM_DEFINITION, String.class);
      entryDescriptor =
          parameters.get(PARAM_MDM_ENTRY_DESCRIPTOR, MDMEntryDescriptor.class);
      if (entryDescriptor != null) {
        isNewEntry = false;
      } else {
        entryDescriptor =
            new MDMEntryDescriptor().vectorCollection(new VectorCollectionDescriptor());
        isNewEntry = true;
      }
      definition = masterDataManagementApi.getDefinition(definitionName);
      refreashActionsCallback = objectApi.asType(InvocationRequest.class,
          view.getCallbacks().get(CALLBACK_REFRESH_ACTIONS));
      mdmBranch = masterDataManagementApi.getGlobalBranch(definitionName);
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
    PageContext ctx = getContextByView(view);
    UiActions.add(view, ACTION_SAVE, new UiAction().code(DEFAULT_CLOSE));
    view.putLayoutsItem(LAYOUT, getLayout());
    view.constraint(getViewConstraint(view.getUuid()));

    return new MDMEntryDescriptorPageModel()
        .name(Boolean.TRUE.equals(ctx.isNewEntry) ? StringConstant.EMPTY
            : ctx.entryDescriptor.getDisplayNameForm().getDefaultValue())
        .vectorCollection(ctx.entryDescriptor.getVectorCollection())
        .importable(Boolean.TRUE.equals(ctx.entryDescriptor.getImportable()));
  }

  private SmartLayoutDefinition getLayout() {
    return new SmartLayoutDefinition().widgets(Arrays.asList(
        new SmartWidgetDefinition().key(MDMEntryDescriptorPageModel.NAME)
            .label(localeSettingApi.get(MDMEntryDescriptorPageModel.class.getSimpleName(),
                MDMEntryDescriptorPageModel.NAME))
            .type(SmartFormWidgetType.TEXT_FIELD),
        new SmartWidgetDefinition().key(MDMEntryDescriptorPageModel.CODE)
            .label(localeSettingApi.get(MDMEntryDescriptorPageModel.class.getSimpleName(),
                MDMEntryDescriptorPageModel.CODE))
            .type(SmartFormWidgetType.TEXT_FIELD),
        new SmartWidgetDefinition()
            .key(ObjectLayoutBuilder.widgetKey(MDMEntryDescriptorPageModel.VECTOR_COLLECTION,
                VectorCollectionDescriptor.VECTOR_COLLECTION_NAME))
            .label(localeSettingApi.get(MDMEntryDescriptorPageModel.class.getSimpleName(),
                VectorCollectionDescriptor.VECTOR_COLLECTION_NAME))
            .type(SmartFormWidgetType.TEXT_FIELD),
        new SmartWidgetDefinition()
            .key(ObjectLayoutBuilder.widgetKey(MDMEntryDescriptorPageModel.VECTOR_COLLECTION,
                VectorCollectionDescriptor.VECTOR_D_B_CONNECTION))
            .label(localeSettingApi.get(MDMEntryDescriptorPageModel.class.getSimpleName(),
                VectorCollectionDescriptor.VECTOR_D_B_CONNECTION))
            .type(SmartFormWidgetType.TEXT_FIELD),
        new SmartWidgetDefinition()
            .key(ObjectLayoutBuilder.widgetKey(MDMEntryDescriptorPageModel.VECTOR_COLLECTION,
                VectorCollectionDescriptor.EMBEDDING_CONNECTION))
            .label(localeSettingApi.get(MDMEntryDescriptorPageModel.class.getSimpleName(),
                VectorCollectionDescriptor.EMBEDDING_CONNECTION))
            .type(SmartFormWidgetType.TEXT_FIELD),
        ObjectLayoutBuilder.toggle(MDMEntryDescriptorPageModel.IMPORTABLE, LAYOUT)
            .label(localeSettingApi.get(MDMEntryDescriptorPageModel.class.getSimpleName(),
                MDMEntryDescriptorPageModel.IMPORTABLE))));
  }

  protected ViewConstraint getViewConstraint(UUID viewUuid) {
    ViewConstraint viewConstraint = new ViewConstraint();
    PageContext ctx = getContextByView(viewUuid);
    if (ObjectUtils.isEmpty(vectorDBApi.getContributionApis())) {
      viewConstraint.addComponentConstraintsItem(
          new ComponentConstraint().dataName(ObjectLayoutBuilder.widgetKey(
              MDMEntryDescriptorPageModel.VECTOR_COLLECTION, StringConstant.DOUBLE_ASTERISK))
              .enabled(false).visible(false).mandatory(false));
    }

    viewConstraint.addComponentConstraintsItem(
        new ComponentConstraint().dataName(MDMEntryDescriptorPageModel.NAME).mandatory(true));

    if (Boolean.TRUE.equals(ctx.isNewEntry)) {
      viewConstraint.addComponentConstraintsItem(
          new ComponentConstraint().dataName(MDMEntryDescriptorPageModel.CODE).enabled(true)
              .visible(true).mandatory(true));
    } else {
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

    if (Strings.isBlank(name)) {
      throw new IllegalArgumentException(
          localeSettingApi.get(MDMEntryDescriptorPageModel.class.getSimpleName(), "error",
              "emptyname"));
    }
    if (code.contains(StringConstant.SPACE)) {
      throw new IllegalArgumentException(
          localeSettingApi.get(MDMEntryDescriptorPageModel.class.getSimpleName(), "error",
              "spacecharacter"));
    }

    VectorCollectionDescriptor vectorCollectionDescriptor =
        clientModel.getVectorCollection() != null ? clientModel.getVectorCollection()
            : ctx.entryDescriptor.getVectorCollection();

    if (Boolean.TRUE.equals(ctx.isNewEntry)) {
      MDMDefinitionOption option = new MDMDefinitionOption(ctx.definition);
      addNewEntryDescriptor(clientModel, code, name, vectorCollectionDescriptor, option);
      // TODO use branch
      masterDataManagementApi.addNewEntries(option, ctx.mdmBranch);
    } else {
      MDMEntryDescriptor entryDescriptorToEdit =
          ctx.entryDescriptor
              .displayNameForm(new LangString().defaultValue(name))
              .displayNameList(new LangString().defaultValue(name))
              .vectorCollection(vectorCollectionDescriptor)
              .importable(Boolean.TRUE.equals(clientModel.getImportable()));
      masterDataManagementApi.modifyEntry(ctx.definition.getName(), entryDescriptorToEdit,
          ctx.mdmBranch);
    }
    if (ctx.refreashActionsCallback != null) {
      try {
        invocationApi.invoke(ctx.refreashActionsCallback);
      } catch (ApiNotFoundException e) {
        log.error(e.getMessage(), e);
      }
    }
    viewApi.closeView(viewUuid);
  }

  protected MDMEntryDescriptor addNewEntryDescriptor(MDMEntryDescriptorPageModel clientModel,
      String code, String name, VectorCollectionDescriptor vectorCollectionDescriptor,
      MDMDefinitionOption option) {
    try {
      MDMEntryDescriptor descriptor =
          option.addDefaultDescriptor(GenericValue.class, code).name(code)
              .tableColumns(Arrays.asList(new MDMTableColumnDescriptor().name(GenericValue.CODE)
                  .addPathItem(GenericValue.CODE),
                  new MDMTableColumnDescriptor().name(GenericValue.NAME)
                      .addPathItem(GenericValue.NAME),
                  new MDMTableColumnDescriptor().name(GenericValue.DESCRIPTION)
                      .addPathItem(GenericValue.DESCRIPTION),
                  new MDMTableColumnDescriptor().name(GenericValue.ICON)
                      .addPathItem(GenericValue.ICON)))
              .displayNameForm(new LangString().defaultValue(name))
              .displayNameList(new LangString().defaultValue(name))
              .listPageGridViews(Collections.emptyList())
              .isValueSet(Boolean.TRUE)
              .vectorCollection(vectorCollectionDescriptor)
              .importable(Boolean.TRUE.equals(clientModel.getImportable()))
              .uniquePropertyPaths(Arrays.asList(Arrays.asList(GenericValue.CODE)));
      MDMDefinitionOption.addCreatedUpdatedExtraProperties(descriptor);
      return descriptor;
    } catch (IllegalArgumentException e) {
      log.debug("Trying to create entry descriptor with invalid code", e);
      throw new IllegalArgumentException(localeSettingApi
          .get(MDMEntryDescriptorPageModel.class.getSimpleName(), "error.usedcode"));
    }
  }
}
