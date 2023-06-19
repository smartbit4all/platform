package org.smartbit4all.api.mdm;

import org.smartbit4all.api.mdm.bean.MDMDefinition;

/**
 * The global master data management api that is responsible for the master data of the application.
 * It is working with the {@link MDMDefinition} object that describes the master data management
 * schemas of the application. We can define the MDMDefinition and its details in spring context. In
 * this case the {@link MasterDataManagementApi} will apply the setting into the storage at the
 * first call. It is a fast operation and the operation state is stored also in the storage.
 * 
 * @author Peter Boros
 */
public interface MasterDataManagementApi {

  static final String SCHEMA = "mdm";

  MDMEntryApi getApi(String definition, String name);

}
