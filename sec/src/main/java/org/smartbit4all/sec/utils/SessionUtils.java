package org.smartbit4all.sec.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SessionUtils {

  private static final Logger log = LoggerFactory.getLogger(SessionUtils.class);

  private SessionUtils() {}

  public static <T> String serializeSessionParameter(T value, ObjectMapper objectMapper) {
    Objects.requireNonNull(value, "value can not be null!");


    Class<? extends Object> clazz = value.getClass();
    if (String.class.isAssignableFrom(clazz)) {
      return (String) value;
    }

    String valueTxt = null;

    if (objectMapper != null && objectMapper.canSerialize(clazz)) {
      try {
        valueTxt = objectMapper.writeValueAsString(value);
      } catch (JsonProcessingException e) {
        log.warn(
            "Parameter can not be serialized from session with ObjectMapper. Class: [{}]",
            clazz.getName(), e);
      }
    }

    if (valueTxt == null && Serializable.class.isAssignableFrom(clazz)) {
      try {
        valueTxt = serializeSerializable((Serializable) value);
      } catch (Exception e) {
        log.warn(
            "Parameter can not be deserialized from session as Serializable. class: [{}]",
            clazz.getName(), e);
      }
    }
    if (valueTxt != null) {
      return valueTxt;
    } else {
      throw new IllegalArgumentException(
          "The the given value of class [" + clazz.getName()
              + "] can not be serilalized and set as session parameter!");
    }
  }

  public static <T> T deserializeSessionParameter(String valueTxt, Class<T> clazz,
      ObjectMapper objectMapper) {
    Objects.requireNonNull(clazz, "clazz can not be null!");

    if (String.class.isAssignableFrom(clazz)) {
      return (T) valueTxt;
    }

    if (ObjectUtils.isEmpty(valueTxt)) {
      return null;
    }
    if (objectMapper != null && objectMapper.canDeserialize(objectMapper.constructType(clazz))) {
      try {
        return objectMapper.readValue(valueTxt, clazz);
      } catch (JsonProcessingException e) {
        log.warn(
            "Parameter can not be deserialized from session with ObjectMapper. Class: [{}]",
            clazz.getName(), e);
      }
    }
    if (Serializable.class.isAssignableFrom(clazz)) {
      try {
        return deserializeSerializable(valueTxt);
      } catch (Exception e) {
        log.warn(
            "Parameter can not be deserialized from session as Serializable. Class: [{}]",
            clazz.getName(), e);
      }
    }
    throw new IllegalArgumentException(
        "The requested parameter can not be deserialized as class " + clazz.getName());
  }

  private static <T> T deserializeSerializable(String valueTxt) throws Exception {
    byte[] data = Base64.getDecoder().decode(valueTxt);
    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
    Object o = ois.readObject();
    ois.close();
    return (T) o;
  }

  private static String serializeSerializable(Serializable object) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(object);
    oos.close();
    return Base64.getEncoder().encodeToString(baos.toByteArray());
  }

}
