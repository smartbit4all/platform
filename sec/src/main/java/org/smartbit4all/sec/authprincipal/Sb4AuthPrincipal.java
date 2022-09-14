package org.smartbit4all.sec.authprincipal;

import java.io.Serializable;
import java.security.Principal;

/**
 * This is only a placeholder so when using Sb4 platform Spring security, the authentication
 * principals can wrapped into descendants of this interface so they can be identified later on.
 */
public interface Sb4AuthPrincipal extends Principal, Serializable {

}
