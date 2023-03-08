package org.smartbit4all.domain.utility.serialize;

import java.net.URI;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

/**
 * The enum that is responsible for the serialization and deserialization of the value objects. The
 * format is always looks like a class type that is the ordinal number of the enumeration.
 *
 * @author Peter Boros
 */
public class SerializationType<T> {

  /**
   * The types indexed by their saved value.
   */
  public static final SerializationType<?> types[] = new SerializationType<?>[256];

  /**
   * The types mapped by the class it manages.
   */
  public static Map<Class<?>, SerializationType<?>> typesByClass = new HashMap<>();

  // @formatter:off
  public static final SerializationType<Void> NULL = new SerializationType<>(0x10, Void.class, null, b -> null);
  public static final SerializationType<Void> NULL_VALUE = new SerializationType<>(0x11, Void.class, null, b -> null);
  public static final SerializationType<String> STRING = new SerializationType<>(0x12, String.class, SerializationType::serializeString, SerializationType::deserializeString);
  public static final SerializationType<Byte> BYTE = new SerializationType<>(0x13, Byte.class, b -> new byte[] {b.byteValue()}, b -> b[0]);
  public static final SerializationType<Integer> INTEGER = new SerializationType<>(0x14, Integer.class, i -> Ints.toByteArray(i), b -> Ints.fromByteArray(b));
  public static final SerializationType<Long> LONG = new SerializationType<>(0x15, Long.class, l -> Longs.toByteArray(l), b -> Longs.fromByteArray(b));
  public static final SerializationType<Double> DOUBLE = new SerializationType<>(0x16, Double.class, SerializationType::serializeDouble, SerializationType::deserializeDouble);
//  BIGDECIMAL    (0x17, BigDecimal.class, bd -> bd.unscaledValue().toByteArray())),
  public static final SerializationType<LocalDate> LOCALDATE = new SerializationType<>(0x18, LocalDate.class, SerializationType::serializeLocalDate, SerializationType::deserializeLocalDate);
  public static final SerializationType<LocalDateTime> LOCALDATETIME = new SerializationType<>(0x19, LocalDateTime.class, SerializationType::serializeLocalDateTime, SerializationType::deserializeLocalDateTime);
//  OFFSETDATE    (0x1a),
  public static final SerializationType<OffsetDateTime> OFFSETDATETIME = new SerializationType<>(0x1b, OffsetDateTime.class, SerializationType::serializeOffsetDateTime, SerializationType::deserializeOffsetDateTime);
  public static final SerializationType<URI> URITYPE = new SerializationType<>(0x1c, URI.class, SerializationType::serializeUri, SerializationType::deserializeUri);
  public static final SerializationType<Boolean> BOOLEAN = new SerializationType<>(0x1d, Boolean.class, SerializationType::serializeBoolean, SerializationType::deserializeBoolean);
  public static final SerializationType<UUID> UUIDTYPE = new SerializationType<>(0x1e, UUID.class, SerializationType::serializeUUID, SerializationType::deserializeUUID);
  public static final SerializationType<Void> OTHER = new SerializationType<>(0x40, Void.class, null, b -> null);
  // @formatter:on

  /**
   * The byte value written to the stream as type.
   */
  private byte value;

  private Class<T> clazz;

  Function<T, byte[]> serializer;

  Function<byte[], T> deserializer;


  public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

  public static <T> SerializationType<?> getType(Class<T> clazz) {
    SerializationType<?> type = typesByClass.get(clazz);
    return type == null ? SerializationType.OTHER : type;
  }

  /**
   * The constructor to create a new instance. Private to ensure singletons.
   *
   * @param value
   * @param clazz
   * @param serializer
   * @param deserializer
   */
  private SerializationType(int value, Class<T> clazz, Function<T, byte[]> serializer,
      Function<byte[], T> deserializer) {
    this.value = (byte) value;
    types[this.value] = this;
    typesByClass.put(clazz, this);
    this.clazz = clazz;
    this.serializer = serializer;
    this.deserializer = deserializer;
  }

  public byte getValue() {
    return value;
  }

  public Class<T> getType() {
    return clazz;
  }

  public Function<T, byte[]> getSerializer() {
    return serializer;
  }

  public Function<byte[], T> getDeserializer() {
    return deserializer;
  }

  private static byte[] serializeDouble(Double d) {
    return d.toString().getBytes(UTF8_CHARSET);
  }

  private static Double deserializeDouble(byte[] b) {
    return Double.valueOf(new String(b, UTF8_CHARSET));
  }

  private static byte[] serializeString(String s) {
    return s.getBytes(UTF8_CHARSET);
  }

  private static String deserializeString(byte[] b) {
    return new String(b, UTF8_CHARSET);
  }

  private static byte[] serializeUri(URI uri) {
    return uri.toString().getBytes(UTF8_CHARSET);
  }

  private static byte[] serializeUUID(UUID uuid) {
    return uuid.toString().getBytes(UTF8_CHARSET);
  }

  private static URI deserializeUri(byte[] b) {
    return URI.create(new String(b, UTF8_CHARSET));
  }

  private static UUID deserializeUUID(byte[] b) {
    return UUID.fromString(new String(b, UTF8_CHARSET));
  }

  private static byte[] serializeLocalDate(LocalDate localDate) {
    return localDate.toString().getBytes(UTF8_CHARSET);
  }

  private static LocalDate deserializeLocalDate(byte[] b) {
    return LocalDate.parse(new String(b, UTF8_CHARSET));
  }

  private static byte[] serializeLocalDateTime(LocalDateTime localDateTime) {
    return localDateTime.toString().getBytes(UTF8_CHARSET);
  }

  private static LocalDateTime deserializeLocalDateTime(byte[] b) {
    return LocalDateTime.parse(new String(b, UTF8_CHARSET));
  }

  private static byte[] serializeOffsetDateTime(OffsetDateTime offsetDateTime) {
    return offsetDateTime.toString().getBytes(UTF8_CHARSET);
  }

  private static OffsetDateTime deserializeOffsetDateTime(byte[] b) {
    return OffsetDateTime.parse(new String(b, UTF8_CHARSET));
  }

  private static byte[] serializeBoolean(Boolean value) {
    return value ? new byte[] {1} : new byte[] {0};
  }

  private static Boolean deserializeBoolean(byte[] b) {
    return b[0] == 1;
  }

}
