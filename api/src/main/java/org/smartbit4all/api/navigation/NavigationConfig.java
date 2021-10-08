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

/**
 * The navigation configuration is a meta class collecting the navigation entries and associations
 * among them. We can use the {@link ConfigBuilder} to setup. This configuration refers to the meta
 * uris defined by the {@link NavigationApi} implementations.
 * 
 * @author Peter Boros
 */
public class NavigationConfig {

  /**
   * If we setup a {@link NavigationConfig} in the spring context (in a Configuration) then it will
   * be available by this name on the {@link NavigationApi}. We can start a new {@link Navigation}
   * by name. If we setup a {@link NavigationConfig} from scratch inline then there is no need to
   * name it! --> The name could be null.
   */
  private String name;

  /**
   * List of available associations for entry uris.
   */
  private Map<URI, List<URI>> assocMetaUrisByEntryMetaUri;

  /**
   * Additional {@link AssocNodeConfig} defined identified by the association uri.
   */
  private Map<URI, AssocNodeConfig> assocConfigsByUri;

  /**
   * Constructor based on the builder object.
   * 
   * @param builder
   */
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

  /**
   * Inside an entry the associations must have a unique name so the name va
   * 
   * @param entryUri
   * @param assocName
   * @return
   */
  public URI findAssocMetaUriByAssocName(URI entryUri, String assocName) {
    if (assocName == null) {
      return null;
    }
    List<URI> assocURIs = assocMetaUrisByEntryMetaUri.get(entryUri);
    if (assocURIs == null) {
      return null;
    }
    for (URI uri : assocURIs) {
      AssocNodeConfig assocNodeConfig = assocConfigsByUri.get(uri);
      if (assocName.toUpperCase().equals(assocNodeConfig.getName())) {
        return uri;
      }
    }
    return null;
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
        // .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
        .forEach(e -> {
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

  /**
   * The builder api to define a {@link NavigationConfig}. It's easier to construct a configuration
   * with this.
   * 
   * @author Peter Boros
   */
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
      assocConfig.setName(assocMeta.getName().toUpperCase());
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

  /**
   * The parameters of the association in the current navigation.
   * 
   * @author Peter Boros
   */
  static class AssocNodeConfig {

    private boolean isVisible;
    private String iconKey;
    private String label;
    private URI assocUri;
    private String name;

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

    protected final String getName() {
      return name;
    }

    protected final void setName(String name) {
      this.name = name;
    }

  }

  public final String getName() {
    return name;
  }

  public final void setName(String name) {
    this.name = name;
  }

}
