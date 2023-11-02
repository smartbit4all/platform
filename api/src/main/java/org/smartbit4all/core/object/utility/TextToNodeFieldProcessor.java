package org.smartbit4all.core.object.utility;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.core.object.ObjectNode;

public class TextToNodeFieldProcessor {
  private static final Logger log = LoggerFactory.getLogger(TextToNodeFieldProcessor.class);

  private TextToNodeFieldProcessor() {
    super();
  }


  /**
   * Extracts the appropriate data from text and sets the value for node property.
   * 
   * @param text From which we want to access a piece of data.
   * @param regex
   * @param converter
   * @param node
   * @param path
   **/
  public static boolean setFieldFromText(String text, String regex,
      TextProcessorConverter<?> converter,
      ObjectNode node,
      String... path) {
    Objects.requireNonNull(text, "text cannot be null!");
    Objects.requireNonNull(regex, "regex cannot be null!");
    Objects.requireNonNull(node, "node cannot be null!");
    Objects.requireNonNull(path, "path cannot be null!");
    boolean hasModified = false;

    String value = getValue(text, regex);
    if (value == null) {
      log.debug("It is not possible to find the data based on regex");
      return hasModified;
    }

    if (converter != null) {
      Object convertedValue = converter.convert(value);
      node.setValue(convertedValue, path);
      hasModified = true;
    } else {
      node.setValue(value, path);
      hasModified = true;
    }
    return hasModified;
  }

  /**
   * Extracts the appropriate data from text and sets the value for node property.
   * 
   * @param text From which we want to access a piece of data.
   * @param node
   * @param descriptors A list of descriptors describing what data to extract and where to insert
   *        it.
   **/

  public static boolean setFieldFromText(String text, ObjectNode node,
      List<TextProcessorDescriptor> descriptors) {
    Objects.requireNonNull(text, "text cannot be null!");
    Objects.requireNonNull(node, "node cannot be null!");
    Objects.requireNonNull(descriptors, "descriptors cannot be null!");
    boolean hasModified = false;
    for (TextProcessorDescriptor descriptor : descriptors) {

      Class<?> clazz;
      TextProcessorConverter<?> converter = null;

      try {
        clazz = Class.forName(descriptor.getConverterQualifiedName());
        if (TextProcessorConverter.class.isAssignableFrom(clazz)) {
          converter = (TextProcessorConverter<?>) clazz.newInstance();
        }

      } catch (Exception e) {
        log.error("No such class with QualifiedName exists", e);
        continue;
      }

      hasModified |=
          setFieldFromText(text, descriptor.getRegex(), converter, node, descriptor.getPath());
    }
    return hasModified;
  }

  private static String getValue(String text, String regex) {
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

  public static interface TextProcessorConverter<T> {
    T convert(String textValue);
  }
}
