import { CUSTOM_ELEMENTS_SCHEMA, NgModule, NO_ERRORS_SCHEMA } from "@angular/core";
import { SmartDialog } from "./smartdialog.component";
import { MatDialogModule } from "@angular/material/dialog";
import { MatIconModule } from "@angular/material/icon";
import { SmartformModule } from "@smartbit4all/form";
import { SmarttableModule } from "@smartbit4all/table";
import { MatCommonModule } from "@angular/material/core";
import * as i0 from "@angular/core";
export class SmartdialogModule {
}
SmartdialogModule.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartdialogModule, deps: [], target: i0.ɵɵFactoryTarget.NgModule });
SmartdialogModule.ɵmod = i0.ɵɵngDeclareNgModule({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartdialogModule, declarations: [SmartDialog], imports: [MatDialogModule, MatCommonModule, MatIconModule, SmartformModule, SmarttableModule], exports: [SmartDialog] });
SmartdialogModule.ɵinj = i0.ɵɵngDeclareInjector({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartdialogModule, imports: [[MatDialogModule, MatCommonModule, MatIconModule, SmartformModule, SmarttableModule]] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartdialogModule, decorators: [{
            type: NgModule,
            args: [{
                    declarations: [SmartDialog],
                    imports: [MatDialogModule, MatCommonModule, MatIconModule, SmartformModule, SmarttableModule],
                    exports: [SmartDialog],
                    schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
                }]
        }] });
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnRkaWFsb2cubW9kdWxlLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vLi4vcHJvamVjdHMvc21hcnRkaWFsb2cvc3JjL2xpYi9zbWFydGRpYWxvZy5tb2R1bGUudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUEsT0FBTyxFQUFFLHNCQUFzQixFQUFFLFFBQVEsRUFBRSxnQkFBZ0IsRUFBRSxNQUFNLGVBQWUsQ0FBQztBQUNuRixPQUFPLEVBQUUsV0FBVyxFQUFFLE1BQU0seUJBQXlCLENBQUM7QUFDdEQsT0FBTyxFQUFFLGVBQWUsRUFBRSxNQUFNLDBCQUEwQixDQUFDO0FBQzNELE9BQU8sRUFBRSxhQUFhLEVBQUUsTUFBTSx3QkFBd0IsQ0FBQztBQUN2RCxPQUFPLEVBQUUsZUFBZSxFQUFFLE1BQU0sb0JBQW9CLENBQUM7QUFDckQsT0FBTyxFQUFFLGdCQUFnQixFQUFFLE1BQU0scUJBQXFCLENBQUM7QUFDdkQsT0FBTyxFQUFFLGVBQWUsRUFBRSxNQUFNLHdCQUF3QixDQUFDOztBQVF6RCxNQUFNLE9BQU8saUJBQWlCOzs4R0FBakIsaUJBQWlCOytHQUFqQixpQkFBaUIsaUJBTFgsV0FBVyxhQUNoQixlQUFlLEVBQUUsZUFBZSxFQUFFLGFBQWEsRUFBRSxlQUFlLEVBQUUsZ0JBQWdCLGFBQ2xGLFdBQVc7K0dBR1osaUJBQWlCLFlBSmpCLENBQUMsZUFBZSxFQUFFLGVBQWUsRUFBRSxhQUFhLEVBQUUsZUFBZSxFQUFFLGdCQUFnQixDQUFDOzJGQUlwRixpQkFBaUI7a0JBTjdCLFFBQVE7bUJBQUM7b0JBQ04sWUFBWSxFQUFFLENBQUMsV0FBVyxDQUFDO29CQUMzQixPQUFPLEVBQUUsQ0FBQyxlQUFlLEVBQUUsZUFBZSxFQUFFLGFBQWEsRUFBRSxlQUFlLEVBQUUsZ0JBQWdCLENBQUM7b0JBQzdGLE9BQU8sRUFBRSxDQUFDLFdBQVcsQ0FBQztvQkFDdEIsT0FBTyxFQUFFLENBQUMsc0JBQXNCLEVBQUUsZ0JBQWdCLENBQUM7aUJBQ3REIiwic291cmNlc0NvbnRlbnQiOlsiaW1wb3J0IHsgQ1VTVE9NX0VMRU1FTlRTX1NDSEVNQSwgTmdNb2R1bGUsIE5PX0VSUk9SU19TQ0hFTUEgfSBmcm9tIFwiQGFuZ3VsYXIvY29yZVwiO1xuaW1wb3J0IHsgU21hcnREaWFsb2cgfSBmcm9tIFwiLi9zbWFydGRpYWxvZy5jb21wb25lbnRcIjtcbmltcG9ydCB7IE1hdERpYWxvZ01vZHVsZSB9IGZyb20gXCJAYW5ndWxhci9tYXRlcmlhbC9kaWFsb2dcIjtcbmltcG9ydCB7IE1hdEljb25Nb2R1bGUgfSBmcm9tIFwiQGFuZ3VsYXIvbWF0ZXJpYWwvaWNvblwiO1xuaW1wb3J0IHsgU21hcnRmb3JtTW9kdWxlIH0gZnJvbSBcIkBzbWFydGJpdDRhbGwvZm9ybVwiO1xuaW1wb3J0IHsgU21hcnR0YWJsZU1vZHVsZSB9IGZyb20gXCJAc21hcnRiaXQ0YWxsL3RhYmxlXCI7XG5pbXBvcnQgeyBNYXRDb21tb25Nb2R1bGUgfSBmcm9tIFwiQGFuZ3VsYXIvbWF0ZXJpYWwvY29yZVwiO1xuXG5ATmdNb2R1bGUoe1xuICAgIGRlY2xhcmF0aW9uczogW1NtYXJ0RGlhbG9nXSxcbiAgICBpbXBvcnRzOiBbTWF0RGlhbG9nTW9kdWxlLCBNYXRDb21tb25Nb2R1bGUsIE1hdEljb25Nb2R1bGUsIFNtYXJ0Zm9ybU1vZHVsZSwgU21hcnR0YWJsZU1vZHVsZV0sXG4gICAgZXhwb3J0czogW1NtYXJ0RGlhbG9nXSxcbiAgICBzY2hlbWFzOiBbQ1VTVE9NX0VMRU1FTlRTX1NDSEVNQSwgTk9fRVJST1JTX1NDSEVNQV0sXG59KVxuZXhwb3J0IGNsYXNzIFNtYXJ0ZGlhbG9nTW9kdWxlIHt9XG4iXX0=