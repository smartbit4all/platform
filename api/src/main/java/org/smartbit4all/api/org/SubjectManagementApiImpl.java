package org.smartbit4all.api.org;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.util.Strings;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredMap;
import org.smartbit4all.api.collection.StoredReference;
import org.smartbit4all.api.contribution.PrimaryApiImpl;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.api.org.bean.SubjectList;
import org.smartbit4all.api.org.bean.SubjectModel;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.toList;

/**
 * The generic implementation of the {@link SubjectManagementApi}.
 * 
 * @author Peter Boros
 */
public class SubjectManagementApiImpl extends PrimaryApiImpl<SubjectContributionApi>
    implements SubjectManagementApi {

  static final String MAP_SUBJECT_MODELS = "subjectModelMaps";

  /**
   * The models from the application context. Later on it should be composed from parts.
   */
  @Autowired(required = false)
  private List<SubjectModel> models;

  @Autowired
  ObjectApi objectApi;

  @Autowired
  CollectionApi collectionApi;

  @Autowired(required = false)
  SessionApi sessionApi;

  /**
   * A quick solution to ensure that we are saving the options only once.
   */
  private boolean optionsSaved = false;

  public SubjectManagementApiImpl() {
    super(SubjectContributionApi.class);
  }

  /**
   * This function is updating currently the code level registered {@link SubjectModel}s and their
   * options in the storage.
   */
  private final void synchronizeModels() {
    if (optionsSaved) {
      return;
    }
    if (models != null) {
      StoredMap map = collectionApi.map(SCHEMA, MAP_SUBJECT_MODELS);
      map.update(m -> {
        if (m == null) {
          m = new HashMap<>();
        }
        for (SubjectModel subjectModel : models) {
          URI uri = m.get(subjectModel.getName());
          if (uri != null) {
            objectApi
                .save(objectApi.loadLatest(uri).modify(SubjectModel.class, model -> subjectModel));
          } else {
            URI latestUri = objectApi.getLatestUri(objectApi.saveAsNew(SCHEMA, subjectModel));
            m.put(subjectModel.getName(), latestUri);
          }
        }
        return m;
      });
    }
    optionsSaved = true;
  }

  @Override
  public SubjectModel getModel(String name) {
    synchronizeModels();
    StoredMap map = collectionApi.map(SCHEMA, MAP_SUBJECT_MODELS);
    URI modelUri = map.uris().get(name);
    if (modelUri == null) {
      throw new IllegalArgumentException("Unable to find the " + name + "subject model.");
    }
    return objectApi.read(modelUri, SubjectModel.class);
  }

  @Override
  public List<Subject> getSubjectsOfUser(String modelName, URI userUri) {
    SubjectModel model = getModel(modelName);
    return model.getDescriptors().entrySet().stream()
        .flatMap(
            e -> getContributionApi(e.getValue().getApiName()).getUserSubjects(userUri).stream())
        .collect(toList());
  }

  @Override
  public List<Subject> getMySubjects(String modelName) {
    if (sessionApi != null && !Strings.isEmpty(modelName)) {
      URI sessionUri = sessionApi.getSessionUri();
      URI userUri = sessionApi.getUserUri();
      if (sessionUri != null && userUri != null) {
        StoredReference<SubjectList> subjectListRef =
            collectionApi.reference(sessionUri, SCHEMA, modelName, SubjectList.class);
        if (!subjectListRef.exists()) {
          List<Subject> subjectsOfUser = getSubjectsOfUser(modelName, userUri);
          subjectListRef.set(new SubjectList().items(subjectsOfUser));
          return subjectsOfUser;
        } else {
          return subjectListRef.get().getItems();
        }
      }
    }
    return Collections.emptyList();
  }

  @Override
  public List<Subject> getAllSubjects(String modelName) {
    SubjectModel model = getModel(modelName);
    return model.getDescriptors().entrySet().stream()
        .flatMap(
            e -> getContributionApi(e.getValue().getApiName()).getAllSubjects().stream())
        .collect(toList());
  }

}
