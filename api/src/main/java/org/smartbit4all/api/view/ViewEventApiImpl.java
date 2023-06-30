package org.smartbit4all.api.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewEventHandler;
import org.smartbit4all.core.utility.StringConstant;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;

/**
 * The default implementation of the event handler api.
 * 
 * @author Peter Boros
 */
public class ViewEventApiImpl implements ViewEventApi {

  /**
   * The view object the event api is working on.
   */
  private View view;

  /**
   * The handler cache contains all the event handlers of the view with the regular expression for
   * their position. If we added an event handler to execute after every action then it has a key.
   * Now we have exact matching later on improve to have regular expressions in the key to be able
   * to identify the proper event handlers for a given path.
   */
  private final Map<String, List<ViewEventHandler>> handlerCache;

  /**
   * The {@link ViewEventApiImpl} constructor with the View object at all.
   * 
   * @param view
   */
  ViewEventApiImpl(View view) {
    super();
    Objects.requireNonNull(view, "Unable to initiate ViewEventApi, the view is missing.");
    this.view = view;
    handlerCache = view.getEventHandlers().stream()
        .collect(groupingBy(this::getPath));
  }

  private final String getPath(ViewEventHandler eh) {
    return eh.getPath().stream().collect(joining(StringConstant.DOT));
  }

  @Override
  public void add(ViewEventHandler eventHandler) {
    List<ViewEventHandler> list =
        handlerCache.computeIfAbsent(getPath(eventHandler), s -> new ArrayList<>());
    list.add(eventHandler);
    view.addEventHandlersItem(eventHandler);
  }

  @Override
  public ViewEventDescriptor get(String... path) {
    ViewEventDescriptor result = new ViewEventDescriptor();
    if (path != null && path.length != 0) {
      String pathString = Stream.of(path).collect(joining(StringConstant.DOT));
      List<ViewEventHandler> list = handlerCache.get(pathString);
      if (list != null) {
        for (ViewEventHandler viewEventHandler : list) {
          switch (viewEventHandler.getViewEventType()) {
            case INSTEAD:
              result.setInsteadOf(viewEventHandler);
              break;
            case BEFORE:
              result.getBeforeEvents().add(viewEventHandler);
              break;
            case AFTER:
              result.getAfterEvents().add(viewEventHandler);
              break;
          }
        }
      }
    }
    return result;
  }

  @Override
  public boolean remove(ViewEventHandler eventHandler) {
    return false;
  }

}
