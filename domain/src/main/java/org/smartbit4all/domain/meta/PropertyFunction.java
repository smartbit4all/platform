package org.smartbit4all.domain.meta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyFunction {
  public static final PropertyFunction UPPER = new PropertyFunction("upper");
  public static final PropertyFunction LOWER = new PropertyFunction("lower");
  public static final PropertyFunction SUM = new PropertyFunction("sum");
  public static final PropertyFunction AVG = new PropertyFunction("avg");
  public static final PropertyFunction MIN = new PropertyFunction("min");
  public static final PropertyFunction MAX = new PropertyFunction("max");
  
  public static final Map<String, PropertyFunction> basicFunctionsByName = new HashMap<>();
  static {
    Arrays.asList(UPPER, LOWER, SUM, AVG, MIN, MAX)
      .forEach(f -> basicFunctionsByName.put(f.getName(), f));
  }
  
  private String name;
  
  private String sqlStatement;
  
  private String parameterString;
  
  private List<Property<?>> requiredProperties = new ArrayList<>();
  
  public PropertyFunction(String name, String parameterString, List<Property<?>> requiredProperties) {
    this(name);
    this.parameterString = parameterString;
    this.requiredProperties.addAll(requiredProperties);
  }
  
  public PropertyFunction(String name) {
    this.name = name;
    this.sqlStatement = name;
  }
  
  public String getStatement() {
    return sqlStatement;
  }
  
  public String getName() {
    return name;
  }
  
  public List<Property<?>> getRequiredProperties() {
    return requiredProperties;
  }

  public String getParameterString() {
    return parameterString;
  }
  
}
