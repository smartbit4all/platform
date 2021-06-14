package org.smartbit4all.domain.meta;

public class PropertyFunction {
  public static final PropertyFunction UPPER = new PropertyFunction("UPPER", "UPPER");
  public static final PropertyFunction LOWER = new PropertyFunction("LOWER", "LOWER");
  
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
