package org.smartbit4all.core.object.serialize;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

public class ZonedLocalDateDeserializer extends LocalDateDeserializer {

  private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

  public static final ZonedLocalDateDeserializer INSTANCE = new ZonedLocalDateDeserializer();

  protected ZonedLocalDateDeserializer() {
    this(DEFAULT_FORMATTER);
  }

  public ZonedLocalDateDeserializer(DateTimeFormatter dtf) {
    super(dtf);
  }

  /**
   * Since 2.10
   */
  public ZonedLocalDateDeserializer(LocalDateDeserializer base, DateTimeFormatter dtf) {
    super(base, dtf);
  }

  /**
   * Since 2.10
   */
  protected ZonedLocalDateDeserializer(LocalDateDeserializer base, Boolean leniency) {
    super(base, leniency);
  }

  /**
   * Since 2.11
   */
  protected ZonedLocalDateDeserializer(LocalDateDeserializer base, JsonFormat.Shape shape) {
    super(base, shape);
  }

  @Override
  protected ZonedLocalDateDeserializer withDateFormat(DateTimeFormatter dtf) {
    return new ZonedLocalDateDeserializer(this, dtf);
  }

  @Override
  protected ZonedLocalDateDeserializer withLeniency(Boolean leniency) {
    return new ZonedLocalDateDeserializer(this, leniency);
  }

  @Override
  protected ZonedLocalDateDeserializer withShape(JsonFormat.Shape shape) {
    return new ZonedLocalDateDeserializer(this, shape);
  }

  @Override
  public LocalDate deserialize(JsonParser parser, DeserializationContext context)
      throws IOException {

    if (parser.hasToken(JsonToken.VALUE_STRING)) {
      String string = parser.getText().trim();
      try {
        if (_formatter == DEFAULT_FORMATTER) {
          // JavaScript by default includes time in JSON serialized Dates (UTC/ISO instant format).
          if (string.length() > 10 && string.charAt(10) == 'T') {
            if (string.endsWith("Z")) {
              // this is why we're here, use ZoneId.systemDefault() instead of ZoneOffset.UTC
              return LocalDateTime.ofInstant(Instant.parse(string), ZoneId.systemDefault())
                  .toLocalDate();
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
