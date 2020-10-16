package org.smartbit4all.json.mixin;

import org.smartbit4all.domain.meta.Property;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class SortOrderPropertyMixin {
  
  @JsonProperty
  Property<?> property;

  @JsonProperty
  boolean asc;

  @JsonProperty
  boolean nullsFirst = false;
}
