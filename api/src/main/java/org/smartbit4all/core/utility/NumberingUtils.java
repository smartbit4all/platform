package org.smartbit4all.core.utility;

import java.util.Comparator;
import org.apache.logging.log4j.util.Strings;

/**
 * Constructs a decimal classification from any numbering symbols like Roman numbers, normal letters
 * and decimals.
 */
public class NumberingUtils {

  public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

  public static final String ROMAN_NUMERALS = "IVXLCDM";

  public static int evaluateRomanNumerals(String roman) {
    if (Strings.isBlank(roman)) {
      return -1;
    }
    for (int i = 0; i < roman.length(); i++) {
      char ch = roman.charAt(i);
      if (ROMAN_NUMERALS.indexOf(ch) == -1) {
        return -1;
      }
    }
    return (int) evaluateNextRomanNumeral(roman, roman.length() - 1, 0);
  }

  private static double evaluateNextRomanNumeral(String roman, int pos, double rightNumeral) {
    if (pos < 0)
      return 0;
    char ch = roman.charAt(pos);
    double value = Math.floor(Math.pow(10, "IXCM".indexOf(ch)))
        + 5 * Math.floor(Math.pow(10, "VLD".indexOf(ch)));
    return value * Math.signum(value + 0.5 - rightNumeral)
        + evaluateNextRomanNumeral(roman, pos - 1, value);
  }

  public static String getCharForNumber(int i) {
    CharSequence css = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    if (i > 25) {
      return null;
    }
    return css.charAt(i) + "";
  }

  public static int evaluateLetter(String letter) {
    if (letter == null || letter.length() != 1) {
      return -1;
    }
    return ALPHABET.indexOf(letter) + 1;
  }

  public static final NumberingComparator comparator = new NumberingComparator();

  public static class NumberingComparator implements Comparator<String> {

    @Override
    public int compare(String o1, String o2) {
      if (o1 == null) {
        return 1;
      } else if (o2 == null) {
        return -1;
      }
      String[] split1 = o1.split("\\.");
      String[] split2 = o2.split("\\.");

      int result = 0;
      int i = 0;

      while (result == 0 && i < split1.length && i < split2.length) {
        result = Integer.compare(NumberingUtils.evaluateToInt(split1[i]),
            NumberingUtils.evaluateToInt(split2[i]));
        i++;
      }

      return result;
    }

  }

  public static int evaluateToInt(String s1) {
    int result;
    if (s1.chars().allMatch(Character::isDigit)) {
      result = Integer.valueOf(s1);
    } else {
      // Try roman and then try letter.
      result = evaluateRomanNumerals(s1);
      if (result == -1) {
        result = evaluateLetter(s1);
      }
    }
    return result == -1 ? 0 : result;
  }

}
