package org.smartbit4all.api.mdm;

/**
 * The global master data management api that is responsible for the master data of the application.
 * It help to find the proper api implementation for a given object. By name we can find the given
 * {@link MDMEntryApi} and we can use it.
 * 
 * @author Peter Boros
 */
public interface MasterDataManagementApi {

  static final String SCHEMA = "mdm";

  <T> MDMEntryApi<T> getApi(Class<T> clazz, String name);

  <T> MDMEntryApi<T> getApi(Class<T> clazz);

}
