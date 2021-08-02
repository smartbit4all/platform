package org.smartbit4all.ui.api.form;

import java.net.URI;
import org.smartbit4all.ui.api.form.model.PredictiveFormInstance;

/**
 * API for the predictive forms, mainly for loading and saving the forms.
 * 
 * @author Zsombor Nyilas
 *
 */
public interface PredictiveFormApi {
  
  /**
   * Loads a {@link PredictiveFormInstance} based on the given URI.
   * 
   * @param uri the URI of the desired {@link PredictiveFormInstance}
   * @return the loaded {@link PredictiveFormInstance} object
   */
  public PredictiveFormInstance loadInstance(URI uri);
  
  /**
   * Saves the {@link PredictiveFormInstance}, saving the state of the navigation graph, and the form data
   * 
   * @param instance the {@link PredictiveFormInstance} to be saved
   */
  public void saveForm(PredictiveFormInstance instance);

}
