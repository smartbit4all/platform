package org.smartbit4all.sec.session;

import java.net.URI;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.api.session.exception.NoCurrentSessionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.SessionRepository;

public class SpringSessionStorageRepository implements SessionRepository<SpringSessionWrapper> {

  @Autowired
  private SessionManagementApi sessionManagementApi;

  @Autowired
  private SessionApi sessionApi;

  @Override
  public SpringSessionWrapper createSession() {
    URI sessionUri;
    try {
      sessionUri = sessionApi.getSessionUri();
    } catch (NoCurrentSessionException e) {
      sessionManagementApi.startSession();
      sessionUri = sessionApi.getSessionUri();
    }
    return SpringSessionWrapper.of(sessionUri, sessionManagementApi);
  }

  @Override
  public void save(SpringSessionWrapper sessionWrapper) {
    // we save every modification eagerly in SpringSessionWrapper, no need to save here.
    // nope
  }

  @Override
  public SpringSessionWrapper findById(String id) {
    Session session = sessionManagementApi.readSession(URI.create(id));
    return SpringSessionWrapper.of(session.getUri(), sessionManagementApi);
  }

  @Override
  public void deleteById(String id) {
    // nope
  }

}
