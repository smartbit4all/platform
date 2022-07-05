package org.smartbit4all.api.invocation;

import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;

class InvocationRequestTest {

  @Test
  void testInvocationWithString() throws Exception {
    String invocationCall =
        "FacekomApi:hu.it4all.kh.FacekomApi.statusUpdate(processId=@@processId@@:java.lang.Long,StatusOk=false:java.lang.Boolean,status=@@root@@/lastFacekomCall#errorCode:java.net.URI)";
    InvocationRequest expectedInvocationRequest = new InvocationRequest()
        .interfaceClass("hu.it4all.kh.FacekomApi")
        .name("FacekomApi")
        .methodName("statusUpdate")
        .parameters(Arrays.asList(
            new InvocationParameter()
                .name("processId")
                .typeClass("java.lang.Long")
                .value("@@processId@@"),
            new InvocationParameter()
                .name("StatusOk")
                .typeClass("java.lang.Boolean")
                .value("false"),
            new InvocationParameter()
                .name("status")
                .typeClass("java.net.URI")
                .value("@@root@@/lastFacekomCall#errorCode")));
    InvocationRequest result = Invocations.createInvocationRequest(invocationCall);
    Assertions.assertEquals(expectedInvocationRequest, result);
  }

  @Test
  void testInvocationWithStringWithoutName() throws Exception {
    String invocationCall =
        "hu.it4all.kh.FacekomApi.statusUpdate(processId=@@processId@@:java.lang.Long,StatusOk=false:java.lang.Boolean,status=@@root@@/lastFacekomCall#errorCode:java.net.URI)";
    InvocationRequest expectedInvocationRequest = new InvocationRequest()
        .interfaceClass("hu.it4all.kh.FacekomApi")
        .name("hu.it4all.kh.FacekomApi")
        .methodName("statusUpdate")
        .parameters(Arrays.asList(
            new InvocationParameter()
                .name("processId")
                .typeClass("java.lang.Long")
                .value("@@processId@@"),
            new InvocationParameter()
                .name("StatusOk")
                .typeClass("java.lang.Boolean")
                .value("false"),
            new InvocationParameter()
                .name("status")
                .typeClass("java.net.URI")
                .value("@@root@@/lastFacekomCall#errorCode")));
    InvocationRequest result = Invocations.createInvocationRequest(invocationCall);
    Assertions.assertEquals(expectedInvocationRequest, result);
  }

}
