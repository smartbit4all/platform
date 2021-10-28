package org.smartbit4all.ui.api.sb4starter;

import java.util.function.BiConsumer;
import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.ui.api.sb4starterui.model.SB4StarterWordFormModel;

public interface SB4StarterWordViewModel extends ObjectEditing {

  public static final String VIEW_NAME = "SB4StarterWord";

  public static final String SCHEMA = "SB4STARTER";

  static final String DOWNLOAD = "download";
  static final String ACCEPT = "accept";
  static final String DECLINE = "decline";

  ObservableObject sb4Starter();

  void createSB4Starter() throws Exception;

  void initSb4StarterFormModel(SB4StarterWordFormModel sb4StarterWordFormModel,
      BiConsumer<BinaryContent, BinaryContent> acceptHandler) throws Exception;

  void accept();

  void close();
}
