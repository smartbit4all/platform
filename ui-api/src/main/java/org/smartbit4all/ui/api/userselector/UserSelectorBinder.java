package org.smartbit4all.ui.api.userselector;

import java.util.List;
import java.util.function.Consumer;
import org.smartbit4all.api.userselector.bean.UserSelector;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.ui.binder.CollectionBinder;

public class UserSelectorBinder extends CollectionBinder<UserSelector> {

  private Consumer<List<UserSelector>> itemsChangedConsumer;

  public UserSelectorBinder(ObservableObject observableObject,
      Consumer<List<UserSelector>> itemsChangedConsumer,
      String... collectionPath) {
    super(observableObject, collectionPath);
    this.itemsChangedConsumer = itemsChangedConsumer;
    registerModelObserver();
  }

  @Override
  protected void addItem(String itemPath, UserSelector item) {
    super.addItem(itemPath, item);
    itemsChangedConsumer.accept(items);
  }

  @Override
  protected UserSelector deleteItemByIndex(int idx) {
    UserSelector result = super.deleteItemByIndex(idx);
    itemsChangedConsumer.accept(items);
    return result;
  }

  @Override
  protected void modifyItem(int idx, UserSelector newItem) {
    super.modifyItem(idx, newItem);
    itemsChangedConsumer.accept(items);
  }

}
