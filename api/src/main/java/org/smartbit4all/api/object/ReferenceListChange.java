package org.smartbit4all.api.object;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.smartbit4all.core.object.ReferenceDefinition;

/**
 * This is the change object a reference that is a list of reference at the end. There are two
 * options to modify the list.
 * <ul>
 * <li><b>Set the whole list as new:</b> In this case we have to set the list with every list items.
 * The list items could remain the same so we don't have to modify the referred objects. It might be
 * a reordering of the list items.</li>
 * <li><b>Modify the list by adding and removing items:</b> The changes are instructions about the
 * list. We can us the add, addFirst, addAfter, addBefore, update and remove functions to apply
 * changes about the list of reference.</li>
 * </ul>
 * 
 * @author Peter Boros
 *
 */
public class ReferenceListChange extends ReferenceChangeRequest {

  /**
   * The object changes to apply on the list.
   */
  final List<ObjectChangeRequest> changeList = new ArrayList<>();

  public ReferenceListChange(ApplyChangeRequest request, ObjectChangeRequest objectChangeRequest,
      ReferenceDefinition definition) {
    super(request, objectChangeRequest, definition);
  }

  /**
   * This will set the list as is and doesn't reserve the previous changes.
   * 
   * @param listToSet
   * @return
   */
  public final ReferenceListChange setList(List<ObjectChangeRequest> listToSet) {
    changeList.clear();
    changeList.addAll(listToSet);
    return this;
  }

  /**
   * Clear the content of the
   * 
   * @return
   */
  public final ReferenceListChange clearList() {
    changeList.clear();
    return this;
  }

  /**
   * This will add the new object at the end of the list without modifying the existing items.
   * 
   * @param item
   * @return
   */
  public final ReferenceListChange add(ObjectChangeRequest item) {
    changeList.add(item);
    return this;
  }

  @Override
  public Iterable<ObjectChangeRequest> changes() {
    return changeList;
  }

  @Override
  public void apply(ObjectChangeRequest refererObj, Map<ObjectChangeRequest, URI> uris) {
    // TODO We replace the list...At this time we already have the
    List<URI> list = uris.values().stream().collect(Collectors.toList());
    refererObj.getOrCreateObjectAsMap().put(definition.getSourcePropertyPath(), list);
  }

}
