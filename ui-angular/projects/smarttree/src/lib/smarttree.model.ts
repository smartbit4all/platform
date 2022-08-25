export interface SmartTreeModel {
    rootNodes: Array<SmartTreeNode>;
    selectedNodeIdentifier?: string;
    navigationUrlsByNodeType: Array<SmartTreeNodeType>;
}

export interface SmartTreeNode {
    icon?: string;
    caption: string;
    styles?: Array<string>;
    hasChildren: boolean;
    childrenNodes: Array<SmartTreeNode>;
    expanded: boolean;
    selected: boolean;
    childrenNodesLoaded: boolean;
    level: number;

    shortDescription?: string;
    identifier?: string;
    objectUri?: string;

    nodeType: string;
}

export interface SmartTreeNodeType {
    nodeType: string;
    navigationUrl: any[];
}
