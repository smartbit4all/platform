import { CUSTOM_ELEMENTS_SCHEMA, NgModule, NO_ERRORS_SCHEMA } from "@angular/core";
import { SmartDialog } from "./smartdialog.component";
import { MatDialogModule } from "@angular/material/dialog";
import { MatIconModule } from "@angular/material/icon";
import { SmartformModule } from "@smartbit4all/form";
import { SmarttableModule } from "@smartbit4all/table";
import { MatCommonModule } from "@angular/material/core";
import { SmartdialogService } from "./smartdialog.service";
import { BrowserModule } from "@angular/platform-browser";
import * as i0 from "@angular/core";
export class SmartdialogModule {
}
SmartdialogModule.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartdialogModule, deps: [], target: i0.ɵɵFactoryTarget.NgModule });
SmartdialogModule.ɵmod = i0.ɵɵngDeclareNgModule({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartdialogModule, declarations: [SmartDialog], imports: [BrowserModule,
        MatDialogModule,
        MatCommonModule,
        MatIconModule,
        SmartformModule,
        SmarttableModule], exports: [SmartDialog] });
SmartdialogModule.ɵinj = i0.ɵɵngDeclareInjector({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartdialogModule, providers: [SmartdialogService], imports: [[
            BrowserModule,
            MatDialogModule,
            MatCommonModule,
            MatIconModule,
            SmartformModule,
            SmarttableModule,
        ]] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartdialogModule, decorators: [{
            type: NgModule,
            args: [{
                    declarations: [SmartDialog],
                    imports: [
                        BrowserModule,
                        MatDialogModule,
                        MatCommonModule,
                        MatIconModule,
                        SmartformModule,
                        SmarttableModule,
                    ],
                    exports: [SmartDialog],
                    schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
                    providers: [SmartdialogService],
                }]
        }] });
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnRkaWFsb2cubW9kdWxlLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vLi4vcHJvamVjdHMvc21hcnRkaWFsb2cvc3JjL2xpYi9zbWFydGRpYWxvZy5tb2R1bGUudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUEsT0FBTyxFQUFFLHNCQUFzQixFQUFFLFFBQVEsRUFBRSxnQkFBZ0IsRUFBRSxNQUFNLGVBQWUsQ0FBQztBQUNuRixPQUFPLEVBQUUsV0FBVyxFQUFFLE1BQU0seUJBQXlCLENBQUM7QUFDdEQsT0FBTyxFQUFFLGVBQWUsRUFBRSxNQUFNLDBCQUEwQixDQUFDO0FBQzNELE9BQU8sRUFBRSxhQUFhLEVBQUUsTUFBTSx3QkFBd0IsQ0FBQztBQUN2RCxPQUFPLEVBQUUsZUFBZSxFQUFFLE1BQU0sb0JBQW9CLENBQUM7QUFDckQsT0FBTyxFQUFFLGdCQUFnQixFQUFFLE1BQU0scUJBQXFCLENBQUM7QUFDdkQsT0FBTyxFQUFFLGVBQWUsRUFBRSxNQUFNLHdCQUF3QixDQUFDO0FBQ3pELE9BQU8sRUFBRSxrQkFBa0IsRUFBRSxNQUFNLHVCQUF1QixDQUFDO0FBQzNELE9BQU8sRUFBRSxhQUFhLEVBQUUsTUFBTSwyQkFBMkIsQ0FBQzs7QUFnQjFELE1BQU0sT0FBTyxpQkFBaUI7OzhHQUFqQixpQkFBaUI7K0dBQWpCLGlCQUFpQixpQkFiWCxXQUFXLGFBRXRCLGFBQWE7UUFDYixlQUFlO1FBQ2YsZUFBZTtRQUNmLGFBQWE7UUFDYixlQUFlO1FBQ2YsZ0JBQWdCLGFBRVYsV0FBVzsrR0FJWixpQkFBaUIsYUFGZixDQUFDLGtCQUFrQixDQUFDLFlBVnRCO1lBQ0wsYUFBYTtZQUNiLGVBQWU7WUFDZixlQUFlO1lBQ2YsYUFBYTtZQUNiLGVBQWU7WUFDZixnQkFBZ0I7U0FDbkI7MkZBS1EsaUJBQWlCO2tCQWQ3QixRQUFRO21CQUFDO29CQUNOLFlBQVksRUFBRSxDQUFDLFdBQVcsQ0FBQztvQkFDM0IsT0FBTyxFQUFFO3dCQUNMLGFBQWE7d0JBQ2IsZUFBZTt3QkFDZixlQUFlO3dCQUNmLGFBQWE7d0JBQ2IsZUFBZTt3QkFDZixnQkFBZ0I7cUJBQ25CO29CQUNELE9BQU8sRUFBRSxDQUFDLFdBQVcsQ0FBQztvQkFDdEIsT0FBTyxFQUFFLENBQUMsc0JBQXNCLEVBQUUsZ0JBQWdCLENBQUM7b0JBQ25ELFNBQVMsRUFBRSxDQUFDLGtCQUFrQixDQUFDO2lCQUNsQyIsInNvdXJjZXNDb250ZW50IjpbImltcG9ydCB7IENVU1RPTV9FTEVNRU5UU19TQ0hFTUEsIE5nTW9kdWxlLCBOT19FUlJPUlNfU0NIRU1BIH0gZnJvbSBcIkBhbmd1bGFyL2NvcmVcIjtcclxuaW1wb3J0IHsgU21hcnREaWFsb2cgfSBmcm9tIFwiLi9zbWFydGRpYWxvZy5jb21wb25lbnRcIjtcclxuaW1wb3J0IHsgTWF0RGlhbG9nTW9kdWxlIH0gZnJvbSBcIkBhbmd1bGFyL21hdGVyaWFsL2RpYWxvZ1wiO1xyXG5pbXBvcnQgeyBNYXRJY29uTW9kdWxlIH0gZnJvbSBcIkBhbmd1bGFyL21hdGVyaWFsL2ljb25cIjtcclxuaW1wb3J0IHsgU21hcnRmb3JtTW9kdWxlIH0gZnJvbSBcIkBzbWFydGJpdDRhbGwvZm9ybVwiO1xyXG5pbXBvcnQgeyBTbWFydHRhYmxlTW9kdWxlIH0gZnJvbSBcIkBzbWFydGJpdDRhbGwvdGFibGVcIjtcclxuaW1wb3J0IHsgTWF0Q29tbW9uTW9kdWxlIH0gZnJvbSBcIkBhbmd1bGFyL21hdGVyaWFsL2NvcmVcIjtcclxuaW1wb3J0IHsgU21hcnRkaWFsb2dTZXJ2aWNlIH0gZnJvbSBcIi4vc21hcnRkaWFsb2cuc2VydmljZVwiO1xyXG5pbXBvcnQgeyBCcm93c2VyTW9kdWxlIH0gZnJvbSBcIkBhbmd1bGFyL3BsYXRmb3JtLWJyb3dzZXJcIjtcclxuXHJcbkBOZ01vZHVsZSh7XHJcbiAgICBkZWNsYXJhdGlvbnM6IFtTbWFydERpYWxvZ10sXHJcbiAgICBpbXBvcnRzOiBbXHJcbiAgICAgICAgQnJvd3Nlck1vZHVsZSxcclxuICAgICAgICBNYXREaWFsb2dNb2R1bGUsXHJcbiAgICAgICAgTWF0Q29tbW9uTW9kdWxlLFxyXG4gICAgICAgIE1hdEljb25Nb2R1bGUsXHJcbiAgICAgICAgU21hcnRmb3JtTW9kdWxlLFxyXG4gICAgICAgIFNtYXJ0dGFibGVNb2R1bGUsXHJcbiAgICBdLFxyXG4gICAgZXhwb3J0czogW1NtYXJ0RGlhbG9nXSxcclxuICAgIHNjaGVtYXM6IFtDVVNUT01fRUxFTUVOVFNfU0NIRU1BLCBOT19FUlJPUlNfU0NIRU1BXSxcclxuICAgIHByb3ZpZGVyczogW1NtYXJ0ZGlhbG9nU2VydmljZV0sXHJcbn0pXHJcbmV4cG9ydCBjbGFzcyBTbWFydGRpYWxvZ01vZHVsZSB7fVxyXG4iXX0=