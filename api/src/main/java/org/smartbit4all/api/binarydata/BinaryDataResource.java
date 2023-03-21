package org.smartbit4all.api.binarydata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.InputStreamResource;

/**
 * {@link Resource} implementation for {@link BinaryData}.
 * 
 * <p>
 * Useful for loading content from binary data, without having to resort to a single-use
 * {@link InputStreamResource}. In most cases, this is just a convenience for interfacing with the
 * Spring ecosystem for I/O operations, such as web controllers and file system related operations.
 * 
 * <p>
 * Especially useful for offering Spring-related clients a way to repeatedly open a fresh stream
 * from a {@code BinaryData}, when working with the {@code Resource} abstraction is necessary. Such
 * is the case with JavaMail, which needs to read mail attachment and inline element contents
 * multiple times.
 * 
 * @author Szabolcs Bazil Papp
 * @since 2023/03/21
 * @see BinaryData
 */
public class BinaryDataResource extends AbstractResource {

  private final BinaryData binaryData;
  private final String description;

  /**
   * Creates a new {@code BinaryDataResource}.
   * 
   * @param binaryData the {@link BinaryData} to wrap, not null
   */
  public BinaryDataResource(BinaryData binaryData) {
    this(binaryData, "binary data resource");
  }

  /**
   * Creates a new {@code BinaryDataResource} with the provided description.
   * 
   * @param binaryData the {@link BinaryData} to wrap, not null
   * @param description a short {@code String} describing the binary data, nullable
   */
  public BinaryDataResource(BinaryData binaryData, String description) {
    this.binaryData = Objects.requireNonNull(binaryData);
    this.description = description;
  }

  /**
   * Checks whether the wrapped {@link BinaryData} is represented as a file or not.
   * 
   * <p>
   * Any given binary data may swap between file-based or in-memory representation at its own
   * discretion. The returned value is <strong>immediately outdated</strong>: even if this method
   * returns true, there is no guarantee a subsequent access will succeed.
   * 
   * @return true if the wrapped {@link BinaryData} is currently known to be represented as a file,
   *         false otherwise
   */
  @Override
  public boolean isFile() {
    return this.binaryData.getDataFile() != null;
  }

  /**
   * Attempts to return the {@link File} represented by the wrapped {@link BinaryData}.
   * 
   * <p>
   * Please see the documentation for the methods below to confirm whether attempting to access the
   * data file is appropriate.
   * 
   * @return the {@code File} backing the wrapped {@code BinaryData}
   * @throws FileNotFoundException if the binary data is currently not backed by a file
   * @see BinaryData#getDataFile()
   * @see #isFile()
   */
  @Override
  public File getFile() throws IOException {
    if (!this.isFile()) {
      throw new FileNotFoundException(description + " could not be resolved to a file!");
    }
    return this.binaryData.getDataFile();
  }

  /**
   * Returns the description supplied during the construction of this instance.
   * 
   * @return the {@code String} description of the wrapped binary data, or null if there isn't any
   */
  @Override
  public String getDescription() {
    return this.description;
  }

  /**
   * Returns a fresh {@link InputStream} for the underlying {@link BinaryData}.
   * 
   * <p>
   * The exact stream implementation is left to the discretion of the binary data. It is the
   * responsibility of clients of this code to close the provided stream when they are finished
   * reading it.
   * 
   * @return a fresh {@link InputStream} for the wrapped binary data
   * @throws IOException if any I/O error occurs during opening, reading or closing the stream
   */
  @Override
  public InputStream getInputStream() throws IOException {
    return this.binaryData.inputStream();
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }

    if (!(other instanceof BinaryDataResource)) {
      return false;
    }

    BinaryDataResource that = (BinaryDataResource) other;
    return this.binaryData.equals(that.binaryData);
  }

  @Override
  public int hashCode() {
    return this.binaryData.hashCode();
  }

}
