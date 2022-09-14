package org.smartbit4all.sec.authprincipal;

import java.net.URI;
import java.util.Objects;

public class SessionAuthPrincipal implements Sb4AuthPrincipal {

  private URI sessionUri;

  private SessionAuthPrincipal(URI sessionUri) {
    Objects.requireNonNull(sessionUri, "SessionUri can not be null!");
    this.sessionUri = sessionUri;
  }

  public URI getSessionUri() {
    return sessionUri;
  }

  public static SessionAuthPrincipal of(URI sessionUri) {
    return new SessionAuthPrincipal(sessionUri);
  }

  @Override
  public String getName() {
    return sessionUri.toString();
  }
}
