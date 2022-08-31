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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnQtdGFiLWdyb3VwLm1vZHVsZS5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uLy4uLy4uL3Byb2plY3RzL3NtYXJ0LXRhYi1ncm91cC9zcmMvbGliL3NtYXJ0LXRhYi1ncm91cC5tb2R1bGUudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUEsT0FBTyxFQUFFLFFBQVEsRUFBRSxNQUFNLGVBQWUsQ0FBQztBQUN6QyxPQUFPLEVBQUUsaUJBQWlCLEVBQUUsTUFBTSw2QkFBNkIsQ0FBQztBQUNoRSxPQUFPLEVBQUUsYUFBYSxFQUFFLE1BQU0sd0JBQXdCLENBQUM7QUFDdkQsT0FBTyxFQUFFLGFBQWEsRUFBRSxNQUFNLDJCQUEyQixDQUFDO0FBQzFELE9BQU8sRUFBRSxxQkFBcUIsRUFBRSxzQkFBc0IsRUFBRSxNQUFNLDBCQUEwQixDQUFDOztBQVF6RixNQUFNLE9BQU8sbUJBQW1COztnSEFBbkIsbUJBQW1CO2lIQUFuQixtQkFBbUIsaUJBTGIsaUJBQWlCLGFBQ3RCLGFBQWEsRUFBRSxhQUFhLEVBQUUscUJBQXFCLGFBQ25ELGlCQUFpQjtpSEFHbEIsbUJBQW1CLGFBRmpCLENBQUMsc0JBQXNCLENBQUMsWUFGMUIsQ0FBQyxhQUFhLEVBQUUsYUFBYSxFQUFFLHFCQUFxQixDQUFDOzJGQUlyRCxtQkFBbUI7a0JBTi9CLFFBQVE7bUJBQUM7b0JBQ04sWUFBWSxFQUFFLENBQUMsaUJBQWlCLENBQUM7b0JBQ2pDLE9BQU8sRUFBRSxDQUFDLGFBQWEsRUFBRSxhQUFhLEVBQUUscUJBQXFCLENBQUM7b0JBQzlELE9BQU8sRUFBRSxDQUFDLGlCQUFpQixDQUFDO29CQUM1QixTQUFTLEVBQUUsQ0FBQyxzQkFBc0IsQ0FBQztpQkFDdEMiLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQgeyBOZ01vZHVsZSB9IGZyb20gXCJAYW5ndWxhci9jb3JlXCI7XHJcbmltcG9ydCB7IFRhYkdyb3VwQ29tcG9uZW50IH0gZnJvbSBcIi4vc21hcnQtdGFiLWdyb3VwLmNvbXBvbmVudFwiO1xyXG5pbXBvcnQgeyBNYXRUYWJzTW9kdWxlIH0gZnJvbSBcIkBhbmd1bGFyL21hdGVyaWFsL3RhYnNcIjtcclxuaW1wb3J0IHsgQnJvd3Nlck1vZHVsZSB9IGZyb20gXCJAYW5ndWxhci9wbGF0Zm9ybS1icm93c2VyXCI7XHJcbmltcG9ydCB7IFNtYXJ0TmF2aWdhdGlvbk1vZHVsZSwgU21hcnROYXZpZ2F0aW9uU2VydmljZSB9IGZyb20gXCJAc21hcnRiaXQ0YWxsL25hdmlnYXRpb25cIjtcclxuXHJcbkBOZ01vZHVsZSh7XHJcbiAgICBkZWNsYXJhdGlvbnM6IFtUYWJHcm91cENvbXBvbmVudF0sXHJcbiAgICBpbXBvcnRzOiBbQnJvd3Nlck1vZHVsZSwgTWF0VGFic01vZHVsZSwgU21hcnROYXZpZ2F0aW9uTW9kdWxlXSxcclxuICAgIGV4cG9ydHM6IFtUYWJHcm91cENvbXBvbmVudF0sXHJcbiAgICBwcm92aWRlcnM6IFtTbWFydE5hdmlnYXRpb25TZXJ2aWNlXSxcclxufSlcclxuZXhwb3J0IGNsYXNzIFNtYXJ0VGFiR3JvdXBNb2R1bGUge31cclxuIl19