package org.smartbit4all.ui.api.form;

import java.net.URI;
import org.smartbit4all.ui.api.form.model.EntityFormInstance;

/**
 * API for the predictive forms, mainly for loading and saving the forms.
 * 
 * @author Zsombor Nyilas
 *
 */
public interface PredictiveFormApi {
  
  /**
   * Loads a {@link EntityFormInstance} object.
   * @param uri 
   * 
   * @return the loaded {@link EntityFormInstance} object
   */
  public EntityFormInstance loadInstance(URI uri);
  
  /**
   * Saves the {@link EntityFormInstance}, saving the state of the navigation graph, and the form data
   * 
   * @param instance the {@link EntityFormInstance} to be saved
   */
  public void saveForm(EntityFormInstance instance);


}
