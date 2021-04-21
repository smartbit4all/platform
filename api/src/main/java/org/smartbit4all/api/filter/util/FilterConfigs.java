package org.smartbit4all.api.filter.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.filter.bean.FilterConfig;
import org.smartbit4all.api.filter.bean.FilterFieldMeta;
import org.smartbit4all.api.filter.bean.FilterGroupMeta;
import org.smartbit4all.api.filter.bean.FilterGroupType;
import org.smartbit4all.api.filter.bean.FilterOperation;

public abstract class FilterConfigs {
  
  private FilterConfigs() {
    
  }
  
  public static ConfigBuilder builder() {
    return new ConfigBuilder();
  }
  
  public static class ConfigBuilder {
    
    private String defaultFilterStyle;
    private String defaultFilterGroupStyle;
    private List<ConfigGroupBuilder> configGroupBuilders;
    
    private ConfigBuilder() {
      configGroupBuilders = new ArrayList<>();
    }
    
    private void addFilterGroups(ConfigGroupBuilder configGroupBuilder) {
      configGroupBuilders.add(configGroupBuilder);
    }
    
    public ConfigBuilder defaultStyle(String defaultFilterStyle) {
      this.defaultFilterStyle = defaultFilterStyle;
      return this;
    }
    
    public ConfigBuilder defaultGroupStyle(String defaultFilterGroupStyle) {
      this.defaultFilterGroupStyle = defaultFilterGroupStyle;
      return this;
    }
    
    public ConfigGroupBuilder addGroupMeta(String id) {
      return new ConfigGroupBuilder(id, this);
    }
    
    public FilterConfig build() {
      FilterConfig filterConfig = new FilterConfig();
      filterConfig.setDefaultFilterGroupStyle(defaultFilterGroupStyle);
      filterConfig.setDefaultFilterStyle(defaultFilterStyle);
      List<FilterGroupMeta> groups = new ArrayList<>();
      for (ConfigGroupBuilder configGroupBuilder : configGroupBuilders) {
        groups.add(configGroupBuilder.build());
      }
      filterConfig.setFilterGroupMetas(groups);
      return filterConfig;
    }
  }
  
  public static class ConfigGroupBuilder {
    
    private ConfigBuilder configBuilder;
    private String id;
    private String labelCode;
    private String iconCode;
    private String style;
    private FilterGroupType type;
    private List<ConfigFieldBuilder> configFieldBuilders;
    
    private ConfigGroupBuilder(String id, ConfigBuilder configBuilder) {
      this.configBuilder = configBuilder;
      this.id = id;
      configFieldBuilders = new ArrayList<>();
    }
    
    private void addFieldBuilder(ConfigFieldBuilder configFieldBuilder) {
      configFieldBuilders.add(configFieldBuilder);
    }
    
    private FilterGroupMeta build() {
      FilterGroupMeta filterGroupMeta = new FilterGroupMeta();
      filterGroupMeta.setId(id);
      filterGroupMeta.setLabelCode(labelCode);
      filterGroupMeta.setIconCode(iconCode);
      filterGroupMeta.setStyle(style);
      filterGroupMeta.setType(type);
      List<FilterFieldMeta> filterFieldMetas = new ArrayList<>();
      for (ConfigFieldBuilder configFieldBuilder : configFieldBuilders) {
        filterFieldMetas.add(configFieldBuilder.build());
      }
      filterGroupMeta.setFilterFieldMetas(filterFieldMetas);
      return filterGroupMeta;
    }
    
    public ConfigGroupBuilder labelCode(String labelCode) {
      this.labelCode = labelCode;
      return this;
    }
    
    public ConfigGroupBuilder iconCode(String iconCode) {
      this.iconCode = iconCode;
      return this;
    }
    
    public ConfigGroupBuilder style(String style) {
      this.style = style;
      return this;
    }
    
    public ConfigGroupBuilder type(FilterGroupType type) {
      this.type = type;
      return this;
    }
    
    public ConfigFieldBuilder addFieldMeta() {
      return new ConfigFieldBuilder(this);
    }
    
    public ConfigBuilder done() {
      configBuilder.addFilterGroups(this);
      return configBuilder;
    }
  }
  
  public static class ConfigFieldBuilder {
    
    private ConfigGroupBuilder configGroupBuilder;
    private String id;
    private String labelCode;
    private String iconCode;
    private String style;
    private List<ConfigOperationBuilder> configOperationBuilders;
    
    private ConfigFieldBuilder(ConfigGroupBuilder configGroupBuilder) {
      this.configGroupBuilder = configGroupBuilder;
      configOperationBuilders = new ArrayList<>();
    }
    
    private void addOperationBuilder(ConfigOperationBuilder operation) {
      configOperationBuilders.add(operation);
    }
    
    private FilterFieldMeta build(){
      FilterFieldMeta filterFieldMeta = new FilterFieldMeta();
      filterFieldMeta.setId(id);
      filterFieldMeta.setLabelCode(labelCode);
      filterFieldMeta.setIconCode(iconCode);
      filterFieldMeta.setStyle(style);
      List<FilterOperation> filterOperations = new ArrayList<>();
      for (ConfigOperationBuilder configOperationBuilder : configOperationBuilders) {
        filterOperations.add(configOperationBuilder.build());
      }
      filterFieldMeta.setOperations(filterOperations);
      return filterFieldMeta;      
    }
    
    public ConfigFieldBuilder labelCode(String labelCode) {
      this.labelCode = labelCode;
      return this;
    }
    
    public ConfigFieldBuilder iconCode(String iconCode) {
      this.iconCode = iconCode;
      return this;
    }
    
    public ConfigFieldBuilder style(String style) {
      this.style = style;
      return this;
    }
    
    public ConfigOperationBuilder addOperation(String id, String operationCode) {
      return new ConfigOperationBuilder(id, operationCode, this);
    }
    
    public ConfigGroupBuilder done() {
      configGroupBuilder.addFieldBuilder(this);
      return configGroupBuilder;
    }
  }
  
  public static class ConfigOperationBuilder {
    
    private ConfigFieldBuilder configFieldBuilder;
    private String id;
    private String filterView;
    private URI propertyUri1;
    private URI propertyUri2;
    private URI propertyUri3;
    private URI possibleValuesUri;
    private String operationCode;
    private String labelCode;
    private String iconCode;
    
    private ConfigOperationBuilder(String id, String operationCode, ConfigFieldBuilder configFieldBuilder) {
      this.configFieldBuilder = configFieldBuilder;
      this.id = id;
      this.operationCode = operationCode;
    }
    
    private FilterOperation build() {
      FilterOperation filterOperation = new FilterOperation();
      filterOperation.setId(id);
      filterOperation.setFilterView(filterView);
      filterOperation.setPropertyUri1(propertyUri1);
      filterOperation.setPropertyUri2(propertyUri2);
      filterOperation.setPropertyUri3(propertyUri3);
      filterOperation.setPossibleValuesUri(possibleValuesUri);
      filterOperation.setOperationCode(operationCode);
      filterOperation.setLabelCode(labelCode);
      filterOperation.setIconCode(iconCode);
      return filterOperation;
    }
    
    public ConfigOperationBuilder filterView(String filterView) {
      this.filterView = filterView;
      return this;
    }
    
    public ConfigOperationBuilder propertyUri1(URI propertyUri1) {
      this.propertyUri1 = propertyUri1;
      return this;
    }
    
    public ConfigOperationBuilder propertyUri2(URI propertyUri2) {
      this.propertyUri2 = propertyUri2;
      return this;
    }
    
    public ConfigOperationBuilder propertyUri3(URI propertyUri3) {
      this.propertyUri3 = propertyUri3;
      return this;
    }
    
    public ConfigOperationBuilder possibleValuesUri(URI possibleValuesUri) {
      this.possibleValuesUri = possibleValuesUri;
      return this;
    }
    
    public ConfigOperationBuilder labelCode(String labelCode) {
      this.labelCode = labelCode;
      return this;
    }
    
    public ConfigOperationBuilder iconCode(String iconCode) {
      this.iconCode = iconCode;
      return this;
    }
    
    public ConfigFieldBuilder done() {
      configFieldBuilder.addOperationBuilder(this);
      return configFieldBuilder;
    }
  }
}
