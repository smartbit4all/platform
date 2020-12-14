/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.secms365.service;

import java.util.Date;
import javax.naming.ServiceUnavailableException;
import org.smartbit4all.sec.service.SecurityService;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties.Registration;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.azure.spring.autoconfigure.aad.AADAuthenticationProperties;
import com.microsoft.azure.spring.autoconfigure.aad.AzureADGraphClient;
import com.microsoft.azure.spring.autoconfigure.aad.ServiceEndpointsProperties;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.requests.extensions.GraphServiceClient;

@Service
@SessionScope
public class MsGraphService {

  private static final String REG_CODE = "azure";
  
  private final AADAuthenticationProperties aadAuthProps;
  private final SecurityService securityService;
  
  private final AzureADGraphClient graphAuthClient;
  
  private IAuthenticationResult authResult;
  private IGraphServiceClient graphServiceClient;
  
  public MsGraphService(OAuth2ClientProperties clientProps, AADAuthenticationProperties aadAuthProps, ServiceEndpointsProperties serviceEndpointsProps, SecurityService securityService) {
    this.aadAuthProps = aadAuthProps;
    this.securityService = securityService;
    Registration registration = clientProps.getRegistration().get(REG_CODE);
    this.graphAuthClient = new AzureADGraphClient(registration.getClientId(), registration.getClientSecret(), aadAuthProps, serviceEndpointsProps);
  }
  
  private String getAccessToken() {
    checkExpiracy();
    if(authResult == null) {
      Authentication authentication = securityService.getCurrentAuthentication();
      DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();
      String idToken = oidcUser.getIdToken().getTokenValue();
      try {
        this.authResult = graphAuthClient.acquireTokenForGraphApi(idToken, aadAuthProps.getTenantId());
      } catch (ServiceUnavailableException e) {
        throw new RuntimeException("Failed to acquire token for Graph API.", e);
      }
    }
    return authResult.accessToken();
  }

  private void checkExpiracy() {
    // TODO the refresh token should be used to refresh the token!
    Date fiveMinutesFromNow = new Date(System.currentTimeMillis() + 1000l * 60l * 5l);
    if(authResult != null && authResult.expiresOnDate().before(fiveMinutesFromNow)) {
      authResult = null;
    }
  }
  
  public IGraphServiceClient getGraphClient() {
    if(graphServiceClient == null) {
      graphServiceClient = GraphServiceClient.builder()
          .authenticationProvider(
              request -> request.addHeader("Authorization", "Bearer " + getAccessToken()))
          .buildClient();
    }
    return graphServiceClient;
  }
  
}
