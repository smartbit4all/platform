package org.smartbit4all.bff.api.assoc;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.object.bean.Association;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectNodeList;
import org.smartbit4all.core.object.ObjectNodeReference;
import org.smartbit4all.domain.data.TableData;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class AssociationGridApiImpl implements AssociationGridApi, InitializingBean {

  private static final Logger log = LoggerFactory.getLogger(AssociationGridApiImpl.class);

  @Autowired(required = false)
  private List<AssociationConfig> associationConfigs;
  private Map<String, AssociationConfig> configsByName;

  @Autowired
  private GridModelApi gridModelApi;
  @Autowired
  private InvocationApi invocationApi;
  @Autowired
  private ObjectApi objectApi;
  @Autowired
  private CollectionApi collectionApi;

  @Override
  public void afterPropertiesSet() throws Exception {
    configsByName = (associationConfigs == null)
        ? new HashMap<>()
        : associationConfigs.stream()
            .collect(toMap(AssociationConfig::name, Function.identity()));
  }



  @Override
  public void initGridInView(UUID viewUuid, String associationConf, URI objectUri,
      String gridIdentifier) {
    final ObjectNode objectNode = objectApi.load(objectUri);
    initGridInView(viewUuid, associationConf, objectNode, gridIdentifier);
  }

  @Override
  public void initGridInView(UUID viewUuid, String associationConf, ObjectNode objectNode,
      String gridIdentifier) {
    final AssociationConfig config = configsByName.get(associationConf);
    if (config == null) {
      log.error(
          "Attempted to initialise [ Grid: {} ] in [ View: {} ] with [ Configuration: {} ], but the referenced configuration is unknown!",
          gridIdentifier, viewUuid, associationConf);
      return;
    }

    final SearchIndex<Association> searchIndex = config.searchIndex(collectionApi);
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
          .build(a -> a.onGridPageRender(null, config.supportedWidgetActions())));
    }
    Object o = objectNode;
    for (String p : config.pathToAssocList()) {
      if (o instanceof ObjectNode) {
        ObjectNode node = (ObjectNode) o;
        URI uri = node.getObjectUri();
        node = objectApi.loadLatest(uri); // re-draw definition
        o = node.getValue(p);
      } else if (o instanceof ObjectNodeReference) {
        URI uri = ((ObjectNodeReference) o).getObjectUri();
        ObjectNode node = objectApi.loadLatest(uri);
        o = node.getValue(p);
      } else if (o instanceof ObjectNodeList) {
        break;
      }
    }

    final List<ObjectNode> nodes = ((ObjectNodeList) o).nodeStream().collect(toList());
    final TableData<?> refObjectNodes = searchIndex.executeSearchOnNodes(nodes.stream(),
        null);
    gridModelApi.setData(viewUuid, gridIdentifier, refObjectNodes);
  }

  @Override
  public GridPage onGridPageRender(GridPage gridPage, List<String> widgetActionCodes) {
    if (widgetActionCodes != null && !widgetActionCodes.isEmpty() && gridPage.getRows() != null) {
      gridPage.getRows().forEach(row -> row.setActions(widgetActionCodes.stream()
          .map(code -> new UiAction().code(code))
          .collect(toList())));
    }
    return gridPage;
  }

}
