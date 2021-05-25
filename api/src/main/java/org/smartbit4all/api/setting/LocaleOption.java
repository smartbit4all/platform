package org.smartbit4all.api.setting;

/**
 * This is a marker interface for the Spring managed beans that contains {@link LocaleString}
 * fields. All these classes will be discovered and managed by the {@link LocaleSettingApi}. If we
 * want to manage Locale specific Strings then we must have a class implementing this interface. It
 * must have fields for {@link LocaleString}s.
 * 
 * @author Peter Boros
 */
public interface LocaleOption {

}
