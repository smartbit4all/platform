package org.smartbit4all.api.object;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.contribution.PrimaryApiImpl;
import org.smartbit4all.api.org.bean.ACLSubjectSubscription;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.toList;

public class SubscriptionConfigApiImpl extends PrimaryApiImpl<SubscriptionConfigContributionApi>
    implements SubscriptionConfigApi {

  private static final Logger log = LoggerFactory.getLogger(SubscriptionConfigApiImpl.class);

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private LocaleSettingApi localeSettingApi;

  public SubscriptionConfigApiImpl() {
    super(SubscriptionConfigContributionApi.class);
  }

  private final SubscriptionConfigContributionApi getApi(String config) {
    Optional<SubscriptionConfigContributionApi> foundApi = getContributionApis().values().stream()
        .filter(a -> a.getManagedConfigs().contains(config)).findFirst();
    return foundApi.orElse(null);
  }

  @Override
  public TableData<?> postProcess(SearchIndex<?> searchIndex,
      TableData<?> td) {
    EntityDefinition contentDef = searchIndex.getDefinition().getDefinition();
    Property<?> entityUriProperty =
        contentDef.getProperty(SUBSCRIPTION_OPERATION_ENTITYURI);
    Property<?> configProperty =
        contentDef.getProperty(SUBSCRIPTION_OPERATION_CONTEXTCONFIG);
    Property<?> entitySummaryProperty =
        contentDef.getProperty(SUBSCRIPTION_OPERATION_ENTITYSUMMARY);
    Property<?> subjectTypeProperty =
        contentDef.getProperty(SUBSCRIPTION_SUBJECT_TYPE);
    Property<?> subjectTypeNameProperty =
        contentDef.getProperty(SUBSCRIPTION_SUBJECT_TYPE_NAME);

    DataColumn<?> configCol = td.getColumn(configProperty);
    DataColumn<?> entityUriCol = td.getColumn(entityUriProperty);
    DataColumn<?> entitySummaryCol = td.getColumn(entitySummaryProperty);

    if (configCol == null || entityUriCol == null || entitySummaryCol == null
        || subjectTypeProperty == null || subjectTypeNameProperty == null) {
      log.debug(
          "Cannot update the subscription table - one or more required columns missing from table.");
      return td;
    }

    List<DataRow> rows = td.rows();
    for (DataRow dataRow : rows) {
      String config = (String) dataRow.get(configProperty);
      SubscriptionConfigContributionApi api = getApi(config);
      if (api != null) {
        dataRow.setObject(entitySummaryProperty,
            api.constructEntitySummary(config,
                objectApi.asType(URI.class, dataRow.get(entityUriProperty))));
      }
      String subjectType = (String) dataRow.get(subjectTypeProperty);
      dataRow.setObject(subjectTypeNameProperty,
          localeSettingApi.get(ACLSubjectSubscription.SUBJECT, Subject.TYPE, subjectType));
    }
    return td;
  }

  @Override
  public List<String> revokableConfigs() {
    return getContributionApis().values().stream().flatMap(c -> c.getRevokableConfigs().stream())
        .distinct().collect(toList());
  }

}
