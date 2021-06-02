package org.smartbit4all.ui.common.components.document;

import java.io.InputStream;

public class ImageWithAlt {
  private InputStream image;
  private String alt;
  private String filename;

  public InputStream getImage() {
    return image;
  }

  public void setImage(InputStream image) {
    this.image = image;
  }

  public String getAlt() {
    return alt;
  }

  public void setAlt(String alt) {
    this.alt = alt;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public ImageWithAlt(InputStream image, String alt, String filename) {
    this.image = image;
    this.alt = alt;
    this.filename = filename;
  }
}
