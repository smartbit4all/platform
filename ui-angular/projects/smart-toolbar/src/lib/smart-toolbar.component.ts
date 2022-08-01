// TODO: a felesleges importokat töröljük
import {
    Component,
    ComponentFactoryResolver,
    Input,
    OnInit,
    ViewChild,
    ViewContainerRef,
} from "@angular/core";
import { SmartToolbar, SmartToolbarButton } from "./smart-toolbar.model";

@Component({
    selector: "smart-toolbar",
    templateUrl: "./smart-toolbar.component.html",
    styleUrls: ["./smart-toolbar.component.css"],
})
export class SmartToolbarComponent implements OnInit {
    @Input() toolbar!: SmartToolbar;
    toolbarDirection = ToolbarDirection;

    constructor() {}

    ngOnInit(): void {}
}

// TODO: ezt kivehetjük a .model.ts-be is?
export enum ToolbarDirection {
    COL,
    ROW,
}
