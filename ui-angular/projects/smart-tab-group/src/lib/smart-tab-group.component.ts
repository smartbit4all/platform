import { Component, Input, OnInit } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { SmartNavigationService } from "@smartbit4all/navigation";
import { TabTile } from "./tabTile.model";

@Component({
    selector: "smart-tab-group",
    templateUrl: "./smart-tab-group.component.html",
    styleUrls: ["./smart-tab-group.component.css"],
})
export class TabGroupComponent implements OnInit {
    @Input() tabTiles!: TabTile[];
    @Input() route!: ActivatedRoute;
    selectedTabIndex: number = 0;

    constructor(private navigationService: SmartNavigationService) {
        this.navigationService.route = this.route;
    }

    ngOnInit(): void {
        this.getTabIndex();
    }

    navigateTabContent($event: any) {
        if (!this.navigationService.route) {
            this.navigationService.route = this.route;
        }
        this.navigationService.navigateTo(
            [`${this.tabTiles[$event.index].url}`],
            undefined,
            "preserve",
            true
        );
    }

    getTabIndex() {
        const url = this.navigationService.getCurrentPath().split("?")[0];
        this.selectedTabIndex = this.tabTiles.findIndex((t) => {
            let tabUrl: string = t.url;
            if (tabUrl.includes("../")) {
                tabUrl = tabUrl.split("../")[1];
            }
            return url.includes(tabUrl);
        });
    }
}
