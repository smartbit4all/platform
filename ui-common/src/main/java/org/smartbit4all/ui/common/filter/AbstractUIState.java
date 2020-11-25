package org.smartbit4all.ui.common.filter;

import java.util.UUID;

public abstract class AbstractUIState {

  protected String id;

  public AbstractUIState() {
    this.id = UUID.randomUUID().toString();
  }

  public String getId() {
    return id;
  }

}
