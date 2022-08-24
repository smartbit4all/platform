import * as i0 from '@angular/core';
import { Injectable, NgModule } from '@angular/core';
import * as i1 from '@angular/router';

class SmartNavigationService {
    constructor(router) {
        this.router = router;
    }
    navigateTo(urls, queryParams, queryParamsHandling, relativeToThis) {
        let navigationExtras = {
            queryParams,
            queryParamsHandling,
            relativeTo: relativeToThis ? this.route : undefined,
        };
        this.router.navigate(urls, navigationExtras);
    }
    subscribeToUrlChange(callback) {
        if (this.route) {
            return this.route.url.subscribe((urlSegments) => {
                let url = this.getCurrentPath();
                if (callback) {
                    callback(url);
                }
            });
        }
        else {
            throw new Error("The route: ActivatedRoute has not been set yet.");
        }
    }
    subscribeToQueryChange(callback) {
        if (this.route) {
            this.querySubscription = this.route.queryParams.subscribe((params) => {
                callback(params);
            });
        }
        else {
            throw new Error("The route: ActivatedRoute has not been set yet.");
        }
    }
    unsubscribeFromQueryChanges() {
        if (this.querySubscription) {
            this.querySubscription.unsubscribe();
        }
    }
    getCurrentPath() {
        return this.router.url;
    }
}
SmartNavigationService.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartNavigationService, deps: [{ token: i1.Router }], target: i0.ɵɵFactoryTarget.Injectable });
SmartNavigationService.ɵprov = i0.ɵɵngDeclareInjectable({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartNavigationService, providedIn: "root" });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartNavigationService, decorators: [{
            type: Injectable,
            args: [{
                    providedIn: "root",
                }]
        }], ctorParameters: function () { return [{ type: i1.Router }]; } });

class SmartNavigationModule {
}
SmartNavigationModule.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartNavigationModule, deps: [], target: i0.ɵɵFactoryTarget.NgModule });
SmartNavigationModule.ɵmod = i0.ɵɵngDeclareNgModule({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartNavigationModule });
SmartNavigationModule.ɵinj = i0.ɵɵngDeclareInjector({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartNavigationModule, providers: [SmartNavigationService], imports: [[]] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartNavigationModule, decorators: [{
            type: NgModule,
            args: [{
                    declarations: [],
                    imports: [],
                    exports: [],
                    providers: [SmartNavigationService],
                }]
        }] });

/*
 * Public API Surface of smart-navigation
 */

/**
 * Generated bundle index. Do not edit.
 */

export { SmartNavigationModule, SmartNavigationService };
//# sourceMappingURL=smartbit4all-navigation.mjs.map
