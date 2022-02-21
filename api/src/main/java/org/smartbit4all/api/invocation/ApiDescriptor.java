package org.smartbit4all.api.invocation;

import org.smartbit4all.api.invocation.bean.ApiData;

/**
 * The descriptor of an api with all the meta adat we need to identify its methods and call them.
 * 
 * @author Peter Boros
 */
class ApiDescriptor {

  /**
   * The original {@link ApiData} stored in the storage based registry.
   */
  private ApiData apiData;

  /**
   * Constructs a new {@link ApiDescriptor} instance.
   * 
   * @param apiData The {@link ApiData} that is the origination of the api.
   */
  public ApiDescriptor(ApiData apiData) {
    super();
    this.apiData = apiData;
  }

  final ApiData getApiData() {
    return apiData;
  }

  final void setApiData(ApiData apiData) {
    this.apiData = apiData;
  }

}
