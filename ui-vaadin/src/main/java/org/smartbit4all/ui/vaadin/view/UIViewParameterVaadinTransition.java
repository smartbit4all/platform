/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.ui.vaadin.view;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.smartbit4all.ui.common.view.UIViewShowCommand;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.server.VaadinSession;

/**
 * The transition of the parameters implemented with the attributes of the {@link VaadinSession}.
 * The {@link UIViewShowCommand} defines the necessary parameters for the view to open. This utility
 * will store the parameters till the navigation. After the navigation the given parameters will be
 * removed to avoid parameter confusion. It's closeable to encourage writing try(
 * 
 * @author Peter Boros
 */
public class UIViewParameterVaadinTransition implements AutoCloseable {

  private static final String PARAM = "param";

  private static final Map<String, Map<String, Object>> parameterMap = new HashMap<>();

  /**
   * The unique identifier of the given parameter transition. It's the url query parameter of the
   * given navigate.
   */
  private String identifier;

  private Map<String, Object> parameters;

  private static final AtomicLong sequence = new AtomicLong();

  public UIViewParameterVaadinTransition(Map<String, Object> parameters) {
    super();
    if (parameters != null) {
      identifier = String.valueOf(sequence.getAndIncrement());
      parameterMap.put(identifier, parameters);
    }
  }

  public QueryParameters construct() {
    if (identifier == null) {
      return null;
    }
    Map<String, List<String>> params = new HashMap<>();
    params.put(PARAM, Arrays.asList(identifier));
    return new QueryParameters(params);
  }

  public static Map<String, Object> extract(QueryParameters queryParameters) {
    if (queryParameters == null || queryParameters.getParameters() == null) {
      return Collections.emptyMap();
    }
    List<String> list = queryParameters.getParameters().get(PARAM);
    if (list == null || list.size() != 1) {
      return Collections.emptyMap();
    }
    Map<String, Object> result = parameterMap.get(list.get(0));
    return result == null ? Collections.emptyMap() : result;
  }

  @Override
  public void close() throws Exception {
    if (identifier != null) {
      parameterMap.remove(identifier);
    }
  };

}
