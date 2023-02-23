package org.smartbit4all.api.object.bean;

import java.util.List;

public class ObjectChangeDataToString {

  public static String toFormattedString(ObjectChangeData change) {
    StringBuilder sb = new StringBuilder();
    formatProperties(sb, "", change.getProperties());
    formatReferences(sb, "", change.getReferences());
    return sb.toString();
  }

  private static void formatReferences(StringBuilder sb, String prefix,
      List<ReferenceChangeData> references) {
    for (ReferenceChangeData referenceChangeData : references) {
      String newPrefix = prefix + referenceChangeData.getPath() + ".";
      formatProperties(sb, newPrefix,
          referenceChangeData.getObjectChange().getProperties());
      formatReferences(sb, newPrefix, referenceChangeData.getObjectChange().getReferences());
    }
  }

  private static void formatProperties(StringBuilder sb, String prefix,
      List<PropertyChangeData> properties) {
    for (PropertyChangeData propertyChangeData : properties) {
      sb.append("<b>")
          .append(prefix)
          .append(propertyChangeData.getPath())
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
}
