package org.smartbit4all.bff.api.mdm.relation;

import java.net.URI;
import org.smartbit4all.api.view.bean.MultiComboBoxElement;
import org.smartbit4all.api.view.bean.MultiComboBoxModel;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;

/**
 * Augments application {@link View}s to correctly handle conjoined combo boxes, where selection
 * flows unidirectionally along a series of interconnected relations.
 * 
 * <p>
 * In such complex widgets, the i-th combo box may contain selectable values from an MDM managed
 * value set, but restricted to the values contained in the relations of the (i-1)-th combo box's
 * selected value (if any).
 */
public interface RelationManagedMultiComboBoxService {

  interface ViewModelValueAccessor {

    URI get(String widgetKey);

    void set(String widgetKey, URI value);

  }

  /**
   * Initialises the view to manage conjoined combo box value sets.
   * 
   * <p>
   * Call this method in <code>initModel(View)</code>, <strong>after the page model has already been
   * created</strong> (so the accessor may actually return live values).
   * 
   * @param view the {@link View} to be augmented, not null
   * @param multiComboBoxModel the {@link MultiComboBoxModel} describing the combo boxes partaking
   *        in the coordinated value set management; describes the
   *        {@link MultiComboBoxElement#getWidgetKey()} (where to access the the combo box's current
   *        value in the {@link View}'s model), the {@link MultiComboBoxElement#getValueSet()}
   *        (under what name are the possible values stored for the combo box) and the
   *        {@link MultiComboBoxElement#getRelation()} (what relation of the selected value should
   *        restrict the possible values of the next combo box)
   * @param accessor a per page defined {@link ViewModelValueAccessor}, to access and mutate combo
   *        box selected values in the {@link View}'s model, not null
   */
  void init(
      final View view,
      final MultiComboBoxModel multiComboBoxModel,
      final ViewModelValueAccessor accessor);

  /**
   * Enforces selection hierarchy after an action occurred.
   * 
   * <p>
   * Call this in any action handler where the combo box values in the {@link View}'s model might
   * have changed.
   * 
   * @param view the {@link View} to be augmented, not null
   * @param actionRequest the {@link UiActionRequest} which instructed the change to one or more
   *        combo box selected values, not null
   * @param accessor a per page defined {@link ViewModelValueAccessor}, to access and mutate combo
   *        box selected values in the {@link View}'s model, not null
   */
  void onManagedWidgetAction(
      final View view,
      final UiActionRequest actionRequest,
      final ViewModelValueAccessor accessor);

}
