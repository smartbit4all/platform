package org.smartbit4all.api.filter;

import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterConfig;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroup;

public interface DynamicFilterApi {

  DynamicFilterConfig getFilterConfig(String uri);

  String saveFilters(String name, DynamicFilterGroup rootFilterGroup);

  DynamicFilterGroup loadFilters(String uri);

}
