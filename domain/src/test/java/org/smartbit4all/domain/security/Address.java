package org.smartbit4all.domain.security;

public interface Address {

  String id();

  Address id(String id);

  Long zipCode();

  Address zipCode(Long zipCode);

}
