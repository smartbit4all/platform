package org.smartbit4all.api.invocation;

public final class InvocationParameter {

  private Kind kind;

  private String typeClass;

  private String stringValue;

  private transient Object value;

  public static enum Kind {
    BYVALUE, BYREFERENCE
  }

  public final Kind getKind() {
    return kind;
  }

  public final void setKind(Kind kind) {
    this.kind = kind;
  }

  public final String getTypeClass() {
    return typeClass;
  }

  public final void setTypeClass(String typeClass) {
    this.typeClass = typeClass;
  }

  public final String getStringValue() {
    return stringValue;
  }

  public final void setStringValue(String stringValue) {
    this.stringValue = stringValue;
  }

  public final Object getValue() {
    return value;
  }

  public final void setValue(Object value) {
    this.value = value;
  }

}
