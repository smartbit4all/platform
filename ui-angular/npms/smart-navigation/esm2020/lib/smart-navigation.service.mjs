import { Injectable } from "@angular/core";
import * as i0 from "@angular/core";
import * as i1 from "@angular/router";
export class SmartNavigationService {
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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnQtbmF2aWdhdGlvbi5zZXJ2aWNlLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vLi4vcHJvamVjdHMvc21hcnQtbmF2aWdhdGlvbi9zcmMvbGliL3NtYXJ0LW5hdmlnYXRpb24uc2VydmljZS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQSxPQUFPLEVBQUUsVUFBVSxFQUFFLE1BQU0sZUFBZSxDQUFDOzs7QUFlM0MsTUFBTSxPQUFPLHNCQUFzQjtJQUcvQixZQUFvQixNQUFjO1FBQWQsV0FBTSxHQUFOLE1BQU0sQ0FBUTtJQUFHLENBQUM7SUFJdEMsVUFBVSxDQUNOLElBQWMsRUFDZCxXQUFnQyxFQUNoQyxtQkFBcUQsRUFDckQsY0FBd0I7UUFFeEIsSUFBSSxnQkFBZ0IsR0FBcUI7WUFDckMsV0FBVztZQUNYLG1CQUFtQjtZQUNuQixVQUFVLEVBQUUsY0FBYyxDQUFDLENBQUMsQ0FBQyxJQUFJLENBQUMsS0FBSyxDQUFDLENBQUMsQ0FBQyxTQUFTO1NBQ3RELENBQUM7UUFDRixJQUFJLENBQUMsTUFBTSxDQUFDLFFBQVEsQ0FBQyxJQUFJLEVBQUUsZ0JBQWdCLENBQUMsQ0FBQztJQUNqRCxDQUFDO0lBRUQsb0JBQW9CLENBQUMsUUFBK0I7UUFDaEQsSUFBSSxJQUFJLENBQUMsS0FBSyxFQUFFO1lBQ1osT0FBTyxJQUFJLENBQUMsS0FBSyxDQUFDLEdBQUcsQ0FBQyxTQUFTLENBQUMsQ0FBQyxXQUF5QixFQUFFLEVBQUU7Z0JBQzFELElBQUksR0FBRyxHQUFHLElBQUksQ0FBQyxjQUFjLEVBQUUsQ0FBQztnQkFDaEMsSUFBSSxRQUFRLEVBQUU7b0JBQ1YsUUFBUSxDQUFDLEdBQUcsQ0FBQyxDQUFDO2lCQUNqQjtZQUNMLENBQUMsQ0FBQyxDQUFDO1NBQ047YUFBTTtZQUNILE1BQU0sSUFBSSxLQUFLLENBQUMsaURBQWlELENBQUMsQ0FBQztTQUN0RTtJQUNMLENBQUM7SUFFRCxzQkFBc0IsQ0FBQyxRQUFrQztRQUNyRCxJQUFJLElBQUksQ0FBQyxLQUFLLEVBQUU7WUFDWixJQUFJLENBQUMsaUJBQWlCLEdBQUcsSUFBSSxDQUFDLEtBQUssQ0FBQyxXQUFXLENBQUMsU0FBUyxDQUFDLENBQUMsTUFBYyxFQUFFLEVBQUU7Z0JBQ3pFLFFBQVEsQ0FBQyxNQUFNLENBQUMsQ0FBQztZQUNyQixDQUFDLENBQUMsQ0FBQztTQUNOO2FBQU07WUFDSCxNQUFNLElBQUksS0FBSyxDQUFDLGlEQUFpRCxDQUFDLENBQUM7U0FDdEU7SUFDTCxDQUFDO0lBRUQsMkJBQTJCO1FBQ3ZCLElBQUksSUFBSSxDQUFDLGlCQUFpQixFQUFFO1lBQ3hCLElBQUksQ0FBQyxpQkFBaUIsQ0FBQyxXQUFXLEVBQUUsQ0FBQztTQUN4QztJQUNMLENBQUM7SUFFRCxjQUFjO1FBQ1YsT0FBTyxJQUFJLENBQUMsTUFBTSxDQUFDLEdBQUcsQ0FBQztJQUMzQixDQUFDOzttSEFwRFEsc0JBQXNCO3VIQUF0QixzQkFBc0IsY0FGbkIsTUFBTTsyRkFFVCxzQkFBc0I7a0JBSGxDLFVBQVU7bUJBQUM7b0JBQ1IsVUFBVSxFQUFFLE1BQU07aUJBQ3JCIiwic291cmNlc0NvbnRlbnQiOlsiaW1wb3J0IHsgSW5qZWN0YWJsZSB9IGZyb20gXCJAYW5ndWxhci9jb3JlXCI7XG5pbXBvcnQge1xuICAgIEFjdGl2YXRlZFJvdXRlLFxuICAgIE5hdmlnYXRpb25FeHRyYXMsXG4gICAgUGFyYW1zLFxuICAgIFF1ZXJ5UGFyYW1zSGFuZGxpbmcsXG4gICAgUm91dGVyLFxuICAgIFVybFNlZ21lbnQsXG59IGZyb20gXCJAYW5ndWxhci9yb3V0ZXJcIjtcbmltcG9ydCB7IFN1YnNjcmlwdGlvbiB9IGZyb20gXCJyeGpzXCI7XG5pbXBvcnQgeyBTbWFydE5hdmlnYXRpb24gfSBmcm9tIFwiLi9zbWFydC1uYXZpZ2F0aW9uLmludGVyZmFjZVwiO1xuXG5ASW5qZWN0YWJsZSh7XG4gICAgcHJvdmlkZWRJbjogXCJyb290XCIsXG59KVxuZXhwb3J0IGNsYXNzIFNtYXJ0TmF2aWdhdGlvblNlcnZpY2UgaW1wbGVtZW50cyBTbWFydE5hdmlnYXRpb24ge1xuICAgIHJvdXRlPzogQWN0aXZhdGVkUm91dGU7XG5cbiAgICBjb25zdHJ1Y3Rvcihwcml2YXRlIHJvdXRlcjogUm91dGVyKSB7fVxuICAgIHVybFN1YnNjcmlwdGlvbj86IFN1YnNjcmlwdGlvbjtcbiAgICBxdWVyeVN1YnNjcmlwdGlvbj86IFN1YnNjcmlwdGlvbjtcblxuICAgIG5hdmlnYXRlVG8oXG4gICAgICAgIHVybHM6IHN0cmluZ1tdLFxuICAgICAgICBxdWVyeVBhcmFtcz86IFBhcmFtcyB8IHVuZGVmaW5lZCxcbiAgICAgICAgcXVlcnlQYXJhbXNIYW5kbGluZz86IFF1ZXJ5UGFyYW1zSGFuZGxpbmcgfCB1bmRlZmluZWQsXG4gICAgICAgIHJlbGF0aXZlVG9UaGlzPzogYm9vbGVhblxuICAgICk6IHZvaWQge1xuICAgICAgICBsZXQgbmF2aWdhdGlvbkV4dHJhczogTmF2aWdhdGlvbkV4dHJhcyA9IHtcbiAgICAgICAgICAgIHF1ZXJ5UGFyYW1zLFxuICAgICAgICAgICAgcXVlcnlQYXJhbXNIYW5kbGluZyxcbiAgICAgICAgICAgIHJlbGF0aXZlVG86IHJlbGF0aXZlVG9UaGlzID8gdGhpcy5yb3V0ZSA6IHVuZGVmaW5lZCxcbiAgICAgICAgfTtcbiAgICAgICAgdGhpcy5yb3V0ZXIubmF2aWdhdGUodXJscywgbmF2aWdhdGlvbkV4dHJhcyk7XG4gICAgfVxuXG4gICAgc3Vic2NyaWJlVG9VcmxDaGFuZ2UoY2FsbGJhY2s6ICh1cmw6IHN0cmluZykgPT4gdm9pZCk6IFN1YnNjcmlwdGlvbiB7XG4gICAgICAgIGlmICh0aGlzLnJvdXRlKSB7XG4gICAgICAgICAgICByZXR1cm4gdGhpcy5yb3V0ZS51cmwuc3Vic2NyaWJlKCh1cmxTZWdtZW50czogVXJsU2VnbWVudFtdKSA9PiB7XG4gICAgICAgICAgICAgICAgbGV0IHVybCA9IHRoaXMuZ2V0Q3VycmVudFBhdGgoKTtcbiAgICAgICAgICAgICAgICBpZiAoY2FsbGJhY2spIHtcbiAgICAgICAgICAgICAgICAgICAgY2FsbGJhY2sodXJsKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9KTtcbiAgICAgICAgfSBlbHNlIHtcbiAgICAgICAgICAgIHRocm93IG5ldyBFcnJvcihcIlRoZSByb3V0ZTogQWN0aXZhdGVkUm91dGUgaGFzIG5vdCBiZWVuIHNldCB5ZXQuXCIpO1xuICAgICAgICB9XG4gICAgfVxuXG4gICAgc3Vic2NyaWJlVG9RdWVyeUNoYW5nZShjYWxsYmFjazogKHBhcmFtczogUGFyYW1zKSA9PiB2b2lkKTogdm9pZCB7XG4gICAgICAgIGlmICh0aGlzLnJvdXRlKSB7XG4gICAgICAgICAgICB0aGlzLnF1ZXJ5U3Vic2NyaXB0aW9uID0gdGhpcy5yb3V0ZS5xdWVyeVBhcmFtcy5zdWJzY3JpYmUoKHBhcmFtczogUGFyYW1zKSA9PiB7XG4gICAgICAgICAgICAgICAgY2FsbGJhY2socGFyYW1zKTtcbiAgICAgICAgICAgIH0pO1xuICAgICAgICB9IGVsc2Uge1xuICAgICAgICAgICAgdGhyb3cgbmV3IEVycm9yKFwiVGhlIHJvdXRlOiBBY3RpdmF0ZWRSb3V0ZSBoYXMgbm90IGJlZW4gc2V0IHlldC5cIik7XG4gICAgICAgIH1cbiAgICB9XG5cbiAgICB1bnN1YnNjcmliZUZyb21RdWVyeUNoYW5nZXMoKTogdm9pZCB7XG4gICAgICAgIGlmICh0aGlzLnF1ZXJ5U3Vic2NyaXB0aW9uKSB7XG4gICAgICAgICAgICB0aGlzLnF1ZXJ5U3Vic2NyaXB0aW9uLnVuc3Vic2NyaWJlKCk7XG4gICAgICAgIH1cbiAgICB9XG5cbiAgICBnZXRDdXJyZW50UGF0aCgpOiBzdHJpbmcge1xuICAgICAgICByZXR1cm4gdGhpcy5yb3V0ZXIudXJsO1xuICAgIH1cbn1cbiJdfQ==