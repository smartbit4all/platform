import { OnInit } from "@angular/core";
import { SmartToolbar, ToolbarButtonStyle, ToolbarDirection } from "./smart-toolbar.model";
import * as i0 from "@angular/core";
export declare class SmartToolbarComponent implements OnInit {
    toolbar: SmartToolbar;
    toolbarDirection: typeof ToolbarDirection;
    ToolbarButtonStyle: typeof ToolbarButtonStyle;
    constructor();
    ngOnInit(): void;
    static ɵfac: i0.ɵɵFactoryDeclaration<SmartToolbarComponent, never>;
    static ɵcmp: i0.ɵɵComponentDeclaration<SmartToolbarComponent, "smart-toolbar", never, { "toolbar": "toolbar"; }, {}, never, never>;
}
