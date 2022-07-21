import { NestedTreeControl } from "@angular/cdk/tree";
import { OnInit } from "@angular/core";
import { MatTreeNestedDataSource } from "@angular/material/tree";
import { Router } from "@angular/router";
import { TreeModel, TreeNode } from "./core/api/tree";
import { TreeStyle } from "./smarttree.node.model";
import * as i0 from "@angular/core";
export declare class SmartTreeComponent implements OnInit {
    private router;
    treeControl: NestedTreeControl<TreeNode, TreeNode>;
    dataSource: MatTreeNestedDataSource<TreeNode>;
    tempActiveNode?: TreeNode;
    treeData: TreeModel;
    treeStyle?: TreeStyle;
    constructor(router: Router);
    ngOnInit(): void;
    onNodeClick(node: TreeNode): void;
    hasChild: (_: number, node: TreeNode) => boolean | undefined;
    getNodeStyle(node: TreeNode): {};
    static ɵfac: i0.ɵɵFactoryDeclaration<SmartTreeComponent, never>;
    static ɵcmp: i0.ɵɵComponentDeclaration<SmartTreeComponent, "smart-tree", never, { "treeData": "treeData"; "treeStyle": "treeStyle"; }, {}, never, never>;
}
