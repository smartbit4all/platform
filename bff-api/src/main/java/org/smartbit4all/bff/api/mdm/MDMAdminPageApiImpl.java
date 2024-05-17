package org.smartbit4all.bff.api.mdm;

import static java.util.stream.Collectors.toList;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.mdm.MDMConstants;
import org.smartbit4all.api.mdm.MDMModificationApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.MDMBranchingStrategy;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.object.bean.LangString;
import org.smartbit4all.api.org.OrgUtils;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionButtonType;
import org.smartbit4all.api.view.bean.UiActionDescriptor;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.base.Objects;
import com.google.common.base.Strings;

public class MDMAdminPageApiImpl extends PageApiImpl<Object> implements MDMAdminPageApi {

  @Autowired
  protected MasterDataManagementApi masterDataManagementApi;
  @Autowired
  protected SessionApi sessionApi;
  @Autowired
  protected LocaleSettingApi localeSettingApi;
  @Autowired
  protected InvocationApi invocationApi;

  public MDMAdminPageApiImpl() {
    super(Object.class);
  }

  /**
   * The page context is a useful object to encapsulate all the parameters necessary to execute the
   * actions of the page.
   */
  protected class PageContext {

    View view;
    public MDMDefinition definition;
    public String alreadySelectedActionCode;
    public URI mdmBranch;
    private MDMModificationApi modificationApi;

    PageContext loadByView() {
      ObjectMapHelper parameters = parameters(view);
      definition = masterDataManagementApi.getDefinition(getDefinition(parameters));
      alreadySelectedActionCode = getAlreadySelectedActionCode(parameters);
      modificationApi = masterDataManagementApi
          .getModificationApiForUser(definition.getName(), sessionApi.getUserUri());
      mdmBranch = modificationApi == null ? null : modificationApi.getModification().getBranchUri();
      return this;
    }

    private final String getDefinition(ObjectMapHelper parameters) {
      return parameters.require(PARAM_MDM_DEFINITION, String.class);
    }

    private final String getAlreadySelectedActionCode(ObjectMapHelper parameters) {
      return parameters.get(PARAM_ALREADY_SELECTED_ACTION_CODE, String.class);
    }

    public boolean checkAdmin() {
      return OrgUtils.securityPredicate(sessionApi, definition.getAdminGroupName());
    }

    public boolean checkAdminApprover() {
      return OrgUtils.securityPredicate(sessionApi, definition.getAdminApproverGroupName());
    }

    public View getView() {
      return view;
    }

    public MDMModificationApi getModificationApi() {
      return modificationApi;
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
  public Object initModel(View view) {
    PageContext context = getContextByView(view);
    return initModelInner(view, context);
  }

  protected Object initModelInner(View view, PageContext context) {
    refreshUiActions(context);

    if (!Strings.isNullOrEmpty(context.alreadySelectedActionCode)) {
      styleViewActions(view, context.alreadySelectedActionCode);
    }

    return new HashMap<String, Object>();
  }

  protected void refreshUiActions(PageContext ctx) {
    List<UiAction> actions = new ArrayList<>();
    if ((ctx.checkAdmin() || ctx.checkAdminApprover())) {
      MDMBranchingStrategy strategy = ctx.definition.getBranchingStrategy();
      if (strategy == MDMBranchingStrategy.GLOBAL) {
        addAction(actions, ACTION_OPEN_MDM_CHANGES);
      } else if (strategy == MDMBranchingStrategy.STRICT_PARALLEL) {
        if (ctx.modificationApi == null) {
          // no modification is active, open sessions
          addAction(actions, ACTION_OPEN_MDM_SESSIONS);
        } else {
          // modification is active, open changes
          addAction(actions, ACTION_OPEN_MDM_CHANGES);
        }
      }
    }

    List<UiAction> openListActions =
        masterDataManagementApi.getEntryDescriptors(ctx.definition, ctx.mdmBranch)
            .values().stream()
            .filter(this::filterDescriptor)
            .filter(entyDesc -> checkDescriptorSecurity(entyDesc,
                ctx.definition.getAdminApproverGroupName()))
            .map(e -> e.getOrder() != null ? e : e.order(Long.MAX_VALUE))
            .sorted(Comparator.comparing(MDMEntryDescriptor::getOrder))
            .map(e -> new UiAction()
                .code(OPEN_LIST_PREFIX + e.getName())
                .descriptor(getUiActionDescriptor(e, e.getName())))
            .collect(toList());
    actions.addAll(openListActions);

    ctx.view.actions(actions);
  }

  private boolean addAction(List<UiAction> actions, String code) {
    return actions.add(new UiAction()
        .code(code)
        .descriptor(
            getUiActionDescriptor(null, localeSettingApi.get(code))));
  }

  protected UiActionDescriptor getUiActionDescriptor(MDMEntryDescriptor e, String title) {
    return new UiActionDescriptor()
        .title(e == null ? title : getTitle(e))
        .color(UiActions.Color.PRIMARY)
        .type(UiActionButtonType.NORMAL);
  }

  // we could use MDMEntryApi.getDisplayNameList, but we would instantiate new MDMEntryApi for each
  // getTitle call and it's probably expensive..
  protected String getTitle(MDMEntryDescriptor e) {
    LangString displayName = e.getDisplayNameList();
    return displayName != null ? localeSettingApi.get(displayName) : e.getName();
  }

  protected boolean filterDescriptor(MDMEntryDescriptor entryDescriptor) {
    return true;
  }

  protected boolean checkDescriptorSecurity(MDMEntryDescriptor entryDescriptor,
      String adminApproverGroupName) {
    if (Strings.isNullOrEmpty(entryDescriptor.getAdminGroupName())) {
      return true;
    } else {
      return OrgUtils.securityPredicate(sessionApi, entryDescriptor.getAdminGroupName())
          || OrgUtils.securityPredicate(sessionApi, adminApproverGroupName);
    }
  }

  @Override
  public void openList(UUID viewUuid, UiActionRequest request) {
    String code = request.getCode();
    if (Strings.isNullOrEmpty(code)) {
      throw new IllegalArgumentException("Missing code");
    }
    if (!code.startsWith(OPEN_LIST_PREFIX)) {
      throw new IllegalArgumentException("Invalid code");
    }

    String descriptorName = code.substring(OPEN_LIST_PREFIX.length());

    PageContext ctx = getContextByViewUUID(viewUuid);
    MDMDefinition definition = ctx.definition;
    MDMEntryDescriptor descriptor = masterDataManagementApi
        .getEntryDescriptor(definition, descriptorName, ctx.mdmBranch);
    if (descriptor.getBranchingStrategy() == null) {
      descriptor.setBranchingStrategy(definition.getBranchingStrategy());
    }
    View listView = new View().viewName(getListViewName())
        .putParametersItem(MDMEntryListPageApi.PARAM_MDM_DEFINITION, definition)
        .putParametersItem(MDMEntryListPageApi.PARAM_ENTRY_DESCRIPTOR, descriptor);
    styleViewActions(ctx.view, code);
    viewApi.showView(listView);
  }

  protected void styleViewActions(View view, String currentSelection) {
    view.getActions().stream()
        .filter(action -> action.getDescriptor() != null)
        .forEach(action -> styleAction(
            action,
            Objects.equal(currentSelection, action.getCode())));
  }

  protected void styleAction(UiAction action, boolean isCurrentSelection) {
    action.getDescriptor()
        .type(isCurrentSelection ? UiActionButtonType.RAISED : UiActionButtonType.NORMAL)
        .color(isCurrentSelection ? UiActions.Color.SECONDARY : UiActions.Color.PRIMARY);
  }

  protected String getListViewName() {
    return MDM_LIST;
  }

  @Override
  public void performNewEntry(UUID viewUuid, UiActionRequest request) {
    // NOP
  }

  @Override
  public void performOpenChanges(UUID viewUuid, UiActionRequest request) {
    View view = viewApi.getView(viewUuid);
    PageContext context = getContextByView(view);
    if (!(context.checkAdmin() || context.checkAdminApprover())) {
      throw new IllegalAccessError("Only admins can view MDM changes!");
    }
    viewApi.showView(new View().viewName(MDMConstants.MDM_CHANGES)
        .putParametersItem(MDMEntryChangesPageApi.PARAM_MDM_DEFINITION,
            context.definition.getName()));
    styleViewActions(view, ACTION_OPEN_MDM_CHANGES);
  }

  @Override
  public void performOpenSessions(UUID viewUuid, UiActionRequest request) {
    // TODO may extract method to avoid duplicate of performOpenChanges
    View view = viewApi.getView(viewUuid);
    PageContext context = getContextByView(view);
    if (!(context.checkAdmin() || context.checkAdminApprover())) {
      throw new IllegalAccessError("Only admins can view MDM sessions!");
    }
    viewApi.showView(new View().viewName(MDMConstants.MDM_SESSIONS)
        .putParametersItem(MDMSessionsPageApi.PARAM_MDM_DEFINITION,
            context.definition.getName()));
    styleViewActions(view, ACTION_OPEN_MDM_SESSIONS);

  }


  @Override
  public void refreshUiActions(UUID viewUuid) {
    PageContext context = getContextByViewUUID(viewUuid);
    refreshUiActions(context);
  }

}
