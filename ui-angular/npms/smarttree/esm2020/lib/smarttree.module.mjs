import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { MatCommonModule } from "@angular/material/core";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { MatTreeModule } from "@angular/material/tree";
import { SmartTreeComponent } from "./smarttree.component";
import { BrowserModule } from "@angular/platform-browser";
import * as i0 from "@angular/core";
export class SmarttreeModule {
}
SmarttreeModule.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmarttreeModule, deps: [], target: i0.ɵɵFactoryTarget.NgModule });
SmarttreeModule.ɵmod = i0.ɵɵngDeclareNgModule({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmarttreeModule, declarations: [SmartTreeComponent], imports: [BrowserModule, MatCommonModule, MatButtonModule, MatIconModule, MatTreeModule], exports: [SmartTreeComponent] });
SmarttreeModule.ɵinj = i0.ɵɵngDeclareInjector({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmarttreeModule, imports: [[BrowserModule, MatCommonModule, MatButtonModule, MatIconModule, MatTreeModule]] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmarttreeModule, decorators: [{
            type: NgModule,
            args: [{
                    declarations: [SmartTreeComponent],
                    imports: [BrowserModule, MatCommonModule, MatButtonModule, MatIconModule, MatTreeModule],
                    exports: [SmartTreeComponent],
                    schemas: [CUSTOM_ELEMENTS_SCHEMA],
                }]
        }] });
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnR0cmVlLm1vZHVsZS5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uLy4uLy4uL3Byb2plY3RzL3NtYXJ0dHJlZS9zcmMvbGliL3NtYXJ0dHJlZS5tb2R1bGUudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUEsT0FBTyxFQUFFLFFBQVEsRUFBRSxzQkFBc0IsRUFBRSxNQUFNLGVBQWUsQ0FBQztBQUNqRSxPQUFPLEVBQUUsZUFBZSxFQUFFLE1BQU0sd0JBQXdCLENBQUM7QUFDekQsT0FBTyxFQUFFLGFBQWEsRUFBRSxNQUFNLHdCQUF3QixDQUFDO0FBQ3ZELE9BQU8sRUFBRSxlQUFlLEVBQUUsTUFBTSwwQkFBMEIsQ0FBQztBQUMzRCxPQUFPLEVBQUUsYUFBYSxFQUFFLE1BQU0sd0JBQXdCLENBQUM7QUFDdkQsT0FBTyxFQUFFLGtCQUFrQixFQUFFLE1BQU0sdUJBQXVCLENBQUM7QUFDM0QsT0FBTyxFQUFFLGFBQWEsRUFBRSxNQUFNLDJCQUEyQixDQUFDOztBQVExRCxNQUFNLE9BQU8sZUFBZTs7NEdBQWYsZUFBZTs2R0FBZixlQUFlLGlCQUxULGtCQUFrQixhQUN2QixhQUFhLEVBQUUsZUFBZSxFQUFFLGVBQWUsRUFBRSxhQUFhLEVBQUUsYUFBYSxhQUM3RSxrQkFBa0I7NkdBR25CLGVBQWUsWUFKZixDQUFDLGFBQWEsRUFBRSxlQUFlLEVBQUUsZUFBZSxFQUFFLGFBQWEsRUFBRSxhQUFhLENBQUM7MkZBSS9FLGVBQWU7a0JBTjNCLFFBQVE7bUJBQUM7b0JBQ04sWUFBWSxFQUFFLENBQUMsa0JBQWtCLENBQUM7b0JBQ2xDLE9BQU8sRUFBRSxDQUFDLGFBQWEsRUFBRSxlQUFlLEVBQUUsZUFBZSxFQUFFLGFBQWEsRUFBRSxhQUFhLENBQUM7b0JBQ3hGLE9BQU8sRUFBRSxDQUFDLGtCQUFrQixDQUFDO29CQUM3QixPQUFPLEVBQUUsQ0FBQyxzQkFBc0IsQ0FBQztpQkFDcEMiLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQgeyBOZ01vZHVsZSwgQ1VTVE9NX0VMRU1FTlRTX1NDSEVNQSB9IGZyb20gXCJAYW5ndWxhci9jb3JlXCI7XG5pbXBvcnQgeyBNYXRDb21tb25Nb2R1bGUgfSBmcm9tIFwiQGFuZ3VsYXIvbWF0ZXJpYWwvY29yZVwiO1xuaW1wb3J0IHsgTWF0SWNvbk1vZHVsZSB9IGZyb20gXCJAYW5ndWxhci9tYXRlcmlhbC9pY29uXCI7XG5pbXBvcnQgeyBNYXRCdXR0b25Nb2R1bGUgfSBmcm9tIFwiQGFuZ3VsYXIvbWF0ZXJpYWwvYnV0dG9uXCI7XG5pbXBvcnQgeyBNYXRUcmVlTW9kdWxlIH0gZnJvbSBcIkBhbmd1bGFyL21hdGVyaWFsL3RyZWVcIjtcbmltcG9ydCB7IFNtYXJ0VHJlZUNvbXBvbmVudCB9IGZyb20gXCIuL3NtYXJ0dHJlZS5jb21wb25lbnRcIjtcbmltcG9ydCB7IEJyb3dzZXJNb2R1bGUgfSBmcm9tIFwiQGFuZ3VsYXIvcGxhdGZvcm0tYnJvd3NlclwiO1xuXG5ATmdNb2R1bGUoe1xuICAgIGRlY2xhcmF0aW9uczogW1NtYXJ0VHJlZUNvbXBvbmVudF0sXG4gICAgaW1wb3J0czogW0Jyb3dzZXJNb2R1bGUsIE1hdENvbW1vbk1vZHVsZSwgTWF0QnV0dG9uTW9kdWxlLCBNYXRJY29uTW9kdWxlLCBNYXRUcmVlTW9kdWxlXSxcbiAgICBleHBvcnRzOiBbU21hcnRUcmVlQ29tcG9uZW50XSxcbiAgICBzY2hlbWFzOiBbQ1VTVE9NX0VMRU1FTlRTX1NDSEVNQV0sXG59KVxuZXhwb3J0IGNsYXNzIFNtYXJ0dHJlZU1vZHVsZSB7fVxuIl19