import { OnInit } from '@angular/core';
import { SmartToolbar } from './smart-toolbar.model';
import * as i0 from "@angular/core";
export declare class SmartToolbarComponent implements OnInit {
    toolbar: SmartToolbar;
    toolbarDirection: typeof ToolbarDirection;
    constructor();
    ngOnInit(): void;
    static ɵfac: i0.ɵɵFactoryDeclaration<SmartToolbarComponent, never>;
    static ɵcmp: i0.ɵɵComponentDeclaration<SmartToolbarComponent, "smart-toolbar", never, { "toolbar": "toolbar"; }, {}, never, never>;
}
export declare enum ToolbarDirection {
    COL = 0,
    ROW = 1
}