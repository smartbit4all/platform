package org.smartbit4all.api.binarydata;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class BinaryDataApiPrimary extends BinaryDataApiImpl implements InitializingBean {

  Map<String, BinaryDataApi> apiByName = new HashMap<>();

  @Autowired(required = false)
  List<BinaryDataApi> apis;

  public BinaryDataApiPrimary() {
    super(BinaryDataApiPrimary.class.getTypeName());
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (apis != null) {
      for (BinaryDataApi api : apis) {
        apiByName.put(api.name(), api);
      }
    }
  }

  private final BinaryDataApi api(URI uri) {
    BinaryDataApi api = apiByName.get(uri.getScheme());
    if (api == null) {
      throw new IllegalArgumentException("The BinaryDataApi was not found for the " + uri);
    }
    return api;
  }

  @Override
  public void save(BinaryData data, URI dataUri) {
    BinaryDataApi api = api(dataUri);
    api.save(data, dataUri);
  }

  @Override
  public Optional<BinaryData> load(URI dataUri) {
    if (dataUri == null) {
      return Optional.empty();
    }
    BinaryDataApi api = api(dataUri);
    return api.load(dataUri);
  }

  @Override
  public void remove(URI dataUri) {
    BinaryDataApi api = api(dataUri);
    api.remove(dataUri);
  }

}
