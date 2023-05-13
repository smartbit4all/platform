package org.smartbit4all.api.object;

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.object.bean.ObjectChangeData;
import org.smartbit4all.api.object.bean.PropertyChangeData;
import org.smartbit4all.api.object.bean.ReferenceChangeData;
import org.smartbit4all.api.setting.LocaleSettingApi;

public class ObjectChangeDataToString {

  public static String toFormattedString(LocaleSettingApi localeSettingApi,
      ObjectChangeData change, boolean htmlFormatted) {
    StringBuilder sb = new StringBuilder();
    formatProperties(localeSettingApi, sb, "", change.getProperties(), htmlFormatted);
    formatReferences(localeSettingApi, sb, "", change.getReferences(), htmlFormatted);
    return sb.toString();
  }

  private static void formatReferences(LocaleSettingApi localeSettingApi, StringBuilder sb,
      String prefix, List<ReferenceChangeData> references, boolean htmlFormatted) {
    for (ReferenceChangeData referenceChangeData : references) {
      String newPrefix = prefix + referenceChangeData.getPath() + ".";
      formatProperties(localeSettingApi, sb, newPrefix,
          referenceChangeData.getObjectChange().getProperties(), htmlFormatted);
      formatReferences(localeSettingApi, sb, newPrefix,
          referenceChangeData.getObjectChange().getReferences(), htmlFormatted);
    }
  }

  private static void formatProperties(LocaleSettingApi localeSettingApi, StringBuilder sb,
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
      if (htmlFormatted) {
        sb.append("<b>")
            .append(path)
            .append("</b>")
            .append(": ");
        if (propertyChangeData.getOldValue() != null) {
          sb
              .append(propertyChangeData.getOldValue())
              .append("<b>")
              .append(" -> ")
              .append("</b>");
        }
        sb
            .append(propertyChangeData.getNewValue())
            .append("<br>");
      } else {
        sb.append(path)
            .append(": ")
            .append(propertyChangeData.getOldValue())
            .append("\n")
            .append(" -> ")
            .append(propertyChangeData.getNewValue());
      }
    }
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
