package org.smartbit4all.api.view;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.view.bean.ViewContext;
import org.smartbit4all.api.view.bean.ViewContextUpdate;
import org.smartbit4all.api.view.bean.ViewStateUpdate;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.springframework.beans.factory.annotation.Autowired;

public class ViewContextServiceImpl implements ViewContextService {

  private static final Logger log = LoggerFactory.getLogger(ViewContextServiceImpl.class);

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
  public ViewContext getCurrentViewContext() {
    return readViewContext(getCurrentViewContextUri());
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

  private URI getCurrentViewContextUri() {
    Map<String, URI> viewContexts = sessionApi.currentSession().getViewContexts();
    if (viewContexts.isEmpty()) {
      // TODO this is not the best here, preliminary lazy stuff
      UUID uuid = UUID.randomUUID();
      URI uri = storage.get().saveAsNew(
          new ViewContext()
              .uuid(uuid));
      sessionApi.addViewContext(uuid, uri);
      return uri;
    }
    if (viewContexts.size() > 1) {
      log.error("More than 1 viewContext is not handled currently!");
    }
    return viewContexts.values().iterator().next();
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
    storage.get().update(getCurrentViewContextUri(), ViewContext.class, update);
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
