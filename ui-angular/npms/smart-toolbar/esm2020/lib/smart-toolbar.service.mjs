import { Injectable } from '@angular/core';
import { CommandType } from './smart-toolbar.model';
import * as i0 from "@angular/core";
import * as i1 from "@angular/router";
export class SmartToolbarService {
    constructor(router) {
        this.router = router;
    }
    executeCommand(button) {
        if (button.btnAction.commandType === CommandType.NAVIGATION) {
            let params = button.btnAction.objectUri ? { queryParams: { uri: button.btnAction.objectUri } } : {};
            this.router.navigate([button.btnAction.url], params);
        }
    }
}
SmartToolbarService.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarService, deps: [{ token: i1.Router }], target: i0.ɵɵFactoryTarget.Injectable });
SmartToolbarService.ɵprov = i0.ɵɵngDeclareInjectable({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarService, providedIn: 'root' });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarService, decorators: [{
            type: Injectable,
            args: [{
                    providedIn: 'root'
                }]
        }], ctorParameters: function () { return [{ type: i1.Router }]; } });
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnQtdG9vbGJhci5zZXJ2aWNlLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vLi4vcHJvamVjdHMvc21hcnQtdG9vbGJhci9zcmMvbGliL3NtYXJ0LXRvb2xiYXIuc2VydmljZS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQSxPQUFPLEVBQUUsVUFBVSxFQUFFLE1BQU0sZUFBZSxDQUFDO0FBRTNDLE9BQU8sRUFBRSxXQUFXLEVBQXNCLE1BQU0sdUJBQXVCLENBQUM7OztBQUt4RSxNQUFNLE9BQU8sbUJBQW1CO0lBRTlCLFlBQW9CLE1BQWM7UUFBZCxXQUFNLEdBQU4sTUFBTSxDQUFRO0lBQUksQ0FBQztJQUV2QyxjQUFjLENBQUMsTUFBMEI7UUFDdkMsSUFBSSxNQUFNLENBQUMsU0FBUyxDQUFDLFdBQVcsS0FBSyxXQUFXLENBQUMsVUFBVSxFQUFFO1lBQzNELElBQUksTUFBTSxHQUFHLE1BQU0sQ0FBQyxTQUFVLENBQUMsU0FBUyxDQUFDLENBQUMsQ0FBQyxFQUFFLFdBQVcsRUFBRSxFQUFFLEdBQUcsRUFBRSxNQUFNLENBQUMsU0FBUyxDQUFDLFNBQVMsRUFBRSxFQUFFLENBQUMsQ0FBQyxDQUFDLEVBQUUsQ0FBQztZQUNyRyxJQUFJLENBQUMsTUFBTSxDQUFDLFFBQVEsQ0FBQyxDQUFDLE1BQU0sQ0FBQyxTQUFTLENBQUMsR0FBRyxDQUFDLEVBQUUsTUFBTSxDQUFDLENBQUM7U0FDdEQ7SUFDSCxDQUFDOztnSEFUVSxtQkFBbUI7b0hBQW5CLG1CQUFtQixjQUZsQixNQUFNOzJGQUVQLG1CQUFtQjtrQkFIL0IsVUFBVTttQkFBQztvQkFDVixVQUFVLEVBQUUsTUFBTTtpQkFDbkIiLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQgeyBJbmplY3RhYmxlIH0gZnJvbSAnQGFuZ3VsYXIvY29yZSc7XG5pbXBvcnQgeyBSb3V0ZXIgfSBmcm9tICdAYW5ndWxhci9yb3V0ZXInO1xuaW1wb3J0IHsgQ29tbWFuZFR5cGUsIFNtYXJ0VG9vbGJhckJ1dHRvbiB9IGZyb20gJy4vc21hcnQtdG9vbGJhci5tb2RlbCc7XG5cbkBJbmplY3RhYmxlKHtcbiAgcHJvdmlkZWRJbjogJ3Jvb3QnXG59KVxuZXhwb3J0IGNsYXNzIFNtYXJ0VG9vbGJhclNlcnZpY2Uge1xuXG4gIGNvbnN0cnVjdG9yKHByaXZhdGUgcm91dGVyOiBSb3V0ZXIpIHsgfVxuXG4gIGV4ZWN1dGVDb21tYW5kKGJ1dHRvbjogU21hcnRUb29sYmFyQnV0dG9uKSB7XG4gICAgaWYgKGJ1dHRvbi5idG5BY3Rpb24uY29tbWFuZFR5cGUgPT09IENvbW1hbmRUeXBlLk5BVklHQVRJT04pIHtcbiAgICAgIGxldCBwYXJhbXMgPSBidXR0b24uYnRuQWN0aW9uIS5vYmplY3RVcmkgPyB7IHF1ZXJ5UGFyYW1zOiB7IHVyaTogYnV0dG9uLmJ0bkFjdGlvbi5vYmplY3RVcmkgfSB9IDoge307XG4gICAgICB0aGlzLnJvdXRlci5uYXZpZ2F0ZShbYnV0dG9uLmJ0bkFjdGlvbi51cmxdLCBwYXJhbXMpO1xuICAgIH1cbiAgfVxufVxuIl19