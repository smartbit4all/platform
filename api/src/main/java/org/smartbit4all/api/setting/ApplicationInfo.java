package org.smartbit4all.api.setting;

import org.springframework.beans.factory.annotation.Value;

public class ApplicationInfo {

  @Value("${application-version:}")
  private String applicationVersion;

  @Value("${application-description:}")
  private String applicationDescription;

  public String getApplicationDescription() {
    return applicationDescription;
  }

  public String getApplicationVersion() {
    return applicationVersion;
  }

}
