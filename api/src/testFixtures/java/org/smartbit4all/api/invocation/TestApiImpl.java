package org.smartbit4all.api.invocation;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Random;
import org.smartbit4all.api.invocation.bean.AsyncInvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationResult;
import org.smartbit4all.api.invocation.bean.InvocationResultDecision;
import org.smartbit4all.api.invocation.bean.InvocationResultDecision.DecisionEnum;
import org.smartbit4all.api.invocation.bean.TestDataBean;
import org.smartbit4all.core.utility.concurrent.FutureValue;

public class TestApiImpl implements TestApi {

  public static final String NAME = TestApiImpl.class.getName();

  public static String lastDo;

  public Random rnd = new Random();

  public int thirdErrorCounter = 1;

  public static FutureValue<String> secondResult = new FutureValue<>();

  public static FutureValue<String> thirdResult = new FutureValue<>();

  @Override
  public void doMethod(String p1) {
    System.out.println("do(" + p1 + ")");
    lastDo = p1;
  }

  @Override
  public String echoMethod(String p1) {
    return p1;
  }

  @Override
  public TestDataBean modifyData(TestDataBean databean) {
    return databean;
  }

  @Override
  public List<TestDataBean> modifyData(List<TestDataBean> databeans) {
    return databeans;
  }

  @Override
  public void setFutureValue(String p1) {
    TestApi.futureValue.setValue(p1);
  }

  @Override
  public void setFutureValueAt(String p1) {
    TestApi.scheduledLatch.countDown();
  }

  @Override
  public String firstStep(String p) {
    return p;
  }

  @Override
  public InvocationResultDecision firstStepOnError(AsyncInvocationRequest r, InvocationResult p) {
    return new InvocationResultDecision().decision(DecisionEnum.CONTINUE)
        .scheduledAt(OffsetDateTime.now().plusSeconds(1));
  }

  @Override
  public String secondStep(String p, String postfix) {
    String result = p + postfix;
    TestApiImpl.secondResult.setValue(result);
    return result;
  }

  @Override
  public String thirdStep(String p, String postfix) {
    try {
      Thread.sleep(rnd.nextInt(1000));
    } catch (InterruptedException e) {
      throw new IllegalStateException(e);
    }
    if ((0 < thirdErrorCounter--) || rnd.nextBoolean()) {
      throw new IllegalArgumentException("IllegalArgumentException");
    }
    String result = p + postfix;
    TestApiImpl.thirdResult.setValue(result);
    return result;
  }

  @Override
  public InvocationResultDecision thirdStepOnError(AsyncInvocationRequest r, InvocationResult p) {
    return p.getError() != null ? new InvocationResultDecision().decision(DecisionEnum.RESCHEDULE)
        .scheduledAt(OffsetDateTime.now().plusSeconds(3))
        : new InvocationResultDecision().decision(DecisionEnum.CONTINUE);
  }

}
