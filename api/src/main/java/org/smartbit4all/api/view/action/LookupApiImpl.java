package org.smartbit4all.api.view.action;

import static java.util.stream.Collectors.toList;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionField;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldList;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.value.bean.GenericValue;
import org.smartbit4all.api.view.filterexpression.FilterExpressionBuilderApi;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;

public class LookupApiImpl implements LookupApi {

  @Autowired
  protected ObjectApi objectApi;

  @Autowired
  protected FilterExpressionBuilderApi filterExpressionBuilderApi;

  @Override
  public void handleSubjectSelected(UUID viewUuid, List<Subject> subjects, String filterId,
      String identifier) {
    FilterExpressionFieldList filterFields =
        filterExpressionBuilderApi.getFilterExpressionFieldList(viewUuid, filterId);
    if (filterFields != null) {
      FilterExpressionField affectedSubjectsField = filterFields.getFilters().stream()
          .filter(field -> identifier
              .equals(field.getExpressionData().getOperand1().getValueAsString()))
          .findFirst()
          .orElse(null);
      if (affectedSubjectsField != null
          && affectedSubjectsField.getExpressionData() != null
          && affectedSubjectsField.getExpressionData().getOperand2() != null) {
        List<Object> selectedObjects =
            affectedSubjectsField.getExpressionData().getOperand2().getSelectedObjects();
        if (selectedObjects == null) {
          selectedObjects = new ArrayList<>();
          affectedSubjectsField.getExpressionData().getOperand2()
              .setSelectedObjects(selectedObjects);
        }
        List<URI> existingSubjects = selectedObjects.stream()
            .map(value -> objectApi.asType(GenericValue.class, value).getUri())
            .map(objectApi::getLatestUri)
            .collect(toList());
        List<Object> newSelection = subjects.stream()
            .map(Subject::getRef)
            .map(objectApi::getLatestUri)
            .filter(uri -> !existingSubjects.contains(uri))
            .map(uri -> {
              ObjectNode objNode = objectApi.load(uri);
              return new GenericValue()
                  .uri(uri)
                  .name(objNode.getValueAsString("name"));
              // OrganizationUnit.NAME, User.NAME
            })
            .collect(toList());
        selectedObjects.addAll(newSelection);
      }
    }
  }

  @Override
  public void handleUserSelected(UUID viewUuid, List<URI> users, String filterId,
      String identifier) {
    // TODO maybe this should be done via SubjectManagementApi?
    List<Subject> subjects = users.stream()
        .map(uri -> new Subject()
            .model(PlatformApiConfig.SUBJECT_ACL)
            .type(User.class.getName())
            .ref(uri))
        .collect(toList());
    handleSubjectSelected(viewUuid, subjects, filterId, identifier);
  }


}
