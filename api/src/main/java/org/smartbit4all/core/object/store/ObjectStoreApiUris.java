package org.smartbit4all.core.object.store;

import java.net.URI;
import org.smartbit4all.core.utility.UriUtils;

public abstract class ObjectStoreApiUris extends UriUtils {

  public static final String OBJECT_STORE_SCHEME = "object-store";
  
  private ObjectStoreApiUris() {
    // non instantiatable
  }

  public static boolean isObjectStoreUri(URI uri) {
    boolean isSchemeOk = OBJECT_STORE_SCHEME.equals(uri.getScheme());
    boolean hasType = uri.getAuthority() != null;
    if(isSchemeOk && hasType) {
      return true;
    }
    return false;
  }

  public static URI getTypeUri(URI key) {
    return createTypeUri(key.getAuthority());
  }
  
  public static URI createTypeUri(String type) {
    String formattedType = formatUriHost(type);
    return createUri(OBJECT_STORE_SCHEME, formattedType, null, null);
  }
  
  public static URI createUri(String type, String objectCode, String objectId) {
    String formattedType = formatUriHost(type);
    
    objectCode = objectCode == null ? null : "/" + objectCode;
    return createUri(OBJECT_STORE_SCHEME, formattedType, objectCode, objectId);
  }
  
  public static void checkObjectStoreURI(URI uriToCheck) {
    checkURI(uriToCheck, OBJECT_STORE_SCHEME, null, null);
  }
  
}
