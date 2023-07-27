package org.smartbit4all.api.object;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.object.bean.ObjectChangeData;
import org.smartbit4all.api.object.bean.PropertyChangeData;
import org.smartbit4all.api.object.bean.ReferenceChangeData;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.core.object.ObjectApi;

public class ObjectChangeDataToString {

  private static DateTimeFormatter dateTimeFormatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  public static String toFormattedString(ObjectApi objectApi, LocaleSettingApi localeSettingApi,
      ObjectChangeData change, boolean htmlFormatted) {
    StringBuilder sb = new StringBuilder();
    formatProperties(objectApi, localeSettingApi, sb, "", change.getProperties(), htmlFormatted);
    formatReferences(objectApi, localeSettingApi, sb, "", change.getReferences(), htmlFormatted);
    return sb.toString();
  }

  private static void formatReferences(ObjectApi objectApi, LocaleSettingApi localeSettingApi,
      StringBuilder sb,
      String prefix, List<ReferenceChangeData> references, boolean htmlFormatted) {
    for (ReferenceChangeData referenceChangeData : references) {
      String newPrefix = prefix + referenceChangeData.getPath() + ".";
      formatProperties(objectApi, localeSettingApi, sb, newPrefix,
          referenceChangeData.getObjectChange().getProperties(), htmlFormatted);
      formatReferences(objectApi, localeSettingApi, sb, newPrefix,
          referenceChangeData.getObjectChange().getReferences(), htmlFormatted);
    }
  }

  private static void formatProperties(ObjectApi objectApi, LocaleSettingApi localeSettingApi,
      StringBuilder sb,
      String prefix, List<PropertyChangeData> properties, boolean htmlFormatted) {
    for (PropertyChangeData propertyChangeData : properties) {
      String path = prefix + propertyChangeData.getPath();
      if (path != null &&
          (path.toLowerCase().endsWith("uri"))
          || path.toLowerCase().endsWith("uuid")) {
        continue;
      }
      List<String> translations = translate(localeSettingApi, path);
      if (!translations.isEmpty()) {
        path += " " + translations;
      }
      Object oldValue = convertOffsetDatetime(objectApi, propertyChangeData.getOldValue());
      Object newValue = convertOffsetDatetime(objectApi, propertyChangeData.getNewValue());
      if (htmlFormatted) {
        sb.append("<b>")
            .append(path)
            .append("</b>")
            .append(": ");
        if (oldValue != null) {
          sb
              .append(oldValue)
              .append("<b>")
              .append(" -> ")
              .append("</b>");
        }
        sb
            .append(newValue)
            .append("<br>");
      } else {
        sb.append(path)
            .append(": ")
            .append(oldValue)
            .append("\n")
            .append(" -> ")
            .append(newValue);
      }
    }
  }

  private static Object convertOffsetDatetime(ObjectApi objectApi, Object date) {
    if (date == null) {
      return date;
    }
    try {
      OffsetDateTime offsetDateTime = objectApi.asType(OffsetDateTime.class, date);
      if (offsetDateTime != null) {
        return dateTimeFormatter
            .format(offsetDateTime.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
      }
    } catch (Exception e) {
    }
    return date;

  }

  /**
   * Returns the translation of the path. <body>
   * <p>
   * E.g.
   * </p>
   * <p>
   * document.types.0.name -> [document.types, name] -> [Dokumentum típusok, név]
   * </p>
   * </body>
   *
   * @param localeSettingApi
   * @param path
   * @return
   */
  private static List<String> translate(LocaleSettingApi localeSettingApi, String path) {
    String[] splitByNumbers = path.split("\\d");
    List<String> translations = new ArrayList<>();
    for (int i = 0; i < splitByNumbers.length; ++i) {
      String[] splitByDots = splitByNumbers[i].split("\\.");
      String translation = localeSettingApi.get(splitByDots);
      if (!splitByDots[splitByDots.length - 1].equals(translation)) {
        translations.add(translation);
      }
    }
    return translations;
  }
}
