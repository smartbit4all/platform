import { NgModule } from '@angular/core';
import { Smartbit4allTreeComponent } from './tree.component';
import { Smartbit4allTreeNodeComponent } from './tree-node/tree-node.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';


@NgModule({
  declarations: [
    Smartbit4allTreeComponent,
    Smartbit4allTreeNodeComponent
  ],
  imports: [
    CommonModule,
    FormsModule
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  exports: [
    Smartbit4allTreeComponent
  ]
})
export class Smartbit4allTreeModule { }
