package org.smartbit4all.bff.api.config;

import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.collection.SearchIndexImpl;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.ProviderApiInvocationHandler;
import org.smartbit4all.api.org.OrgApiStorageImpl;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.bff.api.assoc.AssociationGridApi;
import org.smartbit4all.bff.api.assoc.AssociationGridApiImpl;
import org.smartbit4all.bff.api.generic.GenericPageApi;
import org.smartbit4all.bff.api.generic.GenericPageApiImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlatformBffApiConfig {

  public static final String SUBJECT_MODEL_NAME = "SUBJECT_MODEL_NAME";

  @Bean
  public SearchIndex<User> userSearch() {
    return new SearchIndexImpl<>(OrgApiStorageImpl.ORG_SCHEME, User.class.getSimpleName(),
        OrgApiStorageImpl.ORG_SCHEME, User.class)
            .map(User.URI, User.URI)
            .map(User.NAME, User.NAME)
            .map(User.USERNAME, User.USERNAME)
            .map(User.EMAIL, User.EMAIL)
            .map(User.PASSWORD, User.PASSWORD)
            .map(User.INACTIVE, User.INACTIVE)
            .map(User.ATTRIBUTES, User.ATTRIBUTES);
  }

  @Bean
  public SearchIndex<Group> groupSearch() {
    return new SearchIndexImpl<>(OrgApiStorageImpl.ORG_SCHEME, Group.class.getSimpleName(),
        OrgApiStorageImpl.ORG_SCHEME, Group.class)
            .map(Group.TITLE, Group.TITLE)
            .map(Group.NAME, Group.NAME)
            .map(Group.DESCRIPTION, Group.DESCRIPTION)
            .map(Group.URI, Group.URI)
            .map(Group.KIND_CODE, Group.KIND_CODE)
            .map(Group.BUILT_IN, Group.BUILT_IN)
            .map(Group.CHILDREN, Group.CHILDREN);
  }

  @Bean
  AssociationGridApi associationGridApi() {
    return new AssociationGridApiImpl();
  }

  @Bean
  ProviderApiInvocationHandler<AssociationGridApi> associationGridApiProvider() {
    return Invocations.asProvider(AssociationGridApi.class, associationGridApi());
  }

  @Bean
  GenericPageApi genericPageApiImpl() {
    return new GenericPageApiImpl();
  }

  @Bean
  public ProviderApiInvocationHandler<GenericPageApi> genericPageApiProvider(
      GenericPageApi genericPageApi) {
    return Invocations.asProvider(GenericPageApi.class, genericPageApi);
  }
}
