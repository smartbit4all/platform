package org.smartbit4all.api.invocation;

import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.invocation.bean.JobDefinition;
import org.smartbit4all.api.invocation.bean.JobParameter;
import org.smartbit4all.api.invocation.bean.JobParameter.TypeEnum;
import org.smartbit4all.api.invocation.bean.ScheduledJobDefinition;
import org.smartbit4all.api.invocation.bean.ScheduledJobDefinition.ExecutionScopeEnum;
import org.smartbit4all.api.invocation.bean.ScheduledTestApiImpl;
import org.smartbit4all.api.invocation.config.InvocationApiMdmConfig;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {
    InvocationTestConfig.class,
    ScheduledJobTestConfig.class
}, properties = {
    "applicationruntime.refreshruntime.fixeddelay=2000",
    "invocationregistry.refresh.fixeddelay=2000"
})
class ScheduledJobTest {

  private static final String PARAM_NAME = "number";
  private static final Integer PARAM_VALUE = 5;

  @Autowired
  private InvocationApi invocationApi;
  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private ScheduledJobManager scheduledJobManager;

  @Autowired
  private MasterDataManagementApi masterDataManagementApi;

  @Value("${applicationruntime.refreshruntime.fixeddelay:5000}")
  private String schedulePeriodString;

  @Test
  void scheduledJobTest() throws Exception {
    JobDefinition jobDef = createJobDef();
    masterDataManagementApi.getApi(MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION,
        InvocationApiMdmConfig.MDM_ENTRY_JOBDEFINITION)
        .save(objectApi.create(Invocations.INVOCATION_SCHEME, jobDef));

    ScheduledJobDefinition scheduledJobDef = createScheduledJobDef(jobDef.getCode());
    List<URI> scheduledJobUris = masterDataManagementApi
        .getApi(MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION,
            InvocationApiMdmConfig.MDM_ENTRY_SCHEDULEDJOBDEFINITION)
        .save(objectApi.create(Invocations.INVOCATION_SCHEME, scheduledJobDef));

    waitForRefresh();
    scheduledJobManager.scheduleJobs(
        List.of(
            objectApi.loadLatest(scheduledJobUris.get(0)).getObject(ScheduledJobDefinition.class)));
    int sleepSeconds = 10;
    Thread.sleep(sleepSeconds * 1000);

    Assertions.assertEquals(sleepSeconds, ScheduledTestApiImpl.counter);
    Assertions.assertTrue(ScheduledTestApiImpl.value > 0);
    Assertions.assertEquals(sleepSeconds * PARAM_VALUE, ScheduledTestApiImpl.value);
  }

  private ScheduledJobDefinition createScheduledJobDef(String jobDefCode) {
    return new ScheduledJobDefinition()
        .code("1")
        .cronExpression("* * * * * *") // every sec
        .description("Ez egy teszt ScheduledJob")
        .name("Teszt")
        .jobDefinitionCode(jobDefCode)
        .executionScope(ExecutionScopeEnum.NODE)
        .parameters(
            List.of(new JobParameter()
                .type(TypeEnum.VALUE)
                .key(PARAM_NAME)
                .value(PARAM_VALUE)));
  }

  private JobDefinition createJobDef() {
    // Increment *ScheduledTestApiImpl.value* with *PARAM_VALUE* every sec
    return new JobDefinition()
        .code("1")
        .description("Ez egy teszt Job")
        .name("Teszt")
        .parameters(
            List.of(new JobParameter()
                .type(TypeEnum.VALUE)
                .key(PARAM_NAME)
                .value(PARAM_VALUE)))
        .task(invocationApi.runBuilder()
            .addItem(ib -> ib
                .request(invocationApi.builder(ScheduledTestApi.class)
                    .build(api -> api.incrementBy(null)))
                .addResolverFromPath(PARAM_NAME))
            .build());
  }

  private void waitForRefresh() throws NumberFormatException, InterruptedException {
    Thread.sleep(Long.parseLong(schedulePeriodString) * 2);
  }
}
