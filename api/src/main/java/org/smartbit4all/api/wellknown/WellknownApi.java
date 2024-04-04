package org.smartbit4all.api.wellknown;

import org.smartbit4all.core.object.ObjectNode;

public interface WellknownApi {

  public static final String WELLKNOWN_SCHEMA = "wellknown";
  public static final String VALUE_SET_WELLKNOWN = "valueSetWellknown";
  public static final String SL_WELLKNOWN = "storedListSetWellknown";

  ObjectNode getWellknownById(String id);
}
