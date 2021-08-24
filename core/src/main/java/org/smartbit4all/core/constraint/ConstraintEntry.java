package org.smartbit4all.core.constraint;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.smartbit4all.core.utility.StringConstant;

/**
 * The constraint entry is the item in the constraint hierarchy that defines a constraint and add
 * some instructions about the context. The context can be an exact scope or can be a subtree also.
 * 
 * @author Peter Boros
 *
 * @param <C> The constraint value itself.
 */
final class ConstraintEntry<C> {

  /**
   * The parent entry reference.
   */
  WeakReference<ConstraintEntry<C>> parentRef;

  /**
   * The children constraint entries mapped by the context inside our context. If we have a longer
   * path then it will appear only in one node with the full path as context. So the lookup will
   * search among the children expanding the context with the fragments of the path one by one until
   * we find a match.
   */
  Map<String, ConstraintEntry<C>> children = new HashMap<>();

  /**
   * The path that the constraint is related to.
   */
  private final String path;

  /**
   * If we change the scope of the scope of the given entry we save the old value.
   */
  private ConstraintEntryScope oldScope;

  /**
   * The scope of the given entry.
   */
  private ConstraintEntryScope scope;

  /**
   * The changed flag is set if the scope is new or modified.
   */
  private boolean scopeChanged;

  /**
   * If we change the constraint then we save the old value.
   */
  private C oldValue;

  /**
   * The value of the constraint. If null then it doesn't have value so the parent value will be
   * applied to.
   */
  private C value;

  /**
   * The changed flag is set if the given constraint is new or modified.
   */
  private boolean valueChanged;

  /**
   * Construct a new entry with parent.
   * 
   * @param parent
   * @param name
   * @param scope
   * @param constraint
   */
  public ConstraintEntry(ConstraintEntry<C> parent, String name,
      ConstraintEntryScope scope, C constraint) {
    if (parent != null) {
      this.parentRef = new WeakReference<>(parent);
      parent.children.put(name, this);
      path = parent.getPath().isEmpty() ? name : parent.getPath() + StringConstant.SLASH + name;
    } else {
      this.path = StringConstant.EMPTY;
    }
    this.scope = scope;
    this.value = constraint;
  }

  /**
   * Construct a new entry without parent. It's a root that is {@link ConstraintEntryScope#SUBTREE}
   * and must have a value to serve as default value for the given constraint.
   * 
   * @param constraint
   */
  public ConstraintEntry(C constraint) {
    super();
    this.path = StringConstant.EMPTY;
    this.scope = ConstraintEntryScope.SUBTREE;
    this.value = constraint;
  }

  public final ConstraintEntryScope getScope() {
    return scope;
  }

  public final void setScope(ConstraintEntryScope scope) {
    if (!scopeChanged && this.scope != scope) {
      oldScope = this.scope;
      scopeChanged = true;
    }
    if (scopeChanged && this.scope == scope) {
      scopeChanged = false;
      oldScope = null;
    }
    this.scope = scope;
  }

  public final C getValue() {
    return value;
  }

  public final void setValue(C value) {
    boolean equal = Objects.equals(this.value, value);
    if (!valueChanged && !equal) {
      oldValue = this.value;
      valueChanged = true;
    }
    if (valueChanged && equal) {
      valueChanged = false;
      oldValue = null;
    }
    this.value = value;
  }

  /**
   * Recursive function to print the whole tree into an indented text.
   * 
   * @param b The builder to append.
   * @param indent The current indent.
   */
  public void printContent(StringBuilder b, String indent) {
    if (b.length() > 0) {
      b.append(StringConstant.NEW_LINE).append(indent);
    }
    b.append(toString());
    children.entrySet().stream().sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
        .forEach(e -> e.getValue().printContent(b, indent + StringConstant.TAB));
  }

  @Override
  public String toString() {
    String[] split = path.split(StringConstant.SLASH);
    return (split[split.length - 1].isEmpty() ? StringConstant.EMPTY
        : split[split.length - 1] + StringConstant.SPACE)
        + StringConstant.LEFT_PARENTHESIS
        + (scopeChanged ? oldScope + StringConstant.ARROW + scope : scope)
        + StringConstant.SEMICOLON_SPACE
        + (valueChanged
            ? (oldValue
                + StringConstant.ARROW
                + value)
            : value)
        + StringConstant.RIGHT_PARENTHESIS;
  }

  public final ConstraintEntryScope getOldScope() {
    return oldScope;
  }

  public final boolean isScopeChanged() {
    return scopeChanged;
  }

  public final void setScopeChanged(boolean scopeChanged) {
    this.scopeChanged = scopeChanged;
  }

  public final boolean isValueChanged() {
    return valueChanged;
  }

  public final void setValueChanged(boolean valueChanged) {
    this.valueChanged = valueChanged;
  }

  public final boolean isChanged() {
    return valueChanged || scopeChanged;
  }

  public final ConstraintEntryScope getScopePrevious() {
    return scopeChanged ? oldScope : scope;
  }

  public final C getValuePrevious() {
    return valueChanged ? oldValue : value;
  }

  public final ConstraintChange<C> getValueChange() {
    if (isValueChanged()) {
      return new ConstraintChange<>(path, value);
    }
    return null;
  }

  /**
   * Go up in the tree until reach the root or find a relevant changed entry. It must be the first
   * parent with children scope or any upper ancestor with sub tree scope.
   * 
   * @return
   */
  public final ConstraintEntry<C> getChangedParentEntry() {
    ConstraintEntry<C> parentEntry = parentRef.get();
    if (parentEntry != null && parentEntry.isChanged()
        && parentEntry.scope == ConstraintEntryScope.CHILDREN) {
      return parentEntry;
    }
    parentEntry = parentEntry.parentRef.get();
    while (parentEntry != null) {
      if (parentEntry.isChanged() && parentEntry.scope == ConstraintEntryScope.SUBTREE) {
        return parentEntry;
      }
      parentEntry = parentEntry.parentRef.get();
    }
    return null;
  }

  public final String getPath() {
    return path;
  }

  void commitChange() {
    scopeChanged = false;
    oldScope = null;
    valueChanged = false;
    oldValue = null;
    for (ConstraintEntry<C> childEntry : children.values()) {
      childEntry.commitChange();
    }
  }

}
