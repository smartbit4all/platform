import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { MatCommonModule } from "@angular/material/core";
import { BrowserModule } from "@angular/platform-browser";
import { ExpandableSectionComponent } from "./expandable-section.component";
import { MatExpansionModule } from "@angular/material/expansion";
import { ComponentFactoryServiceModule } from "@smartbit4all/component-factory-service";
import * as i0 from "@angular/core";
export class SmartExpandableSectionModule {
}
SmartExpandableSectionModule.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartExpandableSectionModule, deps: [], target: i0.ɵɵFactoryTarget.NgModule });
SmartExpandableSectionModule.ɵmod = i0.ɵɵngDeclareNgModule({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartExpandableSectionModule, declarations: [ExpandableSectionComponent], imports: [BrowserModule, MatCommonModule, MatExpansionModule, ComponentFactoryServiceModule], exports: [ExpandableSectionComponent] });
SmartExpandableSectionModule.ɵinj = i0.ɵɵngDeclareInjector({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartExpandableSectionModule, imports: [[BrowserModule, MatCommonModule, MatExpansionModule, ComponentFactoryServiceModule]] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartExpandableSectionModule, decorators: [{
            type: NgModule,
            args: [{
                    declarations: [ExpandableSectionComponent],
                    imports: [BrowserModule, MatCommonModule, MatExpansionModule, ComponentFactoryServiceModule],
                    exports: [ExpandableSectionComponent],
                    schemas: [CUSTOM_ELEMENTS_SCHEMA],
                }]
        }] });
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnQtZXhwYW5kYWJsZS1zZWN0aW9uLm1vZHVsZS5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uLy4uLy4uL3Byb2plY3RzL3NtYXJ0LWV4cGFuZGFibGUtc2VjdGlvbi9zcmMvbGliL3NtYXJ0LWV4cGFuZGFibGUtc2VjdGlvbi5tb2R1bGUudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUEsT0FBTyxFQUFFLFFBQVEsRUFBRSxzQkFBc0IsRUFBRSxNQUFNLGVBQWUsQ0FBQztBQUNqRSxPQUFPLEVBQUUsZUFBZSxFQUFFLE1BQU0sd0JBQXdCLENBQUM7QUFDekQsT0FBTyxFQUFFLGFBQWEsRUFBRSxNQUFNLDJCQUEyQixDQUFDO0FBQzFELE9BQU8sRUFBRSwwQkFBMEIsRUFBRSxNQUFNLGdDQUFnQyxDQUFDO0FBQzVFLE9BQU8sRUFBRSxrQkFBa0IsRUFBRSxNQUFNLDZCQUE2QixDQUFDO0FBQ2pFLE9BQU8sRUFBRSw2QkFBNkIsRUFBRSxNQUFNLHlDQUF5QyxDQUFDOztBQVF4RixNQUFNLE9BQU8sNEJBQTRCOzt5SEFBNUIsNEJBQTRCOzBIQUE1Qiw0QkFBNEIsaUJBTHRCLDBCQUEwQixhQUMvQixhQUFhLEVBQUUsZUFBZSxFQUFFLGtCQUFrQixFQUFFLDZCQUE2QixhQUNqRiwwQkFBMEI7MEhBRzNCLDRCQUE0QixZQUo1QixDQUFDLGFBQWEsRUFBRSxlQUFlLEVBQUUsa0JBQWtCLEVBQUUsNkJBQTZCLENBQUM7MkZBSW5GLDRCQUE0QjtrQkFOeEMsUUFBUTttQkFBQztvQkFDTixZQUFZLEVBQUUsQ0FBQywwQkFBMEIsQ0FBQztvQkFDMUMsT0FBTyxFQUFFLENBQUMsYUFBYSxFQUFFLGVBQWUsRUFBRSxrQkFBa0IsRUFBRSw2QkFBNkIsQ0FBQztvQkFDNUYsT0FBTyxFQUFFLENBQUMsMEJBQTBCLENBQUM7b0JBQ3JDLE9BQU8sRUFBRSxDQUFDLHNCQUFzQixDQUFDO2lCQUNwQyIsInNvdXJjZXNDb250ZW50IjpbImltcG9ydCB7IE5nTW9kdWxlLCBDVVNUT01fRUxFTUVOVFNfU0NIRU1BIH0gZnJvbSBcIkBhbmd1bGFyL2NvcmVcIjtcbmltcG9ydCB7IE1hdENvbW1vbk1vZHVsZSB9IGZyb20gXCJAYW5ndWxhci9tYXRlcmlhbC9jb3JlXCI7XG5pbXBvcnQgeyBCcm93c2VyTW9kdWxlIH0gZnJvbSBcIkBhbmd1bGFyL3BsYXRmb3JtLWJyb3dzZXJcIjtcbmltcG9ydCB7IEV4cGFuZGFibGVTZWN0aW9uQ29tcG9uZW50IH0gZnJvbSBcIi4vZXhwYW5kYWJsZS1zZWN0aW9uLmNvbXBvbmVudFwiO1xuaW1wb3J0IHsgTWF0RXhwYW5zaW9uTW9kdWxlIH0gZnJvbSBcIkBhbmd1bGFyL21hdGVyaWFsL2V4cGFuc2lvblwiO1xuaW1wb3J0IHsgQ29tcG9uZW50RmFjdG9yeVNlcnZpY2VNb2R1bGUgfSBmcm9tIFwiQHNtYXJ0Yml0NGFsbC9jb21wb25lbnQtZmFjdG9yeS1zZXJ2aWNlXCI7XG5cbkBOZ01vZHVsZSh7XG4gICAgZGVjbGFyYXRpb25zOiBbRXhwYW5kYWJsZVNlY3Rpb25Db21wb25lbnRdLFxuICAgIGltcG9ydHM6IFtCcm93c2VyTW9kdWxlLCBNYXRDb21tb25Nb2R1bGUsIE1hdEV4cGFuc2lvbk1vZHVsZSwgQ29tcG9uZW50RmFjdG9yeVNlcnZpY2VNb2R1bGVdLFxuICAgIGV4cG9ydHM6IFtFeHBhbmRhYmxlU2VjdGlvbkNvbXBvbmVudF0sXG4gICAgc2NoZW1hczogW0NVU1RPTV9FTEVNRU5UU19TQ0hFTUFdLFxufSlcbmV4cG9ydCBjbGFzcyBTbWFydEV4cGFuZGFibGVTZWN0aW9uTW9kdWxlIHt9XG4iXX0=