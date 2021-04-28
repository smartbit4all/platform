package org.smartbit4all.ui.vaadin.localization;

import java.util.List;
import java.util.Locale;
import org.smartbit4all.ui.vaadin.localization.ComponentLocalizations.ComponentLocalizer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePicker.DatePickerI18n;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.upload.Upload;

/**
 * 
 * This ComponentLocalizer should be created in a Spring {@link Configuration} as a {@link Bean}
 * with the {@link ConfigurationProperties} annotation referencing a property prefix. The prefixed
 * properties should hold the information to handle the localization of the components. <br>
 * <br>
 * <u>Example of initialization: </u><br>
 * <code>
 * &#64;Bean<br>
 * &#64;ConfigurationProperties(prefix = "component-localizer-hu")<br>
 * public ComponentLocalizer componentLocalizerHu() {<br>
 * &nbsp;&nbsp; return new PropertyBasedComponentLocalizer(); <br>
 * }<br>
 * </code> <br>
 * <u>Example of property settings:</u><br>
 * <code>
 * component-localizer-hu:<br>
 * &nbsp;&nbsp;   localeLang: hu<br>
 * &nbsp;&nbsp;   localeCountry: HU<br>
 * &nbsp;&nbsp;   datePicker:<br>
 * &nbsp;&nbsp;&nbsp;     months: Januar,Februar,Marcius...<br>
 * &nbsp;&nbsp;&nbsp;     weekdaysShort: V,H,K,SZ,CS,P,SZ<br>
 * &nbsp;&nbsp;&nbsp;     cancel: Megsem<br>
 * &nbsp;&nbsp;&nbsp;     ...<br>
 * &nbsp;&nbsp;   dateTimePicker:<br>
 * &nbsp;&nbsp;&nbsp;     ...<br>
 * &nbsp;&nbsp;   upload:<br>
 * &nbsp;&nbsp;&nbsp;     ...<br>
 * </code>
 * 
 * @author Balazs Horvath
 *
 */
public class PropertyBasedComponentLocalizer implements ComponentLocalizer {

  private String localeLang;

  private String localeCountry;

  private DateAndDateTimeLocalizerProperties datePicker;

  private DateAndDateTimeLocalizerProperties dateTimePicker;

  private UploadLocalizerProperties upload;


  @Override
  public Locale getLocale() {
    return new Locale(localeLang, localeCountry);
  }

  @Override
  public void localize(Upload upload) {
    throw new RuntimeException("Method not yet implemented!");
  }

  @Override
  public void localize(DateTimePicker dateTimePickerComponent) {
    if (dateTimePickerComponent == null) {
      return;
    }
    DatePickerI18n i18n = dateTimePickerComponent.getDatePickerI18n();
    if (i18n == null) {
      i18n = new DatePickerI18n();
    }
    localizeDatePickerI18n(i18n, dateTimePicker);
    dateTimePickerComponent.setDatePickerI18n(i18n);
  }

  @Override
  public void localize(DatePicker datePickerComponent) {
    if (datePickerComponent == null) {
      return;
    }
    DatePickerI18n i18n = datePickerComponent.getI18n();
    if (i18n == null) {
      i18n = new DatePickerI18n();
    }
    localizeDatePickerI18n(i18n, datePicker);
    datePickerComponent.setI18n(i18n);
  }

  private void localizeDatePickerI18n(DatePickerI18n i18n, DateAndDateTimeLocalizerProperties p) {
    if (p == null) {
      return;
    }
    i18n.setCancel(p.cancel);
    i18n.setClear(p.clear);
    i18n.setFirstDayOfWeek(p.firstDayOfWeek);
    i18n.setToday(p.today);
    i18n.setWeek(p.week);
    i18n.setMonthNames(p.months);
    i18n.setWeekdays(p.weekdays);
    i18n.setWeekdaysShort(p.weekdaysShort);
  }


  public String getLocaleLang() {
    return localeLang;
  }

  public void setLocaleLang(String local) {
    this.localeLang = local;
  }

  public DateAndDateTimeLocalizerProperties getDatePicker() {
    return datePicker;
  }

  public void setDatePicker(DateAndDateTimeLocalizerProperties datePicker) {
    this.datePicker = datePicker;
  }

  public DateAndDateTimeLocalizerProperties getDateTimePicker() {
    return dateTimePicker;
  }

  public void setDateTimePicker(DateAndDateTimeLocalizerProperties dateTimePicker) {
    this.dateTimePicker = dateTimePicker;
  }


  public String getLocaleCountry() {
    return localeCountry;
  }

  public void setLocaleCountry(String localeCountry) {
    this.localeCountry = localeCountry;
  }


  public static class DateAndDateTimeLocalizerProperties {

    private List<String> months;

    private List<String> weekdays;

    private List<String> weekdaysShort;

    private String cancel;

    private String clear;

    private String today;

    private String week;

    private int firstDayOfWeek;

    public List<String> getMonths() {
      return months;
    }

    public void setMonths(List<String> months) {
      this.months = months;
    }

    public List<String> getWeekdays() {
      return weekdays;
    }

    public void setWeekdays(List<String> weekdays) {
      this.weekdays = weekdays;
    }

    public List<String> getWeekdaysShort() {
      return weekdaysShort;
    }

    public void setWeekdaysShort(List<String> weekdaysShort) {
      this.weekdaysShort = weekdaysShort;
    }

    public String getCancel() {
      return cancel;
    }

    public void setCancel(String cancel) {
      this.cancel = cancel;
    }

    public String getClear() {
      return clear;
    }

    public void setClear(String clear) {
      this.clear = clear;
    }

    public String getToday() {
      return today;
    }

    public void setToday(String today) {
      this.today = today;
    }

    public String getWeek() {
      return week;
    }

    public void setWeek(String week) {
      this.week = week;
    }

    public int getFirstDayOfWeek() {
      return firstDayOfWeek;
    }

    public void setFirstDayOfWeek(int firstDayOfWeek) {
      this.firstDayOfWeek = firstDayOfWeek;
    }

  }

  private static class UploadLocalizerProperties {
    // TODO
  }
  
//
//  public static void hungarize(Upload upload) {
//    if (upload == null)
//        return;
//    upload.setId("i18n-upload");
//    UploadI18N i18n = new UploadI18N();
//    i18n.setDropFiles(
//            new UploadI18N.DropFiles().setOne("Húzzon ide egy fájlt...").setMany("Húzzon ide több fájlt..."))
//            .setAddFiles(new UploadI18N.AddFiles().setOne("Tallózás").setMany("Tallózás")).setCancel("Mégsem")
//            .setError(new UploadI18N.Error().setTooManyFiles("Túl sok fájl.").setFileIsTooBig("A fájl túl nagy.")
//                    .setIncorrectFileType("Nem érvényes fájltípus."))
//            .setUploading(new UploadI18N.Uploading()
//                    .setStatus(new UploadI18N.Uploading.Status().setConnecting("Kapcsolódás...")
//                            .setStalled("Leállítva.").setProcessing("Feltöltés..."))
//                    .setRemainingTime(new UploadI18N.Uploading.RemainingTime().setPrefix("Hátralévő idő: ")
//                            .setUnknown("ismeretlen"))
//                    .setError(new UploadI18N.Uploading.Error().setServerUnavailable("A szerver nem elérhető.")
//                            .setUnexpectedServerError("Vártlan szerverhiba történt.")
//                            .setForbidden("Tiltott művelet.")))
//            .setUnits(Stream.of("B", "kB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB")
//                    .collect(Collectors.toList()));
//
//    upload.setI18n(i18n);
//}
  
  public static class ParamsAsd {
    private String localeLang;

    private String localeCountry;

    private DateAndDateTimeLocalizerProperties datePicker;

    private DateAndDateTimeLocalizerProperties dateTimePicker;

    private UploadLocalizerProperties upload;

    public String getLocaleLang() {
      return localeLang;
    }

    public void setLocaleLang(String localeLang) {
      this.localeLang = localeLang;
    }

    public String getLocaleCountry() {
      return localeCountry;
    }

    public void setLocaleCountry(String localeCountry) {
      this.localeCountry = localeCountry;
    }

    public DateAndDateTimeLocalizerProperties getDatePicker() {
      return datePicker;
    }

    public void setDatePicker(DateAndDateTimeLocalizerProperties datePicker) {
      this.datePicker = datePicker;
    }

    public DateAndDateTimeLocalizerProperties getDateTimePicker() {
      return dateTimePicker;
    }

    public void setDateTimePicker(DateAndDateTimeLocalizerProperties dateTimePicker) {
      this.dateTimePicker = dateTimePicker;
    }

    public UploadLocalizerProperties getUpload() {
      return upload;
    }

    public void setUpload(UploadLocalizerProperties upload) {
      this.upload = upload;
    }

  }
}
