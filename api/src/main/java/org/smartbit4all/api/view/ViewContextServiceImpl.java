package org.smartbit4all.api.view;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.view.bean.ViewContext;
import org.smartbit4all.api.view.bean.ViewContextUpdate;
import org.smartbit4all.api.view.bean.ViewStateUpdate;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.springframework.beans.factory.annotation.Autowired;

public class ViewContextServiceImpl implements ViewContextService {

  private static final ThreadLocal<UUID> currentViewContextUuid = new ThreadLocal<>();

  private static final String SCHEMA = "viewcontext";

  @Autowired
  private StorageApi storageApi;

  @Autowired
  private SessionApi sessionApi;

  private Supplier<Storage> storage = new Supplier<Storage>() {

    private Storage storageInstance;

    @Override
    public Storage get() {
      if (storageInstance == null) {
        storageInstance = storageApi.get(SCHEMA);
      }
      return storageInstance;
    }
  };

  @Override
  public ViewContext createViewContext() {
    UUID uuid = UUID.randomUUID();
    URI uri = storage.get().saveAsNew(
        new ViewContext()
            .uuid(uuid));
    sessionApi.addViewContext(uuid, uri);
    return readViewContext(uri);
  }

  @Override
  public void setCurrentViewContext(UUID uuid) {
    if (uuid == null) {
      currentViewContextUuid.remove();
    }
    currentViewContextUuid.set(uuid);
  }

  @Override
  public ViewContext getCurrentViewContext() {
    return readViewContext(getViewContextUri(currentViewContextUuid.get()));
  }

  @Override
  public ViewContext getViewContext(UUID uuid) {
    return readViewContext(getViewContextUri(uuid));
  }

  private ViewContext readViewContext(URI uri) {
    if (uri == null) {
      return null;
    }
    return storage.get().read(uri, ViewContext.class);
  }

  private URI getViewContextUri(UUID uuid) {
    Objects.requireNonNull(uuid, "ViewContext UUID must be not null");
    Map<String, URI> viewContexts = sessionApi.currentSession().getViewContexts();
    URI uri = viewContexts.get(uuid.toString());
    if (uri == null) {
      throw new IllegalArgumentException("ViewContext not found by UUID");
    }
    return uri;
  }

  @Override
  public void updateCurrentViewContext(UnaryOperator<ViewContext> update) {
    storage.get().update(getViewContextUri(currentViewContextUuid.get()), ViewContext.class,
        update);
  }

  @Override
  public void updateViewContext(ViewContextUpdate updates) {
    storage.get().update(getViewContextUri(updates.getUuid()), ViewContext.class,
        c -> {
          updates.getUpdates().forEach(u -> updateViewState(c, u));
          return c;
        });
  }

  private void updateViewState(ViewContext context, ViewStateUpdate update) {
    context.getViews().stream()
        .filter(v -> update.getUuid().equals(v.getUuid()))
        .findFirst()
        .ifPresent(view -> view.setState(update.getState()));
  }
}
