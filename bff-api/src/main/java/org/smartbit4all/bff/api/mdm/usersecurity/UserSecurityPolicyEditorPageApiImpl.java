package org.smartbit4all.bff.api.mdm.usersecurity;

import static org.smartbit4all.core.object.ObjectLayoutBuilder.widgetKey;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.UserSecurityPolicy;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.LayoutDirection;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.value.bean.GenericValue;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.api.value.bean.ValueSetData;
import org.smartbit4all.api.view.bean.ComponentConstraint;
import org.smartbit4all.api.view.bean.ValueSet;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewConstraint;
import org.smartbit4all.bff.api.mdm.MDMEntryEditPageApiImpl;
import org.smartbit4all.core.object.ObjectLayoutBuilder;
import org.springframework.beans.factory.annotation.Autowired;

public class UserSecurityPolicyEditorPageApiImpl extends MDMEntryEditPageApiImpl
    implements UserSecurityPolicyEditorPageApi {

  @Autowired
  private OrgApi orgApi;

  @Autowired
  private LocaleSettingApi localeSettingApi;

  @Override
  public Object initModel(View view) {
    Object model = super.initModel(view);

    putValueSetsIntoView(view);
    setConstraints(view);
    view.putComponentLayoutsItem("default", getLayout());
    return model;
  }

  private SmartComponentLayoutDefinition getLayout() {
    return ObjectLayoutBuilder.container(LayoutDirection.VERTICAL)
        .addComponentsItem(ObjectLayoutBuilder.form(LayoutDirection.VERTICAL,
            ObjectLayoutBuilder
                .textfield(UserSecurityPolicy.NAME, localeSettingApi.get(UserSecurityPolicy.NAME)),
            ObjectLayoutBuilder
                .textfieldNumber(UserSecurityPolicy.INACTIVITY_LOCKOUT_DAYS,
                    localeSettingApi.get(UserSecurityPolicy.INACTIVITY_LOCKOUT_DAYS)),
            ObjectLayoutBuilder
                .textfieldNumber(UserSecurityPolicy.PASSWORD_EXPIRATION_DAYS,
                    localeSettingApi.get(UserSecurityPolicy.PASSWORD_EXPIRATION_DAYS)),
            ObjectLayoutBuilder
                .textfieldNumber(UserSecurityPolicy.PASSWORD_REMINDER_DAYS,
                    localeSettingApi.get(UserSecurityPolicy.PASSWORD_REMINDER_DAYS)),
            ObjectLayoutBuilder.multiSelectCombobox(
                widgetKey(UserSecurityPolicy.USER_GROUPS),
                localeSettingApi.get(UserSecurityPolicy.USER_GROUPS),
                ObjectLayoutBuilder.selectionDefinition(Group.class.getName(),
                    Value.DISPLAY_VALUE))));
  }

  private void setConstraints(View view) {
    ViewConstraint viewConstraint = new ViewConstraint().componentConstraints(Arrays.asList(
        new ComponentConstraint().dataName(widgetKey(UserSecurityPolicy.NAME))
            .enabled(true).mandatory(true).visible(true),
        new ComponentConstraint().dataName(widgetKey(UserSecurityPolicy.PASSWORD_EXPIRATION_DAYS))
            .enabled(true).mandatory(true).visible(true),
        new ComponentConstraint().dataName(widgetKey(UserSecurityPolicy.PASSWORD_REMINDER_DAYS))
            .enabled(true).mandatory(true).visible(true),
        new ComponentConstraint().dataName(widgetKey(UserSecurityPolicy.INACTIVITY_LOCKOUT_DAYS))
            .enabled(true).mandatory(true).visible(true)));
    view.constraint(viewConstraint);

  }

  private void putValueSetsIntoView(View view) {
    List<Group> groups = orgApi.getAllGroups();
    List<Object> groupValues = groups.stream()
        .map(group -> new GenericValue().uri(objectApi.getLatestUri(group.getUri()))
            .name(localeSettingApi.get(group.getTitle())))
        .collect(Collectors.toList());

    ValueSetData kindValueSet = new ValueSetData()
        .keyProperty(GenericValue.URI)
        .values(groupValues);

    view.putValueSetsItem(Group.class.getName(), new ValueSet()
        .valueSetData(kindValueSet)
        .valueSetName(Group.class.getName()));

  }


}
