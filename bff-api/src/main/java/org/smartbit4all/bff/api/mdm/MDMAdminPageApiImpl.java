package org.smartbit4all.bff.api.mdm;

import static java.util.stream.Collectors.toList;
import java.util.UUID;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.org.OrgUtils;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.base.Strings;

public class MDMAdminPageApiImpl extends PageApiImpl<MDMDefinition> implements MDMAdminPageApi {

  @Autowired
  private MasterDataManagementApi masterDataManagementApi;

  @Autowired
  private SessionApi sessionApi;

  public MDMAdminPageApiImpl() {
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
      definition = masterDataManagementApi.getDefinition(getDefinition(view));
      return this;
    }

    private final String getDefinition(View view) {
      return extractParam(String.class, PARAM_MDM_DEFINITION, view.getParameters());
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
    view.actions(context.definition.getDescriptors().values().stream()
        .map(e -> new UiAction()
            .code(ACTION_OPEN_LIST + "-" + e.getName()))
        .collect(toList()));
    return context.definition;
  }

  @Override
  public void openList(UUID viewUuid, UiActionRequest request) {
    String code = request.getCode();
    if (Strings.isNullOrEmpty(code)) {
      throw new IllegalArgumentException("Missing code");
    }
    if (!code.startsWith(ACTION_OPEN_LIST + "-")) {
      throw new IllegalArgumentException("Invalid code");
    }

    String descriptorName = code.substring((ACTION_OPEN_LIST + "-").length());

    PageContext ctx = getContextByViewUUID(viewUuid);
    MDMDefinition definition = ctx.definition;

    View querySetView = new View().viewName(MDM_LIST)
        .putParametersItem(MDMEntryListPageApi.PARAM_MDM_DEFINITION, definition)
        .putParametersItem(MDMEntryListPageApi.PARAM_ENTRY_DESCRIPTOR, masterDataManagementApi
            .getEntryDescriptor(definition, descriptorName));

    viewApi.showView(querySetView);

  }
}
