package org.smartbit4all.api.invocation;

import org.smartbit4all.api.invocation.bean.InvocationParameterKind;

public class InvocationParameter {

  private InvocationParameterKind kind;

  private String typeClass;

  private String stringValue;

  private transient Object value;

  final InvocationParameterKind getKind() {
    return kind;
  }

  final void setKind(InvocationParameterKind kind) {
    this.kind = kind;
  }

  final String getTypeClass() {
    return typeClass;
  }

  final void setTypeClass(String typeClass) {
    this.typeClass = typeClass;
  }

  final String getStringValue() {
    return stringValue;
  }

  final void setStringValue(String stringValue) {
    this.stringValue = stringValue;
  }

  final Object getValue() {
    return value;
  }

  final void setValue(Object value) {
    this.value = value;
  }

}
