package org.smartbit4all.bff.api.acl;

import static java.util.stream.Collectors.toList;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.formdefinition.bean.SmartFormWidgetType;
import org.smartbit4all.api.formdefinition.bean.SmartLayoutDefinition;
import org.smartbit4all.api.formdefinition.bean.SmartMatrixModel;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.object.AccessControlInternalApi;
import org.smartbit4all.api.org.SubjectManagementApi;
import org.smartbit4all.api.org.bean.ACL;
import org.smartbit4all.api.org.bean.ACLEntry;
import org.smartbit4all.api.org.bean.ACLEntry.EntryKindEnum;
import org.smartbit4all.api.org.bean.ACLEntry.SetOperationEnum;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.api.value.bean.ValueSetData;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionButtonType;
import org.smartbit4all.api.view.bean.UiActionDescriptor;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.ValueSet;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewType;
import org.smartbit4all.bff.api.config.PlatformViewNames;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import com.google.common.base.Strings;

public class AclEditingPageApiImpl extends PageApiImpl<ACL> implements AclEditingPageApi {

  @Autowired
  protected LocaleSettingApi localeSettingApi;

  @Autowired
  protected SubjectManagementApi subjectManagementApi;

  @Autowired
  private InvocationApi invocationApi;

  protected static final String ACL_MATRIX = "ACL_MATRIX";
  protected static final String ACL = AccessControlInternalApi.ACL_DEFAULT;

  public AclEditingPageApiImpl() {
    super(ACL.class);
  }

  @Override

  public ACL initModel(View view) {
    ObjectMapHelper parameters = parameters(view);

    ACL acl = getAcl(view);

    List<String> operations = parameters.getAsList(PARAM_OPERATIONS, String.class);

    String label = parameters.get(PARAM_TITLE, String.class);
    if (Strings.isNullOrEmpty(label)) {
      label = localeSettingApi.get(ACL);
    }
    view.putLayoutsItem(ACL_MATRIX, new SmartLayoutDefinition()
        .addWidgetsItem(
            new SmartWidgetDefinition()
                .key(ACL_MATRIX)
                .label(label)
                .type(SmartFormWidgetType.MATRIX)
                .matrix(consturctMatrixModel(acl, operations))));

    view.putValueSetsItem("OPERATIONS", new ValueSet().valueSetData(
        new ValueSetData().values(operations.stream().map(Object.class::cast).collect(toList()))));

    view.addActionsItem(new UiAction().code(CANCEL));
    view.addActionsItem(new UiAction().code(SAVE));
    view.addActionsItem(
        new UiAction()
            .code(OPEN_SUBJECT_SELECTOR)
            .descriptor(new UiActionDescriptor()
                .title(localeSettingApi.get(OPEN_SUBJECT_SELECTOR))
                .color(UiActions.Color.PRIMARY)
                .type(UiActionButtonType.RAISED)));


    return acl;
  }

  protected ACL getAcl(View view) {
    ACL acl = objectApi.loadLatest(view.getObjectUri()).aspects().get(ACL, ACL.class);

    if (acl == null) {
      acl = new ACL().rootEntry(
          new ACLEntry().entryKind(EntryKindEnum.SET).setOperation(SetOperationEnum.UNION));
    }
    return acl;
  }

  protected SmartMatrixModel consturctMatrixModel(ACL acl, List<String> operations) {
    SmartMatrixModel matrix = new SmartMatrixModel();
    matrix.data(new HashMap<>());

    acl.getRootEntry().getEntries().stream().forEach(aclEntry -> {
      matrix
          .addRowsItem(new Value().code(aclEntry.getSubject().getRef().toString())
              .displayValue(resolveAclEntryDisplayValue(aclEntry.getSubject())));

      // Put value into the matrix data where the key is the name of the "row"(the Subject)
      // and set the the value with list of filtered operation
      matrix.getData().put(aclEntry.getSubject().getRef().toString(),
          aclEntry.getOperations().stream().filter(operations::contains).collect(toList()));

      // Set the columns only once
    });
    if (matrix.getColumns() == null) {
      matrix
          .columns(operations.stream()
              .map(operation -> new Value().code(operation)
                  .displayValue(localeSettingApi.get(operation)))
              .collect(Collectors.toList()));
    }

    return matrix;
  }

  private String resolveAclEntryDisplayValue(Subject subject) {
    List<String> names = subjectManagementApi.getDisplayValue(
        subject.getModel() == null ? PlatformApiConfig.SUBJECT_ACL : subject.getModel(),
        Arrays.asList(subject));
    return names.size() == 1 ? names.get(0) : "N/A";

  }

  @Override
  public void closeAclEditing(UUID viewUuid, UiActionRequest request) {
    viewApi.closeView(viewUuid);
  }

  @Override
  public void saveEditing(UUID viewUuid, UiActionRequest request) {
    ObjectNode rootNode = objectApi.loadLatest(viewApi.getView(viewUuid).getObjectUri());
    ObjectMapHelper requestParams = actionRequestHelper(request);
    ACL updatedAcl = requestParams.get(UiActions.MODEL, ACL.class);

    updateAclInRootNode(rootNode, updatedAcl);

    objectApi.save(rootNode);
    closeAclEditing(viewUuid, request);

  }

  protected void updateAclInRootNode(ObjectNode rootNode, ACL updatedAcl) {
    rootNode.aspects().modify(AccessControlInternalApi.ACL_ASPECT, ACL.class,
        acl -> updatedAcl);
  }

  @Override
  public void openSubjectSelector(UUID viewUuid, UiActionRequest request) {
    Map<String, Object> params = viewApi.getView(viewUuid).getParameters();
    Object subjectTypes = params.get(PARAM_SUBJECT_TYPES);
    if (ObjectUtils.isEmpty(subjectTypes)) {
      subjectTypes = getDefaultSubjectTypes();
    }
    viewApi.showView(new View().viewName(PlatformViewNames.SUBJECT_SELECTOR_PAGE)
        .putParametersItem(SubjectSelectorPageApi.PARAM_SELECTION_CALLBACK,
            invocation(api -> api.handleSubjectSelected(viewUuid,
                Invocations.listOf(Collections.emptyList(), Subject.class))))
        .putParametersItem(SubjectSelectorPageApi.PARAM_SUBJECT_TYPES, subjectTypes)
        .type(ViewType.DIALOG));
  }

  @Override
  public void handleSubjectSelected(UUID viewUuid, List<Subject> subjects) {
    View view = viewApi.getView(viewUuid);
    ObjectMapHelper parameters = parameters(view);
    ACL acl = getModel(viewUuid);

    for (Subject subject : subjects) {
      if (checkSubjectIsAlreadyInAcl(acl, subject.getRef())) {
        throw new RuntimeException(
            String.format("Subject reference by %s is already in ACL", subject));
      }
    }

    for (Subject subject : subjects) {
      acl.getRootEntry().addEntriesItem(new ACLEntry().subject(subject));
    }

    List<String> operations = parameters.getAsList(PARAM_OPERATIONS, String.class);

    view.getLayouts().get(ACL_MATRIX).setWidgets(aclMatrixWidget(acl, operations));

    setModel(viewUuid, acl);
  }



  @Override
  public void removeRowFromSubjectMatrix(UUID viewUuid, UiActionRequest request) {
    View view = viewApi.getView(viewUuid);
    Value entryToRemove = actionRequestHelper(request).get(UiActions.INPUT, Value.class);

    ACL acl = getAcl(view);
    ObjectMapHelper parameters = parameters(view);

    acl.getRootEntry().getEntries()
        .removeIf(entry -> entry.getSubject().getRef().equals(URI.create(entryToRemove.getCode())));

    List<String> operations = parameters.getAsList(PARAM_OPERATIONS, String.class);

    view.putLayoutsItem(ACL_MATRIX, new SmartLayoutDefinition()
        .addWidgetsItem(
            new SmartWidgetDefinition().label(localeSettingApi.get(ACL)).key(ACL_MATRIX)
                .type(SmartFormWidgetType.MATRIX)
                .matrix(consturctMatrixModel(acl, operations))));

    setModel(viewUuid, acl);

  }

  private boolean checkSubjectIsAlreadyInAcl(ACL acl, URI subjectUri) {
    return acl.getRootEntry().getEntries().stream().map(entry -> entry.getSubject().getRef())
        .collect(toList())
        .contains(subjectUri);
  }

  private List<SmartWidgetDefinition> aclMatrixWidget(ACL acl, List<String> ops) {
    final List<SmartWidgetDefinition> widgets = new ArrayList<>();
    widgets.add(new SmartWidgetDefinition()
        .label(ACL)
        .key(ACL_MATRIX)
        .type(SmartFormWidgetType.MATRIX)
        .matrix(consturctMatrixModel(acl, ops)));
    return widgets;
  }

  protected InvocationRequest invocation(Consumer<AclEditingPageApi> apiCall) {
    return invocationApi
        .builder(AclEditingPageApi.class)
        .build(apiCall);
  }

  protected List<String> getDefaultSubjectTypes() {
    return Arrays.asList(Group.class.getName(), User.class.getName());
  }

}
