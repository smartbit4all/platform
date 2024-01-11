package org.smartbit4all.bff.api.mdm;

import static java.util.stream.Collectors.toList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.mdm.MDMConstants;
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
import com.google.common.base.Strings;

public class MDMAdminPageApiImpl extends PageApiImpl<Object> implements MDMAdminPageApi {

  @Autowired
  protected MasterDataManagementApi masterDataManagementApi;

  @Autowired
  private SessionApi sessionApi;

  @Autowired
  protected LocaleSettingApi localeSettingApi;

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

    PageContext loadByView() {
      ObjectMapHelper parameters = parameters(view);
      definition = masterDataManagementApi.getDefinition(getDefinition(parameters));
      alreadySelectedActionCode = getAlreadySelectedActionCode(parameters);
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
    refreshUiActions(context);

    if (!Strings.isNullOrEmpty(context.alreadySelectedActionCode)) {
      styleViewActions(view, context.alreadySelectedActionCode);
    }

    return new HashMap<String, Object>();
  }

  protected void refreshUiActions(PageContext context) {
    List<UiAction> actions = new ArrayList<>();
    if ((context.checkAdmin() || context.checkAdminApprover())
        && context.definition.getBranchingStrategy() != null
        && context.definition.getBranchingStrategy() != MDMBranchingStrategy.NONE) {
      actions.add(new UiAction()
          .code(ACTION_OPEN_MDM_CHANGES)
          .descriptor(getUiActionDescriptor(null, localeSettingApi.get(ACTION_OPEN_MDM_CHANGES))));
    }

    List<UiAction> openListActions = context.definition.getDescriptors().values().stream()
        .filter(this::filterDescriptor)
        .map(e -> e.getOrder() != null ? e : e.order(Long.MAX_VALUE))
        .sorted(Comparator.comparing(MDMEntryDescriptor::getOrder))
        .map(e -> new UiAction()
            .code(OPEN_LIST_PREFIX + e.getName())
            .descriptor(getUiActionDescriptor(e, e.getName())))
        .collect(toList());
    actions.addAll(openListActions);

    context.view.actions(actions);
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
        .getEntryDescriptor(definition, descriptorName);
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
        .forEach(action -> styleAction(action, currentSelection.equals(action.getCode())));
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

}
