package org.smartbit4all.sec.session;

import java.net.URI;
import org.smartbit4all.api.session.bean.Session;

public interface SessionManagementSubsciberApi {

  void sessionCreated(URI sessionURI);

  void sessionModified(Session prevSession, Session nextSession);

}
