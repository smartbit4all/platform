/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.domain.service.dataset;

import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.core.utility.StringConstant;

/**
 * This API object is a descriptor for the data set. This is used by the {@link DataSetApi}.
 * 
 * @author Peter Boros
 */
public class DataSetEntry {

  private static final Logger log = LoggerFactory.getLogger(DataSetEntry.class);

  /**
   * The identification of the data set. Consists of the provider as scheme. The provider is a kind
   * of storage for the data set. The path refers the storage inside the provider. Something like
   * numbers or strings. Usually it is the table name for example in case of RDBMS storage. The
   * fragment will identify the data set as a name inside the given provider.
   */
  private URI uri;

  /**
   * The unique identifier inside the given storage. It's the fragment part of the URI.
   */
  private Long id;

  /**
   * The URI of the property that is stored in the data set. It's important to see what is the
   * content. It can help to identify how to apply this set in a filtering situation. The URI looks
   * like this: provider:/entity#property
   */
  private URI propertyUri;

  public DataSetEntry(URI propertyUri, Long id) {
    super();
    this.id = id;
    this.propertyUri = propertyUri;
    try {
      this.uri = new URI(propertyUri.toString() + StringConstant.HASH + id);
    } catch (URISyntaxException e) {
      log.error("Unable to construct data set uri (property=" + propertyUri + ", id = " + id + ")",
          e);
    }
  }

  public final URI getUri() {
    return uri;
  }

  public final void setUri(URI uri) {
    this.uri = uri;
  }

  public final Long getId() {
    return id;
  }

  public final void setId(Long id) {
    this.id = id;
  }

  public final URI getPropertyUri() {
    return propertyUri;
  }

  public final void setPropertyUri(URI propertyUri) {
    this.propertyUri = propertyUri;
  }



}
