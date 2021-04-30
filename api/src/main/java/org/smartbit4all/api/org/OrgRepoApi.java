package org.smartbit4all.api.org;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.User;

/**
 * The organizations can come from several repositories in a running system. These repositories are
 * managed by the {@link OrgApi} that collects all of them and ensure a consistent view for the
 * application.
 * 
 * @author Peter Boros
 */
public interface OrgRepoApi {

  String getName();

  List<User> getAllUsersStorage();

  List<Group> getAllGroupsStorage();

  List<Group> getGroupsOfUserStorage(URI userUri);

}
