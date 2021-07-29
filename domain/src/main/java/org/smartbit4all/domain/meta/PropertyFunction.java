package org.smartbit4all.domain.meta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Describes a function that can be applied on a property. </br>
 *  A function can use literals and other properties in its argument list. Also it is possible to
 *  embed other functions. </br> 
 *  </br>
 *  Use the {@link PropertyFunction#build(String)} static method to create an instance.
 */
public final class PropertyFunction {
  public static final PropertyFunction UPPER = PropertyFunction.withSelfPropertyArgument("upper");
  public static final PropertyFunction LOWER = PropertyFunction.withSelfPropertyArgument("lower");
  public static final PropertyFunction SUM = PropertyFunction.withSelfPropertyArgument("sum");
  public static final PropertyFunction AVG = PropertyFunction.withSelfPropertyArgument("avg");
  public static final PropertyFunction MIN = PropertyFunction.withSelfPropertyArgument("min");
  public static final PropertyFunction MAX = PropertyFunction.withSelfPropertyArgument("max");
  
  public static final Map<String, PropertyFunction> basicFunctionsByName = new HashMap<>();
  static {
    Arrays.asList(UPPER, LOWER, SUM, AVG, MIN, MAX)
      .forEach(f -> basicFunctionsByName.put(f.getName(), f));
  }
  
  private String name;
  
  private String sqlStatement;
  
  private String parameterString;
  
  private List<Property<?>> requiredProperties = new ArrayList<>();
  
  private PropertyFunction(Builder builder) {
    this.name = builder.functionName;
    this.sqlStatement = builder.sqlStatement;
    this.requiredProperties = builder.requiredProperties;
    this.parameterString = builder.paramString;
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
  
  /**
   * Builder method for a {@link PropertyFunction} object.
   * 
   * @param functionName 
   *    It is required to set the function name. Function names must start with an alphabetic letter
   *    and can be max 30 characters long with numbers, $, # and underscore characters allowed.
   */
  public static Builder build(String functionName) {
    return new Builder(functionName);
  }
  
  /**
   * It's a shortcut to build a {@link PropertyFunction} that has only one argument that is the
   * property itself. </br>
   * Example: <br>
   * <code>PropertyFunction.withSelfPropertyArgument("UPPER")</code> 
   * may result an <code>UPPER(myProperty)</code> function.
   * @param functionName
   *    It is required to set the function name. Function names must start with an alphabetic letter
   *    and can be max 30 characters long with numbers, $, # and underscore characters allowed.
   */
  public static PropertyFunction withSelfPropertyArgument(String functionName) {
    return new Builder(functionName).build();
  }
  
  public static final class Builder {
    
    // Starts with letter, max 30 characters, including numbers, $, # and underscore
    private static final String REGEX_FUNCTIONNAME = "^[a-zA-Z][a-zA-Z0-9$#_]{0,29}$";
    
    // This regular expression validates whether there is any inline or block comment, basic sql statment, text block, statement break
    private static final String REGEX_PARAM = "[\\t\\r\\n]|(--[^\\r\\n]*)|(\\/\\*[\\w\\W]*?(?=\\*)\\*\\/)|('(''|[^'])*')|\\b(ALTER|CREATE|DELETE|DROP|EXEC(UTE){0,1}|INSERT( +INTO){0,1}|MERGE|SELECT|UPDATE|UNION( +ALL){0,1})\\b|(;)";
    
    private String functionName;
    
    private String sqlStatement;
    
    private int propertyParamCounter = 0;
    
    private int innerFunctionCounter = 0;

    private String paramString;
    private StringBuilder paramStringBuilder = new StringBuilder();
    
    private List<Property<?>> requiredProperties = new ArrayList<>();

    private Builder(String functionName) {
      checkFunctionName(functionName);
      this.sqlStatement = functionName;
    }
    
    /**
     * Creates a {@link PropertyFunction} object with the set parameters. 
     */
    public PropertyFunction build() {
      if(innerFunctionCounter != 0) {
        throw new RuntimeException("There is an inner function that has not been closed!"
            + "Use the 'closeInnerFunction()' method of the builder to close it!");
      }
      this.paramString = paramStringBuilder.toString();
      this.functionName = createFunctionName();
      return new PropertyFunction(this);
    }
    
    /**
     * Adds the current property as parameter.
     */
    public Builder selfPropertyParam() {
      addPropNumberToParamStringBuilder(0);
      return this;
    }
    
    /**
     * Adds the given property as parameter.
     */
    public Builder propertyParam(Property<?> prop) {
      int indexOfProp = requiredProperties.indexOf(prop);
      if(indexOfProp < 0) {
        indexOfProp = ++propertyParamCounter;
        requiredProperties.add(prop);
      }
      addPropNumberToParamStringBuilder(indexOfProp);
      return this;
    }
    
    /**
     * Adds a parameter as is.</br>
     * The given parameter will be checked for injection attacks.
     */
    public Builder param(String param) {
      checkParam(param);
      addToParamStringBuilder(param);
      return this;
    }
    
    /**
     * Adds a parameter as a string.</br>
     * The given parameter will be checked for injection attacks.
     */
    public Builder stringParam(String param) {
      checkParam(param);
      addToParamStringBuilder("''" + param + "''");
      return this;
    }
    
    /**
     * Adds an inner function. This function can get arguments the same was as the outer one. </br>
     * <b>It is importatnt</b> to close the inner function with the 
     * {@link Builder#closeInnerFunction()} method!
     * 
     * @param functionName
     *  It is required to set the function name. Function names must start with an alphabetic letter
     *  and can be max 30 characters long with numbers, $, # and underscore characters allowed.
     */
    public Builder addInnerFunction(String functionName) {
      checkFunctionName(functionName);
      addToParamStringBuilder(functionName + "(");
      innerFunctionCounter++;
      return this;
    }
    
    /**
     * When an inner function was applied on the builder it is required to close it with this method
     */
    public Builder closeInnerFunction() {
      if(--innerFunctionCounter < 0) {
        throw new RuntimeException("There is no function that can be closed!");
      }
      paramStringBuilder.append(")");
      return this;
    }
    
    private void addPropNumberToParamStringBuilder(int propNumber) {
      addToParamStringBuilder("{" + propNumber + "}");
    }
    
    private void addToParamStringBuilder(String param) {
      if (paramStringBuilder.length() != 0 && !"("
          .equals(String.valueOf(paramStringBuilder.charAt(paramStringBuilder.length() - 1)))) {
        paramStringBuilder.append(", ");
      }
      paramStringBuilder.append(param);
    }
    
    /**
     * @return functionName_transformedParameterString_propertyName1_propertyName2 and the
     * parameter string is transformed like: 0-1-paramstring-2
     */
    private String createFunctionName() {
      if(paramString == null || paramString.isEmpty()) {
        return sqlStatement;
      }
      StringBuilder sb = new StringBuilder();
      
      sb.append(sqlStatement);
      sb.append("_");
      sb.append(paramString.replaceAll("\\{|\\}", "").replace(",", "-").replace(" ", ""));
      requiredProperties.forEach(p -> {
        sb.append("_");
        sb.append(p.getName());
      });
      
      return sb.toString();
    }
    
    private void checkFunctionName(String functionName) {
      if(functionName == null || functionName.isEmpty()) {
        throw new RuntimeException("Missing function name!");
      }
      if(!functionName.matches(REGEX_FUNCTIONNAME)) {
        throw new RuntimeException("Invalid function name! Must match regex " + REGEX_FUNCTIONNAME);
      }
    }
    
    private void checkParam(String param) {
      if(param == null || param.isEmpty()) {
        throw new RuntimeException("Added param can not be null nor empty!");
      }
      if(param.matches(REGEX_PARAM)) {
        throw new RuntimeException("Invalid parameter! Must match regex " + REGEX_PARAM);
      }
    }
    
  }
  
}
