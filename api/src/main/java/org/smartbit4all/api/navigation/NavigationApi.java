package org.smartbit4all.api.navigation;

import java.util.List;
import org.smartbit4all.api.ApiItemChangeEvent;
import org.smartbit4all.api.navigation.bean.NavigationEntry;

/**
 * The platform level collaboration API for navigating the data nodes. In an application there can
 * be more navigation identified by a unique name. The APIs have a primary one that is responsible
 * for delegating the request to the appropriate instance.
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
   * If we have a navigation then we can start a navigation session by calling the {@link #start()}
   * to retrieve the entries directly attached into the root. The {@link NavigationEntry} we get
   * back will contains every information to navigate further.
   * 
   * @return The list of the first level entries.
   */
  List<NavigationEntry> start();

  /**
   * The expand will retrieve the available entries starting from the given entry. The expand will
   * retrieve the related entries from the underlying api.
   * 
   * @param entry The entry to expand. The entry will be filled with new entries as children.
   * @return The api will return the newly created entries for further processing.
   */
  List<ApiItemChangeEvent<NavigationEntry>> expand(NavigationEntry entry);

  /**
   * This function will retrieve again the navigation entries and it's children. The object
   * structure will be refreshed but also we get back the list of changes in an
   * {@link ApiItemChangeEvent} list with the changed {@link NavigationEntry}s.
   * 
   * @param entry The entry to refresh. The refresh
   * @return
   */
  List<ApiItemChangeEvent<NavigationEntry>> refresh(NavigationEntry entry);

}
