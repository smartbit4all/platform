package org.smartbit4all.api.filter;

import java.util.List;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterDescriptor;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroup;

public interface DynamicFilterApi {

  List<DynamicFilterDescriptor> getFilterConfig(String uri);
  
  String saveFilters(String name, DynamicFilterGroup rootFilterGroup);
  
  DynamicFilterGroup loadFilters(String uri);
  
}
