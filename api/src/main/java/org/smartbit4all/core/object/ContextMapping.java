package org.smartbit4all.core.object;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.object.bean.ContextMappingDefinition;
import org.smartbit4all.api.object.bean.ContextMappingItem;
import org.smartbit4all.api.object.bean.ObjectMappingDefinition;

/**
 * 
 * @author Peter Boros
 */
public final class ContextMapping {

  private static final Logger log = LoggerFactory.getLogger(ContextMapping.class);

  private WeakReference<ObjectApi> objectApiRef;

  private final ContextObject from;

  private final ContextObject to;

  private ContextMappingDefinition definition;

  ContextMapping(ObjectApi objectApi) {
    super();
    this.from = new ContextObject(objectApi);
    this.to = new ContextObject(objectApi);
    this.objectApiRef = new WeakReference<>(objectApi);
  }

  public ContextMapping mapping(ContextMappingDefinition definition) {
    this.definition = definition;
    return this;
  }

  private ObjectApi objectApi() {
    return objectApiRef.get();
  }

  public ContextObject from() {
    return from;
  }

  public ContextObject to() {
    return to;
  }

  /**
   * Executes the mappings evaluates all the mapping and modify the target context with the result.
   * Depending on the target context the result is available in the target context in {@link Map},
   * in Beans or in {@link ObjectNode}.
   * 
   * @return
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public void execute() {
    ObjectApi objectApi = objectApi();
    for (ContextMappingItem mapping : definition.getItems()) {
      List<String> outputPath = mapping.getOutputPath();
      ObjectMappingDefinition valueMapping = mapping.getValueMapping();
      if (valueMapping != null) {
        Object value = objectApi.mapper().mapping(valueMapping).setContext(from).execute();
        // Depending on the parameters set the value into the target context.
        to.setValue(outputPath, value, mapping.getMerge() == null ? true : mapping.getMerge());
      } else {
        log.warn("The value mapping of the " + mapping + " is missing, the mapping is skipped");
      }
    }
  }

}
