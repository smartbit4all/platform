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
import java.util.Optional;
import java.util.function.Consumer;
import org.smartbit4all.api.navigation.bean.NavigationConfig;
import org.smartbit4all.api.navigation.bean.NavigationEntry;
import org.smartbit4all.api.navigation.bean.NavigationReferenceEntry;
import org.smartbit4all.core.object.ApiObjectRef;

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
   * @param callBack This callback api could be an instance that should be notified when some data
   *        of the newly created entries changed.
   * @return The map of the references by the URI of association meta we passed in the associations
   *         parameter.
   * @throws Exception
   */
  Map<URI, List<NavigationReferenceEntry>> navigate(URI objectUri, List<URI> associationMetaUris,
      Consumer<URI> nodeChangedListener);

  /**
   * Retrieve the entries from the navigations.
   * 
   * @param uris The {@link URI} list that we have.
   * @return The navigation entry if we found it or null if missing.
   */
  NavigationEntry getEntry(URI entryMetaUri, URI objectUri);

  /**
   * The navigation api offer a contribution for the apis to participate in a navigation. If we have
   * a navigation node in a navigation that it might have an object behind. With this service we can
   * ask the underlying api to load the object and we can use it inside an {@link ApiObjectRef} to
   * access it's properties.
   *
   * @param entryMetaUri entry meta.
   * @param objectUri The uri of the object.
   * @return The {@link ApiObjectRef} initiated with the object identified by the uri.
   */
  Optional<ApiObjectRef> loadObject(URI entryMetaUri, URI objectUri);

  /**
   * Constructs a navigation with the called api and navigation config, only if a config can be
   * found by the given name in the api's navConfigsByName map, after that the root nodes will be
   * added into the constructed navigation.
   * 
   * @param navigationConfigName The name of the configuration used for constructing the navigation.
   * @return
   */
  Navigation start(String navigationConfigName);

}
