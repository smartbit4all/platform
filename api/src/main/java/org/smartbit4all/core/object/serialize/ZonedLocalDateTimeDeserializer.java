package org.smartbit4all.core.object.serialize;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

public class ZonedLocalDateTimeDeserializer extends LocalDateTimeDeserializer {

  private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  public static final ZonedLocalDateTimeDeserializer INSTANCE =
      new ZonedLocalDateTimeDeserializer();

  private ZonedLocalDateTimeDeserializer() {
    this(DEFAULT_FORMATTER);
  }

  public ZonedLocalDateTimeDeserializer(DateTimeFormatter formatter) {
    super(formatter);
  }

  /**
   * Since 2.10
   */
  protected ZonedLocalDateTimeDeserializer(LocalDateTimeDeserializer base, Boolean leniency) {
    super(base, leniency);
  }

  @Override
  protected ZonedLocalDateTimeDeserializer withDateFormat(DateTimeFormatter formatter) {
    return new ZonedLocalDateTimeDeserializer(formatter);
  }

  @Override
  protected ZonedLocalDateTimeDeserializer withLeniency(Boolean leniency) {
    return new ZonedLocalDateTimeDeserializer(this, leniency);
  }

  @Override
  protected ZonedLocalDateTimeDeserializer withShape(JsonFormat.Shape shape) {
    return this;
  }

  @Override
  public LocalDateTime deserialize(JsonParser parser, DeserializationContext context)
      throws IOException {

    if (parser.hasTokenId(JsonTokenId.ID_STRING)) {
      String string = parser.getText().trim();
      try {
        if (_formatter == DEFAULT_FORMATTER) {
          // JavaScript by default includes time and zone in JSON serialized Dates (UTC/ISO instant
          // format).
          if (string.length() > 10 && string.charAt(10) == 'T') {
            if (string.endsWith("Z")) {
              // this is why we're here, use ZoneId.systemDefault() instead of ZoneOffset.UTC
              return LocalDateTime.ofInstant(Instant.parse(string), ZoneId.systemDefault());
            }
          }
        }
      } catch (DateTimeException e) {
        return _handleDateTimeException(context, e, string);
      }
    }

    return super.deserialize(parser, context);
  }

}
