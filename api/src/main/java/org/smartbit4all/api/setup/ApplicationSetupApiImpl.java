package org.smartbit4all.api.setup;

import java.util.ArrayList;
import java.util.Collection;
import org.smartbit4all.api.contribution.ContributionApiImpl;
import org.smartbit4all.api.mdm.bean.ApplicationSetupData;
import org.smartbit4all.api.mdm.bean.ApplicationSetupKind;
import static java.util.stream.Collectors.toList;

public abstract class ApplicationSetupApiImpl extends ContributionApiImpl
    implements ApplicationSetupApi {

  private ApplicationSetupData setupData;

  protected ApplicationSetupApiImpl(Class<? extends ApplicationSetupApi> clazz,
      Collection<Class<? extends ApplicationSetupApi>> preRequisites) {
    super(clazz.getName());
    this.setupData = new ApplicationSetupData().name(getApiName()).kind(ApplicationSetupKind.API)
        .preRequisites(preRequisites != null
            ? preRequisites.stream().map(a -> a.getClass().getName()).collect(toList())
            : new ArrayList<>());
  }

  @Override
  public ApplicationSetupData getData() {
    return setupData;
  }

}
