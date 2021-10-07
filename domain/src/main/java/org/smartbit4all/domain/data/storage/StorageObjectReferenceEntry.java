package org.smartbit4all.domain.data.storage;

import org.smartbit4all.api.storage.bean.ObjectReference;

public class StorageObjectReferenceEntry {

  private ObjectReference referenceData;

  private Object object;

  private Class<?> clazz;

  private boolean delete;

  StorageObjectReferenceEntry() {
    super();
    delete = true;
  }

  /**
   * The copy constructor
   * 
   * @param reference
   */
  StorageObjectReferenceEntry(StorageObjectReferenceEntry reference) {
    super();
    this.object = reference.object;
    this.clazz = reference.clazz;
    this.delete = reference.delete;
    if (reference.referenceData != null) {
      this.referenceData = new ObjectReference();
      this.referenceData
          .expirationTime(reference.referenceData.getExpirationTime())
          .referenceId(reference.referenceData.getReferenceId())
          .uri(reference.referenceData.getUri());
    }
    delete = true;
  }

  StorageObjectReferenceEntry(ObjectReference referenceData) {
    super();
    this.referenceData = referenceData;
    delete = referenceData == null;
  }

  StorageObjectReferenceEntry(Object object, Class<?> clazz) {
    super();
    this.object = object;
    this.clazz = clazz;
    delete = object == null;
  }

  public final ObjectReference getReferenceData() {
    return referenceData;
  }

  public final void setReferenceData(ObjectReference referenceData) {
    this.referenceData = referenceData;
  }

  public final Object getObject() {
    return object;
  }

  public final void setObject(Object object) {
    this.object = object;
  }

  public final Class<?> getClazz() {
    return clazz;
  }

  public final void setClazz(Class<?> clazz) {
    this.clazz = clazz;
  }

  public final boolean isDelete() {
    return delete;
  }

  public final void setDelete(boolean delete) {
    this.delete = delete;
  }

}
