package org.smartbit4all.api.org;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredMap;
import org.smartbit4all.api.contribution.PrimaryApiImpl;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.api.org.bean.SubjectModel;
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

}
