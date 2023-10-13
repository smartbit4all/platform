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

  /**
   * Retrieves the list of subjects the user belongs to.
   * 
   * @param userUri The user URI.
   * @return The list of subjects.
   */
  List<Subject> getUserSubjects(URI userUri);

  /**
   * @return All the subjects available in the given contribution. Like all the organization units
   *         or so.
   */
  List<Subject> getAllSubjects();

  /**
   * The users belongs to the given subjects.
   * 
   * @param subjects The URI list of the subjects. Not necessarily managed by this contribution, so
   *        the implementation must tolerate the foreign URI.
   * @return The
   */
  List<URI> getUsersOf(List<URI> subjects);

  /**
   * @param baseList The base list of the subjects.
   * @return The expanded list of the subjects with all the implicitly included subjects.
   */
  List<Subject> getAllSubjects(List<URI> baseList);

}
