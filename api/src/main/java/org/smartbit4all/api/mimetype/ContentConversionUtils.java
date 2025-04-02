package org.smartbit4all.api.mimetype;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.core.utility.StringConstant;

public class ContentConversionUtils {

  public static String binaryDataToString(BinaryData data) throws IOException {
    Objects.requireNonNull(data);
    try (InputStream in = data.inputStream();
        InputStreamReader reader = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(reader);
        Stream<String> lines = br.lines()) {
      return lines.collect(Collectors.joining(StringConstant.NEW_LINE));
    }
  }

  private ContentConversionUtils() {}
}
