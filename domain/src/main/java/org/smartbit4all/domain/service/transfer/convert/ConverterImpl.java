package org.smartbit4all.domain.service.transfer.convert;

import java.util.function.Function;
import org.smartbit4all.domain.service.transfer.TransferServiceImpl;

/**
 * The basic implementation. We extend this in the configurations.
 * 
 * @author Peter Boros
 */
public class ConverterImpl<F extends Object, T extends Object> implements Converter<F, T> {

  /**
   * The convert to function.
   */
  protected Function<F, T> toward;

  /**
   * The class of the to value.
   */
  protected Class<T> toClass;

  /**
   * The convert from function.
   */
  protected Function<T, F> backward;

  /**
   * The class of the from value.
   */
  protected Class<F> fromClass;

  /**
   * The name of the converter.
   */
  protected String name;

  public ConverterImpl(Class<T> toClass,
      Function<F, T> toward, Class<F> fromClass, Function<T, F> backward) {
    super();
    this.toward = toward;
    this.backward = backward;
    this.fromClass = fromClass;
    this.toClass = toClass;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Function<Object, Object> toward() {
    return (Function<Object, Object>) toward;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Function<Object, Object> backward() {
    return (Function<Object, Object>) backward;
  }

  @Override
  public Class<F> fromType() {
    return fromClass;
  }

  @Override
  public Class<T> toType() {
    return toClass;
  }

  @Override
  public String name() {
    return name == null ? TransferServiceImpl.generateName(fromClass, toClass) : name;
  }



  /**
   * Builder API for setting the name of the conversion. More readable in the configuration.
   * Something like this:
   * 
   * <p/>
   * 
   * <code>
   * return new ConverterImpl(...).name("MyFavoriteConversion");
   * </code>
   * 
   * @param name
   * @return
   */
  public ConverterImpl<F, T> name(String name) {
    this.name = name;
    return this;
  }

}
