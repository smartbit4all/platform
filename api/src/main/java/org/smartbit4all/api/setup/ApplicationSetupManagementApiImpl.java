package org.smartbit4all.api.setup;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredMap;
import org.smartbit4all.api.contribution.PrimaryApiImpl;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.ApplicationSetup;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.utility.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

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

      Map<String, ApplicationSetupApi> setups =
          new HashMap<>(getContributionApis().values().stream()
              .filter(setup -> {
                boolean already = alreadyExecuted.containsKey(setup.getData().getName());
                return !already || setup.checkRunAgain();
              })
              .collect(toMap(a -> a.getApiName(), a -> a)));
      Map<String, Set<String>> setupPreRequisites =
          new HashMap<>(setups.entrySet().stream().collect(toMap(e -> e.getKey(),
              e -> e.getValue().getData().getPreRequisites().stream().collect(toSet()))));

      List<String> orderedNames = new ArrayList<>();
      Set<String> noPrereqs = setupPreRequisites.entrySet().stream()
          .filter(e -> e.getValue().isEmpty())
          .map(Map.Entry::getKey)
          .collect(Collectors.toSet());
      noPrereqs.forEach(it -> {
        setupPreRequisites.remove(it);
        orderedNames.add(it);
      });
      final int customStart = orderedNames.size() - 1;
      Map<String, Integer> boundHigh = new HashMap<>();
      for (final var e : setupPreRequisites.entrySet()) {
        final String name = e.getKey();
        final var prerequisites = e.getValue();

        final Set<String> missingPrereqs = new HashSet<>();
        int targetIdx = customStart;
        for (final var prereq : prerequisites) {
          if (!setups.containsKey(prereq)) {
            continue;
          }
          int idx = orderedNames.indexOf(prereq);
          if (idx < 0) {
            // not present in the ordered list yet:
            missingPrereqs.add(prereq);
          } else {
            targetIdx = Math.max(targetIdx, idx);
          }
        }

        final Integer myBoundHigh = boundHigh.get(name);
        final int myBoundLow;
        if (myBoundHigh == null) {
          myBoundLow = Math.max(orderedNames.size(), targetIdx);
        } else if (targetIdx + 1 > myBoundHigh) {
          throw new IllegalStateException("Cycle detected!");
        } else {
          myBoundLow = myBoundHigh;
        }

        orderedNames.add(myBoundLow, name);
        missingPrereqs.forEach(prereq -> boundHigh.compute(prereq, (k, v) -> {
          return v == null ? myBoundLow : Math.min(v, myBoundLow);
        }));
        boundHigh.keySet()
            .forEach(
                k -> boundHigh.computeIfPresent(k, (key, v) -> myBoundLow > v ? v + 1 : v));
      }

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
