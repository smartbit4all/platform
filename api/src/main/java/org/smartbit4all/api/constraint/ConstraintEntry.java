package org.smartbit4all.api.constraint;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
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
  private String context;

  /**
   * The scope of the given entry.
   */
  private ConstraintEntryScope scope;

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
  private boolean changed;

  /**
   * Construct a new entry with parent.
   * 
   * @param parent
   * @param context
   * @param scope
   * @param constraint
   */
  public ConstraintEntry(ConstraintEntry<C> parent, String context,
      ConstraintEntryScope scope, C constraint) {
    this(context, scope, constraint);
    this.parentRef = new WeakReference<>(parent);
    parent.children.put(context, this);
  }

  /**
   * Construct a new entry without parent.
   * 
   * @param context
   * @param scope
   * @param constraint
   */
  public ConstraintEntry(String context, ConstraintEntryScope scope, C constraint) {
    super();
    this.context = context;
    this.scope = scope;
    this.value = constraint;
  }

  public final String getContext() {
    return context;
  }

  public final void setContext(String context) {
    this.context = context;
  }

  public final ConstraintEntryScope getScope() {
    return scope;
  }

  public final void setScope(ConstraintEntryScope scope) {
    this.scope = scope;
  }

  public final C getValue() {
    return value;
  }

  public final void setValue(C constraint) {
    this.value = constraint;
  }

  public final C getOldValue() {
    return oldValue;
  }

  public final void setOldValue(C oldValue) {
    this.oldValue = oldValue;
  }

  public final boolean isChanged() {
    return changed;
  }

  public final void setChanged(boolean changed) {
    this.changed = changed;
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
    Object valueString = value == null ? StringConstant.SPACE_HYPHEN_SPACE : value;
    Object oldValueString = oldValue == null ? StringConstant.SPACE_HYPHEN_SPACE : oldValue;
    return (context == null || context.isEmpty() ? StringConstant.EMPTY
        : context + StringConstant.SPACE)
        + StringConstant.LEFT_PARENTHESIS + scope
        + StringConstant.SEMICOLON_SPACE
        + (changed
            ? (oldValueString
                + StringConstant.ARROW
                + valueString)
            : valueString)
        + StringConstant.RIGHT_PARENTHESIS;
  }

}
