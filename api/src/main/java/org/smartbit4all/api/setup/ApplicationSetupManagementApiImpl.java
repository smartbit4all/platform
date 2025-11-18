package org.smartbit4all.api.setup;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredMap;
import org.smartbit4all.api.contribution.PrimaryApiImpl;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.ApplicationSetup;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.setup.util.PrerequisiteEvaluator;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.utility.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class ApplicationSetupManagementApiImpl extends PrimaryApiImpl<ApplicationSetupApi>
    implements ApplicationSetupManagementApi {

  private static final Logger log =
      LoggerFactory.getLogger(ApplicationSetupManagementApiImpl.class);


  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private CollectionApi collectionApi;

  @Autowired(required = false)
  private SessionManagementApi sessionManagementApi;

  public ApplicationSetupManagementApiImpl() {
    super(ApplicationSetupApi.class);
  }

  @Override
  @Scheduled(initialDelayString = "${applicationsetup.schedule.initdelay:2000}",
      fixedDelayString = "${applicationsetup.schedule.fixeddelay:60000}")
  public void scheduleSetup() {
    URI lockUri =
        UriUtils.constructMethodUri(MasterDataManagementApi.SCHEMA,
            ApplicationSetupManagementApi.class,
            "scheduleSetup");
    Lock lock = objectApi.getLock(lockUri);
    lock.lock();
    try {
      StoredMap map = collectionApi.map(MasterDataManagementApi.SCHEMA, SETUP_MAP);
      Map<String, URI> alreadyExecuted = map.uris();

      Map<String, ApplicationSetupApi> setups = getContributionApis()
          .values().stream()
          .filter(setup -> {
            boolean already = alreadyExecuted.containsKey(setup.getData().getName());
            return !already || setup.checkRunAgain();
          })
          .collect(toMap(a -> a.getApiName(), a -> a));
      Map<String, Set<String>> setupPreRequisites = setups
          .entrySet().stream()
          .collect(toMap(
              e -> e.getKey(),
              e -> e.getValue().getData().getPreRequisites().stream().collect(toSet())));

      List<String> orderedNames = PrerequisiteEvaluator
          .of(setupPreRequisites)
          .evaluate(setups::containsKey);

      List<ApplicationSetupApi> sortedList = orderedNames.stream()
          .map(setups::get)
          .toList();
      for (ApplicationSetupApi setupApi : sortedList) {
        tryEnsureTechnicalSession();
        try {
          URI uri = objectApi.saveAsNew(MasterDataManagementApi.SCHEMA,
              new ApplicationSetup().data(setupApi.getData()));
          setupApi.execute();
          map.put(setupApi.getData().getName(), uri);
        } catch (Exception e) {
          log.error("Failed to execute " + setupApi.getData() + " setup api.", e);
        }
      }
    } finally {
      lock.unlock();
    }
  }

  private void tryEnsureTechnicalSession() {
    if (sessionManagementApi != null) {
      try {
        sessionManagementApi.startTechnicalSession();
      } catch (final Exception e) {
        log.error("Could not start technical session: {}", e.getMessage(), e);
      }
    }
  }
}
