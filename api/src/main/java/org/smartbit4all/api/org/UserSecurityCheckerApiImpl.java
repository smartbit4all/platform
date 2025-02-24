package org.smartbit4all.api.org;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.org.bean.UserLastAccess;
import org.smartbit4all.api.org.bean.UserSecurityPolicy;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectNodeReference;
import org.smartbit4all.domain.application.TimeManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

public class UserSecurityCheckerApiImpl implements UserSecurityCheckerApi {
  private static final Logger log = LoggerFactory.getLogger(UserSecurityCheckerApiImpl.class);

  @Autowired
  private ObjectApi objectApi;

  @Autowired(required = false)
  private OrgApi orgApi;

  @Autowired
  private MasterDataManagementApi mdmApi;

  @Autowired
  private TimeManagementService timeManagementService;

  @Autowired
  private InvocationApi invocationApi;

  @Override
  public URI updateOrCreateUserLastAccess(URI userUri, String path) {
    ObjectNode userNode = objectApi.loadLatest(userUri);
    updateOrCreateUserLastAccess(userNode, path);
    return objectApi.save(userNode);
  }

  @Override
  public void updateOrCreateUserLastAccess(ObjectNode userNode, String path) {
    ObjectNodeReference accessRef = userNode.ref(LAST_ACCESS_IN_ORG);
    ObjectNode lastAccessNode;
    if (accessRef.isPresent()) {
      lastAccessNode = accessRef.get();
    } else {
      lastAccessNode =
          objectApi.create(SCHEMA, new UserLastAccess());
    }
    lastAccessNode.setValue(OffsetDateTime.now(timeManagementService.getSynchronizedClock()),
        path);
    accessRef.set(lastAccessNode);
  }

  @Override
  public boolean shouldRemindToChangePassword(URI userUri) {
    MDMEntryApi mdmEntryApi =
        mdmApi.getApi(MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION,
            MDM_NAME);

    if (!mdmEntryApi.getList().exists() || ObjectUtils.isEmpty(mdmEntryApi.getList().uris())) {
      return false;
    }

    ObjectNode userNode = objectApi.loadLatest(userUri);
    List<ObjectNode> possiblePoliciesList = getPoliciesByUser(userUri, mdmEntryApi);

    ObjectNode highSecurityPolicy =
        getHighSecurityPolicy(possiblePoliciesList, UserSecurityPolicy.PASSWORD_EXPIRATION_DAYS);

    if (ObjectUtils.isEmpty(highSecurityPolicy)) {
      return false;
    }
    ObjectNodeReference accessRef = userNode.ref(LAST_ACCESS_IN_ORG);
    if (accessRef.isPresent()) {
      return needPasswordChange(highSecurityPolicy, accessRef);
    }
    return true;
  }

  @Override
  public URI resetLoginAttemptAndBlockDate(URI userUri) {
    ObjectNode userNode = objectApi.loadLatest(userUri);
    ObjectNodeReference accessRef = userNode.ref(LAST_ACCESS_IN_ORG);
    ObjectNode lastAccessNode;
    if (accessRef.isPresent()) {
      lastAccessNode = accessRef.get();
    } else {
      lastAccessNode =
          objectApi.create(SCHEMA, new UserLastAccess());
    }
    lastAccessNode.setValue(OffsetDateTime.now(timeManagementService.getSynchronizedClock()),
        UserLastAccess.LAST_LOGIN);
    setBlockingValues(lastAccessNode);
    accessRef.set(lastAccessNode);
    return objectApi.save(userNode);
  }

  private void setBlockingValues(ObjectNode lastAccessNode) {
    lastAccessNode.setValue(null,
        UserLastAccess.BLOCKING_DATE);
    lastAccessNode.setValue(0l,
        UserLastAccess.LOGIN_ATTEMPT_COUNTER);
  }

  @Override
  public boolean isUserBlocked(URI userUri) {
    MDMEntryApi mdmEntryApi =
        mdmApi.getApi(MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION,
            MDM_NAME);

    if (!mdmEntryApi.getList().exists() || ObjectUtils.isEmpty(mdmEntryApi.getList().uris())) {
      return false;
    }

    ObjectNode userNode = objectApi.loadLatest(userUri);
    List<ObjectNode> possiblePoliciesList = getPoliciesByUser(userUri, mdmEntryApi);

    ObjectNode highSecurityPolicy =
        getHighSecurityPolicy(possiblePoliciesList, UserSecurityPolicy.LOGIN_ATTEMPT_LIMIT);

    if (ObjectUtils.isEmpty(highSecurityPolicy)) {
      return false;
    }
    ObjectNodeReference accessRef = userNode.ref(LAST_ACCESS_IN_ORG);
    if (accessRef.isPresent()) {
      ObjectNode lastAccessNode = accessRef.get();
      OffsetDateTime blockingDate =
          lastAccessNode.getValue(OffsetDateTime.class, UserLastAccess.BLOCKING_DATE);
      if (blockingDate == null) {
        return false;
      }
      Long lockoutPeriod =
          highSecurityPolicy.getValue(Long.class, UserSecurityPolicy.LOGIN_LOCKOUT_PERIOD)
              .longValue();

      OffsetDateTime currentDate = OffsetDateTime.now(timeManagementService.getSynchronizedClock());
      long minutesSinceUserBlocked = ChronoUnit.MINUTES.between(blockingDate, currentDate);
      if (minutesSinceUserBlocked <= lockoutPeriod) {
        return true;
      }

      setBlockingValues(lastAccessNode);
      accessRef.set(lastAccessNode);
      objectApi.save(userNode);
    }
    return false;
  }



  @Override
  public URI increaseLoginAttemptAndCheck(String userName) {
    User user = orgApi.getUserByUsername(userName);
    if (user != null && Boolean.FALSE.equals(user.getInactive())) {
      MDMEntryApi mdmEntryApi =
          mdmApi.getApi(MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION,
              MDM_NAME);

      if (!mdmEntryApi.getList().exists() || ObjectUtils.isEmpty(mdmEntryApi.getList().uris())) {
        return null;
      }

      URI userUri = user.getUri();
      ObjectNode userNode = objectApi.loadLatest(userUri);
      List<ObjectNode> possiblePoliciesList = getPoliciesByUser(userUri, mdmEntryApi);

      ObjectNode highSecurityPolicy =
          getHighSecurityPolicy(possiblePoliciesList, UserSecurityPolicy.LOGIN_ATTEMPT_LIMIT);

      if (ObjectUtils.isEmpty(highSecurityPolicy)) {
        return null;
      }
      ObjectNodeReference accessRef = userNode.ref(LAST_ACCESS_IN_ORG);
      if (accessRef.isPresent()) {
        ObjectNode lastAccessNode = accessRef.get();

        Long loginAttemptCounter =
            calculateLoginAttemptNumber(lastAccessNode, highSecurityPolicy);
        Long loginAttemptLimit =
            highSecurityPolicy.getValue(Long.class, UserSecurityPolicy.LOGIN_ATTEMPT_LIMIT);
        if (ObjectUtils.isEmpty(loginAttemptLimit)) {
          return null;
        }
        int compareResult = Long.compare(loginAttemptCounter, loginAttemptLimit);
        if (compareResult == 0 || compareResult > 0) {
          lastAccessNode.setValue(OffsetDateTime.now(timeManagementService.getSynchronizedClock()),
              UserLastAccess.BLOCKING_DATE);
        }

        lastAccessNode.setValue(loginAttemptCounter, UserLastAccess.LOGIN_ATTEMPT_COUNTER);
        lastAccessNode.setValue(OffsetDateTime.now(timeManagementService.getSynchronizedClock()),
            UserLastAccess.LAST_LOGIN_ATTEMPT);
        accessRef.set(lastAccessNode);
        return objectApi.save(userNode);
      }
    }
    return null;
  }

  private Long calculateLoginAttemptNumber(ObjectNode lastAccessNode,
      ObjectNode highSecurityPolicy) {
    Long loginAttemptCounter =
        lastAccessNode.getValue(Long.class, UserLastAccess.LOGIN_ATTEMPT_COUNTER);
    OffsetDateTime lastLoginAttempt =
        lastAccessNode.getValue(OffsetDateTime.class, UserLastAccess.LAST_LOGIN_ATTEMPT);

    if (!ObjectUtils.isEmpty(lastLoginAttempt)) {
      Long lockoutPeriod =
          highSecurityPolicy.getValue(Long.class, UserSecurityPolicy.LOGIN_LOCKOUT_PERIOD);
      if (lockoutPeriod != null) {
        long minutesSinceUserBlocked = ChronoUnit.MINUTES.between(lastLoginAttempt,
            OffsetDateTime.now(timeManagementService.getSynchronizedClock()));
        if (minutesSinceUserBlocked >= lockoutPeriod) {
          return 1L;
        }
      }

    }

    if (loginAttemptCounter == null) {
      loginAttemptCounter = 1L;
    } else {
      loginAttemptCounter++;
    }
    return loginAttemptCounter;
  }


  @Override
  public void checkUsersBySecurityPolicy() {
    MDMEntryApi mdmEntryApi =
        mdmApi.getApi(MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION,
            MDM_NAME);

    if (!mdmEntryApi.getList().exists() || ObjectUtils.isEmpty(mdmEntryApi.getList().uris())) {
      return;
    }
    List<User> activeUsers = orgApi.getActiveUsers();

    activeUsers.stream().forEach(user -> {


      URI userUri = user.getUri();
      ObjectNodeReference accessRef = objectApi.loadLatest(userUri).ref(LAST_ACCESS_IN_ORG);
      if (accessRef.isPresent()) {
        List<ObjectNode> policiesByUser = getPoliciesByUser(userUri, mdmEntryApi);
        ObjectNode highSecurityPolicyToInactivitiCheck =
            getHighSecurityPolicy(policiesByUser, UserSecurityPolicy.INACTIVITY_LOCKOUT_DAYS);
        if (!ObjectUtils.isEmpty(highSecurityPolicyToInactivitiCheck)
            && isUserInactive(highSecurityPolicyToInactivitiCheck, accessRef)) {
          orgApi.removeUser(userUri);
          invocationApi
              .publisher(UserSecurityCheckerApi.class,
                  OrgSubscriberApi.class, USER_INACTIVE)
              .publish(api -> api.userInactivedEvent(userUri,
                  highSecurityPolicyToInactivitiCheck.getObjectUri()));
        } else {

          checkPassword(userUri, accessRef, policiesByUser);

        }
      }
    });
  }

  private void checkPassword(URI userUri, ObjectNodeReference accessRef,
      List<ObjectNode> policiesByUser) {
    ObjectNode highSecurityPolicyToPasswordCheck =
        getHighSecurityPolicy(policiesByUser, UserSecurityPolicy.PASSWORD_EXPIRATION_DAYS);

    if (ObjectUtils.isEmpty(highSecurityPolicyToPasswordCheck)) {
      return;
    }

    if (isPasswordExpired(highSecurityPolicyToPasswordCheck, accessRef)) {
      orgApi.removeUser(userUri);
      invocationApi
          .publisher(UserSecurityCheckerApi.class,
              OrgSubscriberApi.class, PASSWORD_EXPIRED)
          .publish(api -> api.passwordExpiredEvent(userUri,
              highSecurityPolicyToPasswordCheck.getObjectUri()));
    } else if (needPasswordChange(highSecurityPolicyToPasswordCheck, accessRef)) {
      invocationApi
          .publisher(UserSecurityCheckerApi.class,
              OrgSubscriberApi.class, PASSWORD_EXPIRE_SOON)
          .publish(api -> api.passwordExpiredSoonEvent(userUri,
              highSecurityPolicyToPasswordCheck.getObjectUri()));
    }
  }

  private List<ObjectNode> getPoliciesByUser(URI userUri, MDMEntryApi mdmEntryApi) {
    List<URI> userGroups = orgApi.getGroupsOfUser(userUri).stream()
        .map(group -> objectApi.getLatestUri(group.getUri()))
        .collect(Collectors.toList());

    List<ObjectNode> policiesNodeList = mdmEntryApi.getList().uris().stream()
        .map(policyUri -> objectApi.loadLatest(policyUri)).collect(Collectors.toList());

    return policiesNodeList.stream()
        .filter(policyNode -> ObjectUtils
            .isEmpty(policyNode.getValueAsList(URI.class, UserSecurityPolicy.USER_GROUPS))
            || policyNode.getValueAsList(URI.class, UserSecurityPolicy.USER_GROUPS)
                .stream()
                .anyMatch(groupUri -> userGroups.contains(objectApi.getLatestUri(groupUri))))
        .collect(Collectors.toList());
  }

  private ObjectNode getHighSecurityPolicy(List<ObjectNode> possiblePoliciesList,
      String path) {
    if (ObjectUtils.isEmpty(possiblePoliciesList)) {
      return null;
    }

    if (possiblePoliciesList.size() == 1) {
      return possiblePoliciesList.get(0);
    }

    ObjectNode highSecurityPolicy = null;
    for (ObjectNode policy : possiblePoliciesList) {
      if (highSecurityPolicy == null) {
        highSecurityPolicy = policy;
      } else if (Long.compare(policy.getValue(Long.class, path),
          highSecurityPolicy.getValue(Long.class,
              path)) > 0) {
        highSecurityPolicy = policy;
      }
    }
    return highSecurityPolicy;
  }

  private boolean isUserInactive(ObjectNode userSecurityPolicy,
      ObjectNodeReference accessRef) {
    Long inactivityLockoutDays =
        userSecurityPolicy.getValue(Long.class, UserSecurityPolicy.INACTIVITY_LOCKOUT_DAYS)
            .longValue();

    OffsetDateTime lastLoginActivity =
        accessRef.get().getValue(OffsetDateTime.class, UserLastAccess.LAST_LOGIN);

    if (lastLoginActivity == null) {
      lastLoginActivity =
          accessRef.get().getValue(OffsetDateTime.class, UserLastAccess.REGISTRATION_DATE);
    }

    OffsetDateTime currentDate = OffsetDateTime.now(timeManagementService.getSynchronizedClock());
    if (lastLoginActivity == null) {
      return false;
    }
    long daysSinceLastLogin = ChronoUnit.DAYS.between(lastLoginActivity, currentDate);
    return daysSinceLastLogin > inactivityLockoutDays;
  }

  private boolean needPasswordChange(ObjectNode policyByUser,
      ObjectNodeReference accessRef) {
    OffsetDateTime lastPasswordChange =
        accessRef.get().getValue(OffsetDateTime.class, UserLastAccess.LAST_PASSWORD_CHANGE);
    if (lastPasswordChange == null) {
      return true;
    }
    Long passwordExpirationDays =
        policyByUser.getValue(Long.class, UserSecurityPolicy.PASSWORD_EXPIRATION_DAYS)
            .longValue();
    Long passwordReminderDays =
        policyByUser.getValue(Long.class, UserSecurityPolicy.PASSWORD_REMINDER_DAYS).longValue();

    OffsetDateTime currentDate = OffsetDateTime.now(timeManagementService.getSynchronizedClock());
    long daysSinceLastChange = ChronoUnit.DAYS.between(lastPasswordChange, currentDate);
    return daysSinceLastChange >= (passwordExpirationDays - passwordReminderDays);
  }

  private boolean isPasswordExpired(ObjectNode policyByUser,
      ObjectNodeReference accessRef) {
    OffsetDateTime lastPasswordChange =
        accessRef.get().getValue(OffsetDateTime.class, UserLastAccess.LAST_PASSWORD_CHANGE);
    if (lastPasswordChange == null) {
      lastPasswordChange =
          accessRef.get().getValue(OffsetDateTime.class, UserLastAccess.REGISTRATION_DATE);
    }
    Long passwordExpirationDays =
        policyByUser
            .getValue(Long.class, UserSecurityPolicy.PASSWORD_EXPIRATION_DAYS)
            .longValue();

    OffsetDateTime currentDate = OffsetDateTime.now(timeManagementService.getSynchronizedClock());
    long daysSinceLastChange = ChronoUnit.DAYS.between(lastPasswordChange, currentDate);
    return daysSinceLastChange > passwordExpirationDays;
  }
}
