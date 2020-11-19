package org.smartbit4all.ui.vaadin.service;

import java.util.HashMap;
import java.util.Map;
import com.vaadin.flow.function.SerializableFunction;

public class GridRowDecoratorProvider {

  private Map<String, SerializableFunction<?, String>> decoratorProviders = new HashMap<>();
  
  public void addDecoratorProvider(String providerKey, SerializableFunction<?, String> decoratorProvider) {
    decoratorProviders.put(providerKey, decoratorProvider);
  }
  
  @SuppressWarnings("unchecked")
  public <T> SerializableFunction<T, String> getDecoratorProvider(String providerKey){
    // TODO make it typesafe
    return (SerializableFunction<T, String>) decoratorProviders.get(providerKey);
  }
  
}
