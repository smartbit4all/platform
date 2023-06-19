package org.smartbit4all.api.mdm;

import org.smartbit4all.api.mdm.bean.MDMDefinition;

/**
 * The object that can be registered into the spring context to define a definition. It will be able
 * to define if the given definition can be edited in the storage. If yes then the settings from
 * this bean will overwrite the storage. Else we just update the records or leave them untouched.
 */
public class MDMDefinitionOption {

  private MDMDefinition definition;

  public MDMDefinitionOption(MDMDefinition definition) {
    super();
    this.definition = definition;
  }

  public final MDMDefinition getDefinition() {
    return definition;
  }

  public final void setDefinition(MDMDefinition definition) {
    this.definition = definition;
  }

}
