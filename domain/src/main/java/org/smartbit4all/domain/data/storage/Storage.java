package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.storage.bean.ObjectReferenceList;
import org.smartbit4all.domain.data.storage.index.ExpressionEntityDefinitionExtractor;
import org.smartbit4all.domain.data.storage.index.StorageIndex;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.springframework.util.Assert;

/**
 * Storage is an ObjectStorage which maintains the indexes on storing the objects. Objects can be
 * searched using the maintained indexes and Expression-s.
 * 
 * @author Zoltan Szegedi
 *
 * @param <T> Type of the objects, which can be stored with the Storage.
 */
public class Storage<T> implements ObjectStorage<T> {

  private static final Logger log = LoggerFactory.getLogger(Storage.class);

  private ObjectStorage<T> storage;

  private List<StorageIndex<T>> indexes;

  /**
   * The class managed by the given storage instance.
   */
  private final Class<T> clazz;

  /**
   * @param clazz The class that is managed by the storage instance.
   * @param objectStorage Stores the serialized objects
   * @param indexes Indexes for the given type
   */
  public Storage(Class<T> clazz,
      ObjectStorage<T> objectStorage,
      List<StorageIndex<T>> indexes) {

    this.storage = objectStorage;
    this.indexes = indexes;
    this.clazz = clazz;
  }

  @Override
  public URI save(T object) {
    URI result = storage.save(object);
    updateIndexes(object);
    return result;
  }

  @Override
  public URI save(T object, URI uri) {
    URI result = storage.save(object, uri);
    updateIndexes(object);
    return result;
  }

  private void updateIndexes(T object) {
    for (StorageIndex<T> index : indexes) {
      try {
        index.updateIndex(object);
      } catch (Exception e) {
        log.error("Unable to update storage index.", e);
        throw new IllegalStateException(
            "Failed to update storage index for " + object + " - " + clazz, e);
      }
    }
  }

  @Override
  public Optional<T> load(URI uri) {
    return storage.load(uri);
  }

  @Override
  public List<T> load(List<URI> uris) {
    if (uris == null || uris.isEmpty()) {
      return Collections.emptyList();
    }

    return storage.load(uris);
  }

  @Override
  public boolean delete(URI uri) {
    return storage.delete(uri);
  }


  @Override
  public List<T> loadAll() {
    return storage.loadAll();
  }

  /**
   * List all data objects. Warning: can be high amount of data in the result, use carefully!
   * 
   * @return All objects stored with the type T
   * @throws Exception
   */
  public List<T> listAllDatas() throws Exception {
    return loadAll();
  }

  /**
   * List the datas which fulfill the criteria of the given expression. The expression must only
   * contains properties of the given entity definition!
   */
  public List<T> listDatas(Expression expression) throws Exception {
    List<URI> uris = listUris(expression);
    return load(uris);
  }

  /**
   * TODO This evaluation should be handled by Query API (eg. exists), so use this judiciously, only
   * if other solutions are not fit for the task.
   * 
   * List the datas by the intersection of the given expression results The result list contains the
   * values which fulfill all expression criterias. The expression must only contains properties of
   * the given entity definition!
   */
  public List<T> listDatas(List<Expression> expressions)
      throws Exception {

    Assert.notEmpty(expressions, "No expression given in listData call");

    List<URI> resultUris = new ArrayList<>();

    Iterator<Expression> iterExpression = expressions.iterator();

    Expression firstExpression = iterExpression.next();
    resultUris.addAll(listUris(firstExpression));

    while (iterExpression.hasNext()) {
      Expression expression = iterExpression.next();
      List<URI> uris = listUris(expression);
      resultUris = intersectUriLists(resultUris, uris);
    }

    return load(resultUris);
  }

  public List<URI> listUris(Expression expression) throws Exception {
    List<URI> uris = new ArrayList<>();

    for (StorageIndex<T> index : indexes) {
      Optional<EntityDefinition> expressionEntityDef =
          ExpressionEntityDefinitionExtractor.getOnlyEntityDefinition(expression);

      if (expressionEntityDef.isPresent() &&
          index.getEntityDef().equals(expressionEntityDef.get())) {

        uris.addAll(index.listUris(expression));
      }
    }

    return uris;
  }

  public static List<URI> intersectUriLists(List<URI> resultUris, List<URI> uris) {
    return uris.stream()
        .distinct()
        .filter(resultUris::contains)
        .collect(Collectors.toList());
  }

  public final Class<T> getClazz() {
    return clazz;
  }

  @Override
  public void saveReferences(ObjectReferenceRequest referenceRequest) {
    storage.saveReferences(referenceRequest);
  }

  @Override
  public ObjectUriProvider<T> getUriProvider() {
    return storage.getUriProvider();
  }

  @Override
  public URI getObjectUri(T Object) {
    return storage.getObjectUri(Object);
  }

  @Override
  public Optional<ObjectReferenceList> loadReferences(URI uri, String typeClass) {
    return storage.loadReferences(uri, typeClass);
  }

  @Override
  public ObjectStorage<T> setUriMutator(BiConsumer<T, URI> mutator) {
    storage.setUriMutator(mutator);
    return this;
  }

}
