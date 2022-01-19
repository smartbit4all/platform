package org.smartbit4all.domain.utility.serialize;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.smartbit4all.domain.utility.serialize.TableDataSerializer.ByteArrayConverter;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

public enum SerializationTypes {

  // @formatter:off
  NULL          (0x10, TypeHelper.of(Void.class, null)),
  NULL_VALUE    (0x11, TypeHelper.of(Void.class, null)),
  STRING        (0x12, TypeHelper.of(String.class, s -> s.getBytes("ISO-8859-1"))),
  BYTE          (0x13, TypeHelper.of(Byte.class, b -> new byte[] {b.byteValue()})),
  INTEGER       (0x14, TypeHelper.of(Integer.class, i -> Ints.toByteArray(i))),
  LONG          (0x15, TypeHelper.of(Long.class, l -> Longs.toByteArray(l))),
  DOUBLE        (0x16, TypeHelper.of(Double.class, SerializationTypes::doubleConverter)),
  BIGDECIMAL    (0x17, TypeHelper.of(BigDecimal.class, bd -> bd.unscaledValue().toByteArray())),
  // TODO
//  LOCALDATE     (0x18),
//  LOCALDATETIME (0x19),
//  OFFSETDATE    (0x1a),
//  OFFSETDATETIME(0x1b),
  URI           (0x1c, TypeHelper.of(URI.class, u -> u.toString().getBytes("ISO-8859-1"))),
  OTHER         (0x40, TypeHelper.of(Void.class, null)),
  ;
  // @formatter:on


  private byte value;
  private TypeHelper<?> typeHelper;

  private SerializationTypes(int value, TypeHelper<?> typeHelper) {
    this.value = (byte) value;
    this.typeHelper = typeHelper;
  }

  public byte getValue() {
    return value;
  }

  public Class<?> getType() {
    return typeHelper.clazz;
  }

  public ByteArrayConverter<?> getConverter() {
    return typeHelper.converter;
  }

  public static SerializationTypes getType(Class<?> clazz) {
    return Arrays.stream(values()).filter(t -> t.getType().equals(clazz)).findFirst().orElse(OTHER);
  }

  private static class TypeHelper<T> {
    Class<T> clazz;
    ByteArrayConverter<T> converter;

    private TypeHelper(Class<T> clazz, ByteArrayConverter<T> converter) {
      super();
      this.clazz = clazz;
      this.converter = converter;
    }

    public static <T> TypeHelper<T> of(Class<T> clazz, ByteArrayConverter<T> converter) {
      return new TypeHelper<>(clazz, converter);
    }

  }

  private static byte[] doubleConverter(Double d) {
    byte[] bytes = new byte[8];
    ByteBuffer.wrap(bytes).putDouble(d);
    return bytes;
  }

}
