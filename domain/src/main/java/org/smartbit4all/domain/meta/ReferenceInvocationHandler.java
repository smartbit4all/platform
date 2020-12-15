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
package org.smartbit4all.domain.meta;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.domain.data.DataColumn;

/**
 * <P>
 * This invocation handler manages a given reference of an entity. If we use this instance variable
 * to access the meta data of the referred entity then this handler will collect the join path into
 * a {@link ThreadLocal}. At the end of the join the last call will access the properties of the
 * last entity. This call returns the {@link DataColumn} definition list for the given properties.
 * </p>
 * <p>
 * It's important to have a clear API to add columns to an entity data table. These properties could
 * be used to access the properties in the data table.
 * </p>
 * 
 * @author Peter Boros
 *
 */
public class ReferenceInvocationHandler implements InvocationHandler {

  private static final ThreadLocal<List<Reference<?, ?>>> currentJoin = new ThreadLocal<>();

  Reference<?, ?> reference;

  public ReferenceInvocationHandler(Reference<?, ?> reference) {
    super();
    this.reference = reference;
  }

  /**
   *
   */
  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    /*
     * We need to decide if it's another reference or it's a property definition at the end.
     */
    List<Reference<?, ?>> joinPath = currentJoin.get();
    /*
     * Now we are navigating through a reference. We need to initialize the thread local list to
     * collect the join path. We add this reference to this list as the next join.
     */
    if (joinPath == null) {
      joinPath = new ArrayList<>(5);
      currentJoin.set(joinPath);
    }
    joinPath.add(reference);
    /*
     * We always delegate the method call. Depending on the type of the result we decide what to do.
     * If we have SB4Property or property list then we add the join path if it's necessary. The join
     * path is not necessary any more.
     */
    Object invokeResult = null;
    try {
      invokeResult = method.invoke(reference.getTarget(), args);
    } catch (Throwable e) {
      currentJoin.remove();
    }
    if (invokeResult == null) {
      // only EntityDefinition result would be able to continue chain -> break it
      currentJoin.remove();
      return null;
    }
    // refer properties
    if (invokeResult instanceof Property<?>) {
      EntityDefinition sourceEntity = joinPath.get(0).getSource();
      Property<?> referredProperty = (Property<?>) invokeResult;
      Property<?> property = findOrCreateRefProperty(sourceEntity, joinPath, referredProperty);
      currentJoin.remove();
      return property;
    }
    if (invokeResult instanceof PropertySet) {
      PropertySet result = new PropertySet();
      EntityDefinition sourceEntity = joinPath.get(0).getSource();
      for (Property<?> referredProperty : ((PropertySet) invokeResult)) {
        Property<?> property = findOrCreateRefProperty(sourceEntity, joinPath, referredProperty);
        result.add(property);
      }
      currentJoin.remove();
      return result;
    }
    if (!(invokeResult instanceof EntityDefinition)) {
      // only EntityDefinition result would be able to continue chain -> break it
      currentJoin.remove();
    }
    return invokeResult;
  }

  private Property<?> findOrCreateRefProperty(EntityDefinition sourceEntity,
      List<Reference<?, ?>> joinPath, Property<?> referredProperty) {
    Property<?> property = sourceEntity.findOrCreateReferredProperty(joinPath, referredProperty);
    return property;
  }

}
