package org.smartbit4all.domain.data.storage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectSerializer;

/**
 * This is a generic interface for a stream of object. The stream can be appended with new objects.
 * The ObjectStream provides {@link Iterator}, {@link Iterable} and {@link Stream} acces to the
 * objects stored in the stream. This {@link ObjectStream} is maganed by the {@link StorageApi} and
 * provided by the {@link ObjectStorage} available. The raw data of {@link BinaryData}, the
 * {@link Map} based data and the typed interfaces are also available.
 */
public abstract class ObjectStream {

  protected long headPosition;

  private ObjectStreamWriter currentWriter;

  public final ObjectStreamWriter getWriter() {
    if (currentWriter != null) {
      return currentWriter;
    }
    currentWriter = getWriterImpl();
    currentWriter.setObjectStream(this);
    return currentWriter;
  }

  public abstract ObjectStreamWriter getWriterImpl();

  public abstract Iterator<BinaryData> iteratorForward(long position);

  public abstract Iterator<BinaryData> iteratorReverse(long position);

  private class ObjectIterator<O> implements Iterator<O> {

    final Iterator<BinaryData> iterator;
    final ObjectDefinition<O> definition;

    public ObjectIterator(Iterator<BinaryData> iterator,
        ObjectDefinition<O> definition) {
      super();
      this.iterator = iterator;
      this.definition = definition;
    }

    @Override
    public boolean hasNext() {
      return iterator.hasNext();
    }

    @Override
    public O next() {
      try {
        return definition.deserialize(iterator.next()).get();
      } catch (IOException e) {
        throw new IllegalStateException("Unable to read the object from the ObjectStrean", e);
      }
    }

  };

  private class MapIterator implements Iterator<Map<String, Object>> {

    final Iterator<BinaryData> iterator;
    final ObjectSerializer serializer;

    public MapIterator(Iterator<BinaryData> iterator,
        ObjectSerializer serializer) {
      super();
      this.iterator = iterator;
      this.serializer = serializer;
    }

    @Override
    public boolean hasNext() {
      return iterator.hasNext();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> next() {
      try {
        return serializer.deserialize(iterator.next(), Map.class).orElseGet(() -> new HashMap<>());
      } catch (IOException e) {
        throw new IllegalStateException("Unable to read the object from the ObjectStrean", e);
      }
    }

  };

  public <T> Iterator<T> iteratorObject(long position, ObjectDefinition<T> definition) {
    return new ObjectIterator<T>(iteratorForward(position), definition);
  }

  public <T> Iterator<T> iteratorObjectReverse(long position, ObjectDefinition<T> definition) {
    return new ObjectIterator<T>(iteratorReverse(position), definition);
  }

  public Iterator<Map<String, Object>> iteratorMap(long position, ObjectSerializer serializer) {
    return new MapIterator(iteratorForward(position), serializer);
  }

  public Iterator<Map<String, Object>> iteratorMapReverse(long position,
      ObjectSerializer serializer) {
    return new MapIterator(iteratorReverse(position), serializer);
  }

  public Iterable<BinaryData> iterable(long position) {
    return new Iterable<BinaryData>() {

      @Override
      public Iterator<BinaryData> iterator() {
        return iteratorForward(position);
      }
    };
  }

  public Iterable<BinaryData> iterableReverse(long position) {
    return new Iterable<BinaryData>() {

      @Override
      public Iterator<BinaryData> iterator() {
        return iteratorReverse(position);
      }
    };
  }

  public <T> Iterable<T> iterableObject(long position, ObjectDefinition<T> definition) {
    return new Iterable<T>() {

      @Override
      public Iterator<T> iterator() {
        return iteratorObject(position, definition);
      }
    };
  }

  public <T> Iterable<T> iterableObjectReverse(long position, ObjectDefinition<T> definition) {
    return new Iterable<T>() {

      @Override
      public Iterator<T> iterator() {
        return iteratorObjectReverse(position, definition);
      }
    };
  }

  public Iterable<Map<String, Object>> iterableMap(long position, ObjectSerializer serializer) {
    return new Iterable<Map<String, Object>>() {

      @Override
      public Iterator<Map<String, Object>> iterator() {
        return iteratorMap(position, serializer);
      }
    };
  }

  public Iterable<Map<String, Object>> iterableMapReverse(long position,
      ObjectSerializer serializer) {
    return new Iterable<Map<String, Object>>() {

      @Override
      public Iterator<Map<String, Object>> iterator() {
        return iteratorMapReverse(position, serializer);
      }
    };
  }

  public Stream<BinaryData> stream(long position) {
    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(iteratorForward(position), Spliterator.ORDERED),
        false);
  }

  public Stream<BinaryData> streamReverse(long position) {
    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(iteratorReverse(position), Spliterator.ORDERED),
        false);
  }

  public <T> Stream<T> streamObject(long position, ObjectDefinition<T> definition) {
    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(iteratorObject(position, definition),
            Spliterator.ORDERED),
        false);
  }

  public <T> Stream<T> streamObjectReverse(long position, ObjectDefinition<T> definition) {
    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(iteratorObjectReverse(position, definition),
            Spliterator.ORDERED),
        false);
  }

  public Stream<Map<String, Object>> streamMap(long position, ObjectSerializer serializer) {
    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(iteratorMap(position, serializer), Spliterator.ORDERED),
        false);
  }

  public Stream<Map<String, Object>> streamMapReverse(long position, ObjectSerializer serializer) {
    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(iteratorMapReverse(position, serializer),
            Spliterator.ORDERED),
        false);
  }

  public long getHeadPosition() {
    return headPosition;
  }

  public void setHeadPosition(long headPosition) {
    this.headPosition = headPosition;
  }

  void clearCurrentWriter() {
    this.currentWriter = null;
  }

}
