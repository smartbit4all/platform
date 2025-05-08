package org.smartbit4all.bff.api.mdm.relation;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.smartbit4all.api.mdm.MDMRelationApi;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.api.value.bean.ValueSetData;
import org.smartbit4all.api.view.bean.MultiComboBoxElement;
import org.smartbit4all.api.view.bean.MultiComboBoxModel;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.ValueSet;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.base.Strings;

public class RelationManagedMultiComboBoxServiceImpl
    implements RelationManagedMultiComboBoxService {

  private static final String VIEW_VAR_INITIAL_VALUE_SETS = "v-rwmsi-initial-valuesets";
  private static final String VIEW_VAR_PERMITTED_EXTRA_REL = "v-rwmsi-permitted-extra";
  private static final String VIEW_VAR_MULTI_COMBO_BOX_MODEL = "v-rwmsi-mcbm";


  @Autowired
  private ObjectApi objectApi;
  @Autowired
  private MDMRelationApi mdmRelationApi;

  @Override
  public void init(
      final View view,
      final MultiComboBoxModel multiComboBoxModel,
      final ViewModelValueAccessor accessor) {
    if (multiComboBoxModel == null || multiComboBoxModel.getElements().isEmpty()) {
      return;
    }

    final var cache = cache(view);
    if (cache.present(multiComboBoxModel)) {
      return;
    }

    final List<ValueSet> valueSets = multiComboBoxModel.getElements().stream()
        .map(MultiComboBoxElement::getValueSet)
        .map(view.getValueSets()::get)
        .filter(Objects::nonNull)
        .toList();
    cacheInitialValueSets(view, valueSets);
    cache.set(multiComboBoxModel);
    cacheInitialValuesAsPermitted(view, multiComboBoxModel, accessor);
    enforceValueSets(
        view,
        multiComboBoxModel,
        accessor,
        multiComboBoxModel.getElements().getFirst().getWidgetKey(),
        true);

  }

  private void cacheInitialValuesAsPermitted(View view, MultiComboBoxModel multiComboBoxModel,
      ViewModelValueAccessor accessor) {
    final Map<String, String> extraVals = new HashMap<>();
    final List<MultiComboBoxElement> elements = multiComboBoxModel.getElements();
    for (int i = 0; i < elements.size() - 1; i++) {
      final var element = elements.get(i);
      final var nextElement = elements.get(i + 1);

      final URI value = accessor.get(element.getWidgetKey());
      final URI nextValue = accessor.get(nextElement.getWidgetKey());

      if (value != null && nextValue != null) {
        extraVals.put(
            objectApi.getLatestUri(value).toString(),
            objectApi.getLatestUri(nextValue).toString());
      }
    }
    view.getVariables().put(VIEW_VAR_PERMITTED_EXTRA_REL, extraVals);
  }

  private Optional<URI> getSpecialPermittedValue(View view, URI key) {
    if (key == null) {
      return Optional.empty();
    }

    Object o = view.getVariables().get(VIEW_VAR_PERMITTED_EXTRA_REL);
    if (o instanceof Map m) {
      Object v = m.get(objectApi.getLatestUri(key).toString());
      return Optional.ofNullable(objectApi.asType(URI.class, v));
    }

    return Optional.empty();
  }

  private void cacheInitialValueSets(final View view, List<ValueSet> valueSets) {
    view.getVariables().put(
        VIEW_VAR_INITIAL_VALUE_SETS,
        valueSets.stream()
            .map(this::copyVs)
            .collect(Collectors.toMap(ValueSet::getValueSetName, Function.identity())));
  }

  private ValueSet getCachedVsCopy(final View view, String valueSet) {
    Object o = view.getVariables().get(VIEW_VAR_INITIAL_VALUE_SETS);
    if (o instanceof Map m) {
      Object vsObj = m.get(valueSet);
      return copyVs(objectApi.asType(ValueSet.class, vsObj));
    }

    throw new IllegalArgumentException("Ree");
  }

  private MultiComboBoxModelCache cache(final View view) {
    return new MultiComboBoxModelCache(view);
  }

  private final class MultiComboBoxModelCache {


    private final View view;

    private MultiComboBoxModelCache(final View view) {
      this.view = view;
    }

    public void set(final MultiComboBoxModel model) {
      Object o = view.getVariables().get(VIEW_VAR_MULTI_COMBO_BOX_MODEL);
      if (o instanceof Map m) {
        m.put(model.getViewKey(), m);
      } else {
        final Map<String, MultiComboBoxModel> models = new HashMap<>();
        models.put(model.getViewKey(), model);
        view.getVariables().put(VIEW_VAR_MULTI_COMBO_BOX_MODEL, models);
      }
    }

    public List<MultiComboBoxModel> get(final String widgetKey) {
      Object o = view.getVariables().get(VIEW_VAR_MULTI_COMBO_BOX_MODEL);
      if (o instanceof Map<?, ?> m) {
        return m.values().stream()
            .map(it -> objectApi.asType(MultiComboBoxModel.class, it))
            .filter(it -> it.getElements().stream()
                .map(MultiComboBoxElement::getWidgetKey)
                .anyMatch(widgetKey::equals))
            .toList();
      } else {
        return Collections.emptyList();
      }
    }

    public boolean present(final MultiComboBoxModel model) {
      Object o = view.getVariables().get(VIEW_VAR_MULTI_COMBO_BOX_MODEL);
      if (o instanceof Map m) {
        return m.containsKey(model.getViewKey());
      }

      return false;
    }
  }

  private void enforceValueSets(
      final View view,
      final MultiComboBoxModel multiComboBoxModel,
      final ViewModelValueAccessor accessor,
      String startKey,
      final boolean lenient) {
    if (startKey == null) {
      return;
    }

    boolean skipping = true;
    boolean blindAccept = true;
    final Set<URI> acceptableValues = new HashSet<>();
    final var elements = multiComboBoxModel.getElements();
    for (int i = 0; i < elements.size(); i++) {
      final var element = elements.get(i);
      final var widgetKey = element.getWidgetKey();
      if (skipping && !Objects.equals(startKey, widgetKey)) {
        continue;
      }

      skipping = false;
      if (!blindAccept) {
        final var currValue = accessor.get(widgetKey);
        if (currValue == null) {
          acceptableValues.clear();
        } else if (acceptableValues.stream()
            .noneMatch(v -> objectApi.equalsIgnoreVersion(v, currValue))) {
          accessor.set(widgetKey, null);
          acceptableValues.clear();
        }
      }

      if (Strings.isNullOrEmpty(element.getRelation()) || i + 1 == elements.size()) {
        break;
      }

      final var nextElement = elements.get(i + 1);
      final Set<URI> permittedChildUris = getPermittedNextValues(
          view,
          element,
          accessor.get(widgetKey),
          nextElement);
      final ValueSet cachedVsCopy = getCachedVsCopy(view, nextElement.getValueSet());
      final List<Object> values = cachedVsCopy.getValueSetData().getValues();
      values.removeIf(v -> {
        final Value value = objectApi.asType(Value.class, v);
        return !permittedChildUris.contains(objectApi.getLatestUri(value.getObjectUri()));
      });
      view.getValueSets().put(cachedVsCopy.getValueSetName(), cachedVsCopy);
      final boolean mandatory = permittedChildUris.isEmpty();
      view.getConstraint().getComponentConstraints().stream()
          .filter(c -> c.getDataName().contains(nextElement.getWidgetKey()))
          .filter(c -> Boolean.TRUE.equals(c.getMandatory()))
          .forEach(c -> c.setMandatory(mandatory));
      acceptableValues.clear();
      acceptableValues.addAll(permittedChildUris);

      blindAccept = lenient;
    }

  }

  private Set<URI> getPermittedNextValues(final View view,
      final MultiComboBoxElement element,
      final URI currValue,
      final MultiComboBoxElement nextElement) {
    if (currValue == null) {
      return Collections.emptySet();
    }

    final var result = mdmRelationApi
        .getRelations(
            objectApi.loadLatest(currValue, view.getBranchUri()),
            element.getRelation())
        .asList().stream()
        .map(objectApi::getLatestUri)
        .collect(Collectors.toCollection(HashSet::new));
    getSpecialPermittedValue(view, currValue).ifPresent(result::add);
    return result;
  }

  private ValueSet copyVs(ValueSet vs) {
    final ValueSetData data = vs.getValueSetData();
    return new ValueSet()
        .timestamp(vs.getTimestamp())
        .valueSetName(vs.getValueSetName())
        .valueSetData(new ValueSetData()
            .filterModel(data.getFilterModel())
            .iconCode(data.getIconCode())
            .keyProperty(data.getKeyProperty())
            .qualifiedName(data.getQualifiedName())
            .properties(data.getProperties() == null
                ? new ArrayList<>()
                : new ArrayList<>(data.getProperties()))
            .lazy(data.getLazy())
            .undefined(data.getUndefined())
            .values(data.getValues() == null
                ? new ArrayList<>()
                : new ArrayList<>(data.getValues())));
  }

  @Override
  public void onManagedWidgetAction(
      final View view,
      final UiActionRequest actionRequest,
      final ViewModelValueAccessor accessor) {
    final String widgetKey = actionRequest.getCode();
    cache(view)
        .get(widgetKey)
        .forEach(model -> enforceValueSets(view, model, accessor, widgetKey, false));
  }

}
