package org.smartbit4all.api.setting.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.setting.bean.LocaleSettingForLanguage;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * The locale settings is the root object for the settings. It consists of local specific settings.
 */
@ApiModel(description = "The locale settings is the root object for the settings. It consists of local specific settings.")

public class LocaleSettingsRoot   {
  @JsonProperty("uri")
  private URI uri;

  @JsonProperty("settings")
  @Valid
  private List<LocaleSettingForLanguage> settings = new ArrayList<>();

  public LocaleSettingsRoot uri(URI uri) {
    this.uri = uri;
    return this;
  }

  /**
   * The uri of the locale setting
   * @return uri
  */
  @ApiModelProperty(required = true, value = "The uri of the locale setting")
  @NotNull

  @Valid

  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }

  public LocaleSettingsRoot settings(List<LocaleSettingForLanguage> settings) {
    this.settings = settings;
    return this;
  }

  public LocaleSettingsRoot addSettingsItem(LocaleSettingForLanguage settingsItem) {
    this.settings.add(settingsItem);
    return this;
  }

  /**
   * Locale specific settings.
   * @return settings
  */
  @ApiModelProperty(required = true, value = "Locale specific settings.")
  @NotNull

  @Valid

  public List<LocaleSettingForLanguage> getSettings() {
    return settings;
  }

  public void setSettings(List<LocaleSettingForLanguage> settings) {
    this.settings = settings;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LocaleSettingsRoot localeSettingsRoot = (LocaleSettingsRoot) o;
    return Objects.equals(this.uri, localeSettingsRoot.uri) &&
        Objects.equals(this.settings, localeSettingsRoot.settings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, settings);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LocaleSettingsRoot {\n");
    
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    settings: ").append(toIndentedString(settings)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

