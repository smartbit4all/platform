package org.smartbit4all.api.filter;

import org.smartbit4all.api.filter.bean.FilterConfig;
import org.smartbit4all.api.filter.bean.FilterGroup;

public interface FilterApi {

  FilterConfig getFilterConfig(String uri);

  String saveFilters(String name, FilterGroup rootFilterGroup);

  FilterGroup loadFilters(String uri);

}
