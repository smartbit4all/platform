/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.api.value;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.core.utility.UriUtils;

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
