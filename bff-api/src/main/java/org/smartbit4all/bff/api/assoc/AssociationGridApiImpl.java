package org.smartbit4all.bff.api.assoc;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.collection.StoredList;
import org.smartbit4all.api.filterexpression.bean.SearchPageConfig;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.grid.bean.GridRow;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.object.bean.RefObject;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.ViewApi;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewType;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.bff.api.search.SearchPageApi;
import org.smartbit4all.bff.api.search.SearchPageApiImpl;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectNodeList;
import org.smartbit4all.core.object.ObjectNodeReference;
import org.smartbit4all.domain.data.TableData;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class AssociationGridApiImpl implements AssociationGridApi, InitializingBean {

  private static final Logger log = LoggerFactory.getLogger(AssociationGridApiImpl.class);

  @Autowired(required = false)
  private List<AssociationGridConfig> associationGridConfigs;
  private Map<String, AssociationGridConfig> configsByName;

  @Autowired
  private GridModelApi gridModelApi;
  @Autowired
  private InvocationApi invocationApi;
  @Autowired
  private ObjectApi objectApi;
  @Autowired
  private CollectionApi collectionApi;
  @Autowired
  private ViewApi viewApi;

  @Autowired
  private MasterDataManagementApi masterDataManagementApi;

  private static UiAction newElementAction(String configName) {
    return new UiAction()
        .code("ADD")
        .identifier(configName)
        .path(configName);
  }

  private static UiAction deleteElementAction(String configName) {
    return new UiAction()
        .code("DELETE")
        .path(configName);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    configsByName = (associationGridConfigs == null)
        ? new HashMap<>()
        : associationGridConfigs.stream()
            .collect(toMap(AssociationGridConfig::name, Function.identity()));
  }

  @Override
  public void initGridInView(View view, String associationConf, URI objectUri,
      String gridIdentifier) {
    final ObjectNode objectNode = objectApi.load(objectUri);
    initGridInView(view, associationConf, objectNode, gridIdentifier);
  }

  @Override
  public void initGridInView(View view, String associationConf, ObjectNode objectNode,
      String gridIdentifier) {
    final UUID viewUuid = view.getUuid();
    final AssociationGridConfig config = configsByName.get(associationConf);
    if (config == null) {
      log.error(
          "Attempted to initialise [ Grid: {} ] in [ View: {} - {} ] with [ Configuration: {} ], "
              + "but the referenced configuration is unknown!",
          gridIdentifier, view.getViewName(), viewUuid, associationConf);
      return;
    }

    final SearchIndex<RefObject> searchIndex = config.searchIndex(collectionApi);
    final GridModel gridModel = gridModelApi.createGridModel(
        searchIndex.getDefinition().getDefinition(),
        config.columnsToInclude());
    gridModel.getView().setOrderedColumnNames(config.columnsToShow());
    gridModelApi.initGridInView(viewUuid, gridIdentifier, gridModel);

    if (config.onGridPageRenderCallback() != null) {
      gridModelApi.addGridPageCallback(viewUuid, gridIdentifier,
          config.onGridSelectionChangedCallback());
    } else {
      gridModelApi.addGridPageCallback(viewUuid, gridIdentifier, invocationApi
          .builder(AssociationGridApi.class)
          .build(a -> a.onGridPageRender(null, config)));
    }
    final List<ObjectNode> nodes =
        getRefObjectNodes(objectNode, config);
    setGridData(gridIdentifier, viewUuid, searchIndex, nodes);

    // we managed to initialise data, set actions now:
    UiActions.add(view, newElementAction(associationConf), deleteElementAction(associationConf));
  }

  private void setGridData(String gridIdentifier, final UUID viewUuid,
      final SearchIndex<RefObject> searchIndex, final List<ObjectNode> nodes) {
    setGridData(gridIdentifier, viewUuid, searchIndex, nodes.stream());
  }

  private void setGridData(String gridIdentifier, final UUID viewUuid,
      final SearchIndex<RefObject> searchIndex, final Stream<ObjectNode> nodes) {
    final TableData<?> refObjectNodes = searchIndex.executeSearchOnNodes(
        nodes,
        null);
    gridModelApi.setData(viewUuid, gridIdentifier, refObjectNodes);
  }

  private List<ObjectNode> getRefObjectNodes(ObjectNode objectNode,
      AssociationGridConfig config) {
    ObjectNodeList refObjectNodeList = getRefObjectNodeList(objectNode, config).childList;
    refObjectNodeList.stream().map(ref -> ref.get());
    return refObjectNodeList == null
        ? Collections.emptyList()
        : refObjectNodeList.nodeStream().collect(toList());
  }

  private ParentAndChildList getRefObjectNodeList(ObjectNode objectNode,
      AssociationGridConfig config) {
    Object o = objectNode;
    ObjectNode parent = null;
    for (String p : config.pathToAssocList()) {
      if (o == null) {
        return null;
      } else if (o instanceof ObjectNode) {
        ObjectNode node = (ObjectNode) o;
        URI uri = node.getObjectUri();
        node = objectApi.loadLatest(uri); // re-draw definition
        parent = node;
        o = node.getValue(p);
      } else if (o instanceof ObjectNodeReference) {
        URI uri = ((ObjectNodeReference) o).getObjectUri();
        if (uri == null) {
          return null;
        }

        ObjectNode node = objectApi.loadLatest(uri);
        parent = node;
        o = node.getValue(p);
      } else if (o instanceof ObjectNodeList) {
        break;
      }
    }
    return new ParentAndChildList(parent, ((ObjectNodeList) o));
  }

  private Stream<URI> streamReferencedObjectUris(List<ObjectNode> refObjectNodes) {
    return refObjectNodes.stream()
        .map(refNode -> refNode.ref(RefObject.REF).getObjectUri())
        .filter(Objects::nonNull);
  }

  @Override
  public boolean isActionSupported(View view, UiActionRequest request) {
    return configsByName.containsKey(request.getIdentifier());
  }

  @Override
  public void performAction(View view, UiActionRequest request) {
    // get the config
    final String configName = request.getIdentifier();
    final AssociationGridConfig config = configsByName.get(configName);
    // get the target MDM entry descriptor
    MDMEntryDescriptor mdmEntryDescriptor =
        masterDataManagementApi.getEntryDescriptor(config.mdmDefinition(), config.descriptorName());
    StoredList list = masterDataManagementApi
        .getApi(config.mdmDefinition(), mdmEntryDescriptor.getName())
        .getList();

    viewApi.showView(new View()
        .viewName(config.getNewAssocDialogName())
        .type(ViewType.DIALOG)
        .putVariablesItem(SearchPageApiImpl.VAR_SEARCHPAGECONFIG, new SearchPageConfig()
            .searchIndexSchema(config.mdmDefinition())
            .searchIndexName(mdmEntryDescriptor.getName()))
        .putParametersItem(SearchPageApi.PARAM_SELECTION_CALLBACK, invocationApi
            .builder(AssociationGridApi.class)
            .build(a -> a.updateGrid(Invocations.listOf(Collections.emptyList(), GridRow.class),
                configName, view.getUuid())))
        .putParametersItem(SearchPageApi.PARAM_URI_LIST, list.uris()));

  }

  @Override
  public void updateGrid(List<GridRow> gridRows, String configName, UUID viewUuid) {
    List<URI> uris = gridRows.stream()
        .map(GridRow::getData)
        .map(data -> ((Map<?, ?>) data).get("uri"))
        .map(u -> objectApi.asType(URI.class, u))
        .collect(toList());
    AssociationGridConfig config = configsByName.get(configName);
    View view = viewApi.getView(viewUuid);
    URI objectUri = view.getObjectUri();
    ObjectNode objectNode = objectApi.loadLatest(objectUri);
    ParentAndChildList refObjectNodeList = getRefObjectNodeList(objectNode, config);
    if (refObjectNodeList == null || refObjectNodeList.parentNode == null) {
      log.error("PANIC");
      return;
    }

    uris.forEach(u -> {
      ObjectNode refObj = objectApi.create("obj-refs", new RefObject());
      refObj.ref(RefObject.REF).set(u);
      refObjectNodeList.childList.add(refObj);
    });
    objectApi.save(refObjectNodeList.parentNode);
    setGridData(configName, viewUuid, config.searchIndex(collectionApi),
        refObjectNodeList.childList.nodeStream());
  }

  @Override
  public GridPage onGridPageRender(GridPage gridPage, AssociationGridConfig config) {
    final List<String> widgetActionCodes = config.supportedWidgetActions();
    final String configName = config.name();
    if (widgetActionCodes != null && !widgetActionCodes.isEmpty() && gridPage.getRows() != null) {
      gridPage.getRows().forEach(row -> row.setActions(widgetActionCodes.stream()
          .map(code -> new UiAction()
              .path(configName)
              .code(code))
          .collect(toList())));
    }
    return gridPage;
  }

  private static final class ParentAndChildList {
    ObjectNode parentNode;
    ObjectNodeList childList;

    private ParentAndChildList(ObjectNode parentNode, ObjectNodeList childList) {
      this.parentNode = parentNode;
      this.childList = childList;
    }
  }

}
