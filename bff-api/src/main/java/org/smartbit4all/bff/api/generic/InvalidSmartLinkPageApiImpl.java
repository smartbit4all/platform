package org.smartbit4all.bff.api.generic;

import org.smartbit4all.api.object.bean.ObjectContainer;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.LayoutDirection;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.core.object.ObjectLayoutApi;
import org.smartbit4all.core.object.ObjectLayoutBuilder;
import org.springframework.beans.factory.annotation.Autowired;

public class InvalidSmartLinkPageApiImpl extends PageApiImpl<ObjectContainer>
    implements InvalidSmartLinkPageApi {

  public InvalidSmartLinkPageApiImpl() {
    super(ObjectContainer.class);
  }

  @Autowired
  private LocaleSettingApi localeSettingApi;

  @Override
  public ObjectContainer initModel(View view) {
    putLayoutIntoView(view);
    return new ObjectContainer();
  }

  private void putLayoutIntoView(View view) {

    SmartComponentLayoutDefinition layout = ObjectLayoutBuilder.container(LayoutDirection.VERTICAL)
        .addComponentsItem(ObjectLayoutBuilder.form(LayoutDirection.VERTICAL,
            ObjectLayoutBuilder.label("label",
                localeSettingApi.get("invalid.smartlink.to.view.page"))));

    view.putComponentLayoutsItem(ObjectLayoutApi.DEFAULT_LAYOUT, layout);
  }
}

