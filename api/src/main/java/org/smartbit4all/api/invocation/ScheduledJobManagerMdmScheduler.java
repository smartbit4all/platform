package org.smartbit4all.api.invocation;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import org.smartbit4all.api.invocation.bean.ScheduledJobDefinition;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

public class ScheduledJobManagerMdmScheduler {

  public static final String MDM_SCHEDULED_JOB = "scheduledJob";

  @Autowired
  private MasterDataManagementApi masterDataManagementApi;

  @Autowired
  private ScheduledJobManager scheduledJobManager;

  @EventListener(ApplicationReadyEvent.class)
  public void initialize() {
    Executors.newSingleThreadExecutor().submit(() -> {
      List<ScheduledJobDefinition> jobDefinitions = loadJobDefinitions();
      scheduledJobManager.scheduleJobs(jobDefinitions);
    });
  }

  private List<ScheduledJobDefinition> loadJobDefinitions() {
    return masterDataManagementApi.getApi(ScheduledJobRunner.SCHEMA, MDM_SCHEDULED_JOB)
        .getList()
        .nodesFromCache()
        .map(node -> node.getObject(ScheduledJobDefinition.class))
        .collect(Collectors.toList());
  }

}
