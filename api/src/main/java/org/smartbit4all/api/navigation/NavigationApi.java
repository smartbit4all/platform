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
package org.smartbit4all.api.navigation;

import java.net.URI;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.invocation.bean.InvocationRequestTemplate;
import org.smartbit4all.api.navigation.bean.NavigationConfig;
import org.smartbit4all.api.navigation.bean.NavigationEntry;
import org.smartbit4all.api.navigation.bean.NavigationReferenceEntry;

/**
 * The platform level collaboration API for navigating the data nodes. The api is built on the
 * {@link NavigationConfig} that describes the possible navigations in an situation. The navigation
 * can be generic and can support wide range of possibilities but the configuration can tailor it to
 * an exact requirement.
 * 
 * 
 * The basic concept of the API is the {@link NavigationEntry} that is identified by an URI:
 * 
 * navigation://navigationPath
 * 
 * example:
 * 
 * certification://mysigneddocs/2020/november/doc123#/case4354/signed/doc123
 * certification://docstosign/doc123#/case4354/signed/doc123
 * 
 * The navigation identifies a navigation instance configured by the spring application context. The
 * navigation path identifies the path for the given navigation and let it be used to figure out the
 * parameterization of the given entry. And at the end the id is the unique identifier for the API
 * that is responsible for the "data" we have at the entry.
 * 
 * @author Peter Boros
 */
public interface NavigationApi {

  String name();

  /**
   * The navigate will queries all the data sources to populate the associations starts from the
   * given entry.
   * 
   * @param objectUri The URI of the api object that is the starting point of the navigation. It
   *        must be a valid URI that can be the starting point of the associations we provided.
   * @param associationMetaUris The list of associations to identify the direction we want to
   *        navigate. If we skip this parameter (null) then we will have all the associations
   *        defined in the {@link NavigationEntry} meta.
   * @return The map of the references by the URI of association meta we passed in the associations
   *         parameter.
   * @throws Exception
   */
  Map<URI, List<NavigationReferenceEntry>> navigate(URI objectUri, List<URI> associationMetaUris);

  /**
   * The navigate will queries all the data sources to populate the associations starts from the
   * given entry.
   * 
   * @param objectUri The URI of the api object that is the starting point of the navigation. It
   *        must be a valid URI that can be the starting point of the associations we provided.
   * @param associationMetaUris The list of associations to identify the direction we want to
   *        navigate. If we skip this parameter (null) then we will have all the associations
   *        defined in the {@link NavigationEntry} meta.
   * @param onChange The callback function to call when the object identified by the URI has been
   *        changed.
   * @return The map of the references by the URI of association meta we passed in the associations
   *         parameter.
   * @throws Exception
   */
  Map<URI, List<NavigationReferenceEntry>> navigate(URI objectUri, List<URI> associationMetaUris,
      InvocationRequestTemplate onChange);

  /**
   * Retrieve the entries from the navigations.
   * 
   * @param uris The {@link URI} list that we have.
   * @return The navigation entry if we found it or null if missing.
   */
  NavigationEntry getEntry(URI entryMetaUri, URI objectUri);

}
