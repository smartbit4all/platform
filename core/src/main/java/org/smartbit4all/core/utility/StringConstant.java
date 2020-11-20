package org.smartbit4all.core.utility;

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

  private StringConstant() {
    super();
  }

  public static final String AMPERSAND = "&";

  public static final String ARROW = "->";

  public static final String ASTERISK = "*";

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

  public static final String DOUBLE_QUOTE = "\"";

  public static final String EMPTY = "";

  public static final String EQUAL = "=";

  public static final String EXCLAMATIONMARK = "!";

  public static final String FORM_FEED = "\f";

  public static final String GREATER = ">";

  public static final String GREATEROREQUAL = ">=";

  public static final String HASH = "#";

  public static final String HYPHEN = "-";

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

  public static final String SPACE_HYPHEN_SPACE = " - ";

  public static final String TAB = "\t";

  public static final String UNDERLINE = "_";

  public static final String UNKNOWN = "N/A";

  public static final String VERTICAL_BAR = "|";

  public static final String ZERO = "0";

}
