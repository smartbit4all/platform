package org.smartbit4all.api.mdm;

import org.smartbit4all.api.binarydata.BinaryData;

public interface MDMSetupApi {

  void loadEntries(BinaryData json);

  void loadValueLists(BinaryData json);

}
