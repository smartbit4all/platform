package org.smartbit4all.sec.session;

import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.bean.Session;

public class SessionUserEntry {

  private Session session;

  private User user;

  public SessionUserEntry() {
    super();
  }

  public Session getSession() {
    return session;
  }

  public void setSession(Session session) {
    this.session = session;
  }

  public SessionUserEntry session(Session session) {
    this.session = session;
    return this;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public SessionUserEntry user(User user) {
    this.user = user;
    return this;
  }

}
