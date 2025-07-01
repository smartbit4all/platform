package org.smartbit4all.api.invocation;

import javax.script.ScriptEngine;
import org.smartbit4all.api.contribution.ContributionApi;

public interface ScriptEngineContributionApi extends ContributionApi {

  ScriptEngine getEngine(String name);

}
