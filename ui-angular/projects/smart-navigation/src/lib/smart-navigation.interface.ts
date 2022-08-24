import { Params, QueryParamsHandling } from "@angular/router";
import { Subscription } from "rxjs";

export interface SmartNavigation {
    urlSubscription?: Subscription;
    querySubscription?: Subscription;

    navigateTo(
        urls: string[],
        queryParams?: Params,
        queryParamsHandling?: QueryParamsHandling,
        relativeToThis?: boolean
    ): void;
    subscribeToUrlChange(callback: (url: string) => void): void;
    subscribeToQueryChange(callback: (params: Params) => void): void;
    unsubscribeFromQueryChanges(): void;
    getCurrentPath(): string;
}
