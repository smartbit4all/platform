package org.smartbit4all.api.mdm;

import java.net.URI;
import org.smartbit4all.api.binarydata.BinaryData;

public interface MDMSetupApi {

  void loadEntries(BinaryData json);

  void loadValueLists(BinaryData json);

  void importEntriesFromCsvFile(String definition, String entry, BinaryData csvFile,
      String csvSeparator, URI branchUri);

}
