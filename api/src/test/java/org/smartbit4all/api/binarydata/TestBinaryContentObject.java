package org.smartbit4all.api.binarydata;

public class TestBinaryContentObject {

  private BinaryContent content;

  public BinaryContent getContent() {
    return content;
  }

  public void setContent(BinaryContent content) {
    this.content = content;
  }

  public TestBinaryContentObject content(BinaryContent content) {
    this.content = content;
    return this;
  }


}
