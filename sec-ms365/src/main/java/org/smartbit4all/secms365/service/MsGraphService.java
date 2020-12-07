package org.smartbit4all.secms365.service;

import javax.naming.ServiceUnavailableException;
import org.smartbit4all.sec.service.SecurityService;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties.Registration;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.azure.spring.autoconfigure.aad.AADAuthenticationProperties;
import com.microsoft.azure.spring.autoconfigure.aad.AzureADGraphClient;
import com.microsoft.azure.spring.autoconfigure.aad.ServiceEndpointsProperties;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.requests.extensions.GraphServiceClient;

@Service
public class MsGraphService {

  private static final String REG_CODE = "azure";
  
  private final AADAuthenticationProperties aadAuthProps;
  private final SecurityService securityService;
  
  private final AzureADGraphClient graphAuthClient;
  
  private String accessToken;
  private IGraphServiceClient graphServiceClient;
  
  public MsGraphService(OAuth2ClientProperties clientProps, AADAuthenticationProperties aadAuthProps, ServiceEndpointsProperties serviceEndpointsProps, SecurityService securityService) {
    this.aadAuthProps = aadAuthProps;
    this.securityService = securityService;
    Registration registration = clientProps.getRegistration().get(REG_CODE);
    this.graphAuthClient = new AzureADGraphClient(registration.getClientId(), registration.getClientSecret(), aadAuthProps, serviceEndpointsProps);
  }
  
  private String getAccessToken() {
    if(accessToken == null) {
      Authentication authentication = securityService.getCurrentAuthentication();
      DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();
      String idToken = oidcUser.getIdToken().getTokenValue();
      IAuthenticationResult authResult;
      try {
        authResult = graphAuthClient.acquireTokenForGraphApi(idToken, aadAuthProps.getTenantId());
        this.accessToken = authResult.accessToken();
      } catch (ServiceUnavailableException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return accessToken;
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
