package org.smartbit4all.bff.api.assoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.object.bean.RefObject;
import com.google.common.base.Strings;

public final class AssociationGridConfig {

  public static AssociationGridConfig.Builder create(String name) {
    return new Builder(name);
  }

  private final String name;
  private final String mdmDefinition;
  /** The descriptor enumerating the possible new values for the assoc list. */
  private final MDMEntryDescriptor entryDescriptor;
  private final SearchIndexIdentifier searchIndex;
  /** Where the assoc list is in the domain object. */
  private final String[] pathToAssocList;
  /** Where the assoc list is in the view model. */
  private final String[] assocListInView;
  private final List<String> columnsToInclude;
  private final List<String> columnsToShow;

  private final InvocationRequest onGridPageRenderCallback;
  private final InvocationRequest onGridSelectionChangedCallback;

  private final List<String> supportedWidgetActions;

  private final String newAssocDialogName;

  private AssociationGridConfig(String name,
      String mdmDefinition,
      MDMEntryDescriptor entryDescriptor,
      SearchIndexIdentifier searchIndex,
      String[] pathToAssocList,
      String[] assocListInView,
      List<String> columnsToInclude, List<String> columnsToShow,
      InvocationRequest onGridPageRenderCallback,
      InvocationRequest onGridSelectionChangedCallback,
      List<String> supportedWidgetActions, String newAssocDialogName) {
    this.name = name;
    this.mdmDefinition = mdmDefinition;
    this.entryDescriptor = entryDescriptor;
    this.searchIndex = searchIndex;
    this.pathToAssocList = pathToAssocList;
    this.assocListInView = assocListInView;
    this.columnsToInclude = columnsToInclude;
    this.columnsToShow = columnsToShow;
    this.onGridPageRenderCallback = onGridPageRenderCallback;
    this.onGridSelectionChangedCallback = onGridSelectionChangedCallback;
    this.supportedWidgetActions = supportedWidgetActions;
    this.newAssocDialogName = newAssocDialogName;
  }

  String name() {
    return name;
  }

  String mdmDefinition() {
    return mdmDefinition;
  }

  MDMEntryDescriptor entryDescriptor() {
    return entryDescriptor;
  }

  SearchIndex<RefObject> searchIndex(CollectionApi collectionApi) {
    return collectionApi.searchIndex(searchIndex.schema, searchIndex.name, RefObject.class);
  }

  public String[] pathToAssocList() {
    return Arrays.copyOf(pathToAssocList, pathToAssocList.length);
  }

  public String[] assocListInView() {
    return Arrays.copyOf(assocListInView, assocListInView.length);
  }

  public List<String> columnsToInclude() {
    return new ArrayList<>(columnsToInclude);
  }

  public List<String> columnsToShow() {
    return new ArrayList<>(columnsToShow);
  }

  public InvocationRequest onGridPageRenderCallback() {
    return onGridPageRenderCallback;
  }

  public InvocationRequest onGridSelectionChangedCallback() {
    return onGridSelectionChangedCallback;
  }

  public List<String> supportedWidgetActions() {
    return new ArrayList<>(supportedWidgetActions);
  }

  public String getNewAssocDialogName() {
    return newAssocDialogName;
  }

  static final class SearchIndexIdentifier {

    static SearchIndexIdentifier of(String schema, String name) {
      return new SearchIndexIdentifier(schema, name);
    }

    static SearchIndexIdentifier of(SearchIndex<?> searchIndex) {
      return new SearchIndexIdentifier(searchIndex.logicalSchema(), searchIndex.name());
    }

    private final String schema;
    private final String name;

    SearchIndexIdentifier(String schema, String name) {
      this.schema = Objects.requireNonNull(schema);
      this.name = Objects.requireNonNull(name);
    }
  }

  public static final class Builder {
    private final String name;

    private String mdmDefinition;
    private MDMEntryDescriptor entryDescriptor;
    private SearchIndexIdentifier searchIndex;
    private String[] pathToAssocList;
    private String[] assocListInView;
    private List<String> columnsToInclude;
    private List<String> columnsToShow;

    private InvocationRequest onGridPageRenderCallback;
    private InvocationRequest onGridSelectionChangedCallback;

    private List<String> supportedWidgetActions;

    private String newAssocDialogName;

    private Builder(String name) {
      if (Strings.isNullOrEmpty(name)) {
        throw new IllegalArgumentException("name cannot be null or empty!");
      }
      this.name = name;
    }

    public Builder mdmDefinition(String mdmDefinition) {
      this.mdmDefinition = mdmDefinition;
      return this;
    }

    public Builder entryDescriptor(MDMEntryDescriptor entryDescriptor) {
      this.entryDescriptor = Objects.requireNonNull(entryDescriptor);
      return this;
    }

    public Builder searchIndex(SearchIndex<RefObject> searchIndex) {
      this.searchIndex = SearchIndexIdentifier.of(searchIndex);
      return this;
    }

    public Builder searchIndex(String schema, String name) {
      this.searchIndex = SearchIndexIdentifier.of(schema, name);
      return this;
    }

    public Builder at(String... pathToAssocList) {
      if (pathToAssocList == null || pathToAssocList.length < 1) {
        throw new IllegalArgumentException("pathToAssocList cannot be null or empty!");
      }
      this.pathToAssocList = Arrays.copyOf(pathToAssocList, pathToAssocList.length);
      return this;
    }

    public Builder inViewModel(String... assocListInView) {
      if (assocListInView == null || assocListInView.length < 1) {
        throw new IllegalArgumentException("pathToAssocList cannot be null or empty!");
      }
      this.assocListInView = Arrays.copyOf(assocListInView, assocListInView.length);
      return this;
    }

    public Builder columnsToUse(String... columnsToUse) {
      if (columnsToUse == null || columnsToUse.length < 1) {
        throw new IllegalArgumentException("columnsToUse cannot be null or empty!");
      }
      this.columnsToInclude = Arrays.asList(columnsToUse);
      return this;
    }

    public Builder columnsToShow(String... columnsToShow) {
      if (columnsToShow == null || columnsToShow.length < 1) {
        throw new IllegalArgumentException("columnsToShow cannot be null or empty!");
      }
      this.columnsToShow = Arrays.asList(columnsToShow);
      return this;
    }

    public Builder rowActions(String... rowActionCodes) {
      if (rowActionCodes == null || rowActionCodes.length < 1) {
        throw new IllegalArgumentException("rowActionCodes cannot be null or empty!");
      }
      this.supportedWidgetActions = Arrays.asList(rowActionCodes);
      return this;
    }

    public Builder onGridPageRender(InvocationRequest invocationRequest) {
      this.onGridPageRenderCallback = Objects.requireNonNull(invocationRequest);
      return this;
    }

    public Builder onGridSelectionChanged(InvocationRequest invocationRequest) {
      this.onGridSelectionChangedCallback = Objects.requireNonNull(invocationRequest);
      return this;
    }

    public Builder newAssocDialogName(String newAssocDialogName) {
      this.newAssocDialogName = newAssocDialogName;
      return this;
    }

    public AssociationGridConfig build() {
      return new AssociationGridConfig(
          name, mdmDefinition, entryDescriptor, searchIndex, pathToAssocList, assocListInView,
          columnsToInclude,
          columnsToShow,
          onGridPageRenderCallback, onGridSelectionChangedCallback, supportedWidgetActions,
          newAssocDialogName);
    }
  }

}
