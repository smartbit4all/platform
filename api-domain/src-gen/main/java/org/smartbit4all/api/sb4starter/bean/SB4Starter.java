/*
 * sb4starter api
 * sb4starter api
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.sb4starter.bean;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.sb4starter.bean.SB4Command;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * SB4Starter
 */
@JsonPropertyOrder({
  SB4Starter.ID,
  SB4Starter.COMMANDS,
  SB4Starter.KEEP_WORKING_DIRECTORY
})
@JsonTypeName("SB4Starter")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class SB4Starter {
  public static final String ID = "id";
  private UUID id;

  public static final String COMMANDS = "commands";
  private List<SB4Command> commands = new ArrayList<>();

  public static final String KEEP_WORKING_DIRECTORY = "keepWorkingDirectory";
  private Boolean keepWorkingDirectory = false;

  public SB4Starter() { 
  }

  public SB4Starter id(UUID id) {
    
    this.id = id;
    return this;
  }

   /**
   * Get id
   * @return id
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(ID)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public UUID getId() {
    return id;
  }


  @JsonProperty(ID)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setId(UUID id) {
    this.id = id;
  }


  public SB4Starter commands(List<SB4Command> commands) {
    
    this.commands = commands;
    return this;
  }

  public SB4Starter addCommandsItem(SB4Command commandsItem) {
    this.commands.add(commandsItem);
    return this;
  }

   /**
   * Get commands
   * @return commands
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(COMMANDS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<SB4Command> getCommands() {
    return commands;
  }


  @JsonProperty(COMMANDS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setCommands(List<SB4Command> commands) {
    this.commands = commands;
  }


  public SB4Starter keepWorkingDirectory(Boolean keepWorkingDirectory) {
    
    this.keepWorkingDirectory = keepWorkingDirectory;
    return this;
  }

   /**
   * Get keepWorkingDirectory
   * @return keepWorkingDirectory
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(KEEP_WORKING_DIRECTORY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean getKeepWorkingDirectory() {
    return keepWorkingDirectory;
  }


  @JsonProperty(KEEP_WORKING_DIRECTORY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setKeepWorkingDirectory(Boolean keepWorkingDirectory) {
    this.keepWorkingDirectory = keepWorkingDirectory;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SB4Starter sb4Starter = (SB4Starter) o;
    return Objects.equals(this.id, sb4Starter.id) &&
        Objects.equals(this.commands, sb4Starter.commands) &&
        Objects.equals(this.keepWorkingDirectory, sb4Starter.keepWorkingDirectory);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, commands, keepWorkingDirectory);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SB4Starter {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    commands: ").append(toIndentedString(commands)).append("\n");
    sb.append("    keepWorkingDirectory: ").append(toIndentedString(keepWorkingDirectory)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

