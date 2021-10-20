package org.smartbit4all.api.navigation;

public class ResolvedValue {

  private Object object;
  private String message;
  private Throwable throwable;

  public ResolvedValue() {}

  public ResolvedValue(Object object) {
    this.object = object;
  }

  public Object getObject() {
    return object;
  }

  public void setObject(Object object) {
    this.object = object;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Throwable getThrowable() {
    return throwable;
  }

  public void setThrowable(Throwable throwable) {
    this.throwable = throwable;
  }

}
