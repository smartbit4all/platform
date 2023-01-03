package org.smartbit4all.api.uitree.restserver;

import org.smartbit4all.api.uitree.bean.SmartTreeNode;
import java.util.UUID;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Generated;

/**
 * A delegate to be called by the {@link TreeApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface TreeApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /{treeId}/{viewUuid}/{treeId}/{nodeId}/collapse
     * Collapses the given treenode. The viewcontext will hold the appropriate instruction  for the ui. Returns the collapsed SmartTreeNode. 
     *
     * @param viewUuid  (required)
     * @param treeId  (required)
     * @param nodeId  (required)
     * @return Collapsed SmartTreeNode object (status code 200)
     * @see TreeApi#collapseNode
     */
    default ResponseEntity<SmartTreeNode> collapseNode(UUID viewUuid,
        String treeId,
        String nodeId) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"identifier\" : \"identifier\", \"expanded\" : true, \"level\" : 0, \"hasChildren\" : true, \"classes\" : [ \"classes\", \"classes\" ], \"icon\" : \"icon\", \"childrenNodes\" : [ null, null ], \"caption\" : \"caption\", \"shortDescription\" : \"shortDescription\", \"nodeType\" : \"nodeType\", \"actions\" : [ \"\", \"\" ], \"selected\" : true }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /{treeId}/{viewUuid}/{treeId}/{nodeId}/expand
     * Expands the given treenode. The viewcontext will hold the appropriate instruction  for the ui, if any. Returns the expanded SmartTreeNode. 
     *
     * @param viewUuid  (required)
     * @param treeId  (required)
     * @param nodeId  (required)
     * @return Expanded SmartTreeNode object (status code 200)
     * @see TreeApi#expandNode
     */
    default ResponseEntity<SmartTreeNode> expandNode(UUID viewUuid,
        String treeId,
        String nodeId) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"identifier\" : \"identifier\", \"expanded\" : true, \"level\" : 0, \"hasChildren\" : true, \"classes\" : [ \"classes\", \"classes\" ], \"icon\" : \"icon\", \"childrenNodes\" : [ null, null ], \"caption\" : \"caption\", \"shortDescription\" : \"shortDescription\", \"nodeType\" : \"nodeType\", \"actions\" : [ \"\", \"\" ], \"selected\" : true }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /{treeId}/{viewUuid}/{treeId}/{nodeId}
     * Query the child nodes of the tree&#39;s node given in the parameter
     *
     * @param viewUuid  (required)
     * @param treeId  (required)
     * @param nodeId  (required)
     * @return List of SmartTreeNode objects (status code 200)
     * @see TreeApi#getChildrenNodes
     */
    default ResponseEntity<List<SmartTreeNode>> getChildrenNodes(UUID viewUuid,
        String treeId,
        String nodeId) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"identifier\" : \"identifier\", \"expanded\" : true, \"level\" : 0, \"hasChildren\" : true, \"classes\" : [ \"classes\", \"classes\" ], \"icon\" : \"icon\", \"childrenNodes\" : [ null, null ], \"caption\" : \"caption\", \"shortDescription\" : \"shortDescription\", \"nodeType\" : \"nodeType\", \"actions\" : [ \"\", \"\" ], \"selected\" : true }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /{treeId}/{viewUuid}/{treeId}/action
     * Query all tree nodes.
     *
     * @param viewUuid  (required)
     * @param treeId  (required)
     * @return List of UiAction objects (status code 200)
     * @see TreeApi#getMainActions
     */
    default ResponseEntity<List<UiAction>> getMainActions(UUID viewUuid,
        String treeId) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "\"\"";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /{treeId}/{viewUuid}/{treeId}
     * Query all tree nodes.
     *
     * @param viewUuid  (required)
     * @param treeId  (required)
     * @return List of SmartTreeNode objects (status code 200)
     * @see TreeApi#getRootNodes
     */
    default ResponseEntity<List<SmartTreeNode>> getRootNodes(UUID viewUuid,
        String treeId) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"identifier\" : \"identifier\", \"expanded\" : true, \"level\" : 0, \"hasChildren\" : true, \"classes\" : [ \"classes\", \"classes\" ], \"icon\" : \"icon\", \"childrenNodes\" : [ null, null ], \"caption\" : \"caption\", \"shortDescription\" : \"shortDescription\", \"nodeType\" : \"nodeType\", \"actions\" : [ \"\", \"\" ], \"selected\" : true }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /{treeId}/{viewUuid}/{treeId}/{nodeId}/action
     * Performs an action on the given treenode. The viewcontext will hold the appropriate instruction  for the ui. Returns the SmartTreeNode which the action was performed on. 
     *
     * @param viewUuid  (required)
     * @param treeId  (required)
     * @param nodeId  (required)
     * @param body  (required)
     * @return Changed tree state. (status code 200)
     * @see TreeApi#performAction
     */
    default ResponseEntity<List<SmartTreeNode>> performAction(UUID viewUuid,
        String treeId,
        String nodeId,
        UiActionRequest body) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"identifier\" : \"identifier\", \"expanded\" : true, \"level\" : 0, \"hasChildren\" : true, \"classes\" : [ \"classes\", \"classes\" ], \"icon\" : \"icon\", \"childrenNodes\" : [ null, null ], \"caption\" : \"caption\", \"shortDescription\" : \"shortDescription\", \"nodeType\" : \"nodeType\", \"actions\" : [ \"\", \"\" ], \"selected\" : true }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /{treeId}/{viewUuid}/{treeId}/action
     * Performs an action on the given treenode. The viewcontext will hold the appropriate instruction  for the ui. Returns the SmartTreeNode which the action was performed on. 
     *
     * @param viewUuid  (required)
     * @param treeId  (required)
     * @param body  (required)
     * @return Changed tree state. (status code 200)
     * @see TreeApi#performMainAction
     */
    default ResponseEntity<List<SmartTreeNode>> performMainAction(UUID viewUuid,
        String treeId,
        UiActionRequest body) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"identifier\" : \"identifier\", \"expanded\" : true, \"level\" : 0, \"hasChildren\" : true, \"classes\" : [ \"classes\", \"classes\" ], \"icon\" : \"icon\", \"childrenNodes\" : [ null, null ], \"caption\" : \"caption\", \"shortDescription\" : \"shortDescription\", \"nodeType\" : \"nodeType\", \"actions\" : [ \"\", \"\" ], \"selected\" : true }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /{treeId}/{viewUuid}/{treeId}/{nodeId}/select
     * Collapses the given treenode. The viewcontext will hold the appropriate instruction  for the ui. Returns the collapsed SmartTreeNode. 
     *
     * @param viewUuid  (required)
     * @param treeId  (required)
     * @param nodeId  (required)
     * @return Collapsed SmartTreeNode object (status code 200)
     * @see TreeApi#selectNode
     */
    default ResponseEntity<SmartTreeNode> selectNode(UUID viewUuid,
        String treeId,
        String nodeId) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"identifier\" : \"identifier\", \"expanded\" : true, \"level\" : 0, \"hasChildren\" : true, \"classes\" : [ \"classes\", \"classes\" ], \"icon\" : \"icon\", \"childrenNodes\" : [ null, null ], \"caption\" : \"caption\", \"shortDescription\" : \"shortDescription\", \"nodeType\" : \"nodeType\", \"actions\" : [ \"\", \"\" ], \"selected\" : true }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
