package org.smartbit4all.core.utility;

import java.util.ArrayList;
import java.util.List;

public class RangeUtils {

  private RangeUtils() {}

  /**
   * Flattens a compact range expression into a list of discrete values.
   * <p>
   * Example: "V1,V4-V7,V11,V13-V16" -> [V1, V4, V5, V6, V7, V11, V13, V14, V15, V16]
   *
   * @param input A comma-separated string of values and ranges. Ranges are denoted with a dash,
   *        e.g. "V4-V7".
   * @return A list of discrete string values in order of expansion.
   * @throws IllegalArgumentException if the range format is invalid
   */
  public static List<String> flattenRanges(String input) {
    return flattenRanges(input, ",", "-");
  }

  /**
   * Flattens a compact range expression into a list of discrete values.
   * <p>
   * Example: "V1,V4-V7,V11,V13-V16" -> [V1, V4, V5, V6, V7, V11, V13, V14, V15, V16]
   *
   * @param input A string of values and ranges. e.g. "V4-V7".
   * @param delimiter A string to split the values by. e.g "," or ";".
   * @param rangeSeparator A string indicating a range. e.g. "-".
   * @return A list of discrete string values in order of expansion.
   * @throws IllegalArgumentException if the range format is invalid
   */
  public static List<String> flattenRanges(String input, String delimiter, String rangeSeparator) {
    List<String> result = new ArrayList<>();
    if (input == null || input.isBlank()) {
      return result;
    }

    String[] parts = input.split(delimiter);
    for (String part : parts) {
      part = part.trim();
      if (part.isEmpty()) {
        continue;
      }

      if (part.contains(rangeSeparator)) {
        // Range case: e.g., V4-V7
        String[] rangeTokens = part.split(rangeSeparator);
        if (rangeTokens.length != 2) {
          throw new IllegalArgumentException("Invalid range format: " + part);
        }

        String start = rangeTokens[0].trim();
        String end = rangeTokens[1].trim();

        // Extract prefix (e.g. "V") and numeric suffix
        String prefix = start.replaceAll("\\d+$", "");
        int startNum = Integer.parseInt(start.substring(prefix.length()));
        int endNum = Integer.parseInt(end.substring(prefix.length()));

        if (endNum < startNum) {
          throw new IllegalArgumentException("Range end must be >= start: " + part);
        }

        for (int i = startNum; i <= endNum; i++) {
          result.add(prefix + i);
        }
      } else {
        // Single value case: e.g., V1
        result.add(part);
      }
    }

    return result;
  }
}
