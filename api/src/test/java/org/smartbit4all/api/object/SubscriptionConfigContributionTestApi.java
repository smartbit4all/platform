package org.smartbit4all.api.object;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;

public class SubscriptionConfigContributionTestApi implements SubscriptionConfigContributionApi {

  public static final String READ_WRITE_CATEGORY = "ReadWriteCategory";

  @Autowired
  private ObjectApi objectApi;

  @Override
  public String getApiName() {
    return "test";
  }

  private final List<String> managedConfigs = new ArrayList<>();

  public SubscriptionConfigContributionTestApi() {
    super();
    managedConfigs.add(READ_WRITE_CATEGORY);
  }

  @Override
  public List<String> getManagedConfigs() {
    return managedConfigs;
  }

  @Override
  public List<String> getRevokableConfigs() {
    return Collections.emptyList();
  }

  @Override
  public String constructEntitySummary(String config, URI entityUri) {
    ObjectNode node = objectApi.loadLatest(entityUri);
    return node.getValueAsString(SampleCategory.NAME);
  }

}
