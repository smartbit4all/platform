package org.smartbit4all.bff.api.mdm;

import static java.util.stream.Collectors.toList;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.textbox;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.textfield;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.toggle;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.widgetKey;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.VectorDBApi;
import org.smartbit4all.api.collection.bean.VectorCollectionDescriptor;
import org.smartbit4all.api.formdefinition.bean.SmartFormWidgetDirection;
import org.smartbit4all.api.formdefinition.bean.SmartFormWidgetType;
import org.smartbit4all.api.formdefinition.bean.SmartLayoutDefinition;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.formdefinition.bean.ValueChangeMode;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.exception.BusinessLogicException;
import org.smartbit4all.api.mdm.MDMDefinitionOption;
import org.smartbit4all.api.mdm.MDMModificationApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMTableColumnDescriptor;
import org.smartbit4all.api.object.bean.LangString;
import org.smartbit4all.api.object.bean.ObjectPropertyFormatter;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.value.bean.GenericValue;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.ViewPublisherApi;
import org.smartbit4all.api.view.bean.ComponentConstraint;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewConstraint;
import org.smartbit4all.bff.api.mdm.bean.MDMEntryDescriptorPageModel;
import org.smartbit4all.bff.api.mdm.util.MDMVectorCollectionUtil;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.core.object.ObjectSerializerByObjectMapper;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MDMEntryDescriptorPageApiImpl
    extends PageApiImpl<MDMEntryDescriptorPageModel>
    implements MDMEntryDescriptorPageApi {

  private static final String LAYOUT = "layout";

  @Autowired
  protected MasterDataManagementApi masterDataManagementApi;
  @Autowired
  protected VectorDBApi vectorDBApi;
  @Autowired
  protected InvocationApi invocationApi;
  @Autowired
  protected LocaleSettingApi localeSettingApi;
  @Autowired
  protected SessionApi sessionApi;
  @Autowired
  private ViewPublisherApi viewPublisherApi;

  private ObjectMapper objectMapper = ObjectSerializerByObjectMapper.getObjectMapper();

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
    MDMModificationApi modificationApi;
    URI mdmBranch;

    PageContext loadByView() {
      ObjectMapHelper parameters = parameters(view);
      String definitionName = parameters.get(PARAM_MDM_DEFINITION, String.class);
      entryDescriptor =
          parameters.get(PARAM_MDM_ENTRY_DESCRIPTOR, MDMEntryDescriptor.class);
      if (getEntryDescriptor() != null) {
        isNewEntry = false;
      } else {
        entryDescriptor =
            new MDMEntryDescriptor().vectorCollection(new VectorCollectionDescriptor());
        isNewEntry = true;
      }
      definition = masterDataManagementApi.getDefinition(definitionName);
      refreashActionsCallback = objectApi.asType(InvocationRequest.class,
          view.getCallbacks().get(CALLBACK_REFRESH_ACTIONS));
      modificationApi = masterDataManagementApi
          .getModificationApiForUser(definition.getName(), sessionApi.getUserUri());
      mdmBranch = modificationApi == null ? null : modificationApi.getModification().getBranchUri();
      return this;
    }

    public boolean getIsNewEntry() {
      return Boolean.TRUE.equals(isNewEntry);
    }

    public MDMEntryDescriptor getEntryDescriptor() {
      return entryDescriptor;
    }

    public MDMDefinition getDefinition() {
      return definition;
    }

    public URI getMdmBranch() {
      return mdmBranch;
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
    view.putLayoutsItem(LAYOUT, getLayout(view.getUuid()));
    view.constraint(getViewConstraint(view.getUuid()));

    MDMEntryDescriptor currentEntryDescriptor = ctx.getEntryDescriptor();
    return createPageModelFromEntryDescriptor(ctx, currentEntryDescriptor);
  }

  private MDMEntryDescriptorPageModel createPageModelFromEntryDescriptor(PageContext ctx,
      MDMEntryDescriptor currentEntryDescriptor) {

    VectorCollectionDescriptor vectorCollection = currentEntryDescriptor.getVectorCollection();
    if (vectorCollection == null) {
      vectorCollection = new VectorCollectionDescriptor();
    }
    String formatter;
    try {
      formatter =
          vectorCollection.getFormatter() != null ? objectMapper.writerWithDefaultPrettyPrinter()
              .writeValueAsString(vectorCollection.getFormatter()) : StringConstant.EMPTY;
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
      formatter = StringConstant.EMPTY;
    }

    return new MDMEntryDescriptorPageModel()
        .name(Boolean.TRUE.equals(ctx.isNewEntry) ? StringConstant.EMPTY
            : currentEntryDescriptor.getDisplayNameForm().getDefaultValue())
        .vectorCollection(vectorCollection)
        .formatter(formatter)
        .importable(Boolean.TRUE.equals(currentEntryDescriptor.getImportable()))
        .csvSeparator(currentEntryDescriptor.getCsvSeparator());
  }

  private SmartLayoutDefinition getLayout(UUID viewUuid) {

    List<SmartWidgetDefinition> subWidgetBoxes = new ArrayList<>();
    if (!ObjectUtils.isEmpty(vectorDBApi.getContributionApis())) {
      subWidgetBoxes.add(new SmartWidgetDefinition()
          .type(SmartFormWidgetType.CONTAINER)
          .direction(SmartFormWidgetDirection.COL)
          .childrenComponents(List.of(
              new SmartWidgetDefinition()
                  .key(widgetKey(MDMEntryDescriptorPageModel.VECTOR_COLLECTION,
                      VectorCollectionDescriptor.VECTOR_COLLECTION_NAME))
                  .label(localeSettingApi.get(
                      VectorCollectionDescriptor.VECTOR_COLLECTION_NAME))
                  .type(SmartFormWidgetType.TEXT_FIELD),
              MDMVectorCollectionUtil.getEmbeddingConnectionWidget(
                  widgetKey(MDMEntryDescriptorPageModel.VECTOR_COLLECTION,
                      VectorCollectionDescriptor.EMBEDDING_CONNECTION),
                  localeSettingApi.get(
                      VectorCollectionDescriptor.EMBEDDING_CONNECTION),
                  masterDataManagementApi),
              MDMVectorCollectionUtil.getVectorDbConnectionWidget(
                  widgetKey(MDMEntryDescriptorPageModel.VECTOR_COLLECTION,
                      VectorCollectionDescriptor.VECTOR_D_B_CONNECTION),
                  localeSettingApi.get(
                      VectorCollectionDescriptor.VECTOR_D_B_CONNECTION),
                  masterDataManagementApi),
              textbox(
                  widgetKey(MDMEntryDescriptorPageModel.FORMATTER),
                  localeSettingApi.get(MDMEntryDescriptorPageModel.class.getSimpleName(),
                      VectorCollectionDescriptor.FORMATTER)))));
    }

    subWidgetBoxes.add(new SmartWidgetDefinition()
        .type(SmartFormWidgetType.CONTAINER).direction(SmartFormWidgetDirection.COL)
        .childrenComponents(List.of(
            toggle(MDMEntryDescriptorPageModel.IMPORTABLE,
                localeSettingApi.get(MDMEntryDescriptorPageModel.class.getSimpleName(),
                    MDMEntryDescriptorPageModel.IMPORTABLE)),
            textfield(MDMEntryDescriptorPageModel.CSV_SEPARATOR,
                localeSettingApi.get(MDMEntryDescriptorPageModel.class.getSimpleName(),
                    MDMEntryDescriptorPageModel.CSV_SEPARATOR)))));

    SmartLayoutDefinition widgets = new SmartLayoutDefinition().widgets(Arrays.asList(
        new SmartWidgetDefinition()
            .type(SmartFormWidgetType.SELECT)
            .key(MDMEntryDescriptorPageModel.SELECTED_TEMPLATE)
            .label(localeSettingApi.get(
                MDMEntryDescriptorPageModel.class.getName(),
                MDMEntryDescriptorPageModel.SELECTED_TEMPLATE))
            .values(getTemplateValueList(viewUuid))
            .valueChangeMode(ValueChangeMode.IMMEDIATE_ACTION),
        new SmartWidgetDefinition().key(MDMEntryDescriptorPageModel.NAME)
            .label(localeSettingApi.get(MDMEntryDescriptorPageModel.class.getSimpleName(),
                MDMEntryDescriptorPageModel.NAME))
            .type(SmartFormWidgetType.TEXT_FIELD),
        new SmartWidgetDefinition().key(MDMEntryDescriptorPageModel.CODE)
            .label(localeSettingApi.get(MDMEntryDescriptorPageModel.class.getSimpleName(),
                MDMEntryDescriptorPageModel.CODE))
            .type(SmartFormWidgetType.TEXT_FIELD),
        new SmartWidgetDefinition()
            .type(SmartFormWidgetType.CONTAINER).direction(SmartFormWidgetDirection.ROW)
            .childrenComponents(subWidgetBoxes)));

    return widgets;
  }

  protected ViewConstraint getViewConstraint(UUID viewUuid) {
    ViewConstraint viewConstraint = new ViewConstraint();
    PageContext ctx = getContextByView(viewUuid);
    if (ObjectUtils.isEmpty(vectorDBApi.getContributionApis())) {
      viewConstraint
          .addComponentConstraintsItem(
              new ComponentConstraint().dataName(widgetKey(
                  MDMEntryDescriptorPageModel.VECTOR_COLLECTION, StringConstant.DOUBLE_ASTERISK))
                  .enabled(false).visible(false).mandatory(false))
          .addComponentConstraintsItem(
              new ComponentConstraint().dataName(MDMEntryDescriptorPageModel.FORMATTER)
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

    if (!Boolean.TRUE.equals(ctx.isNewEntry)
        || ObjectUtils.isEmpty(ctx.getDefinition().getTemplates())) {
      viewConstraint.addComponentConstraintsItem(
          new ComponentConstraint().dataName(MDMEntryDescriptorPageModel.SELECTED_TEMPLATE)
              .enabled(false).visible(false).mandatory(false));
    }
    return viewConstraint;
  }

  @Override
  public void saveEntry(UUID viewUuid, UiActionRequest request) {
    PageContext ctx = getContextByView(viewUuid);
    MDMEntryDescriptorPageModel clientModel = extractClientModel(request);
    String code =
        Boolean.TRUE.equals(ctx.isNewEntry)
            ? clientModel.getCode()
            : ctx.getEntryDescriptor().getName();
    String name = clientModel.getName();

    validateDescriptorProperties(code, name);

    VectorCollectionDescriptor vectorCollectionDescriptor =
        clientModel.getVectorCollection();

    if (!ObjectUtils.isEmpty(clientModel.getFormatter())) {
      try {
        ObjectPropertyFormatter formatter =
            objectMapper.readValue(clientModel.getFormatter(), ObjectPropertyFormatter.class);
        vectorCollectionDescriptor.setFormatter(formatter);
      } catch (Exception e) {
        throw new BusinessLogicException(
            localeSettingApi.get(MDMEntryDescriptorPageModel.class.getSimpleName(), "error",
                "malformedFormatterJson"));
      }
    }

    if (Boolean.TRUE.equals(ctx.isNewEntry)) {
      MDMDefinitionOption option = new MDMDefinitionOption(ctx.getDefinition());
      MDMEntryDescriptor newDescriptor = addNewEntryDescriptor(clientModel,
          code, name, vectorCollectionDescriptor, option);
      // clear descriptors to not add already created descriptions again
      option.getDefinition().getDescriptors().clear();
      option.addDescriptor(newDescriptor);
      masterDataManagementApi.addNewEntries(option, ctx.getMdmBranch());
    } else {
      MDMEntryDescriptor entryDescriptorToEdit =
          ctx.getEntryDescriptor()
              .displayNameForm(new LangString().defaultValue(name))
              .displayNameList(new LangString().defaultValue(name))
              .vectorCollection(vectorCollectionDescriptor)
              .importable(Boolean.TRUE.equals(clientModel.getImportable()))
              .csvSeparator(clientModel.getCsvSeparator());
      masterDataManagementApi.modifyEntry(ctx.getDefinition().getName(), entryDescriptorToEdit,
          ctx.getMdmBranch());
    }
    viewPublisherApi.fireActionPerformed(viewApi.getView(viewUuid), request, code, name,
        getModel(viewUuid), clientModel);
    if (ctx.refreashActionsCallback != null) {
      try {
        invocationApi.invoke(ctx.refreashActionsCallback);
      } catch (ApiNotFoundException e) {
        log.error(e.getMessage(), e);
      }
    }
    viewApi.closeView(viewUuid);
  }


  @Override
  public void templateSelected(UUID viewUuid, UiActionRequest request) {
    MDMEntryDescriptorPageModel pageModel = actionRequestHelper(request)
        .get(UiActions.MODEL, MDMEntryDescriptorPageModel.class);

    PageContext context = getContextByView(viewUuid);
    MDMEntryDescriptor selectedTemplate =
        context.getDefinition().getTemplates().get(pageModel.getSelectedTemplate());


    if (selectedTemplate == null) {
      selectedTemplate = context.getEntryDescriptor();
    }

    MDMEntryDescriptorPageModel pageModelFromEntryDescriptor =
        createPageModelFromEntryDescriptor(context, selectedTemplate);
    setModel(viewUuid, pageModelFromEntryDescriptor);

  }

  protected void validateDescriptorProperties(String code, String name) {
    if (Strings.isBlank(name)) {
      throw new BusinessLogicException(
          localeSettingApi.get(MDMEntryDescriptorPageModel.class.getSimpleName(), "error",
              "emptyname"));
    }

    if (!StringConstant.isValidCode(code)) {
      throw new BusinessLogicException(
          localeSettingApi.get(MDMEntryDescriptorPageModel.class.getSimpleName(), "error",
              "invalidcharacters"));
    }
  }

  protected MDMEntryDescriptor addNewEntryDescriptor(MDMEntryDescriptorPageModel clientModel,
      String code, String name, VectorCollectionDescriptor vectorCollectionDescriptor,
      MDMDefinitionOption option) {
    try {

      MDMEntryDescriptor descriptor;

      if (clientModel.getSelectedTemplate() != null) {
        descriptor =
            option.getDefinition().getTemplates().get(clientModel.getSelectedTemplate())
                .name(code)
                .publishedListName(code)
                .displayNameForm(new LangString().defaultValue(name))
                .displayNameList(new LangString().defaultValue(name))
                .isValueSet(Boolean.TRUE)
                .vectorCollection(vectorCollectionDescriptor)
                .importable(Boolean.TRUE.equals(clientModel.getImportable()))
                .csvSeparator(clientModel.getCsvSeparator());
        option.addDescriptor(descriptor);

      } else {
        descriptor =
            option.addDefaultDescriptor(GenericValue.class, code)
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
                .displayNamePropertyPath(Arrays.asList(GenericValue.NAME))
                .listPageGridViews(Collections.emptyList())
                .isValueSet(Boolean.TRUE)
                .vectorCollection(vectorCollectionDescriptor)
                .importable(Boolean.TRUE.equals(clientModel.getImportable()))
                .csvSeparator(clientModel.getCsvSeparator())
                .uniquePropertyPaths(Arrays.asList(Arrays.asList(GenericValue.CODE)));
      }

      MDMDefinitionOption.addCreatedUpdatedExtraProperties(descriptor);
      return descriptor;
    } catch (IllegalArgumentException e) {
      log.debug("Trying to create entry descriptor with invalid code", e);
      throw new BusinessLogicException(localeSettingApi
          .get(MDMEntryDescriptorPageModel.class.getSimpleName(), "error.usedcode"));
    }
  }

  private List<Value> getTemplateValueList(UUID viewUuid) {
    PageContext ctx = getContextByView(viewUuid);

    return ctx.getDefinition().getTemplates()
        .entrySet().stream()
        .map(entrySet -> {
          return new Value().code(entrySet.getKey())
              .displayValue(localeSettingApi.get(entrySet.getValue().getDisplayNameList()));
        }).collect(toList());
  }

}
