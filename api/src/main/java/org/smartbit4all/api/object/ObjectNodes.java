package org.smartbit4all.api.object;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.smartbit4all.core.object.ReferenceDefinition;
import org.smartbit4all.core.utility.StringConstant;

public class ObjectNodes {

  public static final String INDENT_INCREMENT = "  ";

  private ObjectNodes() {
    super();
  }

  /**
   * The versions of the object tree defined by the given {@link ObjectNode}. This collects in a in
   * order traversal way all the versions of the object tree. A node is rendered with the type - the
   * version number. The tree is defined by the {@link ReferenceDefinition}s.
   * 
   * @param objectNode
   * @return
   */
  public static String versionTree(ObjectNode objectNode, ObjectProperties properties) {
    StringBuilder sb = new StringBuilder();
    traverseNodeVersionTree(objectNode, sb, StringConstant.EMPTY, properties.getPropertyMap());
    return sb.toString();
  }

  public static void traverseNodeVersionTree(ObjectNode objectNode, StringBuilder sb,
      String indent, Map<Class<?>, List<String>> propertyMap) {
    // Render the actual node
    List<String> propertyList = propertyMap.get(objectNode.getDefinition().getClazz());
    String objectLabel =
        propertyList != null
            ? propertyList.stream().map(p -> ((String) objectNode.getObjectAsMap().get(p)))
                .collect(Collectors.joining(StringConstant.COMMA_SPACE))
            : null;
    sb.append(indent).append(objectNode.getDefinition().getClazz().getSimpleName())
        .append(objectLabel != null
            ? StringConstant.LEFT_PARENTHESIS + objectLabel + StringConstant.RIGHT_PARENTHESIS
            : StringConstant.EMPTY)
        .append(StringConstant.SPACE_HYPHEN_SPACE).append(objectNode.getVersionNr())
        .append(StringConstant.NEW_LINE);
    // Traverse the referred nodes also.
    String subIndent = indent + INDENT_INCREMENT;
    for (Entry<ReferenceDefinition, ObjectNode> entry : objectNode.getReferenceValues()
        .entrySet()) {
      sb.append(subIndent).append(entry.getKey().getSourcePropertyPath())
          .append(StringConstant.ARROW).append(StringConstant.NEW_LINE);
      traverseNodeVersionTree(entry.getValue(), sb, subIndent + INDENT_INCREMENT, propertyMap);
    }
    for (Entry<ReferenceDefinition, ReferenceListEntry> entry : objectNode.getReferenceListValues()
        .entrySet()) {
      if (!entry.getValue().getPublicNodeList().isEmpty()) {
        sb.append(subIndent).append(entry.getKey().getSourcePropertyPath())
            .append(StringConstant.COLON).append(StringConstant.NEW_LINE);
        for (ObjectNode node : entry.getValue().getPublicNodeList()) {
          traverseNodeVersionTree(node, sb, subIndent + INDENT_INCREMENT, propertyMap);
        }
      }
    }
  }

}
