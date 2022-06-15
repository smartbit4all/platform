package org.smartbit4all.api.invocation;

import java.util.UUID;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;

/**
 * The execution api for the invocations.
 * 
 * @author Peter Boros
 */
public interface InvocationExecutionApi {

  InvocationParameter invoke(UUID runtime, InvocationRequest request);

}
