package org.smartbit4all.ui.vaadin.localization;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.i18n.I18NProvider;

@Component
public class ComponentLocalizations {

  private static final Locale DEFAULT_LOCALE = new Locale("en");

  private static final Logger log = LoggerFactory.getLogger(ComponentLocalizations.class);

  private static ComponentLocalizations INSTANCE;

  private Map<Locale, ComponentLocalizer> localizersByLocale;

  private I18NProvider i18nProvider;

  /**
   * <b>CAUTION!</b><br>
   * <b>ComponentLocalizationProvider can be initialized only by the Spring framework!</b><br>
   * Initializing in code causes {@link IllegalStateException}!
   */
  public ComponentLocalizations(List<ComponentLocalizer> localizers,
      Optional<I18NProvider> i18nProvider) {
    if (INSTANCE != null) {
      throw new IllegalStateException(this.getClass()
          + " has been already initialized by the Spring framework! Use the static methods instead!");
    }
    this.i18nProvider = i18nProvider.orElse(null);
    this.localizersByLocale = sortLocalizers(localizers);
    INSTANCE = this;
  }

  private Map<Locale, ComponentLocalizer> sortLocalizers(List<ComponentLocalizer> localizers) {
    Map<Locale, ComponentLocalizer> localizersByLocale = new HashMap<>();
    localizers.forEach(localizer -> localizersByLocale.put(localizer.getLocale(), localizer));
    return localizersByLocale;
  }

  protected static Locale getLocale() {
    UI currentUi = UI.getCurrent();
    Locale locale = currentUi == null ? null : currentUi.getLocale();
    if (locale == null) {
      List<Locale> locales = INSTANCE.i18nProvider.getProvidedLocales();
      if (locales != null && !locales.isEmpty()) {
        locale = locales.get(0);
      } else {
        locale = Locale.getDefault();
      }
    }
    return locale;
  }

  /**
   * Sets the localization string to the given {@link DatePicker} based on the current Locale
   * 
   * @param datePicker
   */
  public static void localize(DatePicker datePicker) {
    getLocalizer().ifPresent(localizer -> localizer.localize(datePicker));
  }

  /**
   * Sets the localization string to the given {@link DateTimePicker} based on the current Locale
   * 
   * @param dateTimePicker
   */
  public static void localize(DateTimePicker dateTimePicker) {
    getLocalizer().ifPresent(localizer -> localizer.localize(dateTimePicker));
  }

  /**
   * Sets the localization string to the given {@link Upload} based on the current Locale
   * 
   * @param upload
   */
  public static void localize(Upload upload) {
    getLocalizer().ifPresent(localizer -> localizer.localize(upload));
  }

  private static Optional<ComponentLocalizer> getLocalizer() {
    if (!isInstancePresent()) {
      return Optional.empty();
    }
    Locale locale = getLocale();
    if (isDefaultLocale(locale)) {
      return Optional.empty();
    }
    if (INSTANCE.localizersByLocale.isEmpty()) {
      throw new IllegalStateException("There is no ComponentLocalizer object registered!");
    }
    ComponentLocalizer componentLocalizer = INSTANCE.localizersByLocale.get(locale);
    if (componentLocalizer == null) {
      componentLocalizer = INSTANCE.localizersByLocale.values().stream().findFirst().orElse(null);
    }
    return Optional.of(componentLocalizer);
  }

  private static boolean isInstancePresent() {
    if (INSTANCE == null) {
      log.debug("There is no ComponentLocalizationProvider instantiated by the framework!");
      return false;
    }
    return true;
  }

  private static boolean isDefaultLocale(Locale locale) {
    if (locale.getLanguage().equals(DEFAULT_LOCALE.getLanguage())) {
      return true;
    }
    return false;
  }


  public static interface ComponentLocalizer {

    Locale getLocale();

    void localize(DatePicker datePicker);

    void localize(DateTimePicker dateTimePicker);

    void localize(Upload upload);

  }
}
