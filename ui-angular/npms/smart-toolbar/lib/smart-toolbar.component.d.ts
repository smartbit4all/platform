import { OnInit } from "@angular/core";
import { SmartToolbar, SmartToolbarButton, ToolbarButtonStyle, ToolbarDirection } from "./smart-toolbar.model";
import { SmartToolbarService } from "./smart-toolbar.service";
import * as i0 from "@angular/core";
export declare class SmartToolbarComponent implements OnInit {
    private service;
    toolbar: SmartToolbar;
    toolbarDirection: typeof ToolbarDirection;
    ToolbarButtonStyle: typeof ToolbarButtonStyle;
    constructor(service: SmartToolbarService);
    ngOnInit(): void;
    executeCommand(button: SmartToolbarButton): void;
    static ɵfac: i0.ɵɵFactoryDeclaration<SmartToolbarComponent, never>;
    static ɵcmp: i0.ɵɵComponentDeclaration<SmartToolbarComponent, "smart-toolbar", never, { "toolbar": "toolbar"; }, {}, never, never>;
}
