package org.smartbit4all.api.org;

import java.net.URI;

/**
 * The {@link OrgApi} based settings manages collections and maps
 * 
 * @author Peter Boros
 */
public interface OrgBasedSettingApi {

  <T> T getUserObject(URI userUri, Class<T> clazz);

  <T> T getUserObject(URI userUri, String objectName, Class<T> clazz);

  <T> void updateUserObject(URI userUri, T object);

  <T> void updateUserObject(URI userUri, String objectName, T object);

}
