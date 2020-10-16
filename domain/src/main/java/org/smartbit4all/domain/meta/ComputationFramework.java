package org.smartbit4all.domain.meta;

import org.smartbit4all.core.SB4Service;

/**
 * For a collaboration this is the basic service to install and run the related logics. The logics
 * are triggered by different events. They all can modify the data itself but at the same time they
 * can provide validity information about the data. The result of the validation is an up to date
 * validation result maintained by this framework.
 * 
 * If we start a collaboration then this framework initiated for and start managing the instantiated
 * logics with their dependencies. The dependency is managed in a graph that describes the order of
 * execution. If we have an event (like changing the values) then this framework evaluate which
 * logic to trigger and what order to take them. The next step to run all the logics and manage the
 * errors.
 * 
 * The computation typically belongs to a collaboration where more entities are involved. So all the
 * API depend on the entities because the scope of the computation logic is always an entity. We can
 * create computations that depends on more then one entity, but in this case we have to start from
 * the root entity to access the other properties that we need. Usually we use wired properties from
 * our own entity and dynamic ones from the others.
 * 
 * TODO Rename
 * 
 * @author Peter Boros
 * 
 */
public interface ComputationFramework extends SB4Service {

  /**
   * Retrieve the instance of the logic for the current framework. If it's not found then it will be
   * created and installed.
   * 
   * @param entityDefinition The entity definition that is the starting context for the logic. This
   *        is the context that must contain the given logic class.
   * @param logicClass
   */
  void install(EntityDefinition entityDef, Class<? extends EventHandler> logicClass);

  void start();

  /**
   * Notify all the logics that the given record is changed. In this case the ComputationLogic input
   * parameters have been set earlier. So here we can call the execute directly.
   * 
   * @param entity
   */
  void fireChangeEvent(EntityDefinition entity);

  /**
   * Notify all the logics that the given record is changed.
   * 
   * @param entity
   * @param prop1
   */
  void fireChangeEvent(EntityDefinition entity, Property<?> prop1);

  /**
   * Notify all the logics that the given record is changed.
   * 
   * @param entity
   */
  void fireChangeEvent(EntityDefinition entity, Property<?> prop1, Property<?> prop2);

  /**
   * Notify all the logics that the given record is changed.
   * 
   * @param entity
   */
  void fireChangeEvent(EntityDefinition entity, Property<?>... prop1);

}
