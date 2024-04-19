package org.smartbit4all.api.object;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredMap;
import org.smartbit4all.api.contribution.ContributionApiImpl;
import org.smartbit4all.api.org.SubjectContributionApi;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.toList;

public class SampleSubjectContributionApi extends ContributionApiImpl
    implements SubjectContributionApi {

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  private ObjectApi objectApi;

  protected SampleSubjectContributionApi() {
    super(SampleCategory.class.getName());
  }

  @Override
  public List<Subject> getUserSubjects(String modelName, URI userUri) {
    StoredMap map = collectionApi.map(ObjectApiTest.SCHEMA_ASPECTS, ObjectApiTest.USER_CATEGORY);
    URI subjectUri = map.uris().get(objectApi.getLatestUri(userUri).toString());
    if (subjectUri == null) {
      return Collections.emptyList();
    }
    List<Subject> result = new ArrayList<>();
    result.add(new Subject()
        .model(modelName)
        .type(getApiName())
        .ref(map.uris().get(objectApi.getLatestUri(userUri).toString())));
    return result;
  }

  @Override
  public List<Subject> getAllSubjects(String modelName) {
    StoredMap map = collectionApi.map(ObjectApiTest.SCHEMA_ASPECTS, ObjectApiTest.USER_CATEGORY);
    return map.uris().values().stream()
        .map(u -> new Subject()
            .model(modelName)
            .type(getApiName())
            .ref(objectApi.getLatestUri(u)))
        .collect(toList());
  }

  @Override
  public List<URI> getUsersOf(String modelName, List<URI> subjects) {
    // TODO implement later on.
    return Collections.emptyList();
  }

  @Override
  public List<Subject> getAllSubjects(String modelName, List<URI> baseList) {
    if (baseList == null || objectApi == null) {
      return Collections.emptyList();
    }
    return baseList.stream()
        .filter(s -> objectApi.definition(s).instanceOf(SampleCategory.class))
        .map(u -> new Subject()
            .model(modelName)
            .type(SampleCategory.class.getName())
            .ref(u))
        .collect(toList());
  }

  @Override
  public List<String> getDisplayValue(String modelName, List<URI> subjects) {
    return subjects.stream()
        .filter(s -> objectApi.definition(s).instanceOf(SampleCategory.class))
        .map(objectApi::loadLatest)
        .map(n -> n.getValueAsString(SampleCategory.NAME))
        .collect(toList());
  }

}
