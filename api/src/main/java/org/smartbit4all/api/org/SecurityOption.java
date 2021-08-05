package org.smartbit4all.api.org;

/**
 * This is a marker interface for the Spring managed beans that contains {@link SecurityGroup}
 * fields. All these classes will be discovered and managed by the {@link OrgApi}. If we want to
 * manage security groups then we must have a class implementing this interface. It must have fields
 * for the available groups.
 * 
 * Every module can have {@link SecurityOption} classes. Typically one class per module. The api
 * provided by the {@link SecurityGroup} can be used directly by the developer without knowing the
 * actual security options.
 * 
 * @author Peter Boros
 */
public interface SecurityOption {

}
