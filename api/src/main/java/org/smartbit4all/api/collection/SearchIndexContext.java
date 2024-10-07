package org.smartbit4all.api.collection;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.smartbit4all.core.object.ObjectNode;

public class SearchIndexContext {

  /**
   * Current row's ObjectNode. This is the same ObjectNode which passes as input to
   * {@link SearchIndexMappingProperty#complexProcessor}.
   */
  private ObjectNode rowNode;

  /**
   * Variables can be used to store row level information, for example if a calculation is used for
   * several properties, this map can store the calculation result. Note, this will be cleared when
   * processing next row / objectNode in searchIndex.
   */
  private Map<String, Object> rowVariables = new HashMap<>();

  /**
   * Similar to {@link #rowVariables} but can be used to store global, searchIndex level
   * information, will not be cleared when processing next row / objectNode in searchIndex.
   */
  private Map<String, Object> globalVariables = new HashMap<>();

  public ObjectNode getRowNode() {
    return rowNode;
  }

  public void setRowNode(ObjectNode rowNode) {
    this.rowNode = rowNode;
  }

  public SearchIndexContext rowNode(ObjectNode rowNode) {
    this.rowNode = rowNode;
    return this;
  }

  public Map<String, Object> getRowVariables() {
    return rowVariables;
  }

  public void setRowVariables(Map<String, Object> rowVariables) {
    this.rowVariables = rowVariables;
  }

  public SearchIndexContext rowVariables(Map<String, Object> rowVariables) {
    this.rowVariables = rowVariables;
    return this;
  }

  public SearchIndexContext putRowVariablesItem(String key, Object rowVariablesItem) {
    this.rowVariables.put(key, rowVariablesItem);
    return this;
  }

  public Object getOrCreateRowVariable(String key,
      Function<? super String, ? extends Object> func) {
    return this.rowVariables.computeIfAbsent(key, func);
  }

  public Map<String, Object> getGlobalVariables() {
    return globalVariables;
  }

  public void setGlobalVariables(Map<String, Object> globalVariables) {
    this.globalVariables = globalVariables;
  }

  public SearchIndexContext globalVariables(Map<String, Object> globalVariables) {
    this.globalVariables = globalVariables;
    return this;
  }

  public SearchIndexContext putGlobalVariablesItem(String key, Object globalVariablesItem) {
    this.globalVariables.put(key, globalVariablesItem);
    return this;
  }

  public Object getOrCreateGlobalVariable(String key,
      Function<? super String, ? extends Object> func) {
    return this.globalVariables.computeIfAbsent(key, func);
  }

}
