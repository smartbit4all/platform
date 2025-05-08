package org.smartbit4all.bff.api.mdm.relation;

import static org.smartbit4all.core.object.ObjectLayoutBuilder.widgetKey;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.formdefinition.bean.SelectionDefinition;
import org.smartbit4all.api.mdm.MDMRelationApi;
import org.smartbit4all.api.mdm.MDMRelationApi.RelatedObjectHolder;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMRelationDefinition;
import org.smartbit4all.api.value.ValueSetApi;
import org.smartbit4all.api.value.bean.GenericValue;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.api.view.bean.ValueSet;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.bff.api.mdm.MDMEntryListPageApi;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectLayoutBuilder;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;

public class MDMRelationEditorServiceImpl implements MDMRelationEditorService {

  private static final Logger log = LoggerFactory.getLogger(MDMRelationEditorServiceImpl.class);

  private static final String TEMP_PROP_RELATIONS = "__relations";

  @Autowired
  private ObjectApi objectApi;
  @Autowired
  private MasterDataManagementApi masterDataManagementApi;
  @Autowired
  private MDMRelationApi mdmRelationApi;
  @Autowired
  private ValueSetApi valueSetApi;

  @Override
  public void addRelationsToViewModel(final View view, final Map viewModel) {
    final Map<String, Object> parameters = view.getParameters();
    final String definition = objectApi
        .asType(MDMDefinition.class, parameters.get(MDMEntryListPageApi.PARAM_MDM_DEFINITION))
        .getName();
    final String entryName = objectApi
        .asType(MDMEntryDescriptor.class,
            parameters.get(MDMEntryListPageApi.PARAM_ENTRY_DESCRIPTOR))
        .getName();

    final List<MDMRelationDefinition> managedRelations = mdmRelationApi.getManagedRelations(
        definition,
        entryName);
    if (managedRelations.isEmpty()) {
      return;
    }

    managedRelations.forEach(rel -> addRelationToViewModel(view, viewModel, rel));
  }

  private void addRelationToViewModel(
      final View view,
      final Map viewModel,
      final MDMRelationDefinition relationDefinition) {
    final var relationProp = (Map<String, Object>) viewModel.computeIfAbsent(
        TEMP_PROP_RELATIONS,
        k -> new HashMap<String, Object>());
    final RelatedObjectHolder relations = mdmRelationApi.getRelations(
        objectApi.loadLatest(view.getObjectUri(), view.getBranchUri()),
        relationDefinition);

    // 1. set the property:
    switch (relations) {
      case RelatedObjectHolder.Singular s -> relationProp.put(
          relationDefinition.getCode(),
          s.relatedObject());
      case RelatedObjectHolder.Multiple m -> relationProp.put(
          relationDefinition.getCode(),
          m.relatedObjects());
    }

    // 2. set the value set:
    final ValueSet valueSet = valueSetApi.getValueSetWithValues(
        relationDefinition.getToDefinition(),
        relationDefinition.getToEntryName(),
        relations.asList(),
        view.getBranchUri(),
        GenericValue.NAME);
    view.putValueSetsItem(valueSet.getValueSetName(), valueSet);

    // 3. set the widget:
    final SelectionDefinition selectionDef = ObjectLayoutBuilder.selectionDefinition(
        valueSet.getValueSetName(),
        Value.DISPLAY_VALUE);
    final String widgetKey = widgetKey(TEMP_PROP_RELATIONS, relationDefinition.getCode());
    final String widgetLabel = relationDefinition.getName();
    final var widget = switch (relations) {
      case RelatedObjectHolder.Singular s -> ObjectLayoutBuilder.combobox(
          widgetKey,
          widgetLabel,
          selectionDef);
      case RelatedObjectHolder.Multiple m -> ObjectLayoutBuilder.multiSelectCombobox(
          widgetKey,
          widgetLabel,
          selectionDef);
    };
    view.getLayouts().get(MDMEntryListPageApi.LAYOUT_EDITOR_FORM).addWidgetsItem(widget);
  }

  @Override
  public void setRelationsInHost(View view, ObjectNode host) {
    final Map<String, Object> relationProp = host.getValueAsMap(Object.class, TEMP_PROP_RELATIONS);
    if (relationProp == null) {
      return;
    }
    
    relationProp.forEach((relation, relatedObj) -> {
      final List<URI> relatedObjectUris = switch (relatedObj) {
        case null -> Collections.emptyList();
        case List<?> list -> objectApi.asList(URI.class, list);
        default -> Collections.singletonList(objectApi.asType(URI.class, relatedObj));
      };
      mdmRelationApi.setRelations(host, relation, relatedObjectUris);
    });

    host.setValue(null, TEMP_PROP_RELATIONS);
  }


}
