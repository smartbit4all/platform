package org.smartbit4all.domain.service.modify;

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.core.SB4CompositeFunction;
import org.smartbit4all.core.SB4Function;
import org.smartbit4all.domain.service.transfer.ObjectEntityBinding;

/**
 * The {@link ApplyChanges} is an api object for the {@link ObjectEntityBinding} services. It
 * contains the operations to in a proper order to refresh the database by the changes of the
 * objects. The operations can rely on each other using their results. The operations must form an
 * acyclic graph to be able to execute.
 * 
 * For example: First we have to insert the master record to use it's id as reference from the
 * detail record. The operations are fail safe if we try to create a record but it's already exist
 * then we apply an update instead of the insert. Or if we try to update a record but it's missing
 * from the database then we insert it rather. In this way the apply changes will always produce the
 * proper database.
 * 
 * The nodes of the graph are {@link ApplyChangeOperation}s that are {@link SB4CompositeFunction}s
 * and they are using the hierarchical execution of the {@link SB4Function} framework. They can have
 * prerequisites where other operations produce the necessary records to be referenced.
 * 
 * @author Peter Boros
 */
public final class ApplyChanges {

  /**
   * The root operations for the apply changes. All of them are {@link SB4CompositeFunction}s so if
   * we have only one object hierarchy the it contains only one operation.
   */
  private final List<ApplyChangeOperation> rootOperations = new ArrayList<>();

  public final List<ApplyChangeOperation> roots() {
    return rootOperations;
  }

}
