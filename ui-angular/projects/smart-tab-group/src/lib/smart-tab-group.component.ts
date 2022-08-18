import { Component, Input, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { TabTile } from "./tabTile.model";

@Component({
    selector: "smart-tab-group",
    templateUrl: "./smart-tab-group.component.html",
    styleUrls: ["./smart-tab-group.component.css"],
})
export class TabGroupComponent implements OnInit {
    @Input() tabTiles!: TabTile[];
    @Input() actualPath!: string;
    selectedTabIndex: number = 0;

    constructor(private router: Router) {}

    ngOnInit(): void {
        this.getTabIndex();
    }

    navigateTabContent($event: any) {
        this.router.navigate([this.actualPath, this.tabTiles[$event.index].url], {
            queryParamsHandling: "preserve",
        });
    }

    getTabIndex() {
        const url = this.router.url.split("?")[0];
        this.selectedTabIndex = this.tabTiles.findIndex((t) => {
            return url.includes(t.url);
        });
    }
}
