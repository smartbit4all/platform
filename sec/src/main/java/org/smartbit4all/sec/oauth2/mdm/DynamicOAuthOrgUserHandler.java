package org.smartbit4all.sec.oauth2.mdm;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.OrgUtils;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.security.bean.OAuthClientProperties;
import org.smartbit4all.sec.oauth2.OAuth2SessionAuthSuccessHandler.OrgUserHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.ObjectUtils;

public class DynamicOAuthOrgUserHandler implements OrgUserHandler {

  private static final Logger log = LoggerFactory.getLogger(DynamicOAuthOrgUserHandler.class);

  @Autowired
  private OrgApi orgApi;
  @Autowired
  private DynamicOAuth2PropertiesApi dynamicOAuth2PropertiesApi;


  private static final Map<String, BiConsumer<User, String>> userParamSetters =
      Collections.unmodifiableMap(createUserParamSetters());

  private static Map<String, BiConsumer<User, String>> createUserParamSetters() {
    Map<String, BiConsumer<User, String>> userParamSetters = new HashMap<>();
    userParamSetters.put("username", User::setUsername);
    userParamSetters.put("name", User::setName);
    userParamSetters.put("email", User::setEmail);
    return userParamSetters;
  }

  /**
   * When a user logs in with oauth and they don't have a User bean stored in the application, we
   * create it, set the properties and add to the groups that are properly mapped
   */
  @Override
  public URI onMissingUser(OAuth2AuthenticationToken oauthToken) throws Exception {
    Objects.requireNonNull(oauthToken, "oauthToken can not be null!");

    User user = new User()
        .username(oauthToken.getName())
        .putAttributesItem(OrgApi.SSO_USER, Boolean.TRUE.toString());

    OAuthClientProperties clientProperties = getClientProperties(oauthToken);
    setPropertiesOnUser(oauthToken, user, clientProperties);

    URI userUri = orgApi.saveUser(user);

    addUserToGroups(oauthToken, clientProperties, user);

    return userUri;
  }

  private OAuthClientProperties getClientProperties(OAuth2AuthenticationToken oauthToken) {
    OAuthClientProperties clientProperties = dynamicOAuth2PropertiesApi
        .getClientPropsForRegId(oauthToken.getAuthorizedClientRegistrationId());
    if (clientProperties == null) {
      throw new IllegalStateException(
          "There is no client registration for the given client registration id: "
              + oauthToken.getAuthorizedClientRegistrationId());
    }
    return clientProperties;
  }


  private void setPropertiesOnUser(OAuth2AuthenticationToken oauthToken,
      User user, OAuthClientProperties clientProperties) {

    Map<String, String> userParameterMapping = clientProperties.getUserParameterMapping();

    if (!ObjectUtils.isEmpty(userParameterMapping)) {
      OAuth2User principal = oauthToken.getPrincipal();
      userParameterMapping.entrySet().forEach(mapping -> {
        String oauthAttributeValue = getAttribute(principal, mapping.getValue());
        if (!ObjectUtils.isEmpty(oauthAttributeValue)) {

          String userAttributeName = mapping.getKey();
          if (userParamSetters.containsKey(userAttributeName)) {
            userParamSetters.get(userAttributeName).accept(user, oauthAttributeValue);
          } else {
            user.putAttributesItem(userAttributeName, oauthAttributeValue);
          }
        }
      });
    } else {
      log.warn("There is no user property mapping configured for oauth2 client registration {}",
          oauthToken.getAuthorizedClientRegistrationId());
    }
  }


  private void addUserToGroups(OAuth2AuthenticationToken oauthToken,
      OAuthClientProperties clientProperties, User user) {
    Set<Group> groupsByOAuthSettings = getGroupsByOAuthSettings(oauthToken, clientProperties);
    if (!ObjectUtils.isEmpty(groupsByOAuthSettings)) {
      groupsByOAuthSettings
          .forEach(group -> addUserToGroup(user, group));
    } else {
      log.warn("There is no role mapping configured for oauth2 client registration {}",
          oauthToken.getAuthorizedClientRegistrationId());
    }
  }

  @Override
  public void checkUser(User user, OAuth2AuthenticationToken oauthToken) throws Exception {
    Objects.requireNonNull(oauthToken, "oauthToken can not be null!");
    Objects.requireNonNull(user, "user can not be null!");

    log.debug("Checking user on OAuth2 login: [{}]", user.getUsername());

    OAuthClientProperties clientProperties = getClientProperties(oauthToken);

    // FIXME shall we update the user attributes on checking???

    Set<Group> groupsByOauthSetting = getGroupsByOAuthSettings(oauthToken, clientProperties);

    List<Group> actualGroupsOfUser = orgApi.getGroupsOfUser(user.getUri());

    Set<Group> groupsToRemoveFrom = new HashSet<>(actualGroupsOfUser);
    groupsToRemoveFrom.removeAll(groupsByOauthSetting);
    groupsByOauthSetting.removeAll(actualGroupsOfUser);

    // add the remained groups
    groupsByOauthSetting.forEach(group -> addUserToGroup(user, group));

    /*
     * FIXME now it removes all other groups that is not defined in the oauth realm. It means that
     * no other groups can be added for the user nor on the ui nor by code. Later on this
     * functionality may be needed to changed by adding multipla strategy choices to handle the
     * different use cases of the synchronization of groups
     */
    // remove from groups
    // groupsToRemoveFrom.forEach(group -> removeUserFromGroup(user, group));


    // FIXME should we consider checks for sub groups here?

    if (Boolean.FALSE.equals(clientProperties.getIsUserWithoutGroupAllowedToLogIn())
        && orgApi.getGroupsOfUser(user.getUri()).isEmpty()) {
      throw new Exception("The user is not added to any organisation group.");
    }

  }

  private Set<Group> getGroupsByOAuthSettings(OAuth2AuthenticationToken oauthToken,
      OAuthClientProperties clientProperties) {
    List<String> roleAttributes = clientProperties.getRoleAttributes();
    Map<String, String> roleMapping = clientProperties.getRoleMapping();

    Set<Group> groupsByOauthSetting = new HashSet<>();

    String defaultGroupName = clientProperties.getDefaultGroupName();
    if (!ObjectUtils.isEmpty(defaultGroupName)) {
      Group defaultGroup = orgApi.getGroupByName(defaultGroupName);
      groupsByOauthSetting.add(defaultGroup);
    }

    if (!ObjectUtils.isEmpty(roleAttributes)) {
      // TODO role keys now work with only one tag. Deeper than one level role descriptions will not
      // work not. (e.g.: realm_access.roles, or resource_access.<client_id>.roles)
      Set<Group> groupsByRealm =
          roleAttributes.stream()
              .map(roleAttribute -> oauthToken.getPrincipal().getAttributes().get(roleAttribute))
              .filter(Collection.class::isInstance)
              .flatMap(roleNames -> ((Collection<?>) roleNames).stream())
              .filter(Objects::nonNull)
              .map(roleName -> grantedAuthority2Group(roleName.toString(), roleMapping))
              .filter(Optional::isPresent)
              .map(Optional::get)
              .collect(Collectors.toSet());
      groupsByOauthSetting.addAll(groupsByRealm);
    }
    return groupsByOauthSetting;
  }


  private String getAttribute(OAuth2User principal, String key) {
    Object value = principal.getAttributes().get(key);
    return value == null ? null : value.toString();
  }

  private void addUserToGroup(User user, Group group) {
    OrgUtils.applyGroupByName(orgApi, group, user);
    log.info("User [{}] added to group [{}]", user.getUsername(), group.getName());
  }

  private void removeUserFromGroup(User user, Group group) {
    orgApi.removeUserFromGroup(user.getUri(), group.getUri());
    log.info("User [{}] removed from group [{}]", user.getUsername(), group.getName());
  }

  private Optional<Group> grantedAuthority2Group(String realmRoleName,
      Map<String, String> roles) {

    String orgGroupName = roles.get(realmRoleName);
    if (!ObjectUtils.isEmpty(orgGroupName)) {
      Group orgGroup = orgApi.getGroupByName(orgGroupName);
      if (orgGroup != null) {
        return Optional.of(orgGroup);
      } else {
        log.debug("There is no org group exists with configured group name [{}]", orgGroupName);
      }
    } else {
      log.debug(
          "There is no role configured in the application for received role [{}].", realmRoleName);
    }
    return Optional.empty();
  }

}
