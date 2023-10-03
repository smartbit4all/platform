package org.smartbit4all.api.org;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.contribution.PrimaryApi;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.api.org.bean.SubjectModel;
import org.smartbit4all.api.org.bean.SubjectTypeDescriptor;

/**
 * The ACL management api is responsible for integration of the {@link SubjectContributionApi}s and
 * produce a common value set definition for all the subjects.
 * 
 * @author Peter Boros
 */
public interface SubjectManagementApi extends PrimaryApi<SubjectContributionApi> {

  static final String SCHEMA = "subjectMgmt";

  /**
   * Get the model from the storage by name.
   * 
   * @param name The name of the subject model.
   * @return The SubjectModel if it exists else throw IllegalArgumentException.
   */
  SubjectModel getModel(String name);

  /**
   * The get subjects of user computes the list of subjects the given user belongs to. It uses the
   * contribution api defined in the given {@link SubjectTypeDescriptor}s.
   * 
   * @param modelName The name of the subject model.
   * @param userUri The uri of the user.
   * @return The list of subjects the user belongs to.
   */
  List<Subject> getSubjectsOfUser(String modelName, URI userUri);

  /**
   * The get subjects of current user in the session computes the list of subjects the given user
   * belongs to. It uses the contribution api defined in the given {@link SubjectTypeDescriptor}s.
   * 
   * @param modelName The name of the subject model.
   * @return The list of subjects the user belongs to.
   */
  List<Subject> getMySubjects(String modelName);

  /**
   * Get all the subjects available in the given model.
   * 
   * @param modelName The name of the subject model.
   * @return The list of the subjects available.
   */
  List<Subject> getAllSubjects(String modelName);

  /**
   * Retrieves the uri list of the users belongs to the given subjects.
   * 
   * @param modelName The name of the model.
   * @param subjects The URI list of the subjects. We might not know what kind of subject it is in
   *        the model. All the {@link SubjectContributionApi}s must recognize quickly that an URI is
   *        managed by the given api or not.
   * @return The distinct list of the user URI belongs to the
   */
  List<URI> getUsersOf(String modelName, List<URI> subjects);

}
