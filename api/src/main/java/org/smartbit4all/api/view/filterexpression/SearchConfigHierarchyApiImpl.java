package org.smartbit4all.api.view.filterexpression;

import static java.util.stream.Collectors.toMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderModel;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionData;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionList;
import org.smartbit4all.api.filterexpression.bean.SearchConfigHierarchy;
import org.smartbit4all.api.filterexpression.bean.SearchConfigHierarchyFilter;
import org.smartbit4all.api.filterexpression.bean.SearchPageConfig;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;

public class SearchConfigHierarchyApiImpl implements SearchConfigHierarchyApi {

  @Autowired
  private ObjectApi objectApi;

  @Override
  public SearchConfigHierarchyBuilder builder() {
    return null;
  }

  @Override
  public List<UiAction> render(SearchConfigHierarchy hierarchy) {
    final List<UiAction> result = new ArrayList<>();
    result.add(new UiAction()
        .code(hierarchy.getCode())
        .descriptor(hierarchy.getActionDescriptor())
        .subActions(hierarchy.getFilters().stream()
            .map(this::convertFilterToAction)
            .collect(Collectors.toList())));
    return result;
  }

  private UiAction convertFilterToAction(SearchConfigHierarchyFilter filter) {
    return new UiAction()
        .code(filter.getCode())
        .descriptor(filter.getActionDescriptor())
        .subActions((filter.getFilters() == null)
            ? new ArrayList<>()
            : filter.getFilters().stream()
                .map(this::convertFilterToAction)
                .collect(Collectors.toCollection(ArrayList::new)));
  }

  private static final class ConfigByCode {
    private final String code;
    private final SearchPageConfig config;

    private ConfigByCode(String code, SearchPageConfig config) {
      this.code = code;
      this.config = config;
    }
  }

  @Override
  public Map<String, SearchPageConfig> assembleForView(SearchConfigHierarchy hierarchy) {
    final SearchPageConfig baseConfig = hierarchy.getSearchConfig();
    final List<ConfigByCode> result = new ArrayList<>();
    result.add(new ConfigByCode(hierarchy.getCode(), baseConfig));
    for (final SearchConfigHierarchyFilter filter : hierarchy.getFilters()) {
      assemble(result, filter, baseConfig);
    }
    return result.stream().collect(toMap(
        it -> it.code,
        it -> it.config,
        (a, b) -> b,
        HashMap::new));
  }

  private void assemble(List<ConfigByCode> result, SearchConfigHierarchyFilter filter,
      SearchPageConfig base) {
    final SearchPageConfig config = copy(base);
    FilterExpressionBuilderModel filterModel = config.getFilterModel();
    if (filterModel == null) {
      filterModel = new FilterExpressionBuilderModel();
      config.setFilterModel(filterModel);
    }

    FilterExpressionList defaultFilters = filterModel.getDefaultFilters();
    if (defaultFilters == null) {
      defaultFilters = new FilterExpressionList();
      filterModel.setDefaultFilters(defaultFilters);
    }

    List<FilterExpressionData> expressions = defaultFilters.getExpressions();
    if (expressions == null) {
      expressions = new ArrayList<>();
      defaultFilters.setExpressions(expressions);
    }

    if (filter.getFilterExpression() != null
        && filter.getFilterExpression().getExpressions() != null) {
      expressions.addAll(filter.getFilterExpression().getExpressions());
    }

    result.add(new ConfigByCode(filter.getCode(), config));
    for (SearchConfigHierarchyFilter subFilter : filter.getFilters()) {
      assemble(result, subFilter, config);
    }
  }

  private SearchPageConfig copy(SearchPageConfig config) {
    return objectApi.fromString(objectApi.asString(config), SearchPageConfig.class);
  }

}
