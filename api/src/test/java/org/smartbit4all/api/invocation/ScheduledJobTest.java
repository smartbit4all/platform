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

  @Value("${applicationruntime.refreshruntime.fixeddelay:5000}")
  private String schedulePeriodString;

  @Test
  void scheduledJobTest() throws Exception {
    JobDefinition jobDef = createJobDef();
    URI jobDefUri = objectApi.saveAsNew(ScheduledJobRunner.SCHEMA, jobDef);
    ScheduledJobDefinition scheduledJobDef = createScheduledJobDef(jobDefUri);
    URI scheduledJobDefUri = objectApi.saveAsNew(ScheduledJobRunner.SCHEMA, scheduledJobDef);
    waitForRefresh();
    scheduledJobManager.scheduleJobs(
        List.of(objectApi.loadLatest(scheduledJobDefUri).getObject(ScheduledJobDefinition.class)));
    int sleepSeconds = 10;
    Thread.sleep(sleepSeconds * 1000);

    Assertions.assertEquals(sleepSeconds, ScheduledTestApiImpl.counter);
    Assertions.assertTrue(ScheduledTestApiImpl.value > 0);
    Assertions.assertTrue(ScheduledTestApiImpl.value / PARAM_VALUE == sleepSeconds);
    Assertions.assertTrue(ScheduledTestApiImpl.value % PARAM_VALUE == 0);
  }

  private ScheduledJobDefinition createScheduledJobDef(URI jobDefUri) {
    return new ScheduledJobDefinition()
        .id("test_scheduled")
        .cronExpression("* * * * * *") // every sec
        .description("Ez egy teszt ScheduledJob")
        .name("Teszt")
        .jobDefinition(jobDefUri)
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
        .id("test_job")
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
