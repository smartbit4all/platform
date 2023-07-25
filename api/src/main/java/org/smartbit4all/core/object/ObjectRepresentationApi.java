package org.smartbit4all.core.object;

import java.net.URI;

/**
 * 
 * @author Szabolcs Bazil Papp
 *
 */
public interface ObjectRepresentationApi {

  ObjectDisplay getObjectDisplay(URI objectUri, String extensionName);

}
