package org.smartbit4all.api.org;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

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

  private BiFunction<SecurityGroup, URI, Boolean> securityPredicate;

  /**
   * The sub groups of the security group.
   */
  private List<SecurityGroup> subGroups = new ArrayList<>();

  public static SecurityGroup of() {
    return new SecurityGroup();
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

  public final String getName() {
    return name;
  }

  final void setName(String name) {
    this.name = name;
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
      return true;
    }
    return securityPredicate.apply(this, userUri);
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

  public void setSecurityPredicate(BiFunction<SecurityGroup, URI, Boolean> securityPredicate) {
    this.securityPredicate = securityPredicate;
  }

}
