package org.smartbit4all.api.org;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.User;

/**
 * This object is the in memory state object for the {@link OrgApi}. This is the basement of the
 * cache, not thread safe so must be produced in protected block and replace the cache variable at
 * once.
 * 
 * @author Peter Boros
 */
public class OrgState {

  private Map<URI, GroupEntry> groups;

  private Map<URI, UserEntry> users;

  public OrgState(Map<URI, GroupEntry> groups, Map<URI, UserEntry> users) {
    super();
    this.groups = groups;
    this.users = users;
  }

  public GroupEntry getGroupEntry(URI uri) {
    return groups.get(uri);
  }

  public UserEntry getUserEntry(URI uri) {
    return users.get(uri);
  }

  public Group getGroup(URI uri) {
    GroupEntry entry = groups.get(uri);
    return entry == null ? null : entry.getGroup();
  }

  public User getUser(URI uri) {
    UserEntry entry = users.get(uri);
    return entry == null ? null : entry.getUser();
  }

  public Collection<UserEntry> getAllUserEntries() {
    return users.values();
  }

  public Collection<GroupEntry> getAllGroupEntries() {
    return groups.values();
  }

  public final Map<URI, GroupEntry> getGroups() {
    return groups;
  }

  public final Map<URI, UserEntry> getUsers() {
    return users;
  }

}
