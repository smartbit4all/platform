package org.smartbit4all.core.io.utility;

import org.smartbit4all.api.binarydata.BinaryData;
import com.google.common.hash.HashingInputStream;

class BinaryDataCRCRecord {

  BinaryData binaryData;

  HashingInputStream hashingInputStream;

  public BinaryDataCRCRecord(BinaryData binaryData, HashingInputStream hashingInputStream) {
    super();
    this.binaryData = binaryData;
    this.hashingInputStream = hashingInputStream;
  }

  void check() {
    if (binaryData.getCrcCheckSum() != null && hashingInputStream != null) {
      if (hashingInputStream.hash().asInt() != binaryData.getCrcCheckSum()) {
        throw new IllegalStateException("CRC checksum error (" + binaryData.getCrcCheckSum()
            + " != " + hashingInputStream.hash().asInt() + ") in " + binaryData.toString());
      }
    }
  }

}
