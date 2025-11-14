/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.core.utility;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import com.google.common.base.Strings;
import static java.util.stream.Collectors.joining;

/**
 * An interface for the string constants. Do not implement this! We created it to avoid the direct
 * dependence on libraries like Google Guava or Apache Commons. This list of constants were inspired
 * by the StringUtils of Apache Commons.
 *
 * @see <a href=
 *      "http://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/StringUtils.html">StringUtils</a>
 *
 * @author Peter Boros
 */
public class StringConstant {

  public static final String AMPERSAND = "&";

  public static final String ARROW = "->";

  public static final String ARROW_SPACE = "-> ";

  public static final String ASTERISK = "*";

  public static final String DOUBLE_ASTERISK = "**";

  public static final String AT = "@";

  public static final String BACK_ARROW = "<-";

  public static final String BACKSLASH = "\\";

  public static final String BACKSPACE = "\b";

  public static final String CARRIAGE_RETURN = "\r";

  public static final String COLON = ":";

  public static final String COLON_SPACE = ": ";

  public static final String COMMA = ",";

  public static final String COMMA_SPACE = ", ";

  public static final String DOLLAR = "$";

  public static final String DOT = ".";

  public static final String DOT_SPACE = ". ";

  public static final String DOT_REGEX = "\\.";

  public static final String DOUBLE_QUOTE = "\"";

  public static final String EMPTY = "";

  public static final String[] EMPTY_ARRAY = new String[0];

  public static final String EQUAL = "=";

  public static final String EXCLAMATIONMARK = "!";

  public static final String FORM_FEED = "\f";

  public static final String GREATER = ">";

  public static final String GREATEROREQUAL = ">=";

  public static final String HASH = "#";

  public static final String HTML_PARAGRAPH = "<p>";

  public static final String HTML_PARAGRAPH_END = "</p>";

  public static final String HTML_NEW_LINE = "<br>";

  public static final String HYPHEN = "-";

  public static final String HYPHEN_SPACE = "- ";

  public static final String[] INVALID_FILE_CHARS =
      {"\"", "/", "*", ":", "<", ">", "?", "\\", "|", new String(new byte[] {(byte) 0x7F}), "\000"};

  public static final String LEFT_CURLY = "{";

  public static final String LEFT_PARENTHESIS = "(";

  public static final String LEFT_SQUARE = "[";

  public static final String LESS = "<";

  public static final String LESSOREQUAL = "<=";

  public static final String MINUS_SIGN = "-";

  public static final String NEW_LINE = System.lineSeparator();

  public static final String NULL = "null";

  public static final String ONE = "1";

  public static final String PERCENT = "%";

  public static final String PLUS_SIGN = "+";

  public static final String QUESTIONMARK = "?";

  public static final String RIGHT_CURLY = "}";

  public static final String RIGHT_PARENTHESIS = ")";

  public static final String RIGHT_SQUARE = "]";

  public static final String SEMICOLON = ";";

  public static final String SEMICOLON_SPACE = "; ";

  public static final String SINGLE_QUOTE = "\'";

  public static final String SLASH = "/";

  public static final String SPACE = " ";

  public static final String SPACE_ARROW_SPACE = " -> ";

  public static final String SPACE_COLON_SPACE = " : ";

  public static final String SPACE_HYPHEN_SPACE = " - ";

  public static final String TAB = "\t";

  public static final String UNDERLINE = "_";

  public static final String UNKNOWN = "N/A";

  public static final String VERTICAL_BAR = "|";

  public static final String ZERO = "0";

  private StringConstant() {
    super();
  }

  public static final String repeat(String s, int n) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < n; i++) {
      result.append(s);
    }
    return result.toString();
  }

  public static final String[] toArray(List<String> stringList) {
    if (stringList == null) {
      return StringConstant.EMPTY_ARRAY;
    }
    return stringList.toArray(StringConstant.EMPTY_ARRAY);
  }

  /**
   * Checks if the given code is valid for creating URI, folder etc.. from it.
   * <p>
   * source: https://www.baeldung.com/java-validate-filename
   * </p>
   */
  public static boolean isValidCode(String code) {
    if (code == null || code.isEmpty() || code.length() > 255) {
      return false;
    }
    return Arrays.stream(getInvalidCharsByOSPlusSpace())
        .noneMatch(ch -> code.contains(ch.toString()));
  }

  public static boolean isNullOrBlank(String str) {
    return str == null || str.isBlank();
  }

  private static final Character[] INVALID_WINDOWS_SPECIFIC_CHARS_PLUS_SPACE =
      {'"', '*', '<', '>', '?', '|', ' '};
  private static final Character[] INVALID_UNIX_SPECIFIC_CHARS_PLUS_SPACE = {'\000', ' '};

  private static Character[] getInvalidCharsByOSPlusSpace() {
    String os = System.getProperty("os.name").toLowerCase();
    if (os.contains("win")) {
      return INVALID_WINDOWS_SPECIFIC_CHARS_PLUS_SPACE;
    } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
      return INVALID_UNIX_SPECIFIC_CHARS_PLUS_SPACE;
    } else {
      return new Character[] {};
    }
  }

  /**
   * Construct a String from the strings joining them with dot.
   * 
   * @param strings
   * @return Empty if the strings is empty or doesn't contain any non empty element
   */
  public static final String joinDot(String... strings) {
    if (strings == null || strings.length == 0) {
      return EMPTY;
    }
    return Stream.of(strings).filter(s -> !Strings.isNullOrEmpty(s)).collect(joining(DOT));
  }

  /**
   * Construct a String from the strings joining them with underline.
   * 
   * @param strings
   * @return Empty if the strings is empty or doesn't contain any non empty element
   */
  public static final String joinUnder(String... strings) {
    if (strings == null || strings.length == 0) {
      return EMPTY;
    }
    return Stream.of(strings).filter(s -> !Strings.isNullOrEmpty(s)).collect(joining(UNDERLINE));
  }

  /**
   * Construct a String from the strings joining them in camel case style where the first string
   * first character remains untouched.
   * 
   * @param strings
   * @return Empty if the strings is empty or doesn't contain any non empty element
   */
  public static final String joinCamel(String... strings) {
    if (strings == null || strings.length == 0) {
      return EMPTY;
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < strings.length; i++) {
      String s = strings[i];
      if (!Strings.isNullOrEmpty(s)) {
        if (sb.length() > 0) {
          sb.append(s.substring(0, 1).toUpperCase());
          sb.append(s.substring(1, s.length()));
        } else {
          sb.append(s);
        }
      }
    }
    return sb.toString();
  }

  /** Result of BOM detection. */
  public static final class BomMatch {
    public final Charset charset;
    public final int bomLength;

    private BomMatch(Charset charset, int bomLength) {
      this.charset = charset;
      this.bomLength = bomLength;
    }
  }

  /**
   * Detects the text encoding of the given bytes based on BOM (Byte Order Mark).
   * 
   * @return BomMatch if a known BOM is found; otherwise {@code null}.
   */
  public static BomMatch detectBom(byte[] data) {
    if (data == null)
      return null;

    // UTF-8: EF BB BF
    if (startsWith(data, (byte) 0xEF, (byte) 0xBB, (byte) 0xBF)) {
      return new BomMatch(StandardCharsets.UTF_8, 3);
    }
    // UTF-32 LE: FF FE 00 00
    if (startsWith(data, (byte) 0xFF, (byte) 0xFE, (byte) 0x00, (byte) 0x00)) {
      return new BomMatch(Charset.forName("UTF-32LE"), 4);
    }
    // UTF-32 BE: 00 00 FE FF
    if (startsWith(data, (byte) 0x00, (byte) 0x00, (byte) 0xFE, (byte) 0xFF)) {
      return new BomMatch(Charset.forName("UTF-32BE"), 4);
    }
    // UTF-16 LE: FF FE
    if (startsWith(data, (byte) 0xFF, (byte) 0xFE)) {
      return new BomMatch(StandardCharsets.UTF_16LE, 2);
    }
    // UTF-16 BE: FE FF
    if (startsWith(data, (byte) 0xFE, (byte) 0xFF)) {
      return new BomMatch(StandardCharsets.UTF_16BE, 2);
    }
    return null;
  }

  /**
   * Strict decode: only decodes if a BOM is present; otherwise returns null. Skips the BOM bytes in
   * the output.
   */
  public static String decodeWithBom(byte[] data) {
    if (data == null)
      return null;
    BomMatch m = detectBom(data);
    if (m == null)
      return null;
    return new String(data, m.bomLength, data.length - m.bomLength, m.charset);
  }

  /**
   * Preferred "Unicode-first" decode: - If BOM present → decode using that Unicode charset,
   * skipping BOM - If no BOM → decode as UTF-8 (Unicode default)
   */
  public static String decodePreferUnicode(byte[] data) {
    if (data == null)
      return null;
    BomMatch m = detectBom(data);
    if (m != null) {
      return new String(data, m.bomLength, data.length - m.bomLength, m.charset);
    }
    // No BOM → default to UTF-8 (Unicode) as a safe, modern default
    return new String(data, StandardCharsets.UTF_8);
  }

  /**
   * Variant with a custom default when no BOM is present.
   */
  public static String decodeWithBomOrDefault(byte[] data, Charset defaultCharset) {
    if (data == null)
      return null;
    BomMatch m = detectBom(data);
    if (m != null) {
      return new String(data, m.bomLength, data.length - m.bomLength, m.charset);
    }
    return new String(data, (defaultCharset != null ? defaultCharset : StandardCharsets.UTF_8));
  }

  // -------- helpers --------

  private static boolean startsWith(byte[] data, byte... prefix) {
    if (data.length < prefix.length)
      return false;
    for (int i = 0; i < prefix.length; i++) {
      if (data[i] != prefix[i])
        return false;
    }
    return true;
  }

}
