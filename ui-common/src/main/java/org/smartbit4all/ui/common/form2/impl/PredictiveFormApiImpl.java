package org.smartbit4all.ui.common.form2.impl;

import java.net.URI;
import java.util.Optional;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.ui.api.form.PredictiveFormApi;
import org.smartbit4all.ui.api.form.model.EntityFormInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class PredictiveFormApiImpl implements PredictiveFormApi {

  @Autowired
  private Storage<EntityFormInstance> storage;
  
  @Override
  public EntityFormInstance loadInstance(URI uri) {
    EntityFormInstance instance = null;
    try {
      Optional<EntityFormInstance> load = storage.load(uri);
      instance = load.orElseGet(null);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return instance;
  }

  @Override
  public void saveForm(EntityFormInstance instance) {
    // TODO Auto-generated method stub
  }
}
