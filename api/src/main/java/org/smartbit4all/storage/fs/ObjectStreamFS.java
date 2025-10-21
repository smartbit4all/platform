package org.smartbit4all.storage.fs;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.core.io.utility.ObjectStreamStore;
import org.smartbit4all.domain.data.storage.ObjectStream;
import org.smartbit4all.domain.data.storage.ObjectStreamWriter;

public class ObjectStreamFS extends ObjectStream {

  private final File file;

  public ObjectStreamFS(File file, long headPosition) {
    super();
    this.file = file;
    this.headPosition = headPosition;
  }

  @Override
  public ObjectStreamWriter getWriterImpl() {
    try {
      return new ObjectStreamWriterFS(file, headPosition);
    } catch (IOException e) {
      throw new IllegalStateException(
          "Unable to initiate the writer for the " + file + " object stream store.", e);
    }
  }

  @Override
  public Iterator<BinaryData> iteratorForward(long position) {
    try {
      ObjectStreamStore streamStore = ObjectStreamStore.openOrCreate(file, 0);
      return new Iterator<BinaryData>() {

        @Override
        public BinaryData next() {
          try {
            return streamStore.read(true);
          } catch (IOException e) {
            throw new IllegalStateException(
                "Unable to read the " + streamStore.getCurrentOffset() + ". element from the  "
                    + file + " object stream store.",
                e);
          }
        }

        @Override
        public boolean hasNext() {
          boolean b = streamStore.getCurrentOffset() < headPosition;
          return b;
        }
      };
    } catch (IOException e) {
      throw new IllegalStateException(
          "Unable to open or create the " + file + " object stream store.", e);
    }
    // TODO Close the ObjectStreamStore after the iteration!
  }

  @Override
  public Iterator<BinaryData> iteratorReverse(long position) {
    try {
      ObjectStreamStore streamStore = ObjectStreamStore.openOrCreate(file, 0);
      return new Iterator<BinaryData>() {

        @Override
        public BinaryData next() {
          try {
            return streamStore.read(false);
          } catch (IOException e) {
            throw new IllegalStateException(
                "Unable to read the " + streamStore.getCurrentOffset() + ". element from the  "
                    + file + " object stream store.",
                e);
          }
        }

        @Override
        public boolean hasNext() {
          boolean b = streamStore.getCurrentOffset() > 0;
          return b;
        }
      };
    } catch (IOException e) {
      throw new IllegalStateException(
          "Unable to open or create the " + file + " object stream store.", e);
    }
    // TODO Close the ObjectStreamStore after the iteration!
  }

}
