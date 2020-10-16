package org.smartbit4all.ui.vaadin.util.sb4;

import java.util.Map;
import java.util.function.Function;

import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.ui.vaadin.components.selector.MultiSelectPopUp;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;


public interface VaadinComponentFactory {
	
	
	ComboBox<DataRow> createComboBox(Property<?> displayProperty) throws Exception;


	MultiSelectPopUp<DataRow> createMultiSelectPopUp(Property<?> displayProperty) throws Exception;

	<T extends EntityDefinition> Grid<DataRow> createGrid(Map<String, Function<T, Property<?>>> columns, T entityDefinition) throws Exception;
}
