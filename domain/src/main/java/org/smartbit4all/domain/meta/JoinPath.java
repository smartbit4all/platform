package org.smartbit4all.domain.meta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.meta.Reference.Join;

/**
 * JoinPath represents all the information which is used for handling a referenced property.
 * Typically it includes the list of references leading to the referred property.
 * 
 * @author Attila Mate
 *
 */
public class JoinPath {

  /**
   * The empty join path constant.
   */
  public static final JoinPath EMPTY = new JoinPath(Collections.emptyList());

  /**
   * The join path from the references between the entities.
   */
  final List<Reference<?, ?>> references;

  public JoinPath(List<Reference<?, ?>> references) {
    this.references = references;
  }

  public Reference<?, ?> last() {
    return references.isEmpty() ? null : references.get(references.size() - 1);
  }

  public Reference<?, ?> first() {
    return references.isEmpty() ? null : references.get(0);
  }

  public EntityDefinition firstEntity() {
    Reference<?, ?> first = first();
    if (first == null) {
      return null;
    }
    Iterator<Join<?>> iterJoin = first.joins.iterator();
    if (iterJoin.hasNext()) {
      Join<?> join = iterJoin.next();
      return join.getSourceProperty().getEntityDef();
    }
    return null;
  }

  public EntityDefinition lastEntity() {
    Reference<?, ?> last = last();
    if (last == null) {
      return null;
    }
    Iterator<Join<?>> iterJoin = last.joins.iterator();
    if (iterJoin.hasNext()) {
      Join<?> join = iterJoin.next();
      return join.getTargetProperty().getEntityDef();
    }
    return null;
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
   * @param records Collection of Records containing targetProperty values on last reference in join
   *        path.
   * @return
   */
  public Expression joinExpression(Collection<? extends DataRow> records) {
    Objects.requireNonNull(records);
    ExpressionClause andClause = Expression.createAndClause();
    Reference<?, ?> lastRef = last();
    if (references.size() == 1) {
      for (Join<?> join : lastRef.joins()) {
        andClause.add(join.inDetail(records));
      }
    } else {
      List<Reference<?, ?>> joinPath = references.subList(0, references.size() - 1);
      for (Join<?> join : lastRef.joins()) {
        andClause.add(join.in(joinPath, records));
      }
    }
    return andClause;
  }

  public final List<Reference<?, ?>> getReferences() {
    return Collections.unmodifiableList(references);
  }

  public final List<Reference<?, ?>> getReferencesWithoutLast() {
    List<Reference<?, ?>> result = new ArrayList<>();
    for (int i = 0; i < references.size() - 1; i++) {
      result.add(references.get(i));
    }
    return result;
  }

}
