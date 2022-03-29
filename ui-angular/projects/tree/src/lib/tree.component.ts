import { Component, Input, OnInit } from '@angular/core';
import { ViewModelImpl } from 'smartbit4all-viewmodel';
import { TreeModel } from './core/api/tree';

@Component({
  selector: 'smartbit4all-tree',
  template: `
    <div class="richter-tree">
    <smartbit4all-tree-node [viewModel]="viewModel" [uuid]="uuid" [treeNodes]="viewModel.model.rootNodes" [level]="0"
        [isEditMode]="isEditMode">
    </smartbit4all-tree-node>
</div>
  `,
  styles: [`

  .richter-tree{
    --lumo-font-family: 'Inter';

    --lumo-primary-text-color: hsla(216, 56%, 47%, 1);
    --lumo-primary-color: hsl(216, 56%, 47%);
	  --lumo-primary-color-50pct: hsl(200, 63%, 56%);
	  --lumo-primary-color-10pct:	hsl(202, 81%, 90%);
    --lumo-primary-contrast-color: #FFFFFF;

    /*RICHTER TREE COLORS*/
    --tree-color-level0: transparent;
    --tree-color-level1: hsla(212, 70%, 96%, 1);
    --tree-color-level2: hsla(212, 70%, 94%, 1);
    --tree-color-level3: hsla(212, 70%, 92%, 1);
    --tree-color-level4: hsla(212, 70%, 90%, 1);
    --tree-border-color: hsla(212, 70%, 88%, 1);

    --lumo-tertiary-text-color: #707070;
    border-top-width: 1px;
    border-top-style: solid;
    border-color: var(--tree-border-color);
    min-width: fit-content;
    width: 100%;
}
  `
  ]
})
export class Smartbit4allTreeComponent implements OnInit {

  @Input() viewModel!: ViewModelImpl<TreeModel>;
  @Input('isEditMode') isEditMode: boolean = false;
  @Input() uuid!: string;

  constructor() {
  }

  ngOnInit(): void {
  }

}
