import { Component, Input, OnInit } from "@angular/core";
import { TreeModel } from "./core/api/tree";

@Component({
    selector: "smartbit4all-bffapi-tree",
    templateUrl: "./tree-bff.component.html",
    styleUrls: ["./tree-bff.component.css"],
})
export class TreeBffComponent {
    @Input() treeModel!: TreeModel;
    @Input() isEditMode!: boolean;
    @Input() uuid!: string;

    constructor() {}
}
