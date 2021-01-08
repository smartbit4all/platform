/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.domain.meta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.core.SB4Context;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

public abstract class ComputationFrameworkImpl implements ComputationFramework {

  private static final Logger log = LoggerFactory.getLogger(ComputationFrameworkImpl.class);

  /**
   * The dependency graph to help the execution. If something changed then we have to evaluate which
   * logics to call and what order.
   */
  MutableGraph<ComputationLogic> dependencyGraph =
      GraphBuilder.directed().expectedNodeCount(100).build();

  /**
   * All the logics we have.
   */
  Set<ComputationLogic> logics = new HashSet<>();

  Map<Property<?>, ComputationLogic> producedBy = new HashMap<>();

  /**
   * The list of logics consuming the given property.
   */
  Map<Property<?>, List<ComputationLogic>> consumedBy = new HashMap<>();

  /**
   * The key is the entity that is the root of the given logic.
   */
  Map<EntityDefinition, List<ComputationLogic>> logicsByEntity = new HashMap<>();

  @Override
  public void install(EntityDefinition entityDef, Class<? extends ComputationLogic> logicClass) {
    ComputationLogic logic = SB4Context.get().get(logicClass);
    // Set the dependents, install all the necessary data. Can be recursive because it installs all
    // the dependencies.
    if (logic != null) {
      ComputationParameterMeta meta = logic.meta();
      for (Field inputField : meta.inputs) {
        // If it's a wired input field then we get the property from the entity.
        // TODO Reflection cache might be necessary!
        PropertyWired propertyWired = inputField.getAnnotation(PropertyWired.class);
        if (propertyWired != null) {
          Property<?> property = entityDef.getProperty(propertyWired.value());
          try {
            inputField.setAccessible(true);
            inputField.set(logic, setupInputValue(entityDef, property));
          } catch (IllegalArgumentException | IllegalAccessException e) {
            log.error("Unable to set the {} logic {} input value.", logicClass.getName(),
                inputField.getName(), e);
          } finally {
          }
        }
      }
    }
    setup(logic);
  }

  protected abstract InputValue<?> setupInputValue(EntityDefinition entityDef,
      Property<?> property);

  /**
   * The setup of the logics with the parameters they defined. It can leads to complete the data
   * model for example.
   * 
   * @param logic
   */
  protected abstract void setup(ComputationLogic logic);

  @Override
  public void start() {
    // Perform the last analysis to produce the execution graph. For this we must create a global
    // producing and consuming registry.
    for (ComputationLogic logic : logics) {
      // Add the logisc by the entity.
      List<ComputationLogic> entityLogicList = logicsByEntity.get(logic.entity());
      if (entityLogicList == null) {
        entityLogicList = new ArrayList<>();
        logicsByEntity.put(logic.entity(), entityLogicList);
      }
      entityLogicList.add(logic);
      for (OutputValue<?> output : logic.parameter().outputs) {
        // Check if we are the only one that produce the given property.
        ComputationLogic existingLogic = producedBy.get(output.property());
        if (existingLogic != null) {
          log.error(
              "The same {} property is produced be more than one logic. {} and {}. The framework skip the second as a dependency!",
              output.property().getName(), existingLogic.getClass().getName(),
              logic.getClass().getName());
        } else {
          producedBy.put(output.property(), logic);
        }
      }
      for (InputValue<?> input : logic.parameter().inputs) {
        List<ComputationLogic> list = consumedBy.get(input.property());
        if (list == null) {
          list = new ArrayList<>(5);
          consumedBy.put(input.property(), list);
        }
        list.add(logic);
      }
    }
    // And then we can call the analyze.
    for (ComputationLogic logic : logics) {
      logic.parameter().analyzeDependencies(producedBy, consumedBy);
    }
    // Now we can draw the dependency graph. There is no trick because we already have the graph in
    // the dependencies.
    // Just add all the nodes and edges from the node list.
    for (ComputationLogic logic : logics) {
      addNode(logic);
    }
  }

  /**
   * Add a logic to the dependency graph
   * 
   * @param logic
   */
  private final void addNode(ComputationLogic logic) {
    if (dependencyGraph.addNode(logic)) {
      // If the graph has been modified then we must process the given node.
      for (ComputationLogic consumer : logic.parameter().outputConsumers) {
        // We add the consumer and put an edge. This is a recursion but we add only if it hasn't
        // been added already. So theoretically there is no infinite loop...
        addNode(consumer);
        dependencyGraph.putEdge(logic, consumer);
      }
    }
  }

  /**
   * The construction order is tricky because not all the related logics are contained in the
   * parameter list. The list must be extended by the consumers of these logics.
   * 
   * @param logics
   * @return
   */
  private final Collection<List<ComputationLogic>> constructExecutionOrder(List<ComputationLogic> logics) {
    Set<ComputationLogic> unprocessedLogics = new HashSet<>(logics);
    Map<ComputationLogic, List<ComputationLogic>> processedLogics = new HashMap<>();
    // Traverser<EventHandler> traverser = Traverser.forGraph(dependencyGraph);
    for (ComputationLogic logic : logics) {
      // We start to collect the logics from every logic. If we find an already existing one then
      // stop going on. Check if it's unprocessed.
      if (unprocessedLogics.remove(logic)) {
        // We haven't traversed this node up till now so let's start traverse
        List<ComputationLogic> myOrderedList = new ArrayList<>();
        // Add the current logic as the first node.
        myOrderedList.add(logic);
        traverseLogic(myOrderedList, logic, processedLogics, unprocessedLogics);
      }
    }
    return processedLogics.values();
  }

  private final void traverseLogic(List<ComputationLogic> myOrderedList, ComputationLogic logic,
      Map<ComputationLogic, List<ComputationLogic>> processedLogics, Set<ComputationLogic> unprocessedLogics) {
    // Let's look at the consumers to see if they are newcomers in this situation.
    for (ComputationLogic consumer : logic.parameter().outputConsumers) {
      List<ComputationLogic> list = processedLogics.get(consumer);
      if (list != null) {
        // If we found the given logic as a processed one. In this case we add the processed list to
        // the current one. And we skip traversing the consumer.
        myOrderedList.addAll(list);
      } else {
        // We can continue traverse but let's check if the given consumer is included in the
        // unprocessed set. We have to remove it to avoid traversing again.
        unprocessedLogics.remove(consumer);
        myOrderedList.add(consumer);
        traverseLogic(myOrderedList, consumer, processedLogics, unprocessedLogics);
      }
    }
  }

  @Override
  public void fireChangeEvent(EntityDefinition entity) {
    // We don't have any property so we run all the logics related to the entity in an appropriate
    // order.
    List<ComputationLogic> entityLogics = logicsByEntity.get(entity);
    Collection<List<ComputationLogic>> parallelExecutions = constructExecutionOrder(entityLogics);
    // The individually processed logics could run parallel because they don't have any relation.
    parallelExecutions.parallelStream().forEach(list -> list.stream().forEach(l -> {
      try {
        l.execute();
      } catch (Exception e) {
        log.error("Error while executing the " + l + " logic.", e);
      }
    }));
  }

  @Override
  public void fireChangeEvent(EntityDefinition entity, Property<?> prop1) {
    // TODO Auto-generated method stub

  }

  @Override
  public void fireChangeEvent(EntityDefinition entity, Property<?> prop1, Property<?> prop2) {
    // TODO Auto-generated method stub

  }

  @Override
  public void fireChangeEvent(EntityDefinition entity, Property<?>... prop1) {
    // TODO Auto-generated method stub

  }

}
