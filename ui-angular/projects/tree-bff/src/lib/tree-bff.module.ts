import { CommonModule } from "@angular/common";
import { CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { TreeBffComponent } from "./tree-bff.component";
import { TreeNodeComponent } from "./tree-node/tree-node.component";

@NgModule({
    declarations: [TreeBffComponent, TreeNodeComponent],
    imports: [CommonModule, FormsModule],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    exports: [TreeBffComponent],
})
export class TreeBffModule {}
