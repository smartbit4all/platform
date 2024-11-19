package org.smartbit4all.domain.meta.jdbc.impl;

import java.net.URI;

public class JDBCUriLikeImpl extends JDBCUriImpl {

  @Override
  public String app2ext(URI appValue) {
    return appValue == null ? null : "%" + appValue.toString() + "%";
  }

}
