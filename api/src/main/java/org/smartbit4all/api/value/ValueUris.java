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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.UriUtils;

public class ValueUris extends UriUtils {
  
  private static final Logger log = LoggerFactory.getLogger(ValueUris.class);

  public static final String VALUE_SCHEME = "value";
  
  private ValueUris() {
  }
  
  /**
   * Creates an uri that can be used to request the possible values for an element. 
   * @param source
   *    The identifier of the source that provides the values.
   * @param objectCode
   *    The identifier of the object that the can hold values.
   * @return
   */
  public static URI createPossibleValueUri(String source, String objectCode) {
    return createUri(source, objectCode, null);
  }
  
  /**
   * Creates the uri of a Value object.
   * @param source
   *    The identifier of the source that provided the value
   * @param objectCode
   *    The identifier of the element the value belongs to. For example it can be an id of a selector field.
   * @param valueId
   *    The identifier of the value
   * @return
   */
  public static URI createUri(String source, String objectCode, String valueId) {
    String formattedSource = formatUriHost(source);
    
    objectCode = objectCode == null ? null : "/" + objectCode;
    return createUri(VALUE_SCHEME, formattedSource, objectCode, valueId);
  }
  
  public static URI createUri(URI valueUri, String valueId) {
    String source = getSource(valueUri);
    String objectCode = getObjectCode(valueUri);
    objectCode = objectCode == null ? null : "/" + objectCode;
    return createUri(VALUE_SCHEME, source, objectCode, valueId);
  }

  public static String getSource(URI valueUri) {
    checkValueURI(valueUri);
    return valueUri.getAuthority();
  }
  
  public static String getObjectCode(URI valueUri) {
    checkValueURI(valueUri);
    String objectCodeWithSlash = valueUri.getPath();
    return objectCodeWithSlash.substring(1, objectCodeWithSlash.length());
  }
  
  public static String getValueId(URI valueUri) {
    checkValueURI(valueUri);
    return valueUri.getFragment();
  }
  
  public static void checkValueURI(URI uriToCheck) {
    checkURI(uriToCheck, VALUE_SCHEME, null, null);
  }
  
}
