package org.smartbit4all.core.utility;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.core.object.ObjectNode;

public class TextToNodeFieldProcessor {
  private static final Logger log = LoggerFactory.getLogger(TextToNodeFieldProcessor.class);

  public TextToNodeFieldProcessor() {
    super();
  }

  boolean setFieldFromText(String text, String regex, Function<String, Object> converter,
      ObjectNode node,
      String... path) {
    Objects.requireNonNull(text, "text cannot be null!");
    Objects.requireNonNull(regex, "regex cannot be null!");
    Objects.requireNonNull(node, "node cannot be null!");
    Objects.requireNonNull(path, "path cannot be null!");
    Boolean madeModify = false;

    String value = getValue(text, regex);
    if (value == null) {
      log.debug("It is not possible to find the data based on regex");
      return madeModify;
    }

    if (converter != null) {
      Object convertedValue = converter.apply(value);
      node.setValue(convertedValue, path);
      madeModify = true;
    } else {
      node.setValue(value, path);
      madeModify = true;
    }
    return madeModify;
  }


  boolean setFieldFromText(String text, ObjectNode node,
      List<TextProcessorDescriptor> descriptors) {
    Objects.requireNonNull(text, "text cannot be null!");
    Objects.requireNonNull(node, "node cannot be null!");
    Objects.requireNonNull(descriptors, "descriptors cannot be null!");
    Boolean madeModify = false;
    for (TextProcessorDescriptor descriptor : descriptors) {

      Class<?> clazz;
      Object newInstance = null;
      String value = getValue(text, descriptor.getRegex());

      if (value == null) {
        log.debug("It is not possible to find the data based on regex");
        continue;
      }

      try {
        clazz = Class.forName(descriptor.getConverterQualifiedName());
        Constructor<?> constructor = clazz.getConstructor(String.class);
        newInstance = constructor.newInstance(value);
      } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException
          | InstantiationException | InvocationTargetException e) {
        log.debug("No such class with QualifiedName exists", e);
        continue;
      }

      node.setValue(newInstance, descriptor.path);
      madeModify = true;
    }
    return madeModify;
  }

  private String getValue(String text, String regex) {
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(text);
    if (matcher.find()) {
      String value = matcher.group(0);
      return value;
    }
    return null;
  }

  public static class TextProcessorDescriptor {
    private String converterQualifiedName;
    private String[] path;
    private String regex;

    public TextProcessorDescriptor(String converterQualifiedName, String regex, String... path) {
      this.converterQualifiedName = converterQualifiedName;
      this.regex = regex;
      this.path = path;
    }

    public String getConverterQualifiedName() {
      return converterQualifiedName;
    }

    public String[] getPath() {
      return path;
    }

    public String getRegex() {
      return regex;
    }



  }
}
