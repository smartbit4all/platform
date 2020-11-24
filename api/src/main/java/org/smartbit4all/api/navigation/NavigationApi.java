package org.smartbit4all.api.navigation;

import java.net.URI;
import java.util.List;
import java.util.Map;
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
   * @param entry The navigation entry that is the starting point of the navigation.
   * @param associations The list of associations to identify the direction we want to navigate. If
   *        we skip this parameter (null) then we will have all the associations defined in the
   *        {@link NavigationEntry} meta.
   * @return The map of the references by the association meta we required.
   * @throws Exception
   */
  Map<URI, List<NavigationReferenceEntry>> navigate(NavigationEntry entry,
      List<URI> associations);

  /**
   * Retrieve the entries from the navigations.
   * 
   * @param uris The {@link URI} list that we have.
   * @return The list of navigation entries.
   * @throws Exception
   */
  List<NavigationEntry> getEntries(List<URI> uris);

}
