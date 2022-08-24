import { Injectable } from '@angular/core';
import { CommandType } from './smart-toolbar.model';
import * as i0 from "@angular/core";
import * as i1 from "@angular/router";
export class SmartToolbarService {
    constructor(router, route) {
        this.router = router;
        this.route = route;
    }
    executeCommand(button) {
        if (button.btnAction.commandType === CommandType.NAVIGATION) {
            let params = button.btnAction.objectUri ? { uri: button.btnAction.objectUri } : {};
            this.router.navigate([button.btnAction.url], { queryParams: params, relativeTo: this.route });
        }
    }
}
SmartToolbarService.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarService, deps: [{ token: i1.Router }, { token: i1.ActivatedRoute }], target: i0.ɵɵFactoryTarget.Injectable });
SmartToolbarService.ɵprov = i0.ɵɵngDeclareInjectable({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarService, providedIn: 'root' });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarService, decorators: [{
            type: Injectable,
            args: [{
                    providedIn: 'root'
                }]
        }], ctorParameters: function () { return [{ type: i1.Router }, { type: i1.ActivatedRoute }]; } });
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnQtdG9vbGJhci5zZXJ2aWNlLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vLi4vcHJvamVjdHMvc21hcnQtdG9vbGJhci9zcmMvbGliL3NtYXJ0LXRvb2xiYXIuc2VydmljZS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQSxPQUFPLEVBQUUsVUFBVSxFQUFFLE1BQU0sZUFBZSxDQUFDO0FBRTNDLE9BQU8sRUFBRSxXQUFXLEVBQXNCLE1BQU0sdUJBQXVCLENBQUM7OztBQUt4RSxNQUFNLE9BQU8sbUJBQW1CO0lBRTlCLFlBQW9CLE1BQWMsRUFBVSxLQUFxQjtRQUE3QyxXQUFNLEdBQU4sTUFBTSxDQUFRO1FBQVUsVUFBSyxHQUFMLEtBQUssQ0FBZ0I7SUFBSSxDQUFDO0lBRXRFLGNBQWMsQ0FBQyxNQUEwQjtRQUN2QyxJQUFJLE1BQU0sQ0FBQyxTQUFTLENBQUMsV0FBVyxLQUFLLFdBQVcsQ0FBQyxVQUFVLEVBQUU7WUFDM0QsSUFBSSxNQUFNLEdBQUcsTUFBTSxDQUFDLFNBQVUsQ0FBQyxTQUFTLENBQUMsQ0FBQyxDQUFDLEVBQUUsR0FBRyxFQUFFLE1BQU0sQ0FBQyxTQUFTLENBQUMsU0FBUyxFQUFFLENBQUMsQ0FBQyxDQUFDLEVBQUUsQ0FBQztZQUNwRixJQUFJLENBQUMsTUFBTSxDQUFDLFFBQVEsQ0FBQyxDQUFDLE1BQU0sQ0FBQyxTQUFTLENBQUMsR0FBRyxDQUFDLEVBQUUsRUFBRSxXQUFXLEVBQUUsTUFBTSxFQUFFLFVBQVUsRUFBRSxJQUFJLENBQUMsS0FBSyxFQUFFLENBQUMsQ0FBQztTQUMvRjtJQUNILENBQUM7O2dIQVRVLG1CQUFtQjtvSEFBbkIsbUJBQW1CLGNBRmxCLE1BQU07MkZBRVAsbUJBQW1CO2tCQUgvQixVQUFVO21CQUFDO29CQUNWLFVBQVUsRUFBRSxNQUFNO2lCQUNuQiIsInNvdXJjZXNDb250ZW50IjpbImltcG9ydCB7IEluamVjdGFibGUgfSBmcm9tICdAYW5ndWxhci9jb3JlJztcbmltcG9ydCB7IEFjdGl2YXRlZFJvdXRlLCBSb3V0ZXIgfSBmcm9tICdAYW5ndWxhci9yb3V0ZXInO1xuaW1wb3J0IHsgQ29tbWFuZFR5cGUsIFNtYXJ0VG9vbGJhckJ1dHRvbiB9IGZyb20gJy4vc21hcnQtdG9vbGJhci5tb2RlbCc7XG5cbkBJbmplY3RhYmxlKHtcbiAgcHJvdmlkZWRJbjogJ3Jvb3QnXG59KVxuZXhwb3J0IGNsYXNzIFNtYXJ0VG9vbGJhclNlcnZpY2Uge1xuXG4gIGNvbnN0cnVjdG9yKHByaXZhdGUgcm91dGVyOiBSb3V0ZXIsIHByaXZhdGUgcm91dGU6IEFjdGl2YXRlZFJvdXRlKSB7IH1cblxuICBleGVjdXRlQ29tbWFuZChidXR0b246IFNtYXJ0VG9vbGJhckJ1dHRvbikge1xuICAgIGlmIChidXR0b24uYnRuQWN0aW9uLmNvbW1hbmRUeXBlID09PSBDb21tYW5kVHlwZS5OQVZJR0FUSU9OKSB7XG4gICAgICBsZXQgcGFyYW1zID0gYnV0dG9uLmJ0bkFjdGlvbiEub2JqZWN0VXJpID8geyB1cmk6IGJ1dHRvbi5idG5BY3Rpb24ub2JqZWN0VXJpIH0gOiB7fTtcbiAgICAgIHRoaXMucm91dGVyLm5hdmlnYXRlKFtidXR0b24uYnRuQWN0aW9uLnVybF0sIHsgcXVlcnlQYXJhbXM6IHBhcmFtcywgcmVsYXRpdmVUbzogdGhpcy5yb3V0ZSB9KTtcbiAgICB9XG4gIH1cbn1cbiJdfQ==