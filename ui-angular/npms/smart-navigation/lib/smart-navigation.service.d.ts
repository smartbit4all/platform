import { ActivatedRoute, Params, QueryParamsHandling, Router } from "@angular/router";
import { Subscription } from "rxjs";
import { SmartNavigation } from "./smart-navigation.interface";
import * as i0 from "@angular/core";
export declare class SmartNavigationService implements SmartNavigation {
    private router;
    route?: ActivatedRoute;
    constructor(router: Router);
    urlSubscription?: Subscription;
    querySubscription?: Subscription;
    navigateTo(urls: string[], queryParams?: Params | undefined, queryParamsHandling?: QueryParamsHandling | undefined, relativeToThis?: boolean): void;
    subscribeToUrlChange(callback: (url: string) => void): Subscription;
    subscribeToQueryChange(callback: (params: Params) => void): void;
    unsubscribeFromQueryChanges(): void;
    getCurrentPath(): string;
    static ɵfac: i0.ɵɵFactoryDeclaration<SmartNavigationService, never>;
    static ɵprov: i0.ɵɵInjectableDeclaration<SmartNavigationService>;
}
