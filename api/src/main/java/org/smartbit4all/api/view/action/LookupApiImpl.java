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
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionButtonType;
import org.smartbit4all.api.view.bean.UiActionDescriptor;
import org.smartbit4all.api.view.bean.View;
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
  public void addLookupAction(View view, String field) {
    // maybe move to some other action handling api?
    view.addActionsItem(
        new UiAction()
            .code(ACTION_LOOKUP)
            .identifier(field)
            .descriptor(new UiActionDescriptor()
                .type(UiActionButtonType.ICON)
                .icon("search")
                .iconColor(UiActions.Color.ACCENT))
            .toolbar(field + UiActions.TOOLBAR_SUFFIX));
  }

  @Override
  public void handleSubjectSelected(UUID viewUuid, List<Subject> subjects, String filterId,
      String identifier) {
    FilterExpressionFieldList filterFields =
        filterExpressionBuilderApi.getFilterExpressionFieldList(viewUuid, filterId);
    if (filterFields != null) {
      FilterExpressionField affectedSubjectsField = filterFields.getFilters().stream()
          .filter(field -> field != null
              && field.getExpressionData() != null
              && field.getExpressionData().getOperand1() != null)
          .filter(field -> identifier
              .equals(field.getExpressionData().getOperand1().getValueAsString()))
          .findFirst()
          .orElse(null);
      setSelectedSubjects(affectedSubjectsField, subjects);
    }
    FilterExpressionField selectedField =
        filterExpressionBuilderApi.getSelectedFilterExpressionField(viewUuid, filterId);
    if (selectedField != null
        && selectedField.getExpressionData() != null
        && selectedField.getExpressionData().getOperand1() != null
        && identifier.equals(selectedField.getExpressionData().getOperand1().getValueAsString())) {
      setSelectedSubjects(selectedField, subjects);
    }
  }

  private void setSelectedSubjects(FilterExpressionField filterField,
      List<Subject> subjects) {
    if (filterField != null
        && filterField.getExpressionData() != null
        && filterField.getExpressionData().getOperand2() != null) {
      List<Object> selectedObjects =
          filterField.getExpressionData().getOperand2().getSelectedObjects();
      if (selectedObjects == null) {
        selectedObjects = new ArrayList<>();
        filterField.getExpressionData().getOperand2()
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
