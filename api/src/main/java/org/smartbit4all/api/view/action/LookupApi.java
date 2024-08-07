package org.smartbit4all.api.view.action;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.org.bean.Subject;

public interface LookupApi {

  void handleSubjectSelected(UUID viewUuid, List<Subject> subjects, String filterId,
      String identifier);

  void handleUserSelected(UUID viewUuid, List<URI> users, String filterId, String identifier);

}
