import { Injectable } from "@angular/core";
import {
    ActivatedRoute,
    NavigationExtras,
    Params,
    QueryParamsHandling,
    Router,
    UrlSegment,
} from "@angular/router";
import { Subscription } from "rxjs";
import { SmartNavigation } from "./smart-navigation.interface";

@Injectable({
    providedIn: "root",
})
export class SmartNavigationService implements SmartNavigation {
    route?: ActivatedRoute;

    constructor(private router: Router) {}
    urlSubscription?: Subscription;
    querySubscription?: Subscription;

    navigateTo(
        urls: string[],
        queryParams?: Params | undefined,
        queryParamsHandling?: QueryParamsHandling | undefined,
        relativeToThis?: boolean
    ): void {
        let navigationExtras: NavigationExtras = {
            queryParams,
            queryParamsHandling,
            relativeTo: relativeToThis ? this.route : undefined,
        };
        this.router.navigate(urls, navigationExtras);
    }

    subscribeToUrlChange(callback: (url: string) => void): Subscription {
        if (this.route) {
            return this.route.url.subscribe((urlSegments: UrlSegment[]) => {
                let url = this.getCurrentPath();
                if (callback) {
                    callback(url);
                }
            });
        } else {
            throw new Error("The route: ActivatedRoute has not been set yet.");
        }
    }

    subscribeToQueryChange(callback: (params: Params) => void): void {
        if (this.route) {
            this.querySubscription = this.route.queryParams.subscribe((params: Params) => {
                callback(params);
            });
        } else {
            throw new Error("The route: ActivatedRoute has not been set yet.");
        }
    }

    unsubscribeFromQueryChanges(): void {
        if (this.querySubscription) {
            this.querySubscription.unsubscribe();
        }
    }

    getCurrentPath(): string {
        return this.router.url;
    }
}
