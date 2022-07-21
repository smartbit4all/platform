import { NestedTreeControl } from "@angular/cdk/tree";
import { Component, Input, OnInit } from "@angular/core";
import { MatTreeNestedDataSource } from "@angular/material/tree";
import { Router } from "@angular/router";
import { TreeModel, TreeNode } from "./core/api/tree";
import { TreeStyle } from "./smarttree.node.model";

@Component({
    selector: "smart-tree",
    templateUrl: "./smarttree.component.html",
    styleUrls: ["./smarttree.component.css"],
})
export class SmartTreeComponent implements OnInit {
    treeControl = new NestedTreeControl<TreeNode>((node) => node.childrenNodes);
    dataSource = new MatTreeNestedDataSource<TreeNode>();
    tempActiveNode?: TreeNode;
    @Input() treeData!: TreeModel;
    @Input() treeStyle?: TreeStyle;

    constructor(private router: Router) {}
    ngOnInit(): void {
        this.dataSource.data = this.treeData.rootNodes;
    }

    onNodeClick(node: TreeNode) {
        if (this.tempActiveNode) this.tempActiveNode.selected = false;
        node.selected = true;
        this.tempActiveNode = node;
        this.router.navigateByUrl(node.objectUri!);
    }

    hasChild = (_: number, node: TreeNode) => node.hasChildren;

    getNodeStyle(node: TreeNode) {
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
