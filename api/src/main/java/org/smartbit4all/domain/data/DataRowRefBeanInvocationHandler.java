/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.domain.data;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import org.smartbit4all.domain.data.TableDataBeanBinding.DataBind;

/**
 * This invocation handler serves the {@link TableData#beansByRef(Class)} function to access a row
 * with the bean getter and setter operations.
 * 
 * @author Peter Boros
 */
final class DataRowRefBeanInvocationHandler implements InvocationHandler {

  TableDataBeanBinding binding;

  DataRow row;

  /**
   * Construct an invocation handler that is bound with the given {@link TableData} instance. We can
   * use this to iterate with this typed interface.
   * 
   */
  DataRowRefBeanInvocationHandler(TableDataBeanBinding binding, DataRow row) {
    this.binding = binding;
    this.row = row;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // If it's getter or setter then it will be served based on the current row.
    DataBind dataBind = binding.bindMap.get(method);
    if (dataBind != null) {
      if (dataBind.getter) {
        Object result = binding.td.get(dataBind.column, row);
        if (dataBind.converter != null) {
          result = dataBind.converter.backward().apply(result);
        }
        return result;
      } else {
        // We set the first argument as new value for the given property
        binding.td.setObject(dataBind.column, row,
            dataBind.converter != null ? dataBind.converter.toward().apply(args[0]) : args[0]);
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
