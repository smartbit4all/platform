package org.smartbit4all.api.setting;

import java.util.function.Consumer;

/**
 * The {@link LocaleUsage} must be created for every object that is using {@link LocaleString}s.
 * This object ensure the reference for the {@link Consumer}s. When this object is finalized the
 * {@link Consumer} objects loose their references and will be removed form the registry of the
 * {@link LocaleString}s.
 * 
 * @author Peter Boros
 */
public interface LocaleUsage {

  /**
   * This function binds the locale specific String defined in a module specific
   * {@link LocaleOption}. This register the given Consumer for further changing of the locale
   * specific labels.
   * 
   * @param stringContant
   * @param setter
   */
  void applyLabel(LocaleString stringContant, Consumer<String> setter);

}
