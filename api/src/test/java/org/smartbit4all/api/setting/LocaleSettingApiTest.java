package org.smartbit4all.api.setting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Locale;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class LocaleSettingApiTest {

  private static Locale hu;

  private static Locale fr;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    hu = new Locale("hu", "HU");
    fr = Locale.FRANCE;
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {}

  @Test
  void test() {

    LocaleSettingApi ls = new LocaleSettingApi();
    ls.add("org.smartbit4all.pcg.Apple.apple", "apple");
    ls.add("org.smartbit4all.pcg.Apple2.apple", "apple");

    // We must find the default value and the added keys as they were inserted.
    assertEquals("apple", ls.get("org.smartbit4all.pcg.Apple.apple"));
    assertEquals("apple", ls.get("org.smartbit4all.pcg.Apple2.apple"));

    assertEquals("apple", ls.get(new Locale("aa", "BB"), "org.smartbit4all.pcg.Apple.apple"));
    assertEquals("apple", ls.get(fr, "org.smartbit4all.pcg.Apple2.apple"));

    // Now we add a translation for the "apple" literal for the Hungarian translation.
    ls.setTranslation("apple", hu, "alma");

    // We must find the hungarian translation for both entry.
    assertEquals("alma", ls.get(hu, "org.smartbit4all.pcg.Apple.apple"));
    assertEquals("alma", ls.get(hu, "org.smartbit4all.pcg.Apple2.apple"));

    // We set the translation for French for one key directly.
    ls.setKeyTranslation("org.smartbit4all.pcg.Apple.apple", fr, "pomme");
    // We must find the translation for entry both the other occurance remains the same.
    assertEquals("pomme", ls.get(fr, "org.smartbit4all.pcg.Apple.apple"));
    assertEquals("apple", ls.get(fr, "org.smartbit4all.pcg.Apple2.apple"));

    // We set the translation for French for apple (wrong at all).
    ls.setTranslation("apple", fr, "pomme de terre");
    // We must find the translation for key and the other entry.
    assertEquals("pomme", ls.get(fr, "org.smartbit4all.pcg.Apple.apple"));
    assertEquals("pomme de terre", ls.get(fr, "org.smartbit4all.pcg.Apple2.apple"));

  }

}
