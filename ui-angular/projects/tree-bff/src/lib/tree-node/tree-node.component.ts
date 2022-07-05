import { Component, Input, OnInit } from '@angular/core';
import { TreeNode } from '../core/api/tree';

@Component({
  selector: 'smartbit4all-bffapi-tree-node',
  templateUrl: './tree-node.component.html',
  styleUrls: ['./tree-node.component.css'],
})
export class TreeNodeComponent implements OnInit {
  @Input() uuid!: string;
  @Input('treeNodes') rootNodes!: TreeNode[];
  @Input() level!: number;
  @Input() isEditMode!: boolean;

  constructor() {}

  ngOnInit(): void {}

  onRowClick(treeNode: TreeNode): void {}

  getLevel(level: number, expanded: any): string {
    if (level >= 4) {
      return 'level4';
    } else if (expanded) {
      return 'level' + (level + 1);
    } else {
      return 'level' + level;
    }
  }

  handleOpen(treeNode: TreeNode, event: Event): void {
    event.stopPropagation();
    treeNode.expanded = !treeNode.expanded;
    // TODO api call
  }

  isNodeHasAttachment(treeNode: TreeNode): boolean {
    //TODO get node uuid and return true if policy has attachment
    return treeNode.styles?.some((s) => s === 'tree-node-has-attachment')!;
  }
}
