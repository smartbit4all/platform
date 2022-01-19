package org.smartbit4all.domain.utility.serialize;

public enum SerializationTokens {

  // @formatter:off
  META          (0x90),
  ROWS          (0x91),
  ROWIDX        (0x92),
  META_COLS     (0x93),
  ;
  // @formatter:on

  private byte value;

  private SerializationTokens(int value) {
    this.value = (byte) value;
  }

  public byte getValue() {
    return value;
  }


}
