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
package org.smartbit4all.domain.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.smartbit4all.core.utility.StringConstant;

/**
 * This property refers another one that is owned by a related entity. It contains the list of
 * {@link Reference} that defines how to joins the given entity.
 * 
 * @author Peter Boros
 *
 * @param <T>
 */
public class PropertyRef<T> extends Property<T> {


  /**
   * The join path leading to the referred property. If the referred property is also a
   * {@link PropertyRef}, this joinPath will include the full join path leading to the end of the
   * property chain.
   */
  private JoinPath joinPath;

  /**
   * This is the referred property. It can be any kind ({@link PropertyOwned}, {@link PropertyRef},
   * {@link PropertyComputed}).
   * 
   */
  private Property<T> referredProperty;

  /**
   * Creates a reference property, where the reference is through the join path, to the
   * referredProperty.
   * 
   * @param joinPath
   * @param referredProperty
   */
  public PropertyRef(String name, List<Reference<?, ?>> joinPath, Property<T> referredProperty) {
    super((name == null || name.isEmpty()) ? PropertyRef.constructName(joinPath, referredProperty)
        : name, referredProperty.type(), referredProperty.jdbcConverter());
    if (referredProperty instanceof PropertyOwned || referredProperty instanceof PropertyComputed) {
      this.joinPath = new JoinPath(joinPath);
    } else if (referredProperty instanceof PropertyRef) {
      List<Reference<?, ?>> firstPart = joinPath;
      List<Reference<?, ?>> secondPart = ((PropertyRef<T>) referredProperty).getJoinReferences();
      List<Reference<?, ?>> allPath = new ArrayList<>(firstPart);
      allPath.addAll(secondPart);
      this.joinPath = new JoinPath(allPath);
    }
    this.referredProperty = referredProperty;
  }

  /**
   * This utility construct the name of a referred property from the join path and the name of the
   * finally referred property. The names are separated with dot.
   * 
   * @param joinPath
   * @param referredProperty
   * @return
   */
  public static final String constructName(List<Reference<?, ?>> joinPath,
      Property<?> referredProperty) {
    String[] joinPathNames = joinPath.stream()
        .map(r -> r.getName())
        .collect(Collectors.toList())
        .toArray(new String[joinPath.size()]);
    return constructName(joinPathNames, referredProperty.getName());
  }

  public static final String constructName(String[] joinPath,
      String referredPropertyName) {
    /*
     * The name of the referenced column comes from the join path plus the name of the referred
     * property at the end of the path.
     */
    StringBuilder sb = new StringBuilder();
    sb.append(String.join(StringConstant.DOT, joinPath));
    sb.append(joinPath == null || joinPath.length == 0 ? StringConstant.EMPTY : StringConstant.DOT);
    sb.append(referredPropertyName);
    return sb.toString();
  }

  /**
   * The references which lead to this property.
   * 
   * @return
   */
  public final List<Reference<?, ?>> getJoinReferences() {
    return joinPath.references;
  }

  /**
   * This is the referred property from the end of the join path. It can be a {@link PropertyOwned},
   * or a {@link PropertyRef} as well. The referred property would be merged into this join.
   * 
   * @return
   */
  public final Property<T> getReferredProperty() {
    return referredProperty;
  }

  /**
   * Returns the owned property at the end of the reference chain (join path).
   * 
   * @return
   */
  public final PropertyOwned<T> getReferredOwnedProperty() {
    if (referredProperty instanceof PropertyOwned) {
      return (PropertyOwned<T>) referredProperty;
    } else if (referredProperty instanceof PropertyRef) {
      return ((PropertyRef<T>) referredProperty).getReferredOwnedProperty();
    } else {
      // TODO computed property has no single owned property
      return null;
    }
  }

  /**
   * The join path is a wrapper object containing the references which lead to this property.
   * 
   * @return
   */
  public JoinPath getJoinPath() {
    return joinPath;
  }

}
