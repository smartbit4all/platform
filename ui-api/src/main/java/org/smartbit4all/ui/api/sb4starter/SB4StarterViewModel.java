package org.smartbit4all.ui.api.sb4starter;

import org.smartbit4all.ui.api.viewmodel.ViewModel;

public interface SB4StarterViewModel extends ViewModel {

  public static final String VIEW_NAME = "SB4Starter";

  public static final String SCHEMA = "SB4STARTER";

  public static final String PARAM_FORMMODEL = "SB4StarterViewModel.SB4StarterFormModel";
  public static final String PARAM_ACCEPTHANDLER = "SB4StarterViewModel.acceptHandler";
  public static final String PARAM_BASELOCATION = "SB4StarterViewModel.baseLocation";

  static final String DOWNLOAD = "download";
  static final String ACCEPT = "accept";
  static final String DECLINE = "decline";
}
