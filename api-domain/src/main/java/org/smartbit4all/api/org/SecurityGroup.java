package org.smartbit4all.api.org;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The security group is an object representing a security group the user can be assigned to. The
 * security groups are defined in the code because the programmer can use it explicitly. The
 * instance of the {@link SecurityGroup} is identified by the fully qualified name of the variable
 * by default. We can set the name directly but it's not necessary. This object provides an api for
 * the developer to be able to check if the current user is assigned to the given group or not.
 *
 * @author Peter Boros
 */
public final class SecurityGroup {

  private static final Logger log = LoggerFactory.getLogger(SecurityGroup.class);

  /**
   * The title of the given security group.
   */
  private String title;

  /**
   * The name of the given security group, that is also used as a unique identifier.
   */
  private String name;

  /**
   * The documentation of the given security group.
   */
  private String description;

  /**
   * If set to true group should be unmodifiable. The code levele {@link SecurityGroup} is always
   * builin!
   */
  @Deprecated
  private boolean builtIn;

  /**
   * If set to true predicate also checks for connected accounts to the primary account.
   */
  private boolean checkForPrimaryAccount;

  private BiFunction<SecurityGroup, URI, Boolean> securityPredicate;

  private Function<URI, List<URI>> usersOfPrimaryAccountSupplier;

  /**
   * The sub groups of the security group.
   */
  private List<SecurityGroup> subGroups = new ArrayList<>();

  public static SecurityGroup of(String name) {
    Objects.requireNonNull(name, "name must be specified");
    return new SecurityGroup().name(name);
  }

  public SecurityGroup title(String title) {
    this.title = title;
    return this;
  }

  public SecurityGroup description(String description) {
    this.description = description;
    return this;
  }

  public SecurityGroup subgroup(SecurityGroup subgroup) {
    subGroups.add(subgroup);
    return this;
  }

  @Deprecated
  public SecurityGroup builtIn(boolean builtIn) {
    this.builtIn = builtIn;
    return this;
  }

  public final String getName() {
    return name;
  }

  final void setName(String name) {
    this.name = name;
  }

  final SecurityGroup name(String name) {
    this.name = name;
    return this;
  }

  public String getDescription() {
    return description;
  }

  /**
   * Checks if the given group is assigned to the current user.
   *
   * @return true if there is no api for accessing the user rights or if the group is not assigned
   *         to the user. Else we get false.
   */
  public boolean check() {
    return check(null);
  }

  /**
   * Checks if the given group is assigned to the given user.
   *
   * @return true if there is no api for accessing the user rights or if the group is not assigned
   *         to the user. Else we get false.
   */
  public boolean check(URI userUri) {
    if (securityPredicate == null) {
      log.warn("No securityPredicate when checking {}, default allow.", this.name);
      return true;
    }
    boolean result = securityPredicate.apply(this, userUri);

    if (!result && checkForPrimaryAccount) {
      List<URI> userUrisToCheck = usersOfPrimaryAccountSupplier.apply(userUri);
      for (URI userUriToCheck : userUrisToCheck) {
        if (securityPredicate.apply(this, userUriToCheck)) {
          result = true;
          break;
        }
      }
    }

    return result;
  }

  public final List<SecurityGroup> getSubGroups() {
    return subGroups;
  }

  public String getTitle() {
    return title;
  }

  void setTitle(String name) {
    this.title = name;
  }

  @Deprecated
  public boolean isbuiltIn() {
    return builtIn;
  }

  @Deprecated
  void setBuiltIn(boolean builtIn) {
    this.builtIn = builtIn;
  }

  public void setSecurityPredicate(BiFunction<SecurityGroup, URI, Boolean> securityPredicate) {
    this.securityPredicate = securityPredicate;
  }

  public void setCheckForPrimaryAccount(boolean checkForPrimaryAccount) {
    this.checkForPrimaryAccount = checkForPrimaryAccount;
  }

  public void setUsersOfPrimaryAccountSupplier(
      Function<URI, List<URI>> usersOfPrimaryAccountSupplier) {
    this.usersOfPrimaryAccountSupplier = usersOfPrimaryAccountSupplier;
  }

}
