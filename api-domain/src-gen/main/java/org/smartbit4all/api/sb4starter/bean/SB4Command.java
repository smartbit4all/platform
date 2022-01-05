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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.sb4starter.bean.CommandKind;
import org.smartbit4all.api.sb4starter.bean.SB4File;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * SB4Command
 */
@JsonPropertyOrder({
  SB4Command.ID,
  SB4Command.COMMAND_KIND,
  SB4Command.REST_URL,
  SB4Command.SB4_FILES,
  SB4Command.COMMAND,
  SB4Command.SUCCESSFUL_EXIT_CODES,
  SB4Command.ON_ERROR_COMMANDS
})
@JsonTypeName("SB4Command")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class SB4Command {
  public static final String ID = "id";
  private UUID id;

  public static final String COMMAND_KIND = "commandKind";
  private CommandKind commandKind;

  public static final String REST_URL = "restUrl";
  private URI restUrl;

  public static final String SB4_FILES = "sb4Files";
  private List<SB4File> sb4Files = new ArrayList<>();

  public static final String COMMAND = "command";
  private String command;

  public static final String SUCCESSFUL_EXIT_CODES = "successfulExitCodes";
  private List<Integer> successfulExitCodes = new ArrayList<>();

  public static final String ON_ERROR_COMMANDS = "onErrorCommands";
  private List<SB4Command> onErrorCommands = new ArrayList<>();


  public SB4Command id(UUID id) {
    
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


  public SB4Command commandKind(CommandKind commandKind) {
    
    this.commandKind = commandKind;
    return this;
  }

   /**
   * Get commandKind
   * @return commandKind
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(COMMAND_KIND)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public CommandKind getCommandKind() {
    return commandKind;
  }


  @JsonProperty(COMMAND_KIND)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setCommandKind(CommandKind commandKind) {
    this.commandKind = commandKind;
  }


  public SB4Command restUrl(URI restUrl) {
    
    this.restUrl = restUrl;
    return this;
  }

   /**
   * Get restUrl
   * @return restUrl
  **/
  @javax.annotation.Nullable
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(REST_URL)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public URI getRestUrl() {
    return restUrl;
  }


  @JsonProperty(REST_URL)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setRestUrl(URI restUrl) {
    this.restUrl = restUrl;
  }


  public SB4Command sb4Files(List<SB4File> sb4Files) {
    
    this.sb4Files = sb4Files;
    return this;
  }

  public SB4Command addSb4FilesItem(SB4File sb4FilesItem) {
    this.sb4Files.add(sb4FilesItem);
    return this;
  }

   /**
   * Get sb4Files
   * @return sb4Files
  **/
  @javax.annotation.Nullable
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(SB4_FILES)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<SB4File> getSb4Files() {
    return sb4Files;
  }


  @JsonProperty(SB4_FILES)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setSb4Files(List<SB4File> sb4Files) {
    this.sb4Files = sb4Files;
  }


  public SB4Command command(String command) {
    
    this.command = command;
    return this;
  }

   /**
   * Get command
   * @return command
  **/
  @javax.annotation.Nullable
  @NotNull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(COMMAND)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getCommand() {
    return command;
  }


  @JsonProperty(COMMAND)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setCommand(String command) {
    this.command = command;
  }


  public SB4Command successfulExitCodes(List<Integer> successfulExitCodes) {
    
    this.successfulExitCodes = successfulExitCodes;
    return this;
  }

  public SB4Command addSuccessfulExitCodesItem(Integer successfulExitCodesItem) {
    this.successfulExitCodes.add(successfulExitCodesItem);
    return this;
  }

   /**
   * Get successfulExitCodes
   * @return successfulExitCodes
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(SUCCESSFUL_EXIT_CODES)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<Integer> getSuccessfulExitCodes() {
    return successfulExitCodes;
  }


  @JsonProperty(SUCCESSFUL_EXIT_CODES)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setSuccessfulExitCodes(List<Integer> successfulExitCodes) {
    this.successfulExitCodes = successfulExitCodes;
  }


  public SB4Command onErrorCommands(List<SB4Command> onErrorCommands) {
    
    this.onErrorCommands = onErrorCommands;
    return this;
  }

  public SB4Command addOnErrorCommandsItem(SB4Command onErrorCommandsItem) {
    this.onErrorCommands.add(onErrorCommandsItem);
    return this;
  }

   /**
   * Get onErrorCommands
   * @return onErrorCommands
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(ON_ERROR_COMMANDS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<SB4Command> getOnErrorCommands() {
    return onErrorCommands;
  }


  @JsonProperty(ON_ERROR_COMMANDS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setOnErrorCommands(List<SB4Command> onErrorCommands) {
    this.onErrorCommands = onErrorCommands;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SB4Command sb4Command = (SB4Command) o;
    return Objects.equals(this.id, sb4Command.id) &&
        Objects.equals(this.commandKind, sb4Command.commandKind) &&
        Objects.equals(this.restUrl, sb4Command.restUrl) &&
        Objects.equals(this.sb4Files, sb4Command.sb4Files) &&
        Objects.equals(this.command, sb4Command.command) &&
        Objects.equals(this.successfulExitCodes, sb4Command.successfulExitCodes) &&
        Objects.equals(this.onErrorCommands, sb4Command.onErrorCommands);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, commandKind, restUrl, sb4Files, command, successfulExitCodes, onErrorCommands);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SB4Command {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    commandKind: ").append(toIndentedString(commandKind)).append("\n");
    sb.append("    restUrl: ").append(toIndentedString(restUrl)).append("\n");
    sb.append("    sb4Files: ").append(toIndentedString(sb4Files)).append("\n");
    sb.append("    command: ").append(toIndentedString(command)).append("\n");
    sb.append("    successfulExitCodes: ").append(toIndentedString(successfulExitCodes)).append("\n");
    sb.append("    onErrorCommands: ").append(toIndentedString(onErrorCommands)).append("\n");
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

