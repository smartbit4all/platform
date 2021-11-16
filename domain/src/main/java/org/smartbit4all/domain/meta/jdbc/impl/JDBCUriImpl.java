package org.smartbit4all.domain.meta.jdbc.impl;

import java.net.URI;
import org.smartbit4all.domain.meta.jdbc.JDBCUri;

public class JDBCUriImpl implements JDBCUri {

  @Override
  public String app2ext(URI appValue) {
    return appValue == null ? null : appValue.toString();
  }

  @Override
  public URI ext2app(String extValue) {
    return extValue == null ? null : URI.create(extValue);
  }

}
