package org.smartbit4all.api.view.filterexpression;

import static java.util.stream.Collectors.toList;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.filterexpression.bean.SearchConfigHierarchy;
import org.smartbit4all.api.filterexpression.bean.SearchPageConfig;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.View;
import com.google.common.base.Strings;

public interface SearchConfigHierarchyApi {

  String SCHEMA = "filter-hierarchy";
  String LIST = "filter-hierarchy-list";
  String MDM_NAME = "filter-hierarchies";

  final class SearchConfigHierarchyViewData {
    private final String code;
    private final List<UiAction> actions;
    private final Map<String, SearchPageConfig> variables;


    public SearchConfigHierarchyViewData(
        String code,
        List<UiAction> actions,
        Map<String, SearchPageConfig> variables) {
      this.code = code;
      this.actions = actions;
      this.variables = variables;
    }

    public void initInView(final View view, String toolbar) {
      UiActions.add(view, Strings.isNullOrEmpty(toolbar)
          ? actions
          : actions.stream().map(it -> it.toolbar(toolbar)).collect(toList()));
      view.putVariablesItem(code, variables);
    }
  }

  /**
   * Creates a new builder for programmatic assembly of a {@link SearchConfigHierarchy}.
   * 
   * @return a fresh {@link SearchConfigHierarchyBuilder}
   */
  SearchConfigHierarchyBuilder builder();

  /**
   * 
   * @param hierarchy
   * @return
   */
  List<UiAction> render(SearchConfigHierarchy hierarchy);

  /**
   * 
   * @param hierarchy
   * @return
   */
  Map<String, SearchPageConfig> assembleForView(SearchConfigHierarchy hierarchy);

  default void initInView(SearchConfigHierarchy hierarchy, View view) {
    initInView(hierarchy, view, null);
  }

  default void initInView(SearchConfigHierarchy hierarchy, View view, String toolbar) {
    final SearchConfigHierarchyViewData data = new SearchConfigHierarchyViewData(
        hierarchy.getCode(),
        render(hierarchy),
        assembleForView(hierarchy));
    data.initInView(view, toolbar);
  }
}
