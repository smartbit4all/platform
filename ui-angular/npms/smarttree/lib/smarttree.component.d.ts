import { NestedTreeControl } from "@angular/cdk/tree";
import { OnInit } from "@angular/core";
import { MatTreeNestedDataSource } from "@angular/material/tree";
import { Router } from "@angular/router";
import { SmartTreeModel, SmartTreeNode } from "./smarttree.model";
import { TreeStyle } from "./smarttree.node.model";
import * as i0 from "@angular/core";
export declare class SmartTreeComponent implements OnInit {
    private router;
    treeControl: NestedTreeControl<SmartTreeNode, SmartTreeNode>;
    dataSource: MatTreeNestedDataSource<SmartTreeNode>;
    tempActiveNode?: SmartTreeNode;
    treeData: SmartTreeModel;
    treeStyle?: TreeStyle;
    constructor(router: Router);
    ngOnInit(): void;
    onNodeClick(node: SmartTreeNode): void;
    hasChild: (_: number, node: SmartTreeNode) => boolean;
    getNodeStyle(node: SmartTreeNode): {};
    static ɵfac: i0.ɵɵFactoryDeclaration<SmartTreeComponent, never>;
    static ɵcmp: i0.ɵɵComponentDeclaration<SmartTreeComponent, "smart-tree", never, { "treeData": "treeData"; "treeStyle": "treeStyle"; }, {}, never, never>;
}
