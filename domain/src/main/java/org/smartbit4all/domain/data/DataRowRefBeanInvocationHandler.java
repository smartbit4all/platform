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
   * @param td The table data instance.
   * @param beanClazz The bean class we have.
   */
  DataRowRefBeanInvocationHandler(TableDataBeanBinding binding, DataRow row) {
    this.binding = binding;
    this.row = row;
  }

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
