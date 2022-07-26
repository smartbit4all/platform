import { ComponentFactoryResolver, OnInit, ViewContainerRef } from '@angular/core';
import { SmartToolbarButton } from './smart-toolbar-button/smart-toolbar-button.model';
import * as i0 from "@angular/core";
export declare class SmartToolbarComponent implements OnInit {
    private resolver;
    buttons: SmartToolbarButton[];
    direction?: ToolbarDirection;
    toolbarDirection: typeof ToolbarDirection;
    vcRef?: ViewContainerRef;
    constructor(resolver: ComponentFactoryResolver);
    ngOnInit(): void;
    ngAfterViewInit(): void;
    static ɵfac: i0.ɵɵFactoryDeclaration<SmartToolbarComponent, never>;
    static ɵcmp: i0.ɵɵComponentDeclaration<SmartToolbarComponent, "smart-toolbar", never, { "buttons": "buttons"; "direction": "direction"; }, {}, never, never>;
}
export declare enum ToolbarDirection {
    COL = 0,
    ROW = 1
}
