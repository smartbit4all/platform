package org.smartbit4all.core.utility;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.smartbit4all.core.utility.StringConstant.EMPTY;
import static org.smartbit4all.core.utility.StringConstant.joinCamel;
import static org.smartbit4all.core.utility.StringConstant.joinDot;
import static org.smartbit4all.core.utility.StringConstant.joinUnder;

class StringConstantTest {

  @BeforeAll
  static void setUpBeforeClass() throws Exception {}

  @AfterAll
  static void tearDownAfterClass() throws Exception {}

  @Test
  void testJoiningDot() {
    Assertions.assertThat(joinDot(null)).isEqualTo(EMPTY);
    Assertions.assertThat(joinDot("1", null, "2")).isEqualTo("1.2");
    Assertions.assertThat(joinDot("1", EMPTY, "2")).isEqualTo("1.2");
    Assertions.assertThat(joinDot("1", "2", "3")).isEqualTo("1.2.3");
  }

  @Test
  void testJoiningUnder() {
    Assertions.assertThat(joinUnder(null)).isEqualTo(EMPTY);
    Assertions.assertThat(joinUnder("1", null, "2")).isEqualTo("1_2");
    Assertions.assertThat(joinUnder("1", EMPTY, "2")).isEqualTo("1_2");
    Assertions.assertThat(joinUnder("1", "2", "3")).isEqualTo("1_2_3");
  }

  @Test
  void testJoiningCamel() {
    Assertions.assertThat(joinCamel(null)).isEqualTo(EMPTY);
    Assertions.assertThat(joinCamel("apple", null, "pear")).isEqualTo("applePear");
    Assertions.assertThat(joinCamel("Apple", EMPTY, "Pear")).isEqualTo("ApplePear");
    Assertions.assertThat(joinCamel("apple", "PEAR", "grape")).isEqualTo("applePEARGrape");
  }

}
