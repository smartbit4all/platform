package org.smartbit4all.api.invocation.registration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiRegisterImpl implements ApiRegister {

  private static final Logger log = LoggerFactory.getLogger(ApiRegisterImpl.class);
  
  private Map<String, List<ApiRegistrationListener>> registrationListenersByIfName =
      new HashMap<>();
  
  private Map<String, ProtocolSpecificApiInstantiator> apiInstantiatorsByProtocol = new HashMap<>();
  
  public ApiRegisterImpl(List<ProtocolSpecificApiInstantiator> protocolSpecifigApiInstantiators) {
    Objects.requireNonNull(protocolSpecifigApiInstantiators,
        "protocolSpecifigApiInstantiators can not be null!");
    protocolSpecifigApiInstantiators
        .forEach(i -> apiInstantiatorsByProtocol.put(i.getProtocol(), i));
  }

  @Override
  public void register(ApiInfo apiInfo) {
    String protocol = apiInfo.getProtocol();
    String interfaceQName = apiInfo.getInterfaceQualifiedName();
    
    
    List<ApiRegistrationListener> listeners = registrationListenersByIfName.get(interfaceQName);
    if(listeners == null || listeners.isEmpty()) {
      log.warn("There will be no impact of the registration of [{}] interface, beacuse there is no "
          + "listeners on it!", interfaceQName);
      return;
    }
    
    ProtocolSpecificApiInstantiator protocolSpecifigApiInstantiator =
        apiInstantiatorsByProtocol.get(protocol);
    if(protocolSpecifigApiInstantiator != null) {
      try {
        Object apiInstance = protocolSpecifigApiInstantiator.instantiate(apiInfo);
        if(apiInstance == null) {
          log.error("Could not instantiate the api on registration! Interface: {}, protocol: {}",
              interfaceQName, protocol);
          return;
        }
        listeners.forEach(listener -> {
          try {
            listener.onRegistration(apiInstance, apiInfo);
          } catch (Exception e) {
            log.error(
              "An error occured during the api registration of interface: {}; with protocol: {}",
              interfaceQName, protocol, e);
          }
        });
      } catch (Exception e1) {
        log.error(
            "An error occured during the api instantiation of interface: {}; with protocol: {}",
            interfaceQName, protocol, e1);
      }
    } else {
      log.error("There is no apiInstantiator registered for protocol {}", protocol);
    }
      
    
  }

  @Override
  public void addRegistrationListener(ApiRegistrationListener registrationListener) {
    String interfaceQName = registrationListener.getInterfaceQName();
    List<ApiRegistrationListener> listenersForInterface =
        registrationListenersByIfName.get(interfaceQName);
    if(listenersForInterface == null) {
      listenersForInterface = new ArrayList<>();
      registrationListenersByIfName.put(interfaceQName, listenersForInterface);
    }
    listenersForInterface.add(registrationListener);
  }

}
