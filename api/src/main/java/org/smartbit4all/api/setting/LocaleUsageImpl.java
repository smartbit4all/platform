package org.smartbit4all.api.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The default and single implementation of the {@link LocaleUsage} interface. This will be
 * configured as prototype bean to initiate for every usage place.
 * 
 * @author Peter Boros
 */
public final class LocaleUsageImpl implements LocaleUsage {

  private List<Consumer<String>> consumers = new ArrayList<>();

  @Override
  public void applyLabel(LocaleString stringContant, Consumer<String> setter) {
    stringContant.apply(setter);
    consumers.add(setter);
  }

}
