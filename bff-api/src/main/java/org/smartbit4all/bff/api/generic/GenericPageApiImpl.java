package org.smartbit4all.bff.api.generic;

import java.util.UUID;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;

public class GenericPageApiImpl extends PageApiImpl<Object>
    implements GenericPageApi {

  public GenericPageApiImpl() {
    super(Object.class);
  }

  @Override
  public Object initModel(View view) {
    Object model = parameters(view).get(PARAM_MODEL, Object.class);
    return model == null ? new Object() : model;
  }

  @Override
  public void closeView(UUID viewUuid, UiActionRequest request) {
    viewApi.closeView(viewUuid);
  }

}
