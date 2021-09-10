package org.smartbit4all.api.registration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.invocation.TestContributionApi;
import org.smartbit4all.api.invocation.TestContributionApiImpl;
import org.smartbit4all.api.invocation.TestPrimaryApi;
import org.smartbit4all.api.invocation.registration.ApiInfo;
import org.smartbit4all.api.invocation.registration.ApiRegister;
import org.smartbit4all.api.invocation.registration.LocalApiInstantiator;
import org.smartbit4all.api.registration.ApiRegistrationTestConfig.TestInterfaceToRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {
    ApiRegistrationTestConfig.class,
})
public class LocalApiRegistrationTests {

  @Autowired
  private ApiRegister apiRegister;
  
  @Autowired
  TestPrimaryApi primaryApi;
  
  @Autowired
  TestInterfaceToRegister testInterfaceToRegister;
  
  @Test
  public void registerLocalContributorApi() {
    
    ApiInfo apiInfo = new ApiInfo();
    String apiIdentifier = "testContributionApi-runtimeRegistered";
    apiInfo.setApiIdentifier(apiIdentifier);
    apiInfo.setInterfaceQualifiedName(TestContributionApi.class.getName());
    apiInfo.setProtocol("local");
    apiInfo.addParameter(LocalApiInstantiator.LOCAL_API_IMPL, new TestContributionApiImpl(apiIdentifier));
    
    apiRegister.register(apiInfo);
    
    TestContributionApi registeredContributionApi = primaryApi.findApiByName(apiIdentifier);
    
    assertNotNull(registeredContributionApi);
    assertEquals(apiIdentifier, registeredContributionApi.getApiName());
  }
  
  @Test
  public void apiPlaceholderTest() {
    boolean failed = false;
    String testValue = "Kutya";
    try {
      testInterfaceToRegister.doSomething(testValue);
    } catch (Exception e) {
      
      failed = true;
    }
    assertTrue(failed);
    
    ApiInfo apiInfo = new ApiInfo();
    String apiIdentifier = "testInterfaceImpl";
    apiInfo.setApiIdentifier(apiIdentifier);
    apiInfo.setInterfaceQualifiedName(TestInterfaceToRegister.class.getName());
    apiInfo.setProtocol("local");
    apiInfo.addParameter(LocalApiInstantiator.LOCAL_API_IMPL, new TestInterfaceToRegister() {
      
      @Override
      public String doSomething(String p1) {
        return p1;
      }
    });
    
    apiRegister.register(apiInfo);
    
    String returnValue = testInterfaceToRegister.doSomething(testValue);
    
    assertEquals(testValue, returnValue);
    
  }
  
}
