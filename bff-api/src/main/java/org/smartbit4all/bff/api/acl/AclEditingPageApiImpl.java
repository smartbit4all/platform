package org.smartbit4all.bff.api.acl;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.smartbit4all.api.formdefinition.bean.SmartFormWidgetType;
import org.smartbit4all.api.formdefinition.bean.SmartLayoutDefinition;
import org.smartbit4all.api.formdefinition.bean.SmartMatrixModel;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.object.bean.ObjectPropertyFormatter;
import org.smartbit4all.api.object.bean.ObjectPropertyFormatterParameter;
import org.smartbit4all.api.org.bean.ACL;
import org.smartbit4all.api.org.bean.ACLEntry;
import org.smartbit4all.api.org.bean.Subject;
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
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectPropertyResolver;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.toList;

public class AclEditingPageApiImpl extends PageApiImpl<ACL> implements AclEditingPageApi {

  @Autowired
  private LocaleSettingApi localeSettingApi;

  private static final String ACL_MATRIX = "ACL_MATRIX";
  private static final String ACL = "ACL";
  private static final String OPERATIONS = "OPERATIONS";

  public AclEditingPageApiImpl() {
    super(ACL.class);
  }

  @Override

  public ACL initModel(View view) {
    ObjectMapHelper parameters = parameters(view);

    ACL acl = objectApi.loadLatest(view.getObjectUri()).aspects().get(ACL, ACL.class);

    List<String> operations = parameters.getAsList(OPERATIONS, String.class);

    view.putLayoutsItem(ACL_MATRIX, new SmartLayoutDefinition()
        .addWidgetsItem(
            new SmartWidgetDefinition().label(localeSettingApi.get(ACL)).key(ACL_MATRIX)
                .type(SmartFormWidgetType.MATRIX)
                .matrix(consturctMatrixModel(acl, operations))));

    view.putValueSetsItem("OPERATIONS", new ValueSet().valueSetData(
        new ValueSetData().values(operations.stream().map(Object.class::cast).collect(toList()))));

    view.addActionsItem(new UiAction().code(CANCEL));
    view.addActionsItem(new UiAction().code(SAVE));
    view.addActionsItem(
        new UiAction().code(OPEN_SUBJECT_SELECTOR).descriptor(new UiActionDescriptor()
            .title(localeSettingApi.get(OPEN_SUBJECT_SELECTOR)).color("primary")
            .type(UiActionButtonType.RAISED)));


    return acl;
  }

  private SmartMatrixModel consturctMatrixModel(ACL acl, List<String> operations) {
    SmartMatrixModel matrix = new SmartMatrixModel();
    matrix.data(new HashMap<String, Object>());

    acl.getEntries().stream().forEach(aclEntry -> {
      matrix
          .addRowsItem(new Value().code(aclEntry.getSubject().getRef().toString())
              .displayValue(resolveAclEntryDisplayValue(aclEntry.getSubject().getRef())));

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

  private String resolveAclEntryDisplayValue(URI entryRef) {
    ObjectPropertyResolver resolver = objectApi.resolver();
    resolver.addContextObject("object", entryRef);
    return resolver.resolve(new ObjectPropertyFormatter().formatString("{0}")
        .addParametersItem(
            new ObjectPropertyFormatterParameter().propertyUri(URI.create("object:/#name"))));
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

    rootNode.aspects().modify("ACL", ACL.class,
        acl -> updatedAcl);

    objectApi.save(rootNode);
    viewApi.closeView(viewUuid);

  }

  @Override
  public void openSubjectSelector(UUID viewUuid, UiActionRequest request) {}

  @Override
  public void handleSubjectSelected(UUID viewUuid, URI subjectUri) {
    View view = viewApi.getView(viewUuid);
    ObjectMapHelper parameters = parameters(view);
    ACL acl = getModel(viewUuid);

    if (!checkSubjectIsAlreadyInAcl(acl, subjectUri)) {
      acl.addEntriesItem(new ACLEntry().subject(new Subject().ref(subjectUri)));
    } else {
      throw new RuntimeException(
          String.format("Subject reference by %s is already in ACL", subjectUri));
    }


    List<String> operations = parameters.getAsList(OPERATIONS, String.class);

    view.getLayouts().get(ACL_MATRIX).setWidgets(aclMatrixWidget(acl, operations));

    setModel(viewUuid, acl);
  }

  private boolean checkSubjectIsAlreadyInAcl(ACL acl, URI subjectUri) {
    return acl.getEntries().stream().map(entry -> entry.getSubject().getRef()).collect(toList())
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
}
