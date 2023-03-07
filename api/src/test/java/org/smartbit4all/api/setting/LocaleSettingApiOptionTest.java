package org.smartbit4all.api.setting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Locale;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {LocaleSettingApiOptionTestConfig.class})
class LocaleSettingApiOptionTest {

  private static Locale hu;

  private static Locale fr;

  @Autowired
  private LocaleSettingApi localeSettings;

  @Autowired
  private LocaleUsage lu;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    hu = new Locale("hu", "HU");
    fr = Locale.FRANCE;
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {}

  private String text;

  void setText(String text) {
    this.text = text;
  }

  @Test
  void testLiteralTranslationOnly() {

    localeSettings.clearTranslations();
    // Now we add a translation for the "apple" literal for the Hungarian translation.
    localeSettings.setTranslation("apple", hu, "alma");
    localeSettings.setTranslation("apple", fr, "pomme");

    localeSettings.setDefaultLocale(hu);

    lu.applyLabel(MyModuleLocale.apple, this::setText);

    // We must find the hungarian translation for both entry.
    assertEquals("alma", MyModuleLocale.apple.get());

    assertEquals("alma", text);

    localeSettings.setDefaultLocale(fr);

    // We must find the hungarian translation for both entry.
    assertEquals("pomme", MyModuleLocale.apple.get());
    assertEquals("pomme", text);

    lu = null;

    System.gc();
    System.gc();
    System.gc();
    System.gc();
    System.gc();

    localeSettings.setDefaultLocale(hu);
    // The text remains the same because there we've lost the reference to the Consumer
    assertEquals("pomme", text);

  }

  @Test
  void testMessages() {
    localeSettings.setDefaultLocale(hu);
    String key = "smartbit4all.message";
    assertEquals("Üdv mindenkinek", localeSettings.get(key));
    localeSettings.setDefaultLocale(Locale.UK);
    assertEquals("Greetings everyone", localeSettings.get(key));
  }

  @Test
  void testMessagesByPath() {
    localeSettings.setDefaultLocale(hu);
    assertEquals("Specific message2",
        localeSettings.get("smartbit4all", "myspecificmessage2"));
    assertEquals("Specific message1",
        localeSettings.get("myspecificmessage1"));
    assertEquals("SB4 specific message1",
        localeSettings.get("smartbit4all", "myspecificmessage1"));
  }

}
