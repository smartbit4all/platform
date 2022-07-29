import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { SmartformComponent } from "./smartform.component";
import { MatChipsModule } from "@angular/material/chips";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatSelectModule } from "@angular/material/select";
import { MatButtonModule } from "@angular/material/button";
import { MatInputModule } from "@angular/material/input";
import { MatIconModule } from "@angular/material/icon";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatNativeDateModule } from "@angular/material/core";
import { MatRadioModule } from "@angular/material/radio";
import { SmartformwidgetComponent } from "./widgets/smartformwidget/smartformwidget.component";
import { SmartfileuploaderComponent } from "./smartfileuploader/smartfileuploader.component";
import { MatCommonModule } from "@angular/material/core";
import { BrowserModule } from "@angular/platform-browser";
import { SmartFormService } from "./services/smartform.service";
import * as i0 from "@angular/core";
export class SmartformModule {
}
SmartformModule.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartformModule, deps: [], target: i0.ɵɵFactoryTarget.NgModule });
SmartformModule.ɵmod = i0.ɵɵngDeclareNgModule({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartformModule, declarations: [SmartformComponent, SmartformwidgetComponent, SmartfileuploaderComponent], imports: [BrowserModule,
        MatCommonModule,
        MatChipsModule,
        FormsModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatCheckboxModule,
        MatSelectModule,
        MatButtonModule,
        MatInputModule,
        MatIconModule,
        MatDatepickerModule,
        MatNativeDateModule,
        MatRadioModule], exports: [SmartformComponent, SmartformwidgetComponent, SmartfileuploaderComponent] });
SmartformModule.ɵinj = i0.ɵɵngDeclareInjector({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartformModule, providers: [SmartFormService], imports: [[
            BrowserModule,
            MatCommonModule,
            MatChipsModule,
            FormsModule,
            ReactiveFormsModule,
            MatFormFieldModule,
            MatCheckboxModule,
            MatSelectModule,
            MatButtonModule,
            MatInputModule,
            MatIconModule,
            MatDatepickerModule,
            MatNativeDateModule,
            MatRadioModule,
        ]] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartformModule, decorators: [{
            type: NgModule,
            args: [{
                    declarations: [SmartformComponent, SmartformwidgetComponent, SmartfileuploaderComponent],
                    imports: [
                        BrowserModule,
                        MatCommonModule,
                        MatChipsModule,
                        FormsModule,
                        ReactiveFormsModule,
                        MatFormFieldModule,
                        MatCheckboxModule,
                        MatSelectModule,
                        MatButtonModule,
                        MatInputModule,
                        MatIconModule,
                        MatDatepickerModule,
                        MatNativeDateModule,
                        MatRadioModule,
                    ],
                    exports: [SmartformComponent, SmartformwidgetComponent, SmartfileuploaderComponent],
                    schemas: [CUSTOM_ELEMENTS_SCHEMA],
                    providers: [SmartFormService],
                }]
        }] });
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnRmb3JtLm1vZHVsZS5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uLy4uLy4uL3Byb2plY3RzL3NtYXJ0Zm9ybS9zcmMvbGliL3NtYXJ0Zm9ybS5tb2R1bGUudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUEsT0FBTyxFQUFFLHNCQUFzQixFQUFFLFFBQVEsRUFBRSxNQUFNLGVBQWUsQ0FBQztBQUNqRSxPQUFPLEVBQUUsa0JBQWtCLEVBQUUsTUFBTSx1QkFBdUIsQ0FBQztBQUMzRCxPQUFPLEVBQUUsY0FBYyxFQUFFLE1BQU0seUJBQXlCLENBQUM7QUFDekQsT0FBTyxFQUFFLFdBQVcsRUFBRSxtQkFBbUIsRUFBRSxNQUFNLGdCQUFnQixDQUFDO0FBQ2xFLE9BQU8sRUFBRSxrQkFBa0IsRUFBRSxNQUFNLDhCQUE4QixDQUFDO0FBQ2xFLE9BQU8sRUFBRSxpQkFBaUIsRUFBRSxNQUFNLDRCQUE0QixDQUFDO0FBQy9ELE9BQU8sRUFBRSxlQUFlLEVBQUUsTUFBTSwwQkFBMEIsQ0FBQztBQUMzRCxPQUFPLEVBQUUsZUFBZSxFQUFFLE1BQU0sMEJBQTBCLENBQUM7QUFDM0QsT0FBTyxFQUFFLGNBQWMsRUFBRSxNQUFNLHlCQUF5QixDQUFDO0FBQ3pELE9BQU8sRUFBRSxhQUFhLEVBQUUsTUFBTSx3QkFBd0IsQ0FBQztBQUN2RCxPQUFPLEVBQUUsbUJBQW1CLEVBQUUsTUFBTSw4QkFBOEIsQ0FBQztBQUNuRSxPQUFPLEVBQUUsbUJBQW1CLEVBQUUsTUFBTSx3QkFBd0IsQ0FBQztBQUM3RCxPQUFPLEVBQUUsY0FBYyxFQUFFLE1BQU0seUJBQXlCLENBQUM7QUFFekQsT0FBTyxFQUFFLHdCQUF3QixFQUFFLE1BQU0scURBQXFELENBQUM7QUFDL0YsT0FBTyxFQUFFLDBCQUEwQixFQUFFLE1BQU0saURBQWlELENBQUM7QUFDN0YsT0FBTyxFQUFFLGVBQWUsRUFBRSxNQUFNLHdCQUF3QixDQUFDO0FBQ3pELE9BQU8sRUFBRSxhQUFhLEVBQUUsTUFBTSwyQkFBMkIsQ0FBQztBQUMxRCxPQUFPLEVBQUUsZ0JBQWdCLEVBQUUsTUFBTSw4QkFBOEIsQ0FBQzs7QUF3QmhFLE1BQU0sT0FBTyxlQUFlOzs0R0FBZixlQUFlOzZHQUFmLGVBQWUsaUJBckJULGtCQUFrQixFQUFFLHdCQUF3QixFQUFFLDBCQUEwQixhQUVuRixhQUFhO1FBQ2IsZUFBZTtRQUNmLGNBQWM7UUFDZCxXQUFXO1FBQ1gsbUJBQW1CO1FBQ25CLGtCQUFrQjtRQUNsQixpQkFBaUI7UUFDakIsZUFBZTtRQUNmLGVBQWU7UUFDZixjQUFjO1FBQ2QsYUFBYTtRQUNiLG1CQUFtQjtRQUNuQixtQkFBbUI7UUFDbkIsY0FBYyxhQUVSLGtCQUFrQixFQUFFLHdCQUF3QixFQUFFLDBCQUEwQjs2R0FJekUsZUFBZSxhQUZiLENBQUMsZ0JBQWdCLENBQUMsWUFsQnBCO1lBQ0wsYUFBYTtZQUNiLGVBQWU7WUFDZixjQUFjO1lBQ2QsV0FBVztZQUNYLG1CQUFtQjtZQUNuQixrQkFBa0I7WUFDbEIsaUJBQWlCO1lBQ2pCLGVBQWU7WUFDZixlQUFlO1lBQ2YsY0FBYztZQUNkLGFBQWE7WUFDYixtQkFBbUI7WUFDbkIsbUJBQW1CO1lBQ25CLGNBQWM7U0FDakI7MkZBS1EsZUFBZTtrQkF0QjNCLFFBQVE7bUJBQUM7b0JBQ04sWUFBWSxFQUFFLENBQUMsa0JBQWtCLEVBQUUsd0JBQXdCLEVBQUUsMEJBQTBCLENBQUM7b0JBQ3hGLE9BQU8sRUFBRTt3QkFDTCxhQUFhO3dCQUNiLGVBQWU7d0JBQ2YsY0FBYzt3QkFDZCxXQUFXO3dCQUNYLG1CQUFtQjt3QkFDbkIsa0JBQWtCO3dCQUNsQixpQkFBaUI7d0JBQ2pCLGVBQWU7d0JBQ2YsZUFBZTt3QkFDZixjQUFjO3dCQUNkLGFBQWE7d0JBQ2IsbUJBQW1CO3dCQUNuQixtQkFBbUI7d0JBQ25CLGNBQWM7cUJBQ2pCO29CQUNELE9BQU8sRUFBRSxDQUFDLGtCQUFrQixFQUFFLHdCQUF3QixFQUFFLDBCQUEwQixDQUFDO29CQUNuRixPQUFPLEVBQUUsQ0FBQyxzQkFBc0IsQ0FBQztvQkFDakMsU0FBUyxFQUFFLENBQUMsZ0JBQWdCLENBQUM7aUJBQ2hDIiwic291cmNlc0NvbnRlbnQiOlsiaW1wb3J0IHsgQ1VTVE9NX0VMRU1FTlRTX1NDSEVNQSwgTmdNb2R1bGUgfSBmcm9tIFwiQGFuZ3VsYXIvY29yZVwiO1xyXG5pbXBvcnQgeyBTbWFydGZvcm1Db21wb25lbnQgfSBmcm9tIFwiLi9zbWFydGZvcm0uY29tcG9uZW50XCI7XHJcbmltcG9ydCB7IE1hdENoaXBzTW9kdWxlIH0gZnJvbSBcIkBhbmd1bGFyL21hdGVyaWFsL2NoaXBzXCI7XHJcbmltcG9ydCB7IEZvcm1zTW9kdWxlLCBSZWFjdGl2ZUZvcm1zTW9kdWxlIH0gZnJvbSBcIkBhbmd1bGFyL2Zvcm1zXCI7XHJcbmltcG9ydCB7IE1hdEZvcm1GaWVsZE1vZHVsZSB9IGZyb20gXCJAYW5ndWxhci9tYXRlcmlhbC9mb3JtLWZpZWxkXCI7XHJcbmltcG9ydCB7IE1hdENoZWNrYm94TW9kdWxlIH0gZnJvbSBcIkBhbmd1bGFyL21hdGVyaWFsL2NoZWNrYm94XCI7XHJcbmltcG9ydCB7IE1hdFNlbGVjdE1vZHVsZSB9IGZyb20gXCJAYW5ndWxhci9tYXRlcmlhbC9zZWxlY3RcIjtcclxuaW1wb3J0IHsgTWF0QnV0dG9uTW9kdWxlIH0gZnJvbSBcIkBhbmd1bGFyL21hdGVyaWFsL2J1dHRvblwiO1xyXG5pbXBvcnQgeyBNYXRJbnB1dE1vZHVsZSB9IGZyb20gXCJAYW5ndWxhci9tYXRlcmlhbC9pbnB1dFwiO1xyXG5pbXBvcnQgeyBNYXRJY29uTW9kdWxlIH0gZnJvbSBcIkBhbmd1bGFyL21hdGVyaWFsL2ljb25cIjtcclxuaW1wb3J0IHsgTWF0RGF0ZXBpY2tlck1vZHVsZSB9IGZyb20gXCJAYW5ndWxhci9tYXRlcmlhbC9kYXRlcGlja2VyXCI7XHJcbmltcG9ydCB7IE1hdE5hdGl2ZURhdGVNb2R1bGUgfSBmcm9tIFwiQGFuZ3VsYXIvbWF0ZXJpYWwvY29yZVwiO1xyXG5pbXBvcnQgeyBNYXRSYWRpb01vZHVsZSB9IGZyb20gXCJAYW5ndWxhci9tYXRlcmlhbC9yYWRpb1wiO1xyXG5cclxuaW1wb3J0IHsgU21hcnRmb3Jtd2lkZ2V0Q29tcG9uZW50IH0gZnJvbSBcIi4vd2lkZ2V0cy9zbWFydGZvcm13aWRnZXQvc21hcnRmb3Jtd2lkZ2V0LmNvbXBvbmVudFwiO1xyXG5pbXBvcnQgeyBTbWFydGZpbGV1cGxvYWRlckNvbXBvbmVudCB9IGZyb20gXCIuL3NtYXJ0ZmlsZXVwbG9hZGVyL3NtYXJ0ZmlsZXVwbG9hZGVyLmNvbXBvbmVudFwiO1xyXG5pbXBvcnQgeyBNYXRDb21tb25Nb2R1bGUgfSBmcm9tIFwiQGFuZ3VsYXIvbWF0ZXJpYWwvY29yZVwiO1xyXG5pbXBvcnQgeyBCcm93c2VyTW9kdWxlIH0gZnJvbSBcIkBhbmd1bGFyL3BsYXRmb3JtLWJyb3dzZXJcIjtcclxuaW1wb3J0IHsgU21hcnRGb3JtU2VydmljZSB9IGZyb20gXCIuL3NlcnZpY2VzL3NtYXJ0Zm9ybS5zZXJ2aWNlXCI7XHJcblxyXG5ATmdNb2R1bGUoe1xyXG4gICAgZGVjbGFyYXRpb25zOiBbU21hcnRmb3JtQ29tcG9uZW50LCBTbWFydGZvcm13aWRnZXRDb21wb25lbnQsIFNtYXJ0ZmlsZXVwbG9hZGVyQ29tcG9uZW50XSxcclxuICAgIGltcG9ydHM6IFtcclxuICAgICAgICBCcm93c2VyTW9kdWxlLFxyXG4gICAgICAgIE1hdENvbW1vbk1vZHVsZSxcclxuICAgICAgICBNYXRDaGlwc01vZHVsZSxcclxuICAgICAgICBGb3Jtc01vZHVsZSxcclxuICAgICAgICBSZWFjdGl2ZUZvcm1zTW9kdWxlLFxyXG4gICAgICAgIE1hdEZvcm1GaWVsZE1vZHVsZSxcclxuICAgICAgICBNYXRDaGVja2JveE1vZHVsZSxcclxuICAgICAgICBNYXRTZWxlY3RNb2R1bGUsXHJcbiAgICAgICAgTWF0QnV0dG9uTW9kdWxlLFxyXG4gICAgICAgIE1hdElucHV0TW9kdWxlLFxyXG4gICAgICAgIE1hdEljb25Nb2R1bGUsXHJcbiAgICAgICAgTWF0RGF0ZXBpY2tlck1vZHVsZSxcclxuICAgICAgICBNYXROYXRpdmVEYXRlTW9kdWxlLFxyXG4gICAgICAgIE1hdFJhZGlvTW9kdWxlLFxyXG4gICAgXSxcclxuICAgIGV4cG9ydHM6IFtTbWFydGZvcm1Db21wb25lbnQsIFNtYXJ0Zm9ybXdpZGdldENvbXBvbmVudCwgU21hcnRmaWxldXBsb2FkZXJDb21wb25lbnRdLFxyXG4gICAgc2NoZW1hczogW0NVU1RPTV9FTEVNRU5UU19TQ0hFTUFdLFxyXG4gICAgcHJvdmlkZXJzOiBbU21hcnRGb3JtU2VydmljZV0sXHJcbn0pXHJcbmV4cG9ydCBjbGFzcyBTbWFydGZvcm1Nb2R1bGUge31cclxuIl19