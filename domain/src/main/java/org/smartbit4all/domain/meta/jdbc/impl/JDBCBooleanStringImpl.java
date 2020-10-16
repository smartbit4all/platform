package org.smartbit4all.domain.meta.jdbc.impl;

import org.smartbit4all.domain.meta.jdbc.JDBCBooleanString;

/**
 * Default implementation of the {@link Boolean} value based types.
 * 
 * @author Zoltán Suller
 */
public class JDBCBooleanStringImpl implements JDBCBooleanString {

  @Override
  public String app2ext(Boolean appValue) {
    return appValue == null ? null : ((Boolean) appValue).toString();
  }

  @Override
  public Boolean ext2app(String extValue) {
    return extValue == null ? null : Boolean.valueOf((String) extValue);
  }

}
