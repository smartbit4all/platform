package org.smartbit4all.domain.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * The utility functions for the {@link CrudApi} and its implementations.
 */
@Service
public class CrudApis implements InitializingBean {

  private static CrudApis instance;

  private CrudApi crudApi;

  /**
   * To avoid on demand instantiation.
   */
  public CrudApis(CrudApi crudApi) {
    this.crudApi = crudApi;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    instance = this;
  }

  public static final CrudApi getCrudApi() {
    if (instance == null) {
      throw new IllegalStateException(
          "There is no QueryApi implementation registered in the Spring Context!");
    }
    return instance.crudApi;
  }

}
