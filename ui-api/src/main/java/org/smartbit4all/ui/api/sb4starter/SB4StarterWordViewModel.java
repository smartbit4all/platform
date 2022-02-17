package org.smartbit4all.ui.api.sb4starter;

import org.smartbit4all.ui.api.viewmodel.ViewModel;

public interface SB4StarterWordViewModel extends ViewModel {

  public static final String VIEW_NAME = "SB4StarterWord";

  public static final String SCHEMA = "SB4STARTER";

  public static final String PARAM_FORMMODEL = "SB4StarterWordViewModel.SB4StarterWordFormModel";
  public static final String PARAM_ACCEPTHANDLER = "SB4StarterWordViewModel.acceptHandler";
  public static final String PARAM_BASELOCATION = "SB4StarterWordViewModel.baseLocation";

  static final String DOWNLOAD = "download";
  static final String ACCEPT = "accept";
  static final String DECLINE = "decline";
}
