package org.smartbit4all.api.mimetype;

public abstract class ConverterImpl implements Converter {
  
  protected String from;
  protected String to;
  
  protected ConverterImpl() {
    super();
  }

  @Override
  public String getFrom() {
    return from;
  }

  @Override
  public String getTo() {
    return to;
  }

}
