package org.smartbit4all.api.navigation;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.smartbit4all.api.navigation.bean.NavigationAssociationMeta;
import org.smartbit4all.core.utility.StringConstant;

public class NavigationConfig {

  private Map<URI, List<URI>> assocMetaUrisByEntryMetaUri;
  private Map<URI, AssocNodeConfig> assocConfigsByUri;

  private NavigationConfig(ConfigBuilder builder) {
    assocMetaUrisByEntryMetaUri = new LinkedHashMap<>();
    assocConfigsByUri = new HashMap<>();
    builder.assocConfigsByAssocUrisByEntryUri.entrySet().forEach(builderEntry -> {
      URI entryUri = builderEntry.getKey();
      Map<URI, AssocNodeConfig> assocConfigsByAssocUri = builderEntry.getValue();
      List<URI> assocUris = new ArrayList<>(assocConfigsByAssocUri.keySet());
      assocMetaUrisByEntryMetaUri.put(entryUri, assocUris);
      assocConfigsByUri.putAll(assocConfigsByAssocUri);
    });
  }

  public List<URI> getAssocMetaUris(URI entryMetaUri) {
    checkUriParam(entryMetaUri, "entryMetaUri");
    return assocMetaUrisByEntryMetaUri.get(entryMetaUri);
  }

  public boolean hasEntryMeta(URI entryUri) {
    checkUriParam(entryUri, "entryUri");
    return assocMetaUrisByEntryMetaUri.containsKey(entryUri);
  }

  public boolean isAssocVisible(URI assocUri) {
    checkUriParam(assocUri, "assocUri");
    AssocNodeConfig assocConfig = assocConfigsByUri.get(assocUri);
    return assocConfig == null ? false : assocConfig.isVisible;
  }

  public String getAssocLabel(URI assocUri) {
    checkUriParam(assocUri, "assocUri");
    AssocNodeConfig assocConfig = assocConfigsByUri.get(assocUri);
    return assocConfig == null ? null : assocConfig.label;
  }

  public String getAssocIconKey(URI assocUri) {
    checkUriParam(assocUri, "assocUri");
    AssocNodeConfig assocConfig = assocConfigsByUri.get(assocUri);
    return assocConfig == null ? null : assocConfig.iconKey;
  }

  private void checkUriParam(URI uri, String paramName) {
    Objects.requireNonNull(uri, "Parameter '" + paramName + "' can not be null!");
  }

  public static ConfigBuilder builder() {
    return new ConfigBuilder();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    assocMetaUrisByEntryMetaUri.entrySet().stream()
        .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey())).forEach(e -> {
          if (sb.length() > 0) {
            sb.append(StringConstant.NEW_LINE);
          }
          sb.append(e.getKey());
          List<URI> associations = e.getValue() != null ? e.getValue() : Collections.emptyList();
          for (URI uri : associations) {
            sb.append(StringConstant.NEW_LINE).append(StringConstant.SPACE)
                .append(StringConstant.ARROW).append(StringConstant.SPACE).append(uri);
          }
        });
    return sb.toString();
  }

  public static class ConfigBuilder {

    Map<URI, Map<URI, AssocNodeConfig>> assocConfigsByAssocUrisByEntryUri;

    private ConfigBuilder() {
      assocConfigsByAssocUrisByEntryUri = new LinkedHashMap<>();
    }

    public NavigationConfig build() {
      return new NavigationConfig(this);
    }

    public ConfigEntryBuilder addEntryMeta(URI entryMetaUri) {
      return new ConfigEntryBuilder(entryMetaUri, this);
    }

    public ConfigBuilder addAssociationMeta(NavigationAssociationMeta assocMeta) {
      return addAssociationMeta(assocMeta, false, null, null);
    }

    // TODO create a builder to handle the parameters
    public ConfigBuilder addAssociationMeta(NavigationAssociationMeta assocMeta, boolean isVisible,
        String label, String iconKey) {
      URI entryUri = assocMeta.getStartEntry().getUri();
      URI assocUri = assocMeta.getUri();
      Map<URI, AssocNodeConfig> nodeConfigByAssocUri =
          assocConfigsByAssocUrisByEntryUri.get(entryUri);
      if (nodeConfigByAssocUri == null) {
        nodeConfigByAssocUri = new LinkedHashMap<>();
        assocConfigsByAssocUrisByEntryUri.put(entryUri, nodeConfigByAssocUri);
      }
      AssocNodeConfig assocConfig = new AssocNodeConfig();
      assocConfig.setAssocUri(assocUri);
      assocConfig.setVisible(isVisible);
      assocConfig.setLabel(label);
      assocConfig.setIconKey(iconKey);
      nodeConfigByAssocUri.put(assocUri, assocConfig);
      return this;
    }

    void addEntry(URI entryMetaUri, LinkedHashMap<URI, AssocNodeConfig> assocConfigsByUri) {
      assocConfigsByAssocUrisByEntryUri.put(entryMetaUri, assocConfigsByUri);
    }

  }

  public static class ConfigEntryBuilder {

    private ConfigBuilder configBuilder;
    private URI entryMetaUri;
    private LinkedHashMap<URI, AssocNodeConfig> assocConfigsByUri;

    private ConfigEntryBuilder(URI entryMetaUri, ConfigBuilder configBuilder) {
      this.configBuilder = configBuilder;
      this.entryMetaUri = entryMetaUri;
      this.assocConfigsByUri = new LinkedHashMap<>();
    }

    public ConfigAssocBuilder addAssociationMeta(URI assocMetaUri) {
      return new ConfigAssocBuilder(assocMetaUri, this);
    }

    public ConfigBuilder done() {
      configBuilder.addEntry(entryMetaUri, assocConfigsByUri);
      return configBuilder;
    }

    void addAssoc(URI assocMetaUri, AssocNodeConfig assocConfig) {
      assocConfigsByUri.put(assocMetaUri, assocConfig);
    }

    public NavigationConfig build() {
      return done().build();
    }

  }

  public static class ConfigAssocBuilder {

    private ConfigEntryBuilder entryBuilder;
    private URI assocMetaUri;
    private AssocNodeConfig assocConfig;

    private ConfigAssocBuilder(URI assocMetaUri, ConfigEntryBuilder entryBuilder) {
      this.entryBuilder = entryBuilder;
      this.assocMetaUri = assocMetaUri;
      this.assocConfig = new AssocNodeConfig();
      assocConfig.setAssocUri(assocMetaUri);
    }

    public ConfigAssocBuilder visible(boolean visible) {
      assocConfig.setVisible(visible);
      return this;
    }

    public ConfigAssocBuilder label(String label) {
      assocConfig.setLabel(label);
      return this;
    }

    public ConfigAssocBuilder iconKey(String iconKey) {
      assocConfig.setIconKey(iconKey);
      return this;
    }

    public ConfigEntryBuilder done() {
      entryBuilder.addAssoc(assocMetaUri, assocConfig);
      return entryBuilder;
    }

    public NavigationConfig build() {
      return done().build();
    }
  }

  static class AssocNodeConfig {

    private boolean isVisible;
    private String iconKey;
    private String label;
    private URI assocUri;

    public AssocNodeConfig() {}

    public boolean isVisible() {
      return isVisible;
    }

    public void setVisible(boolean isVisible) {
      this.isVisible = isVisible;
    }

    public String getIconKey() {
      return iconKey;
    }

    public void setIconKey(String iconKey) {
      this.iconKey = iconKey;
    }

    public String getLabel() {
      return label;
    }

    public void setLabel(String label) {
      this.label = label;
    }

    public URI getAssocUri() {
      return assocUri;
    }

    public void setAssocUri(URI assocUri) {
      this.assocUri = assocUri;
    }

  }

}
