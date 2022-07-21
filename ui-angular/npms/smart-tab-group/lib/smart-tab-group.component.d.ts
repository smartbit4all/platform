import { OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { TabTile } from "./tabTile.model";
import * as i0 from "@angular/core";
export declare class TabGroupComponent implements OnInit {
    private router;
    tabTiles: TabTile[];
    actualPath: string;
    selectedTabIndex: number;
    constructor(router: Router);
    ngOnInit(): void;
    navigateTabContent($event: any): void;
    getTabIndex(): void;
    static ɵfac: i0.ɵɵFactoryDeclaration<TabGroupComponent, never>;
    static ɵcmp: i0.ɵɵComponentDeclaration<TabGroupComponent, "smart-tab-group", never, { "tabTiles": "tabTiles"; "actualPath": "actualPath"; }, {}, never, ["*"]>;
}
