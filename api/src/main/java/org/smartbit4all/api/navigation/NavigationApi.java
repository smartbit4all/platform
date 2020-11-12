package org.smartbit4all.api.navigation;

import java.net.URI;
import java.util.List;
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
   * This can retrieve the navigation entry identified by the uri.
   * 
   * @param uri
   * @return
   */
  NavigationEntry get(URI uri);

  /**
   * The children of the navigation entry identified by the uri.
   * 
   * @param uri
   * @return
   */
  List<NavigationEntry> children(URI uri);

  /**
   * The parent entry of the navigation entry identified by the uri.
   * 
   * @param uri
   * @return
   */
  NavigationEntry parent(URI uri);

}
