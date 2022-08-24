import { OnInit } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { SmartNavigationService } from "@smartbit4all/navigation";
import { TabTile } from "./tabTile.model";
import * as i0 from "@angular/core";
export declare class TabGroupComponent implements OnInit {
    private navigationService;
    tabTiles: TabTile[];
    route: ActivatedRoute;
    selectedTabIndex: number;
    constructor(navigationService: SmartNavigationService);
    ngOnInit(): void;
    navigateTabContent($event: any): void;
    getTabIndex(): void;
    static ɵfac: i0.ɵɵFactoryDeclaration<TabGroupComponent, never>;
    static ɵcmp: i0.ɵɵComponentDeclaration<TabGroupComponent, "smart-tab-group", never, { "tabTiles": "tabTiles"; "route": "route"; }, {}, never, ["*"]>;
}
