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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.meta.Reference.Join;

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
   * JoinPath represents all the information which is used for handling a referenced property.
   * Typically it includes the list of references leading to the referred property.
   * 
   * @author Attila Mate
   *
   */
  public static class JoinPath {

    /**
     * The join path from the references between the entities.
     */
    List<Reference<?, ?>> references = Collections.emptyList();

    public JoinPath(List<Reference<?, ?>> references) {
      this.references = references;
    }

    public Reference<?, ?> last() {
      return references.size() == 0 ? null : references.get(references.size() - 1);
    }

    /**
     * Creates an expression representing join expression based on last reference in join path.
     * 
     * @param record Record containing targetProperty values on last reference in join path.
     * @return
     */
    public Expression joinExpression(DataRow record) {
      ExpressionClause andClause = Expression.createAndClause();
      Reference<?, ?> lastRef = last();
      if (references.size() == 1) {
        for (Join<?> join : lastRef.joins()) {
          andClause.add(join.eq(record));
        }
      } else {
        List<Reference<?, ?>> joinPath = references.subList(0, references.size() - 1);
        for (Join<?> join : lastRef.joins()) {
          andClause.add(join.eq(joinPath, record));
        }
      }
      return andClause;
    }

    /**
     * Creates an expression representing join expression based on last reference in join path.
     * 
     * @param records Collection of Records containing targetProperty values on last reference in
     *        join path.
     * @return
     */
    public Expression joinExpression(Collection<? extends DataRow> records) {
      Objects.requireNonNull(records);
      ExpressionClause andClause = Expression.createAndClause();
      Reference<?, ?> lastRef = last();
      if (references.size() == 1) {
        for (Join<?> join : lastRef.joins()) {
          andClause.add(join.in(records));
        }
      } else {
        List<Reference<?, ?>> joinPath = references.subList(0, references.size() - 1);
        for (Join<?> join : lastRef.joins()) {
          andClause.add(join.in(joinPath, records));
        }
      }
      return andClause;
    }

  }

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
    /*
     * The name of the referenced column comes from the join path plus the name of the referred
     * property at the end of the path.
     */
    StringBuilder sb = new StringBuilder();
    for (Reference<?, ?> reference : joinPath) {
      sb.append(reference.getName());
      sb.append(".");
    }
    sb.append(referredProperty.getName());
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
