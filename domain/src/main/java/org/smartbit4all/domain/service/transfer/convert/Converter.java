package org.smartbit4all.domain.service.transfer.convert;

import java.util.function.Function;

/**
 * The converter is binding two {@link Function} that makes the conversion to and from between the
 * types. The conversion could be named and in this case it's a symbolic name that can be used if we
 * would like to refer to the conversion. Otherwise we can find the default conversion between the
 * types. The default is the unnamed version.
 * 
 * @author Peter Boros
 *
 */
public interface Converter<F extends Object, T extends Object> {

  /**
   * The name of the converter. Could be a manually set name but also a name generated from the
   * types converted by the instance.
   * 
   * @return
   */
  String name();

  /**
   * The converter function to produce the target value from the source value.
   * 
   * @return
   */
  Function<Object, Object> toward();

  /**
   * The converter function to produce the source value from the target value.
   * 
   * @return
   */
  Function<Object, Object> backward();

  Class<F> fromType();

  Class<T> toType();

}
