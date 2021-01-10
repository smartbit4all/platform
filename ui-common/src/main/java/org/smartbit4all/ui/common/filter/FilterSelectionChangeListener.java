package org.smartbit4all.ui.common.filter;

import java.net.URI;
import java.util.List;

public interface FilterSelectionChangeListener {

  void filterSelectionChanged(String filterId, List<URI> values);

}
