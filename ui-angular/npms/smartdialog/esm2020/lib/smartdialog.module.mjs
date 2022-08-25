import { CUSTOM_ELEMENTS_SCHEMA, NgModule, NO_ERRORS_SCHEMA } from "@angular/core";
import { SmartDialog } from "./smartdialog.component";
import { MatDialogModule } from "@angular/material/dialog";
import { MatIconModule } from "@angular/material/icon";
import { SmartformModule } from "@smartbit4all/form";
import { SmarttableModule } from "@smartbit4all/table";
import { MatCommonModule } from "@angular/material/core";
import { SmartdialogService } from "./smartdialog.service";
import * as i0 from "@angular/core";
export class SmartdialogModule {
}
SmartdialogModule.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartdialogModule, deps: [], target: i0.ɵɵFactoryTarget.NgModule });
SmartdialogModule.ɵmod = i0.ɵɵngDeclareNgModule({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartdialogModule, declarations: [SmartDialog], imports: [MatDialogModule, MatCommonModule, MatIconModule, SmartformModule, SmarttableModule], exports: [SmartDialog] });
SmartdialogModule.ɵinj = i0.ɵɵngDeclareInjector({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartdialogModule, providers: [SmartdialogService], imports: [[MatDialogModule, MatCommonModule, MatIconModule, SmartformModule, SmarttableModule]] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartdialogModule, decorators: [{
            type: NgModule,
            args: [{
                    declarations: [SmartDialog],
                    imports: [MatDialogModule, MatCommonModule, MatIconModule, SmartformModule, SmarttableModule],
                    exports: [SmartDialog],
                    schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
                    providers: [SmartdialogService],
                }]
        }] });
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnRkaWFsb2cubW9kdWxlLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vLi4vcHJvamVjdHMvc21hcnRkaWFsb2cvc3JjL2xpYi9zbWFydGRpYWxvZy5tb2R1bGUudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUEsT0FBTyxFQUFFLHNCQUFzQixFQUFFLFFBQVEsRUFBRSxnQkFBZ0IsRUFBRSxNQUFNLGVBQWUsQ0FBQztBQUNuRixPQUFPLEVBQUUsV0FBVyxFQUFFLE1BQU0seUJBQXlCLENBQUM7QUFDdEQsT0FBTyxFQUFFLGVBQWUsRUFBRSxNQUFNLDBCQUEwQixDQUFDO0FBQzNELE9BQU8sRUFBRSxhQUFhLEVBQUUsTUFBTSx3QkFBd0IsQ0FBQztBQUN2RCxPQUFPLEVBQUUsZUFBZSxFQUFFLE1BQU0sb0JBQW9CLENBQUM7QUFDckQsT0FBTyxFQUFFLGdCQUFnQixFQUFFLE1BQU0scUJBQXFCLENBQUM7QUFDdkQsT0FBTyxFQUFFLGVBQWUsRUFBRSxNQUFNLHdCQUF3QixDQUFDO0FBQ3pELE9BQU8sRUFBRSxrQkFBa0IsRUFBRSxNQUFNLHVCQUF1QixDQUFDOztBQVMzRCxNQUFNLE9BQU8saUJBQWlCOzs4R0FBakIsaUJBQWlCOytHQUFqQixpQkFBaUIsaUJBTlgsV0FBVyxhQUNoQixlQUFlLEVBQUUsZUFBZSxFQUFFLGFBQWEsRUFBRSxlQUFlLEVBQUUsZ0JBQWdCLGFBQ2xGLFdBQVc7K0dBSVosaUJBQWlCLGFBRmYsQ0FBQyxrQkFBa0IsQ0FBQyxZQUh0QixDQUFDLGVBQWUsRUFBRSxlQUFlLEVBQUUsYUFBYSxFQUFFLGVBQWUsRUFBRSxnQkFBZ0IsQ0FBQzsyRkFLcEYsaUJBQWlCO2tCQVA3QixRQUFRO21CQUFDO29CQUNOLFlBQVksRUFBRSxDQUFDLFdBQVcsQ0FBQztvQkFDM0IsT0FBTyxFQUFFLENBQUMsZUFBZSxFQUFFLGVBQWUsRUFBRSxhQUFhLEVBQUUsZUFBZSxFQUFFLGdCQUFnQixDQUFDO29CQUM3RixPQUFPLEVBQUUsQ0FBQyxXQUFXLENBQUM7b0JBQ3RCLE9BQU8sRUFBRSxDQUFDLHNCQUFzQixFQUFFLGdCQUFnQixDQUFDO29CQUNuRCxTQUFTLEVBQUUsQ0FBQyxrQkFBa0IsQ0FBQztpQkFDbEMiLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQgeyBDVVNUT01fRUxFTUVOVFNfU0NIRU1BLCBOZ01vZHVsZSwgTk9fRVJST1JTX1NDSEVNQSB9IGZyb20gXCJAYW5ndWxhci9jb3JlXCI7XG5pbXBvcnQgeyBTbWFydERpYWxvZyB9IGZyb20gXCIuL3NtYXJ0ZGlhbG9nLmNvbXBvbmVudFwiO1xuaW1wb3J0IHsgTWF0RGlhbG9nTW9kdWxlIH0gZnJvbSBcIkBhbmd1bGFyL21hdGVyaWFsL2RpYWxvZ1wiO1xuaW1wb3J0IHsgTWF0SWNvbk1vZHVsZSB9IGZyb20gXCJAYW5ndWxhci9tYXRlcmlhbC9pY29uXCI7XG5pbXBvcnQgeyBTbWFydGZvcm1Nb2R1bGUgfSBmcm9tIFwiQHNtYXJ0Yml0NGFsbC9mb3JtXCI7XG5pbXBvcnQgeyBTbWFydHRhYmxlTW9kdWxlIH0gZnJvbSBcIkBzbWFydGJpdDRhbGwvdGFibGVcIjtcbmltcG9ydCB7IE1hdENvbW1vbk1vZHVsZSB9IGZyb20gXCJAYW5ndWxhci9tYXRlcmlhbC9jb3JlXCI7XG5pbXBvcnQgeyBTbWFydGRpYWxvZ1NlcnZpY2UgfSBmcm9tIFwiLi9zbWFydGRpYWxvZy5zZXJ2aWNlXCI7XG5cbkBOZ01vZHVsZSh7XG4gICAgZGVjbGFyYXRpb25zOiBbU21hcnREaWFsb2ddLFxuICAgIGltcG9ydHM6IFtNYXREaWFsb2dNb2R1bGUsIE1hdENvbW1vbk1vZHVsZSwgTWF0SWNvbk1vZHVsZSwgU21hcnRmb3JtTW9kdWxlLCBTbWFydHRhYmxlTW9kdWxlXSxcbiAgICBleHBvcnRzOiBbU21hcnREaWFsb2ddLFxuICAgIHNjaGVtYXM6IFtDVVNUT01fRUxFTUVOVFNfU0NIRU1BLCBOT19FUlJPUlNfU0NIRU1BXSxcbiAgICBwcm92aWRlcnM6IFtTbWFydGRpYWxvZ1NlcnZpY2VdLFxufSlcbmV4cG9ydCBjbGFzcyBTbWFydGRpYWxvZ01vZHVsZSB7fVxuIl19