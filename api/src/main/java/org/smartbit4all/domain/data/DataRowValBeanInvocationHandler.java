/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.domain.data;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.smartbit4all.domain.data.TableDataBeanBinding.DataBind;
import org.smartbit4all.domain.service.transfer.convert.Converter;

/**
 * This bean invocation handler is a copy of the given row and holds the values in a {@link Map}. If
 * we set the value then we only write this copy.
 * 
 * @author Peter Boros
 */
public class DataRowValBeanInvocationHandler implements InvocationHandler {

  /**
   * The value box for the bean properties.
   * 
   * @author Peter Boros
   */
  static final class PropertyValue {

    Object value;

    public PropertyValue(Object value) {
      super();
      this.value = value;
    }

  }

  /**
   * The values map contains the
   */
  final Map<Method, PropertyValue> values;

  DataRowValBeanInvocationHandler(TableDataBeanBinding binding, DataRow row) {
    values = new HashMap<>();
    Map<DataColumn<?>, PropertyValue> valueByColumn = new HashMap<>();
    for (Entry<Method, DataBind> entry : binding.bindMap.entrySet()) {
      DataColumn<?> column = entry.getValue().column;
      PropertyValue propertyValue = valueByColumn.get(column);
      if (propertyValue == null) {
        Object value = binding.td.get(column, row);
        Converter<?, ?> converter = entry.getValue().converter;
        // The property should be converted if we have any conversion.
        propertyValue =
            new PropertyValue(converter != null ? converter.backward().apply(value) : value);
        valueByColumn.put(column, propertyValue);
      }
      values.put(entry.getKey(), propertyValue);
    }

  }


  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    PropertyValue propertyValue = values.get(method);
    if (propertyValue != null) {
      if (method.getParameterCount() == 0) {
        // There is no parameter so it's a getter. Let's return the value itself.
        return propertyValue.value;
      } else if (method.getParameterCount() == 1) {
        // There is exactly one parameter. We set the value.
        propertyValue.value = args[0];
        // For the fluid API support we return the proxy itself.
        if (!method.getReturnType().equals(Void.class)) {
          return proxy;
        }
      }
    }
    // It's not a bean property accessor method. Do nothing!
    return null;
  }

}
