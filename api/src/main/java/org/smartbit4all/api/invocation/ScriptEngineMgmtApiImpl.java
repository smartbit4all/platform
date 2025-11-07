package org.smartbit4all.api.invocation;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.smartbit4all.api.contribution.PrimaryApiImpl;

public class ScriptEngineMgmtApiImpl extends PrimaryApiImpl<ScriptEngineContributionApi>
    implements ScriptEngineMgmtApi {

  public ScriptEngineMgmtApiImpl() {
    super(ScriptEngineContributionApi.class);
  }

  @Override
  public ScriptEngine getEngine(String name) {
    ScriptEngineContributionApi contributionApi = getContributionApi(name);
    if (contributionApi != null) {
      return contributionApi.getEngine(name);
    } else {
      final ScriptEngineManager scriptEngineManager =
          new ScriptEngineManager(getClass().getClassLoader());
      return scriptEngineManager.getEngineByName(name);
    }
  }

}
