import { NestedTreeControl } from "@angular/cdk/tree";
import { Component, Input, OnInit } from "@angular/core";
import { MatTreeNestedDataSource } from "@angular/material/tree";
import { Router } from "@angular/router";
import { SmartTreeModel, SmartTreeNode } from "./smarttree.model";
import { TreeStyle } from "./smarttree.node.model";

@Component({
    selector: "smart-tree",
    templateUrl: "./smarttree.component.html",
    styleUrls: ["./smarttree.component.css"],
})
export class SmartTreeComponent implements OnInit {
    treeControl = new NestedTreeControl<SmartTreeNode>((node) => node.childrenNodes);
    dataSource = new MatTreeNestedDataSource<SmartTreeNode>();
    tempActiveNode?: SmartTreeNode;
    @Input() treeData!: SmartTreeModel;
    @Input() treeStyle?: TreeStyle;

    constructor(private router: Router) {}
    ngOnInit(): void {
        this.dataSource.data = this.treeData.rootNodes;
    }

    onNodeClick(node: SmartTreeNode) {
        if (this.tempActiveNode === node) {
            this.tempActiveNode.selected = false;
            this.tempActiveNode = undefined;
            node.selected = false;
            return;
        }

        if (this.tempActiveNode) this.tempActiveNode.selected = false;
        node.selected = true;
        this.tempActiveNode = node;

        let navigationUrlByNodeType = this.treeData.navigationUrlsByNodeType.find((nav) => {
            return nav.nodeType === node.nodeType;
        });

        if (navigationUrlByNodeType) {
            this.router.navigate([`${navigationUrlByNodeType.navigationUrl}`], {
                queryParams: { uri: node.objectUri },
            });
        }
    }

    hasChild = (_: number, node: SmartTreeNode) => node.hasChildren;

    getNodeStyle(node: SmartTreeNode) {
        if (this.treeStyle) {
            var style = node.selected
                ? {
                      background: this.treeStyle.activeStyle?.backgroundColor,
                      color: this.treeStyle.activeStyle?.color,
                      "padding-left": 15 * node.level! + "px",
                  }
                : {
                      background: this.treeStyle.levelBackgroundColor![node.level! - 1],
                      color: this.treeStyle.color!,
                      "padding-left": 15 * node.level! + "px",
                  };
            return style;
        }
        return {};
    }
}
