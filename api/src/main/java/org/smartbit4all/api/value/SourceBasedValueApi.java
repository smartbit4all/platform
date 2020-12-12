package org.smartbit4all.api.value;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.UriUtils;
import org.smartbit4all.api.value.bean.Value;

public class SourceBasedValueApi implements ValueApi {

  private static final Logger log = LoggerFactory.getLogger(SourceBasedValueApi.class);

  Map<String, ValueSource> sourcesById = new HashMap<>();
  Map<String, ValueSource> sourcesByObjectCode = new HashMap<>();

  public SourceBasedValueApi(List<ValueSource> sources) {
    sources.forEach(source -> {
      String formatedUriSource = UriUtils.formatUriHost(source.getSourceId());
      sourcesById.put(formatedUriSource, source);
      List<String> providedObjectCodes = source.getProvidedObjectCodes();
      if (providedObjectCodes != null) {
        for (String objectCode : providedObjectCodes) {
          if (sourcesByObjectCode.get(objectCode) != null) {
            log.warn("There are more value source registered for objectCode: " + objectCode);
          }
          sourcesByObjectCode.put(objectCode, source);
        }
      }
    });
  }

  @Override
  public List<Value> getPossibleValues(URI uri) throws Exception {
    ValueUris.checkValueURI(uri);
    ValueSource source = null;
    String sourceId = ValueUris.getSource(uri);
    if (sourceId != null && !sourceId.isEmpty()) {
      source = sourcesById.get(sourceId);
    } else {
      String objectCode = ValueUris.getObjectCode(uri);
      if (objectCode != null && !objectCode.isEmpty()) {
        source = sourcesByObjectCode.get(objectCode);
      }
    }
    if (source != null) {
      return source.getPossibleValues(uri);
    }
    log.warn("There is no value source registered to provide possible values for uri: "
        + uri.toString());
    return Collections.emptyList();
  }

}
