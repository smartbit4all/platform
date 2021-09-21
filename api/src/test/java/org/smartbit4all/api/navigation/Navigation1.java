package org.smartbit4all.api.navigation;

import java.net.URI;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.navigation.bean.NavigationEntry;
import org.smartbit4all.api.navigation.bean.NavigationReferenceEntry;

public class Navigation1 extends NavigationImpl {

  public Navigation1(String name) {
    super(name);
    // TODO Auto-generated constructor stub
  }

  @Override
  public Map<URI, List<NavigationReferenceEntry>> navigate(URI objectUri,
      List<URI> associationMetaUris) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public NavigationEntry getEntry(URI entryMetaUri, URI objectUri) {
    // TODO Auto-generated method stub
    return null;
  }

}
