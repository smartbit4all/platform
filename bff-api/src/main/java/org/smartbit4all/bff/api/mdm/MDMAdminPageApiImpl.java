package org.smartbit4all.bff.api.mdm;

import java.util.UUID;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.org.OrgUtils;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.View;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.toList;

public class MDMAdminPageApiImpl extends PageApiImpl<MDMDefinition> implements MDMAdminPageApi {

  @Autowired
  private MasterDataManagementApi masterDataManagementApi;

  @Autowired
  private SessionApi sessionApi;

  public MDMAdminPageApiImpl(Class<MDMDefinition> clazz) {
    super(MDMDefinition.class);
  }

  /**
   * The page context is a useful object to encapsulate all the parameters necessary to execute the
   * actions of the page.
   */
  protected class PageContext {

    View view;
    MDMDefinition definition;

    PageContext loadByView() {
      definition = getDefinition(view);
      return this;
    }

    private final MDMDefinition getDefinition(View view) {
      return extractParam(MDMDefinition.class, PARAM_MDM_DEFINITION, view.getParameters());
    }

    public boolean checkAdmin() {
      return OrgUtils.securityPredicate(sessionApi, definition.getAdminGroupName());
    }

  }

  protected PageContext getContextByViewUUID(UUID viewUuid) {
    PageContext result = new PageContext();
    result.view = viewApi.getView(viewUuid);
    return result.loadByView();
  }

  protected PageContext getContextByView(View view) {
    PageContext result = new PageContext();
    result.view = view;
    return result.loadByView();
  }

  @Override
  public MDMDefinition initModel(View view) {
    PageContext context = getContextByView(view);
    view.setActions(context.definition.getDescriptors().entrySet().stream().map(e -> new UiAction()
        .code(ACTION_OPEN_LIST).putParamsItem(ACTION_PARAM_MDM_ENTRY, e.getValue().getName()))
        .collect(toList()));
    return context.definition;
  }

}
