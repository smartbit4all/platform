package org.smartbit4all.api.org;

import java.net.URI;
import java.time.OffsetDateTime;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;

public class UserExpirationApiImpl implements UserExpirationApi {

  @Autowired
  private ObjectApi objectApi;
  @Autowired
  private CollectionApi collectionApi;
  @Autowired
  private OrgApi orgApi;
  @Autowired
  private InvocationApi invocationApi;
  @Autowired
  private SessionApi sessionApi;
  @Autowired
  private SessionManagementApi sessionManagementApi;

  @Override
  public void setUserExpiration(URI userUri, Long minutes) {
    OffsetDateTime expireAt = OffsetDateTime.now().plusMinutes(minutes);
    setUserExpirationInner(userUri, expireAt);
  }

  private void setUserExpirationInner(URI userUri, OffsetDateTime expireAt) {
    invocationApi.invokeAt(invocationApi.builder(UserExpirationApi.class).build(
        api -> api.disableExpiredUser(userUri)), USER_EXPIRATION_INVOCATION_CHANEL, expireAt);
  }

  @Override
  public void disableExpiredUser(URI userUri) {
    orgApi.removeUser(userUri);
    sessionManagementApi.getActiveSessionsOfUser(userUri)
        .forEach(s -> sessionManagementApi.removeSessionAuthentications(s.getUri()));
  }

}
