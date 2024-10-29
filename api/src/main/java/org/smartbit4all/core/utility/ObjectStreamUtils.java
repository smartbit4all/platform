package org.smartbit4all.core.utility;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectNodeList;
import org.smartbit4all.core.object.ObjectNodeReference;
import org.springframework.util.StringUtils;

public final class ObjectStreamUtils {

  private ObjectStreamUtils() {}

  public static Optional<ObjectNode> toOptionalNode(ObjectNodeReference ref) {
    return ref == null || ref.isEmpty() ? Optional.empty() : Optional.of(ref.get());
  }

  public static Optional<URI> toOptionalUri(ObjectNodeReference ref) {
    return ref == null || ref.isEmpty()
        ? Optional.empty()
        : Optional.ofNullable(ref.getObjectUri());
  }

  /**
   * Checks whether an {@link ObjectNodeReference} is considered present or not.
   * 
   * @param ref an {@link ObjectNodeReference} to examine, nullable
   * @return false, if the ref is either null or empty, true otherwise
   */
  public static boolean isPresent(final ObjectNodeReference ref) {
    return ref != null && ref.isPresent();
  }

  /**
   * Converts an {@link ObjectNodeReference} to a {@link Stream} of a single {@link ObjectNode}, or
   * an empty {@code Stream} if the reference was empty.
   *
   * <p>
   * May incur I/O operation if the reference was not already loaded.
   *
   * <p>
   * Example usage: shorthand for loading only present references in an {@code ObjectNodeList}.
   *
   * <pre>
   * <code>
   * final ObjectNodeList nodes = ...;
   * List&lt;ObjectNode&gt; nodesPresent = nodes.stream()
   *   .flatMap(ObjectStreamUtils::toNode)
   *   .collect(toList());
   * </code>
   * </pre>
   *
   * @param ref an {@link ObjectNodeReference} to map, nullable
   * @return a {@link Stream} either containing the {@link ObjectNode} the reference was pointing
   *         to, or an empty {@code Stream} if the reference was empty
   * @see ObjectNodeList#nodeStream()
   */
  public static Stream<ObjectNode> toNode(ObjectNodeReference ref) {
    return ref == null || ref.isEmpty() ? Stream.empty() : Stream.of(ref.get());
  }

  /**
   * Converts an {@link ObjectNodeReference} to a {@link Stream} of a single {@link URI}, or an
   * empty {@code Stream} if the reference was empty.
   *
   * <p>
   * Example usage: shorthand for collecting the {@code URI}s in an {@code ObjectNodeList}.
   *
   * <pre>
   * <code>
   * final ObjectNodeList nodes = ...;
   * List&lt;URI&gt; nodesPresent = nodes.stream()
   *   .flatMap(ObjectStreamUtils::toUri)
   *   .collect(toList());
   * </code>
   * </pre>
   *
   * @param ref an {@link ObjectNodeReference} to map, nullable
   * @return a {@link Stream} either containing the {@link URI} the reference was pointing to, or an
   *         empty {@code Stream} if the reference was empty
   */
  public static Stream<URI> toUri(ObjectNodeReference ref) {
    return ref == null || ref.isEmpty() || ref.getObjectUri() == null
        ? Stream.empty()
        : Stream.of(ref.getObjectUri());
  }

  /**
   * Streams the elements of an {@link ObjectNodeList}, similar to {@link ObjectNodeList#stream()},
   * but in reverse order.
   *
   * <p>
   * It is highly advised to process the returned {@link Stream} sequentially.
   *
   * @param list an {@link ObjectNodeList} to stream, nullable
   * @return a {@link Stream} of {@link ObjectNodeReference}s found in the list in reverse order.
   */
  public static Stream<ObjectNodeReference> streamReversed(ObjectNodeList list) {
    if (list == null || list.isEmpty()) {
      return Stream.empty();
    }

    final ObjectNodeReference[] arr = list.stream().toArray(ObjectNodeReference[]::new);
    return IntStream.range(0, arr.length).mapToObj(i -> arr[arr.length - 1 - i]);
  }

  public static <T> Stream<T> toValue(ObjectNodeReference ref, Class<T> type, String... path) {
    return toNode(ref).map(n -> n.getValue(type, path));
  }

  /**
   * Provides a convenient {@link Predicate} to check whether an {@link ObjectNode} has the desired
   * value at an arbitrary path.
   *
   * <p>
   * Example usage: Retain all nodes that reference cats of 4 years of age:
   *
   * <pre>
   * <code>
   * List&lt;ObjectNode&gt; catNodes = ...;
   * List&lt;URI&gt; fourYearOldCatUris = catNodes.stream()
   *   .filter(matching(4, "catData", "age"))
   *   .map(ObjectNode::getObjectUri)
   *   .collect(toList());
   * </code>
   * </pre>
   *
   * @param <T> the type of the value to check against
   * @param value the value the nodes must possess, nullable
   * @param path the path from the {@link ObjectNode} to traverse to reach the value.
   * @return an {@link ObjectNode} {@link Predicate} testing for value match
   */
  public static <T> Predicate<? super ObjectNode> matching(T value, String... path) {
    if (value == null) {
      return n -> n.getValue(path) == null;
    }

    return n -> value.equals(n.getValue(value.getClass(), path));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static boolean mapEq(Map m1, Map m2) {
    if (m1 == null && m2 == null) {
      return true;
    }
    if (m1 == null || m2 == null) {
      return false;
    }
    return m1.keySet().stream()
        .noneMatch(it -> !m2.containsKey(it))
        && m2.keySet().stream()
            .noneMatch(it -> !m1.containsKey(it))
        && m1.keySet().stream()
            .noneMatch(it -> {
              Object v1 = m1.get(it);
              Object v2 = m2.get(it);
              if (v1 instanceof URI) {
                v1 = String.valueOf(v1);
              }
              if (v2 instanceof URI) {
                v2 = String.valueOf(v2);
              }
              if (v1 instanceof Boolean
                  && Boolean.FALSE.equals(v1)
                  && StringUtils.isEmpty(v2)) {
                return false;
              }
              if (v2 instanceof Boolean
                  && Boolean.FALSE.equals(v2)
                  && StringUtils.isEmpty(v1)) {
                return false;
              }
              if (v1 instanceof Map && v2 instanceof Map) {
                Map innerMap1 = (Map) v1;
                Map innerMap2 = (Map) v2;
                return !mapEq(innerMap1, innerMap2);
              }
              return !Objects.equals(v1, v2);
            });
  }

}
