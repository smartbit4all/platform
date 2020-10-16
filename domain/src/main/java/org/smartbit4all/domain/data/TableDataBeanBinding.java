package org.smartbit4all.domain.data;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.smartbit4all.domain.config.DomainAPI;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.service.transfer.BeanEntityBinding;
import org.smartbit4all.domain.service.transfer.BeanEntityBinding.Binding;
import org.smartbit4all.domain.service.transfer.convert.Converter;

/**
 * This class is a map construction between the {@link TableData} and a bean class.
 * 
 * @author Peter Boros
 */
class TableDataBeanBinding {

  static final class DataBind {

    /**
     * The column reference for the {@link TableData} instance.
     */
    DataColumn<?> column;

    /**
     * True if it's a getter method and false if a setter.
     */
    boolean getter;

    /**
     * TODO It will be the key to convert the values of the domain for the given bean interface.
     */
    Converter<?, ?> converter;

    /**
     * Constructor
     * 
     * @param column
     * @param getter
     * @param converter
     */
    DataBind(DataColumn<?> column, boolean getter, Converter<?, ?> converter) {
      super();
      this.column = column;
      this.getter = getter;
      this.converter = converter;
    }

  }

  TableData<?> td;

  /**
   * The bind map that contains every method of the given bean with the bind information. The column
   * that we set and the conversion if we have any.
   */
  Map<Method, DataBind> bindMap = new HashMap<>();

  /**
   * Construct an invocation handler that is bound with the given {@link TableData} instance. We can
   * use this to iterate with this typed interface.
   * 
   * @param td The table data instance.
   * @param beanClazz The bean class we have.
   */
  TableDataBeanBinding(TableData<? extends EntityDefinition> td, Class<?> beanClazz)
      throws Exception {
    this.td = td;
    // We bind the table data to the bean. We need the descriptor for this.
    BeanEntityBinding binding = DomainAPI.get().transferService().binding(beanClazz, td.entity());
    // We assume the properties that has getter. We would like to iterate firstly and without getter
    // it's not easy.
    for (Entry<Property<?>, List<Binding>> entry : binding.bindingsByProperties().entrySet()) {
      DataColumn<?> column = td.getColumn(entry.getKey());
      if (column != null) {
        List<Binding> bindings = entry.getValue();
        for (Binding propertyBinding : bindings) {
          bindMap.put(propertyBinding.getter,
              new DataBind(column, true, propertyBinding.converter));
          bindMap.put(propertyBinding.setter,
              new DataBind(column, false, propertyBinding.converter));
        }
      }
    }
  }

}
