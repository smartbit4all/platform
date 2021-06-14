package org.smartbit4all.api.setting;

/**
 * This is a marker interface for the Spring managed beans that contains {@link LocaleString}
 * fields. All these classes will be discovered and managed by the {@link LocaleSettingApi}. If we
 * want to manage Locale specific Strings then we must have a class implementing this interface. It
 * must have fields for {@link LocaleString}s.
 * 
 * Every module can have {@link LocaleOption} classes. Typically one class per module. There can be
 * built-in options for the given module in the resource with the same name as the class itself. The
 * name starts with the locale supported. Like hu_org.smartbit4all.mymodule.config.MyModule.locale
 * 
 * @author Peter Boros
 */
public interface LocaleOption {

}
