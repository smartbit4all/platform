package org.smartbit4all.api.setting;

import java.util.Locale;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {LocaleSettingApiOptionTestConfig.class})
class LocaleSettingApiOptionTest {

  private static Locale hu;

  private static Locale fr;

  @Autowired
  private LocaleSettingApi localeSettings;

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

    System.gc();
    System.gc();
    System.gc();
    System.gc();
    System.gc();

    localeSettings.setDefaultLocale(hu);
    // The text remains the same because there we've lost the reference to the Consumer

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
    // global myspecificmessage1 defined
    assertEquals("Specific message1",
        localeSettings.get("myspecificmessage1"));
    // smartbit4all specific myspecificmessage1 defined
    assertEquals("SB4 specific message1",
        localeSettings.get("smartbit4all", "myspecificmessage1"));
    // smartbit4all specific myspecificmessage2 defined
    assertEquals("SB4 specific message2",
        localeSettings.get("smartbit4all", "myspecificmessage2"));
    // global specific myspecificmessage2 NOT defined - get returns key
    assertEquals("myspecificmessage2",
        localeSettings.get("myspecificmessage2"));
    // only global specific myspecificmessage3 defined
    assertEquals("Specific message3",
        localeSettings.get("smartbit4all", "myspecificmessage3"));
    // myspecificmessage_which_doesnt_exist NOT defined - get returns key
    assertEquals("myspecificmessage_which_doesnt_exist",
        localeSettings.get("smartbit4all", "myspecificmessage_which_doesnt_exist"));
  }

}
