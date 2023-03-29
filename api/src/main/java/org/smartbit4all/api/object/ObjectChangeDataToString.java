package org.smartbit4all.api.object;

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.object.bean.ObjectChangeData;
import org.smartbit4all.api.object.bean.PropertyChangeData;
import org.smartbit4all.api.object.bean.ReferenceChangeData;
import org.smartbit4all.api.setting.LocaleSettingApi;

public class ObjectChangeDataToString {

  public static String toFormattedString(LocaleSettingApi localeSettingApi,
      ObjectChangeData change) {
    StringBuilder sb = new StringBuilder();
    formatProperties(localeSettingApi, sb, "", change.getProperties());
    formatReferences(localeSettingApi, sb, "", change.getReferences());
    return sb.toString();
  }

  private static void formatReferences(LocaleSettingApi localeSettingApi, StringBuilder sb,
      String prefix,
      List<ReferenceChangeData> references) {
    for (ReferenceChangeData referenceChangeData : references) {
      String newPrefix = prefix + referenceChangeData.getPath() + ".";
      formatProperties(localeSettingApi, sb, newPrefix,
          referenceChangeData.getObjectChange().getProperties());
      formatReferences(localeSettingApi, sb, newPrefix,
          referenceChangeData.getObjectChange().getReferences());
    }
  }

  private static void formatProperties(LocaleSettingApi localeSettingApi, StringBuilder sb,
      String prefix,
      List<PropertyChangeData> properties) {
    for (PropertyChangeData propertyChangeData : properties) {
      String path = prefix + propertyChangeData.getPath();
      List<String> translations = translate(localeSettingApi, path);
      if (!translations.isEmpty()) {
        path += " " + translations;
      }
      sb.append("<b>")
          .append(path)
          .append("</b>")
          .append(": ")
          .append(propertyChangeData.getOldValue())
          .append("<b>")
          .append(" -> ")
          .append("</b>")
          .append(propertyChangeData.getNewValue())
          .append("<br>");
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