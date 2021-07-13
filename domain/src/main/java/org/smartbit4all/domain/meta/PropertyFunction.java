package org.smartbit4all.domain.meta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PropertyFunction {
  public static final PropertyFunction UPPER = new PropertyFunction("upper", "UPPER");
  public static final PropertyFunction LOWER = new PropertyFunction("lower", "LOWER");
  public static final PropertyFunction SUM = new PropertyFunction("sum", "SUM");
  public static final PropertyFunction AVG = new PropertyFunction("avg", "AVG");
  public static final PropertyFunction MIN = new PropertyFunction("min", "MIN");
  public static final PropertyFunction MAX = new PropertyFunction("max", "MAX");
  
  public static final Map<String, PropertyFunction> basicFunctionsByName = new HashMap<>();
  static {
    Arrays.asList(UPPER, LOWER, SUM, AVG, MIN, MAX)
      .forEach(f -> basicFunctionsByName.put(f.getName(), f));
  }
  
  
  private String name;
  
  private String sqlStatement;
  
  
  
  public PropertyFunction(String name, String sqlStatement) {
    this.name = name;
    this.sqlStatement = sqlStatement;
  }
  
  public String getStatement() {
    return sqlStatement;
  }
  
  public String getName() {
    return name;
  }
  
}
