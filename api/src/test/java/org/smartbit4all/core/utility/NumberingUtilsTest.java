package org.smartbit4all.core.utility;

import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class NumberingUtilsTest {

  @Test
  void testRoman() {
    Assertions.assertThat(NumberingUtils.evaluateRomanNumerals("I")).isEqualTo(1);
    Assertions.assertThat(NumberingUtils.evaluateRomanNumerals("II")).isEqualTo(2);
    Assertions.assertThat(NumberingUtils.evaluateRomanNumerals("III")).isEqualTo(3);
    Assertions.assertThat(NumberingUtils.evaluateRomanNumerals("IV")).isEqualTo(4);
    Assertions.assertThat(NumberingUtils.evaluateRomanNumerals("V")).isEqualTo(5);
    Assertions.assertThat(NumberingUtils.evaluateRomanNumerals("VI")).isEqualTo(6);
    Assertions.assertThat(NumberingUtils.evaluateRomanNumerals("VII")).isEqualTo(7);
    Assertions.assertThat(NumberingUtils.evaluateRomanNumerals("VIII")).isEqualTo(8);
    Assertions.assertThat(NumberingUtils.evaluateRomanNumerals("IX")).isEqualTo(9);
    Assertions.assertThat(NumberingUtils.evaluateRomanNumerals("X")).isEqualTo(10);
    Assertions.assertThat(NumberingUtils.evaluateRomanNumerals("XX")).isEqualTo(20);
    Assertions.assertThat(NumberingUtils.evaluateRomanNumerals("XLII")).isEqualTo(42);
    Assertions.assertThat(NumberingUtils.evaluateRomanNumerals("XCVIII")).isEqualTo(98);
    Assertions.assertThat(NumberingUtils.evaluateRomanNumerals("CDIL")).isEqualTo(449);
    Assertions.assertThat(NumberingUtils.evaluateRomanNumerals("ksdbgdibsCVBV")).isEqualTo(-1);
  }

  @Test
  void testLetters() {
    Assertions.assertThat(NumberingUtils.evaluateLetter("a")).isEqualTo(1);
    Assertions.assertThat(NumberingUtils.evaluateLetter("b")).isEqualTo(2);
    Assertions.assertThat(NumberingUtils.evaluateLetter("c")).isEqualTo(3);
    Assertions.assertThat(NumberingUtils.evaluateLetter("d")).isEqualTo(4);
    Assertions.assertThat(NumberingUtils.evaluateLetter("e")).isEqualTo(5);
    Assertions.assertThat(NumberingUtils.evaluateLetter("f")).isEqualTo(6);
    Assertions.assertThat(NumberingUtils.evaluateLetter("g")).isEqualTo(7);
    Assertions.assertThat(NumberingUtils.evaluateLetter("h")).isEqualTo(8);
    Assertions.assertThat(NumberingUtils.evaluateLetter("i")).isEqualTo(9);
  }

  @Test
  void testChapterSort() {
    List<String> chapters =
        Arrays.asList("3.IV.c", "1.a.III", "V", "II.a", "I.b", "IX.3.a.I.C", "IX.3.a.I.XC");
    chapters.sort(NumberingUtils.comparator);

    Assertions.assertThat(chapters).containsExactly("1.a.III", "I.b", "II.a", "3.IV.c", "V",
        "IX.3.a.I.XC", "IX.3.a.I.C");
  }

}
