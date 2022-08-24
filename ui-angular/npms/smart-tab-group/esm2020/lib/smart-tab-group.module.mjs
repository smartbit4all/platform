import { NgModule } from "@angular/core";
import { TabGroupComponent } from "./smart-tab-group.component";
import { MatTabsModule } from "@angular/material/tabs";
import { BrowserModule } from "@angular/platform-browser";
import { SmartNavigationModule, SmartNavigationService } from "@smartbit4all/navigation";
import * as i0 from "@angular/core";
export class SmartTabGroupModule {
}
SmartTabGroupModule.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartTabGroupModule, deps: [], target: i0.ɵɵFactoryTarget.NgModule });
SmartTabGroupModule.ɵmod = i0.ɵɵngDeclareNgModule({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartTabGroupModule, declarations: [TabGroupComponent], imports: [BrowserModule, MatTabsModule, SmartNavigationModule], exports: [TabGroupComponent] });
SmartTabGroupModule.ɵinj = i0.ɵɵngDeclareInjector({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartTabGroupModule, providers: [SmartNavigationService], imports: [[BrowserModule, MatTabsModule, SmartNavigationModule]] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartTabGroupModule, decorators: [{
            type: NgModule,
            args: [{
                    declarations: [TabGroupComponent],
                    imports: [BrowserModule, MatTabsModule, SmartNavigationModule],
                    exports: [TabGroupComponent],
                    providers: [SmartNavigationService],
                }]
        }] });
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnQtdGFiLWdyb3VwLm1vZHVsZS5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uLy4uLy4uL3Byb2plY3RzL3NtYXJ0LXRhYi1ncm91cC9zcmMvbGliL3NtYXJ0LXRhYi1ncm91cC5tb2R1bGUudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUEsT0FBTyxFQUFFLFFBQVEsRUFBRSxNQUFNLGVBQWUsQ0FBQztBQUN6QyxPQUFPLEVBQUUsaUJBQWlCLEVBQUUsTUFBTSw2QkFBNkIsQ0FBQztBQUNoRSxPQUFPLEVBQUUsYUFBYSxFQUFFLE1BQU0sd0JBQXdCLENBQUM7QUFDdkQsT0FBTyxFQUFFLGFBQWEsRUFBRSxNQUFNLDJCQUEyQixDQUFDO0FBQzFELE9BQU8sRUFBRSxxQkFBcUIsRUFBRSxzQkFBc0IsRUFBRSxNQUFNLDBCQUEwQixDQUFDOztBQVF6RixNQUFNLE9BQU8sbUJBQW1COztnSEFBbkIsbUJBQW1CO2lIQUFuQixtQkFBbUIsaUJBTGIsaUJBQWlCLGFBQ3RCLGFBQWEsRUFBRSxhQUFhLEVBQUUscUJBQXFCLGFBQ25ELGlCQUFpQjtpSEFHbEIsbUJBQW1CLGFBRmpCLENBQUMsc0JBQXNCLENBQUMsWUFGMUIsQ0FBQyxhQUFhLEVBQUUsYUFBYSxFQUFFLHFCQUFxQixDQUFDOzJGQUlyRCxtQkFBbUI7a0JBTi9CLFFBQVE7bUJBQUM7b0JBQ04sWUFBWSxFQUFFLENBQUMsaUJBQWlCLENBQUM7b0JBQ2pDLE9BQU8sRUFBRSxDQUFDLGFBQWEsRUFBRSxhQUFhLEVBQUUscUJBQXFCLENBQUM7b0JBQzlELE9BQU8sRUFBRSxDQUFDLGlCQUFpQixDQUFDO29CQUM1QixTQUFTLEVBQUUsQ0FBQyxzQkFBc0IsQ0FBQztpQkFDdEMiLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQgeyBOZ01vZHVsZSB9IGZyb20gXCJAYW5ndWxhci9jb3JlXCI7XG5pbXBvcnQgeyBUYWJHcm91cENvbXBvbmVudCB9IGZyb20gXCIuL3NtYXJ0LXRhYi1ncm91cC5jb21wb25lbnRcIjtcbmltcG9ydCB7IE1hdFRhYnNNb2R1bGUgfSBmcm9tIFwiQGFuZ3VsYXIvbWF0ZXJpYWwvdGFic1wiO1xuaW1wb3J0IHsgQnJvd3Nlck1vZHVsZSB9IGZyb20gXCJAYW5ndWxhci9wbGF0Zm9ybS1icm93c2VyXCI7XG5pbXBvcnQgeyBTbWFydE5hdmlnYXRpb25Nb2R1bGUsIFNtYXJ0TmF2aWdhdGlvblNlcnZpY2UgfSBmcm9tIFwiQHNtYXJ0Yml0NGFsbC9uYXZpZ2F0aW9uXCI7XG5cbkBOZ01vZHVsZSh7XG4gICAgZGVjbGFyYXRpb25zOiBbVGFiR3JvdXBDb21wb25lbnRdLFxuICAgIGltcG9ydHM6IFtCcm93c2VyTW9kdWxlLCBNYXRUYWJzTW9kdWxlLCBTbWFydE5hdmlnYXRpb25Nb2R1bGVdLFxuICAgIGV4cG9ydHM6IFtUYWJHcm91cENvbXBvbmVudF0sXG4gICAgcHJvdmlkZXJzOiBbU21hcnROYXZpZ2F0aW9uU2VydmljZV0sXG59KVxuZXhwb3J0IGNsYXNzIFNtYXJ0VGFiR3JvdXBNb2R1bGUge31cbiJdfQ==