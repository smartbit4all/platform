package org.smartbit4all.api.mdm;

import java.net.URI;
import java.util.List;

public interface MDMApprovalApi {

  List<URI> getApprovers(String definition);

}
