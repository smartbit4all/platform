package org.smartbit4all.api.org;

import java.net.URI;

public interface OrgSubscriberApi {

  void passwordExpiredEvent(URI userUri, URI userSecurityPolicyUri);

  void passwordExpiredSoonEvent(URI userUri, URI userSecurityPolicyUri);

  void userInactivedEvent(URI userUri, URI userSecurityPolicyUri);

  void userRegisteredEvent(URI userUri);
}
