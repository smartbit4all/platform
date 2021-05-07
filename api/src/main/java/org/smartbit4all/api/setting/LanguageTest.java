package org.smartbit4all.api.setting;

public class LanguageTest implements HasOption {

  private int i;

  private LocaleString message = new LocaleString("default message");

  public LanguageTest(int i) {
    super();
    this.i = i;
    System.out.println(message.get());
  }

}
