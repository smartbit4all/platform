package org.smartbit4all.api.invocation;

import javax.script.ScriptEngine;
import org.smartbit4all.api.contribution.PrimaryApi;

public interface ScriptEngineMgmtApi extends PrimaryApi<ScriptEngineContributionApi> {

  String SCHEMA = "script";

  String SCRIPT_SETTINGS_LIST = "scriptSettings";

  String SCRIPT_KIND_GROOVY = "Groovy";

  ScriptEngine getEngine(String name);

}
