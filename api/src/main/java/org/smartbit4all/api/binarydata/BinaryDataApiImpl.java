package org.smartbit4all.api.binarydata;

public abstract class BinaryDataApiImpl implements BinaryDataApi {

  protected String name;

  protected BinaryDataApiImpl(String name) {
    super();
    this.name = name;
  }

  @Override
  public final String name() {
    return name;
  }

}
