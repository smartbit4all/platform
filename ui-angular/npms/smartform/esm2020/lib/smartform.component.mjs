import { Component, Input } from '@angular/core';
import { SmartFormService } from './services/smartform.service';
import { SmartFormWidgetDirection } from './smartform.model';
import * as i0 from "@angular/core";
import * as i1 from "./services/smartform.service";
import * as i2 from "./widgets/smartformwidget/smartformwidget.component";
import * as i3 from "@angular/forms";
import * as i4 from "@angular/common";
export class SmartformComponent {
    constructor(service) {
        this.service = service;
        this.direction = SmartFormWidgetDirection;
    }
    ngOnInit() {
        this.form = this.service.toFormGroup(this.smartForm);
    }
    getForm() {
        return this.form;
    }
}
SmartformComponent.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartformComponent, deps: [{ token: i1.SmartFormService }], target: i0.ɵɵFactoryTarget.Component });
SmartformComponent.ɵcmp = i0.ɵɵngDeclareComponent({ minVersion: "12.0.0", version: "13.2.7", type: SmartformComponent, selector: "smartform", inputs: { smartForm: "smartForm" }, providers: [SmartFormService], ngImport: i0, template: "<form [formGroup]=\"form\" class=\"flex form\">\r\n    <h2>\r\n        {{ smartForm.name }}\r\n    </h2>\r\n    <div [ngClass]=\"smartForm.direction === direction.ROW ? 'row' : 'col'\">\r\n        <smartformwidget\r\n            *ngFor=\"let widget of smartForm.widgets\"\r\n            [widgetInstance]=\"widget\"\r\n            [form]=\"form\"\r\n            class=\"grid-item\"\r\n        ></smartformwidget>\r\n    </div>\r\n</form>\r\n", styles: [".col{display:flex;flex-direction:column;padding:1em}.row{display:flex;flex-wrap:wrap;padding:1em}.grid-item{padding:.25em;text-align:left}\n"], components: [{ type: i2.SmartformwidgetComponent, selector: "smartformwidget", inputs: ["form", "widgetInstance"] }], directives: [{ type: i3.ɵNgNoValidate, selector: "form:not([ngNoForm]):not([ngNativeValidate])" }, { type: i3.NgControlStatusGroup, selector: "[formGroupName],[formArrayName],[ngModelGroup],[formGroup],form:not([ngNoForm]),[ngForm]" }, { type: i3.FormGroupDirective, selector: "[formGroup]", inputs: ["formGroup"], outputs: ["ngSubmit"], exportAs: ["ngForm"] }, { type: i4.NgClass, selector: "[ngClass]", inputs: ["class", "ngClass"] }, { type: i4.NgForOf, selector: "[ngFor][ngForOf]", inputs: ["ngForOf", "ngForTrackBy", "ngForTemplate"] }] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartformComponent, decorators: [{
            type: Component,
            args: [{ selector: 'smartform', providers: [SmartFormService], template: "<form [formGroup]=\"form\" class=\"flex form\">\r\n    <h2>\r\n        {{ smartForm.name }}\r\n    </h2>\r\n    <div [ngClass]=\"smartForm.direction === direction.ROW ? 'row' : 'col'\">\r\n        <smartformwidget\r\n            *ngFor=\"let widget of smartForm.widgets\"\r\n            [widgetInstance]=\"widget\"\r\n            [form]=\"form\"\r\n            class=\"grid-item\"\r\n        ></smartformwidget>\r\n    </div>\r\n</form>\r\n", styles: [".col{display:flex;flex-direction:column;padding:1em}.row{display:flex;flex-wrap:wrap;padding:1em}.grid-item{padding:.25em;text-align:left}\n"] }]
        }], ctorParameters: function () { return [{ type: i1.SmartFormService }]; }, propDecorators: { smartForm: [{
                type: Input
            }] } });
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnRmb3JtLmNvbXBvbmVudC5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uLy4uLy4uL3Byb2plY3RzL3NtYXJ0Zm9ybS9zcmMvbGliL3NtYXJ0Zm9ybS5jb21wb25lbnQudHMiLCIuLi8uLi8uLi8uLi9wcm9qZWN0cy9zbWFydGZvcm0vc3JjL2xpYi9zbWFydGZvcm0uY29tcG9uZW50Lmh0bWwiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUEsT0FBTyxFQUFFLFNBQVMsRUFBRSxLQUFLLEVBQVUsTUFBTSxlQUFlLENBQUM7QUFFekQsT0FBTyxFQUFFLGdCQUFnQixFQUFFLE1BQU0sOEJBQThCLENBQUM7QUFDaEUsT0FBTyxFQUFhLHdCQUF3QixFQUFFLE1BQU0sbUJBQW1CLENBQUM7Ozs7OztBQVF4RSxNQUFNLE9BQU8sa0JBQWtCO0lBTTlCLFlBQW9CLE9BQXlCO1FBQXpCLFlBQU8sR0FBUCxPQUFPLENBQWtCO1FBRjdDLGNBQVMsR0FBRyx3QkFBd0IsQ0FBQztJQUVXLENBQUM7SUFFakQsUUFBUTtRQUNQLElBQUksQ0FBQyxJQUFJLEdBQUcsSUFBSSxDQUFDLE9BQU8sQ0FBQyxXQUFXLENBQUMsSUFBSSxDQUFDLFNBQVMsQ0FBQyxDQUFDO0lBQ3RELENBQUM7SUFFRCxPQUFPO1FBQ04sT0FBTyxJQUFJLENBQUMsSUFBSSxDQUFDO0lBQ2xCLENBQUM7OytHQWRXLGtCQUFrQjttR0FBbEIsa0JBQWtCLHdFQUZuQixDQUFDLGdCQUFnQixDQUFDLDBCQ1Q5QiwwYkFhQTsyRkRGYSxrQkFBa0I7a0JBTjlCLFNBQVM7K0JBQ0MsV0FBVyxhQUdWLENBQUMsZ0JBQWdCLENBQUM7dUdBR3BCLFNBQVM7c0JBQWpCLEtBQUsiLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQgeyBDb21wb25lbnQsIElucHV0LCBPbkluaXQgfSBmcm9tICdAYW5ndWxhci9jb3JlJztcclxuaW1wb3J0IHsgRm9ybUdyb3VwIH0gZnJvbSAnQGFuZ3VsYXIvZm9ybXMnO1xyXG5pbXBvcnQgeyBTbWFydEZvcm1TZXJ2aWNlIH0gZnJvbSAnLi9zZXJ2aWNlcy9zbWFydGZvcm0uc2VydmljZSc7XHJcbmltcG9ydCB7IFNtYXJ0Rm9ybSwgU21hcnRGb3JtV2lkZ2V0RGlyZWN0aW9uIH0gZnJvbSAnLi9zbWFydGZvcm0ubW9kZWwnO1xyXG5cclxuQENvbXBvbmVudCh7XHJcblx0c2VsZWN0b3I6ICdzbWFydGZvcm0nLFxyXG5cdHRlbXBsYXRlVXJsOiAnLi9zbWFydGZvcm0uY29tcG9uZW50Lmh0bWwnLFxyXG5cdHN0eWxlVXJsczogWycuL3NtYXJ0Zm9ybS5jb21wb25lbnQuY3NzJ10sXHJcblx0cHJvdmlkZXJzOiBbU21hcnRGb3JtU2VydmljZV1cclxufSlcclxuZXhwb3J0IGNsYXNzIFNtYXJ0Zm9ybUNvbXBvbmVudCBpbXBsZW1lbnRzIE9uSW5pdCB7XHJcblx0QElucHV0KCkgc21hcnRGb3JtITogU21hcnRGb3JtO1xyXG5cdGZvcm0hOiBGb3JtR3JvdXA7XHJcblxyXG5cdGRpcmVjdGlvbiA9IFNtYXJ0Rm9ybVdpZGdldERpcmVjdGlvbjtcclxuXHJcblx0Y29uc3RydWN0b3IocHJpdmF0ZSBzZXJ2aWNlOiBTbWFydEZvcm1TZXJ2aWNlKSB7fVxyXG5cclxuXHRuZ09uSW5pdCgpOiB2b2lkIHtcclxuXHRcdHRoaXMuZm9ybSA9IHRoaXMuc2VydmljZS50b0Zvcm1Hcm91cCh0aGlzLnNtYXJ0Rm9ybSk7XHJcblx0fVxyXG5cclxuXHRnZXRGb3JtKCk6IEZvcm1Hcm91cCB7XHJcblx0XHRyZXR1cm4gdGhpcy5mb3JtO1xyXG5cdH1cclxufVxyXG4iLCI8Zm9ybSBbZm9ybUdyb3VwXT1cImZvcm1cIiBjbGFzcz1cImZsZXggZm9ybVwiPlxyXG4gICAgPGgyPlxyXG4gICAgICAgIHt7IHNtYXJ0Rm9ybS5uYW1lIH19XHJcbiAgICA8L2gyPlxyXG4gICAgPGRpdiBbbmdDbGFzc109XCJzbWFydEZvcm0uZGlyZWN0aW9uID09PSBkaXJlY3Rpb24uUk9XID8gJ3JvdycgOiAnY29sJ1wiPlxyXG4gICAgICAgIDxzbWFydGZvcm13aWRnZXRcclxuICAgICAgICAgICAgKm5nRm9yPVwibGV0IHdpZGdldCBvZiBzbWFydEZvcm0ud2lkZ2V0c1wiXHJcbiAgICAgICAgICAgIFt3aWRnZXRJbnN0YW5jZV09XCJ3aWRnZXRcIlxyXG4gICAgICAgICAgICBbZm9ybV09XCJmb3JtXCJcclxuICAgICAgICAgICAgY2xhc3M9XCJncmlkLWl0ZW1cIlxyXG4gICAgICAgID48L3NtYXJ0Zm9ybXdpZGdldD5cclxuICAgIDwvZGl2PlxyXG48L2Zvcm0+XHJcbiJdfQ==