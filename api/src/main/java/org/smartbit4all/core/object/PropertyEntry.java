package org.smartbit4all.core.object;

import org.smartbit4all.core.object.PropertyMeta.PropertyKind;
import org.smartbit4all.core.utility.PathUtility;
import org.smartbit4all.core.utility.StringConstant;

/**
 * The property entry is an inner class for the {@link ApiObjectRef}. It summarizes the
 * {@link PropertyMeta} and the value for the given property.
 * 
 * @author Peter Boros
 */
public final class PropertyEntry {

  PropertyEntry(String parentPath, PropertyMeta meta) {
    super();
    this.meta = meta;
    this.path = parentPath == null || parentPath.isEmpty() ? meta.getName()
        : (parentPath + StringConstant.SLASH + meta.getName());
  }

  /**
   * The meta of the given entry.
   */
  private final PropertyMeta meta;

  /**
   * The path of the given property. The parent path / {@link PropertyMeta#getName()}
   */
  private final String path;

  /**
   * If the meta defines a reference {@link PropertyKind#REFERENCE} then it's the
   * {@link ApiObjectRef} for the given reference.
   */
  DomainObjectRef reference;

  /**
   * The collection reference of the given property if it's a {@link PropertyKind#COLLECTION}.
   */
  ApiObjectCollection collection;

  /**
   * The map reference of the given property if it's a {@link PropertyKind#MAP}.
   */
  ApiObjectMap map;

  /**
   * In case of {@link PropertyKind#VALUE} if we change the value this change will
   */
  PropertyChange valueChange = null;


  public final PropertyMeta getMeta() {
    return meta;
  }

  public final DomainObjectRef getReference() {
    return reference;
  }

  public final void setReference(DomainObjectRef reference) {
    this.reference = reference;
  }

  public final ApiObjectCollection getCollection() {
    return collection;
  }

  public final void setCollection(ApiObjectCollection collection) {
    this.collection = collection;
  }

  void setChangedValue(Object oldValue, Object newValue) {
    if (valueChange == null) {
      valueChange =
          new PropertyChange(PathUtility.getParentPath(path), meta.getName(), oldValue, newValue);
    } else {
      valueChange.setNewValue(newValue);
    }
  }

  public final PropertyChange getValueChange() {
    return valueChange;
  }

  public void clearValueChange() {
    valueChange = null;
  }

  public final String getPath() {
    return path;
  }

  public final void setMap(ApiObjectMap map) {
    this.map = map;
  }

  public final ApiObjectMap getMap() {
    return map;
  }

}
