package org.smartbit4all.domain.meta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The parameter for the {@link EventHandler} that defines all the {@link InputValue},
 * {@link OutputValue} and {@link InOutValue} parameters.
 * 
 * @author Peter Boros
 */
public class EventParameter {

  /**
   * If we need to serialize somehow the execution order of the {@link EventHandler}s then we need
   * to know what is the relation between them. It's a kind of compare. If we are the predecessor
   * then we must run earlier then the other. If we are the successor then we must run later then
   * the other. If the relation is none then there is no direct relation between the two parameter
   * set. So it doesn't matter what is the execution order. The bidirection means that we have
   * dependency in both direction.
   * 
   * @author Peter Boros
   */
  public static enum Relation {

    PREDECESSOR, SUCCESSOR, BIDIRECTION, NONE

  }

  /**
   * The input properties of the logic. Some of them are both input and output.
   */
  protected List<InputValue<?>> inputs = new ArrayList<InputValue<?>>();

  /**
   * The output properties of the logic. Some of them are both input and output.
   */
  protected List<OutputValue<?>> outputs = new ArrayList<OutputValue<?>>();

  /**
   * These are the logics that consumes the results of this logic. They can consumes the fields that
   * are produced or they can have direct reference for this. They must run later to have all the
   * necessary input. They are the direct successor
   */
  Set<EventHandler> outputConsumers = new HashSet<>();

  /**
   * The qualified name of the result properties. The qualified name means the qualified name of the
   * entity definition class + dot + name of the property. If we have a one entity related logic
   * then it look unnecessary but in case of a complex logic it's a must.
   */
  Set<EventHandler> inputProviders = new HashSet<>();

  /**
   * The class level meta holding the fields for accessing the instance level parameters.
   */
  protected final EventParameterMeta meta;

  /**
   * Constructs the parameter instance for the logic. At that point all the parameter field must be
   * set. Therefore we can use the values of the fields.
   * 
   * @param logic
   * @param meta
   */
  EventParameter(EventHandler logic, EventParameterMeta meta) {
    super();
    this.meta = meta;
    // The direct references to the inputProviders and outputConsumers will be evaluated later.
    // After every logic in the collaboration will be created.
    boolean hasAnyMissing = false;
    for (InputValue<?> input : meta.inputs(logic)) {
      if (input != null) {
        inputs.add(input);
      } else {
        hasAnyMissing = true;
      }
    }
    for (OutputValue<?> output : meta.outputs(logic)) {
      if (output != null) {
        outputs.add(output);
      } else {
        hasAnyMissing = true;
      }
    }
    for (EventHandler input : meta.inputProviders(logic)) {
      if (input != null) {
        inputProviders.add(input);
      } else {
        hasAnyMissing = true;
      }
    }
    for (EventHandler output : meta.outputConsumers(logic)) {
      if (output != null) {
        outputConsumers.add(output);
      } else {
        hasAnyMissing = true;
      }
    }
    // TODO If we had any null field then we should produce a log about the whole logic.
    if (hasAnyMissing) {

    }
  }

  /**
   * This must be called to analyze the dependencies by the input and output values. If an input
   * field is produced by a logic then we add it to our {@link #inputProviders}. If an output
   * consumed by another logic then we add it to the {@link #outputConsumers} set. As a result in
   * these sets we will have all the dependencies.
   * 
   * @param producedBy Contains the logics by the property fully qualified name they produce.
   * @param consumedBy Contains the logics by the property fully qualified name they consume.
   */
  final void analyzeDependencies(Map<Property<?>, EventHandler> producedBy,
      Map<Property<?>, List<EventHandler>> consumedBy) {
    // Those logics that are producing any of our inputs will be added to inputProviders.
    for (InputValue<?> input : inputs) {
      EventHandler logic = producedBy.get(input.property());
      inputProviders.add(logic);
    }
    // Those logics that are producing any of our inputs will be added to inputProviders.
    for (OutputValue<?> output : outputs) {
      List<EventHandler> consumerLogics = consumedBy.get(output.property());
      outputConsumers.addAll(consumerLogics);
    }
  }

  /**
   * If a computation doesn't have any parameter.
   */
  public static final EventParameter voidParameter = new EventParameter(null, null);

}
