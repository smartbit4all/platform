import { Component, Input, OnInit } from '@angular/core';
import '@vaadin/icon';
import { ViewModelImpl } from 'smartbit4all-viewmodel';
import { TreeModel, TreeNode } from '../core/api/tree';

@Component({
  selector: 'smartbit4all-tree-node',
  templateUrl: './tree-node.component.html',
  styleUrls: ['./tree-node.component.css']
})
export class Smartbit4allTreeNodeComponent implements OnInit {

  @Input() viewModel!: ViewModelImpl<TreeModel>;
  @Input('treeNodes') rootNodes?: TreeNode[];
  @Input('level') level: number = 0;
  @Input('isEditMode') isEditMode: boolean = false;

  @Input() uuid!: string;

  constructor() {
  }

  ngOnInit(): void {
  }

  handleOpen(treeNode: TreeNode, event: Event): void {
    event.stopPropagation();
    treeNode.expanded = !treeNode.expanded;

    this.viewModel.executeCommandWithoutModel("tree.toggle", treeNode.identifier);
  }

  onRowClick(treeNode: TreeNode): void {
    this.viewModel.executeCommandWithoutModel("tree.select", treeNode.identifier);
  }

  isVisible(treeNode: TreeNode): boolean {
    // TODO: "hatálytalan" and "inaktív mappák" categories can't be seen in not editor mode
    // return !this.isEditMode && (treeNode.type! === this.category || treeNode.type! === this.policyEntry) || this.isEditMode;
    return true;
  }

  getLevel(level: number, expanded: any): string {
    if (level >= 4) {
      return 'level4';
    }
    else if (expanded) {
      return 'level' + (level + 1);
    }
    else {
      return 'level' + level;
    }
  }

  isNodeHasAttachment(treeNode: TreeNode): boolean {
    //TODO get node uuid and return true if policy has attachment
    return treeNode.styles?.some(s => s === "tree-node-has-attachment")!;
  }

}
