package org.smartbit4all.bff.api.mdm.relation;

import java.util.Map;
import java.util.function.UnaryOperator;
import org.smartbit4all.api.mdm.bean.MDMRelationDefinition;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.core.object.ObjectNode;

/**
 * Augments an MDM Entry editor {@link View} to enable selection of related objects along the
 * configured {@link MDMRelationDefinition}s originating from the entry.
 */
public interface MDMRelationEditorService {

  /**
   * Configures the editor view to contain the necessary value sets, widgets and model extensions to
   * facilitate editing the managed domain object's relations.
   * 
   * <p>
   * Call this method in the editor view's {@link PageApiImpl#initModel(View)}
   * 
   * @param view the {@link View} to be augmented, not null
   * @param viewModel a raw {@link Map} representation of the editor view's model, not null, must be
   *        mutable
   */
  @SuppressWarnings("unchecked")
  void addRelationsToViewModel(final View view, final Map viewModel);

  @SuppressWarnings("unchecked")
  void addRelationsToViewModel(final View view, final Map viewModel,
      final UnaryOperator<Value> postProcessValue);

  /**
   * Sets the host objects relations defined by the user.
   * 
   * <p>
   * The {@link View} is expected to be augmented by {@link #addRelationsToViewModel(View, Map)} to
   * enable correct updates to the relations.
   * 
   * <p>
   * Call this method as a <i>save preprocessor</i> before submitting the host {@link ObjectNode} to
   * an MDM save operation.
   * 
   * @param view the managed domain object editor {@link View}, not null
   * @param host the {@link ObjectNode} representation of the domain object, not null
   */
  void setRelationsInHost(final View view, final ObjectNode host);

}
