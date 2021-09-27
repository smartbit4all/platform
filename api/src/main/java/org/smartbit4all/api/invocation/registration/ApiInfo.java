package org.smartbit4all.api.invocation.registration;

import java.util.HashMap;
import java.util.Map;

public class ApiInfo {

  private String interfaceQualifiedName;

  private String apiIdentifier;

  private String protocol;

  private Map<String, String> parameters = new HashMap<>();

  public String getInterfaceQualifiedName() {
    return interfaceQualifiedName;
  }

  public void setInterfaceQualifiedName(String interfaceQualifiedName) {
    this.interfaceQualifiedName = interfaceQualifiedName;
  }

  public String getProtocol() {
    return protocol;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  public Map<String, String> getParameters() {
    return parameters;
  }

  public void addParameter(String key, String value) {
    this.parameters.put(key, value);
  }

  public String getApiIdentifier() {
    return apiIdentifier;
  }

  public void setApiIdentifier(String apiIdentifier) {
    this.apiIdentifier = apiIdentifier;
  }

}
