package org.smartbit4all.core.object;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.smartbit4all.api.object.bean.ObjectNodeData;
import org.smartbit4all.core.utility.StringConstant;
import com.google.common.base.Strings;

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
    for (Entry<String, ObjectNode> entry : objectNode.getReferenceNodes()
        .entrySet()) {
      sb.append(subIndent).append(entry.getKey())
          .append(StringConstant.ARROW).append(StringConstant.NEW_LINE);
      traverseNodeVersionTree(entry.getValue(), sb, subIndent + INDENT_INCREMENT, propertyMap);
    }
    for (Entry<String, List<ObjectNode>> entry : objectNode.getReferenceLists()
        .entrySet()) {
      if (!entry.getValue().isEmpty()) {
        sb.append(subIndent).append(entry.getKey())
            .append(StringConstant.COLON).append(StringConstant.NEW_LINE);
        for (ObjectNode node : entry.getValue()) {
          traverseNodeVersionTree(node, sb, subIndent + INDENT_INCREMENT, propertyMap);
        }
      }
    }
    // TODO maps!
  }

  /**
   * A recursive function to collect all nodes inclusively itself into a {@link Stream} for further
   * processing.
   * 
   * @return The nodes {@link Stream}.
   */
  public static Stream<ObjectNodeData> allNodes(ObjectNodeData data) {
    return Stream.of(
        Stream.of(data),
        data.getReferenceValues().values().stream().flatMap(ObjectNodes::allNodes),
        data.getReferenceListValues().values().stream().flatMap(List::stream)
            .flatMap(ObjectNodes::allNodes),
        data.getReferenceMapValues().values().stream()
            .flatMap(n -> n.values().stream())
            .flatMap(ObjectNodes::allNodes))
        .flatMap(s -> s);
  }

  public static ObjectNode of(ObjectApi objectApi, ObjectNodeData data) {
    return new ObjectNode(objectApi, data);
  }

  /**
   * TODO add more specifiec gets, Object is too general
   * 
   * @param data
   * @param paths
   * @return
   */
  public static Object getValue(ObjectNodeData data, String... paths) {
    if (paths == null || paths.length == 0) {
      return data;
    }
    String path = paths[0];
    if (Strings.isNullOrEmpty(path)) {
      throw new IllegalArgumentException("Path part cannot be null or empty");
    }
    Object result = getValueFromReference(data, paths);
    if (result != null) {
      return result;
    }
    result = getValueFromReferenceList(data, paths);
    if (result != null) {
      return result;
    }
    result = getValueFromReferenceMap(data, paths);
    if (result != null) {
      return result;
    }
    return getValueFromObjectMap(data.getObjectAsMap(), paths);
  }

  private static Object getValueFromReference(ObjectNodeData data, String[] paths) {
    String path = paths[0];
    ObjectNodeData nodeOnPath = data.getReferenceValues().get(path);
    if (nodeOnPath != null) {
      String[] subPaths = Arrays.copyOfRange(paths, 1, paths.length);
      return getValue(nodeOnPath, subPaths);
    }
    return null;
  }

  private static Object getValueFromReferenceList(ObjectNodeData data, String[] paths) {
    String path = paths[0];
    List<ObjectNodeData> listOnPath = data.getReferenceListValues().get(path);
    if (listOnPath != null) {
      if (paths.length == 1) {
        // TODO wrap into ObjectNodeList
        return listOnPath;
      }
      String idxString = paths[1];
      try {
        Integer idx = Integer.valueOf(idxString);
        String[] subPaths = Arrays.copyOfRange(paths, 2, paths.length);
        return getValue(listOnPath.get(idx), subPaths);
      } catch (NumberFormatException ex1) {
        throw new IllegalArgumentException("List index is not a number: "
            + path + "(" + idxString + ")");
      } catch (IndexOutOfBoundsException ex2) {
        throw new IllegalArgumentException("List item not found by index: "
            + path + "(" + idxString + ")");
      }
    }
    return null;
  }

  private static Object getValueFromReferenceMap(ObjectNodeData data, String[] paths) {
    String path = paths[0];
    Map<String, ObjectNodeData> mapOnPath = data.getReferenceMapValues().get(path);
    if (mapOnPath != null) {
      if (paths.length == 1) {
        // TODO Wrap into ObjectNodeMap
        return mapOnPath;
      }
      String key = paths[1];
      if (mapOnPath.containsKey(key)) {
        String[] subPaths = Arrays.copyOfRange(paths, 2, paths.length);
        return getValue(mapOnPath.get(key), subPaths);
      }
      throw new IllegalArgumentException(
          "Map item not found by index: " + path + "(" + key + ")");
    }
    return null;
  }

  private static Object getValueFromObjectMap(Map<String, Object> map, String... paths) {
    if (paths != null && paths.length > 0) {
      String path = paths[0];
      Object value = map.get(path);
      if (paths.length == 1) {
        return value;
      }
      if (value instanceof Map) {
        @SuppressWarnings("unchecked")
        Map<String, Object> subMap = (Map<String, Object>) value;
        String[] subPaths = Arrays.copyOfRange(paths, 2, paths.length);
        return getValueFromObjectMap(subMap, subPaths);
      }
      return null;
    }
    return map;
  }

}
