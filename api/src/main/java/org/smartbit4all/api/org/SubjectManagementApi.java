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
   * @param userUri The uri of the user.
   * @return The list of subjects the user belongs to.
   */
  List<Subject> getSubjectsOfUser(String modelName, URI userUri);

}
