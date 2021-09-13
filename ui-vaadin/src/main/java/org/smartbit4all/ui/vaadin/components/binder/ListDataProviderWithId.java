package org.smartbit4all.ui.vaadin.components.binder;

import java.util.Collection;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.function.ValueProvider;

public class ListDataProviderWithId<T> extends ListDataProvider<T> {

  private final ValueProvider<T, Object> idGetter;

  public ListDataProviderWithId(Collection<T> items, ValueProvider<T, Object> idGetter) {
    super(items);
    this.idGetter = idGetter;
  }

  @Override
  public Object getId(T item) {
    return idGetter.apply(item);
  }
}
