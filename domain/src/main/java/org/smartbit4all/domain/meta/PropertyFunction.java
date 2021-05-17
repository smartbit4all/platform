package org.smartbit4all.domain.meta;

public enum PropertyFunction {
  UPPER("upper", "UPPER"),
  LOWER("lower", "LOWER")
  ;
  
  private String name;
  
  private String sqlStatement;
  
  private PropertyFunction(String name, String sqlStatement) {
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
