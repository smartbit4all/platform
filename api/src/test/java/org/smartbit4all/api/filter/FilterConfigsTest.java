package org.smartbit4all.api.filter;

import java.net.URI;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.filter.bean.FilterConfig;
import org.smartbit4all.api.filter.bean.FilterFieldMeta;
import org.smartbit4all.api.filter.bean.FilterGroupMeta;
import org.smartbit4all.api.filter.bean.FilterGroupType;
import org.smartbit4all.api.filter.bean.FilterOperation;
import org.smartbit4all.api.filter.util.FilterConfigs;
import org.smartbit4all.api.filter.util.FilterOperations;
import org.smartbit4all.domain.meta.Property;

public class FilterConfigsTest {
  
  @Test
  void test() {
    FilterConfig mockFilterConfig = createMockFilterConfig();
    
    FilterConfig buildFilterConfig = FilterConfigs.builder()
      .defaultGroupStyle("defaultGroupStyle")
      .defaultStyle("defaultStyle")
      .addGroupMeta("label")
        .iconCode("icon")
        .labelCode("label")
        .type(FilterGroupType.AND)
        .addFieldMeta()
          .labelCode("labelCode")
          .addOperation("10", "operationCode")
            .labelCode("lCode")
            .propertyUri1(URI.create("property"))
            .possibleValuesUri(URI.create("possible"))
            .done()
          .done()
        .done()
      .build();
    
    Assertions.assertTrue(mockFilterConfig.equals(buildFilterConfig));
  }
  
  private FilterConfig createMockFilterConfig() {
    FilterConfig filterConfig = new FilterConfig();
    filterConfig.setDefaultFilterGroupStyle("defaultGroupStyle");
    filterConfig.setDefaultFilterStyle("defaultStyle");
    FilterGroupMeta filterGroup = 
        createFilterGroupMeta("label", "icon");
    
    filterConfig.addFilterGroupMetasItem(filterGroup);
    
    filterGroup.addFilterFieldMetasItem(createTextFieldMeta("labelCode"));

    return filterConfig;
  }
  
  private FilterGroupMeta createFilterGroupMeta(String labelCode, String iconCode) {
    FilterGroupMeta group = new FilterGroupMeta();
    group.setId(labelCode);
    group.setLabelCode(labelCode);
    group.setIconCode(iconCode);
    group.setType(FilterGroupType.AND);
    return group;
  }
  
  private FilterFieldMeta createTextFieldMeta(String labelCode) {
    FilterFieldMeta filter = new FilterFieldMeta();
    filter.setLabelCode(labelCode);
    
    FilterOperation filterOperation =  new FilterOperation()
        .id("10")
        .operationCode("operationCode")
        .labelCode("lCode")
        .propertyUri1(URI.create("property"))
        .possibleValuesUri(URI.create("possible"));
    
    filter.addOperationsItem(filterOperation);
    return filter;
  }

}
