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

import static java.util.stream.Collectors.joining;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import com.google.common.base.Strings;

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

}
