package org.smartbit4all.api.mimetype;

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.binarydata.BinaryData;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Collects the {@link Converter}s available in the current application. They can be used to convert
 * a given content from one mimeType to another.
 * 
 * @author András Palló
 *
 */
public class ConverterApi {

  @Autowired(required = false)
  private List<Converter> converters;

  public ConverterApi() {}

  public Converter get(String from, String to) {
    if (from.equals(to)) {
      return null;
    }
    if (converters != null) {
      for (Converter converter : converters) {
        if (converter.getFrom().equals(from) && converter.getTo().equals(to)) {
          return converter;
        }
      }
    }
    return null;
  }

  public List<Converter> getAllFrom(String from) {
    List<Converter> foundConverters = new ArrayList<>();
    if (converters != null) {
      for (Converter converter : converters) {
        if (converter.getFrom().equals(from)) {
          foundConverters.add(converter);
        }
      }
    }
    return foundConverters;
  }

  public BinaryData convert(BinaryData content, String from, String to) {
    if (from.equals(to)) {
      return content;
    }

    if (converters != null) {
      for (Converter converter : converters) {
        if (converter.getFrom().equals(from) && converter.getTo().equals(to)) {
          return converter.convert(content);
        }
      }
    }
    return null;
  }
}
