package org.smartbit4all.ui.vaadin.util.sb4;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertyRef;
import org.smartbit4all.domain.meta.Reference;
import org.smartbit4all.domain.service.query.Query;
import org.smartbit4all.ui.vaadin.components.selector.MultiSelectPopUp;
import org.springframework.stereotype.Service;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;


@Service
public class VaadinComponentFactoryImpl implements VaadinComponentFactory {

  @Override
  public ComboBox<DataRow> createComboBox(Property<?> displayProperty) throws Exception {
    if (!(displayProperty instanceof PropertyRef)) {
      throw new RuntimeException("Only PropertyRef can be displayed as referred element!");
    }
    PropertyRef<?> refProperty = (PropertyRef<?>) displayProperty;

    List<DataRow> values = queryValues(refProperty);

    Property<?> labelProperty = refProperty.getReferredOwnedProperty();
    ComboBox<DataRow> comboBox = createCB(values, labelProperty);
    return comboBox;
  }

  @Override
  public MultiSelectPopUp<DataRow> createMultiSelectPopUp(Property<?> displayProperty)
      throws Exception {
    if (!(displayProperty instanceof PropertyRef)) {
      throw new RuntimeException("Only PropertyRef can be displayed as referred element!");
    }
    PropertyRef<?> refProperty = (PropertyRef<?>) displayProperty;

    List<DataRow> values = queryValues(refProperty);

    Property<?> labelProperty = refProperty.getReferredOwnedProperty();
    MultiSelectPopUp<DataRow> multiSelect = createMS(values, labelProperty);
    return multiSelect;
  }

  private List<DataRow> queryValues(PropertyRef<?> refProperty) throws Exception {
    List<DataRow> values = null;
    List<Reference<?, ?>> references = refProperty.getJoinReferences();
    Reference<?, ?> ref = references.get(references.size() - 1);
    EntityDefinition targetDef = ref.getTarget();

    List<Property<?>> properties = ref.joins().stream().map(join -> join.getTargetProperty())
        .collect(Collectors.toList());
    properties.add(refProperty.getReferredOwnedProperty());

    // TODO create entity service from query

    Query query = targetDef.services().crud().query();
    TableData<?> result = new TableData<>(targetDef);
    query.from(targetDef).select(properties).into(result).execute();
    values = result.rows();
    return values;
  }

  private ComboBox<DataRow> createCB(Collection<DataRow> values, Property<?> labelProperty) {
    ComboBox<DataRow> comboBox = new ComboBox<>();
    comboBox.setItems(values);
    // TODO Property<?> could be something else, maybe rendered to a component...
    comboBox.setItemLabelGenerator(item -> String.valueOf(item.get(labelProperty)));
    return comboBox;
  }

  private MultiSelectPopUp<DataRow> createMS(Collection<DataRow> values,
      Property<?> labelProperty) {
    MultiSelectPopUp<DataRow> multiSelect = new MultiSelectPopUp<>();
    multiSelect.setItems(values);
    multiSelect.setItemDisplayValueProvider(item -> item.get(labelProperty));
    return multiSelect;
  }

  @Override
  public <T extends EntityDefinition> Grid<DataRow> createGrid(
      Map<String, Function<T, Property<?>>> columns, T entityDefinition)
      throws Exception {
    Grid<DataRow> grid = new Grid<>();
    for (Entry<String, Function<T, Property<?>>> entry : columns.entrySet()) {
      Property<?> prop = entry.getValue().apply(entityDefinition);
      String headerLabel = entry.getKey();
      grid.addColumn(row -> row.get(prop))
          .setHeader(grid.getTranslation(headerLabel))
          .setAutoWidth(true)
          .setSortable(true);
    }
    return grid;
  }

}
