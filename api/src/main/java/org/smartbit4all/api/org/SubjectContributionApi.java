package org.smartbit4all.api.org;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.contribution.ContributionApi;
import org.smartbit4all.api.org.bean.Subject;

/**
 * The {@link SubjectContributionApi} is responsible for introducing new subjects to the access
 * control lists. First of all it provides value sets for the selection of the
 * 
 * @author Peter Boros
 */
public interface SubjectContributionApi extends ContributionApi {

  List<Subject> getUserSubjects(URI userUri);

  List<Subject> getAllSubjects();

}
