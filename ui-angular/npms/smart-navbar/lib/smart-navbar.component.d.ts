import { OnInit } from '@angular/core';
import { SmartNavbar } from './smart-navbar.model';
import * as i0 from "@angular/core";
export declare class SmartNavbarComponent implements OnInit {
    smartNavbar: SmartNavbar;
    constructor();
    ngOnInit(): void;
    onIconClick(): void;
    openFilters(): void;
    onNotificationClick(): void;
    static ɵfac: i0.ɵɵFactoryDeclaration<SmartNavbarComponent, never>;
    static ɵcmp: i0.ɵɵComponentDeclaration<SmartNavbarComponent, "smart-navbar", never, { "smartNavbar": "smartNavbar"; }, {}, never, ["[titleComponent]"]>;
}
