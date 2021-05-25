package org.smartbit4all.api.setting;

import java.util.Locale;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LocaleSettingApiOptionTest {

  private static Locale hu;

  private static Locale fr;

  private static LocaleSettingApi localeSettings;

  private static MyModuleLocale moduleLocale;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    hu = new Locale("hu", "HU");
    fr = Locale.FRANCE;
    // Initiate the api and the module locale.
    localeSettings = new LocaleSettingApi();

    moduleLocale = new MyModuleLocale();

    // Instead of Spring we initiate the MyModuleLocale instance.
    localeSettings.analyzeLocaleStrings(moduleLocale);

  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {}

  @Test
  void testLiteralTranslationOnly() {

    localeSettings.clearTranslations();
    // Now we add a translation for the "apple" literal for the Hungarian translation.
    localeSettings.setTranslation("apple", hu, "alma");
    localeSettings.setTranslation("apple", fr, "pomme");

    localeSettings.setDefaultLocale(hu);

    // We must find the hungarian translation for both entry.
    assertEquals("alma", MyModuleLocale.apple.get());

    localeSettings.setDefaultLocale(fr);

    // We must find the hungarian translation for both entry.
    assertEquals("pomme", MyModuleLocale.apple.get());

  }

}
