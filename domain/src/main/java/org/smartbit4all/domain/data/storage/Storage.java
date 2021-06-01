package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import org.smartbit4all.domain.data.storage.index.StorageIndex;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;

/**
 * Storage is an ObjectStorage which maintains the indexes on storing the objects. Objects can be
 * searched using the maintained indexes and Expression-s.
 * 
 * @author Zoltan Szegedi
 *
 * @param <T> Type of the objects, which can be stored with the Storage.
 */
public class Storage<T> implements ObjectStorage<T> {

  private ObjectStorage<T> storage;

  private List<StorageIndex<T>> indexes;

  /**
   * @param objectStorage Stores the serialized objects
   * @param indexes Indexes for the given type
   */
  public Storage(
      ObjectStorage<T> objectStorage,
      List<StorageIndex<T>> indexes) {

    this.storage = objectStorage;
    this.indexes = indexes;
  }

  @Override
  public void save(T object) throws Exception {
    storage.save(object);
    updateIndexes(object);
  }

  @Override
  public void save(T object, URI uri) throws Exception {
    storage.save(object, uri);
    updateIndexes(object);
  }

  private void updateIndexes(T object) throws Exception {
    for (StorageIndex<T> index : indexes) {
      index.updateIndex(object);
    }
  }

  @Override
  public Optional<T> load(URI uri) throws Exception {
    return storage.load(uri);
  }

  @Override
  public List<T> load(List<URI> uris) throws Exception {
    if (uris.size() < 1) {
      return Collections.emptyList();
    }

    return storage.load(uris);
  }

  @Override
  public boolean delete(URI uri) throws Exception {
    return storage.delete(uri);
  }

  /**
   * List the datas which fulfill the criteria of the given expression.
   * The expression must only contains properties of the given entity definition!
   */
  public List<T> listDatas(EntityDefinition entityDef, Expression expression) throws Exception {
    List<URI> uris = listUris(entityDef, expression);
    return load(uris);
  }
  
  /**
   * TODO This evaluation should be handled by Query API (eg. exists),
   * so use this judiciously, only if other solutions are not fit for the task.
   * 
   * List the datas by the intersection of the given expression results
   * The result list contains the values which fulfill all expression criterias.
   * The expression must only contains properties of the given entity definition!
   */
  public List<T> listDatas(Map<EntityDefinition, Expression> expressionsByEntityDef)
      throws Exception {

    List<URI> resultUris = new ArrayList<>();

    Iterator<Entry<EntityDefinition, Expression>> iterExpression =
        expressionsByEntityDef.entrySet().iterator();

    Entry<EntityDefinition, Expression> firstExpression = iterExpression.next();
    resultUris.addAll(listUris(firstExpression));

    while (iterExpression.hasNext()) {
      Entry<EntityDefinition, Expression> expression = iterExpression.next();
      List<URI> uris = listUris(expression);
      resultUris = intersectUriLists(resultUris, uris);
    }

    return load(resultUris);
  }

  public List<URI> listUris(EntityDefinition entityDef, Expression expression) throws Exception {
    List<URI> uris = new ArrayList<>();

    for (StorageIndex<T> index : indexes) {
      if (index.getEntityDef() == entityDef) {
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

  private List<URI> listUris(Entry<EntityDefinition, Expression> firstExpression) throws Exception {
    return listUris(firstExpression.getKey(), firstExpression.getValue());
  }
  
}
