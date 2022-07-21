import * as i0 from '@angular/core';
import { Injectable, Component, Input, NgModule } from '@angular/core';
import * as i1 from '@angular/router';
import * as i2 from '@angular/material/tabs';
import { MatTabsModule } from '@angular/material/tabs';
import * as i3 from '@angular/common';
import { BrowserModule } from '@angular/platform-browser';

class SmartTabGroupService {
    constructor() { }
}
SmartTabGroupService.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartTabGroupService, deps: [], target: i0.ɵɵFactoryTarget.Injectable });
SmartTabGroupService.ɵprov = i0.ɵɵngDeclareInjectable({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartTabGroupService, providedIn: 'root' });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartTabGroupService, decorators: [{
            type: Injectable,
            args: [{
                    providedIn: 'root'
                }]
        }], ctorParameters: function () { return []; } });

class TabGroupComponent {
    constructor(router) {
        this.router = router;
        this.selectedTabIndex = 0;
    }
    ngOnInit() {
        this.getTabIndex();
    }
    navigateTabContent($event) {
        this.router.navigateByUrl(this.actualPath + "/" + this.tabTiles[$event.index].url);
    }
    getTabIndex() {
        const url = this.router.url.split("?")[0];
        this.selectedTabIndex = this.tabTiles.findIndex((t) => {
            return url.includes(t.url);
        });
    }
}
TabGroupComponent.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: TabGroupComponent, deps: [{ token: i1.Router }], target: i0.ɵɵFactoryTarget.Component });
TabGroupComponent.ɵcmp = i0.ɵɵngDeclareComponent({ minVersion: "12.0.0", version: "13.2.7", type: TabGroupComponent, selector: "smart-tab-group", inputs: { tabTiles: "tabTiles", actualPath: "actualPath" }, ngImport: i0, template: "<div class=\"sm-tab-group-container\">\n\t<mat-tab-group\n\t\tclass=\"sm-tab-group\"\n\t\t(selectedTabChange)=\"navigateTabContent($event)\"\n\t\t[(selectedIndex)]=\"selectedTabIndex\"\n\t>\n\t\t<mat-tab\n\t\t\tclass=\"sm-tab\"\n\t\t\tappearance=\"outline\"\n\t\t\t*ngFor=\"let tabTile of tabTiles\"\n\t\t\tlabel=\"{{ tabTile.tileName }}\"\n\t\t>\n\t\t</mat-tab>\n\t</mat-tab-group>\n\t<div class=\"sm-tab-content-container\">\n\t\t<ng-content></ng-content>\n\t</div>\n</div>\n", styles: [".sm-tab-group-container{margin:50px}.sm-tab-group ::ng-deep .mat-tab-label{background-color:#f9fafb;margin-left:10px;border-radius:10px;opacity:100!important}.mat-tab-body-content{border-radius:10px}.mat-tab-nav-bar,.mat-tab-header{border-bottom:0}.tab-content-container{padding:2em;border:1px solid #e6e6e6;box-shadow:0 4px 8px -4px #1a1a1a33;border-radius:4px;background-color:#fff}.mat-tab-label-active{background-color:#00a8da!important;opacity:1!important}\n"], components: [{ type: i2.MatTabGroup, selector: "mat-tab-group", inputs: ["color", "disableRipple"], exportAs: ["matTabGroup"] }, { type: i2.MatTab, selector: "mat-tab", inputs: ["disabled", "label", "aria-label", "aria-labelledby", "labelClass", "bodyClass"], exportAs: ["matTab"] }], directives: [{ type: i3.NgForOf, selector: "[ngFor][ngForOf]", inputs: ["ngForOf", "ngForTrackBy", "ngForTemplate"] }] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: TabGroupComponent, decorators: [{
            type: Component,
            args: [{ selector: "smart-tab-group", template: "<div class=\"sm-tab-group-container\">\n\t<mat-tab-group\n\t\tclass=\"sm-tab-group\"\n\t\t(selectedTabChange)=\"navigateTabContent($event)\"\n\t\t[(selectedIndex)]=\"selectedTabIndex\"\n\t>\n\t\t<mat-tab\n\t\t\tclass=\"sm-tab\"\n\t\t\tappearance=\"outline\"\n\t\t\t*ngFor=\"let tabTile of tabTiles\"\n\t\t\tlabel=\"{{ tabTile.tileName }}\"\n\t\t>\n\t\t</mat-tab>\n\t</mat-tab-group>\n\t<div class=\"sm-tab-content-container\">\n\t\t<ng-content></ng-content>\n\t</div>\n</div>\n", styles: [".sm-tab-group-container{margin:50px}.sm-tab-group ::ng-deep .mat-tab-label{background-color:#f9fafb;margin-left:10px;border-radius:10px;opacity:100!important}.mat-tab-body-content{border-radius:10px}.mat-tab-nav-bar,.mat-tab-header{border-bottom:0}.tab-content-container{padding:2em;border:1px solid #e6e6e6;box-shadow:0 4px 8px -4px #1a1a1a33;border-radius:4px;background-color:#fff}.mat-tab-label-active{background-color:#00a8da!important;opacity:1!important}\n"] }]
        }], ctorParameters: function () { return [{ type: i1.Router }]; }, propDecorators: { tabTiles: [{
                type: Input
            }], actualPath: [{
                type: Input
            }] } });

class SmartTabGroupModule {
}
SmartTabGroupModule.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartTabGroupModule, deps: [], target: i0.ɵɵFactoryTarget.NgModule });
SmartTabGroupModule.ɵmod = i0.ɵɵngDeclareNgModule({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartTabGroupModule, declarations: [TabGroupComponent], imports: [BrowserModule, MatTabsModule], exports: [TabGroupComponent] });
SmartTabGroupModule.ɵinj = i0.ɵɵngDeclareInjector({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartTabGroupModule, imports: [[BrowserModule, MatTabsModule]] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartTabGroupModule, decorators: [{
            type: NgModule,
            args: [{
                    declarations: [TabGroupComponent],
                    imports: [BrowserModule, MatTabsModule],
                    exports: [TabGroupComponent],
                }]
        }] });

/*
 * Public API Surface of smart-tab-group
 */

/**
 * Generated bundle index. Do not edit.
 */

export { SmartTabGroupModule, SmartTabGroupService, TabGroupComponent };
//# sourceMappingURL=smartbit4all-tab-group.mjs.map
