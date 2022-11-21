package org.smartbit4all.core.object;

public class DetailBeanWithId {

  private String id;

  private String title;

  public DetailBeanWithId(String id, String title) {
    super();
    this.id = id;
    this.title = title;
  }

  public DetailBeanWithId() {
    super();
  }

  public DetailBeanWithId title(String title) {
    this.title = title;
    return this;
  }

  public DetailBeanWithId id(String id) {
    this.id = id;
    return this;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String toString() {
    return String.format("(id: %s, title: %s)", id, title);
  }

}
