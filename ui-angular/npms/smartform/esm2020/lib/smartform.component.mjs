import { Component, Input } from "@angular/core";
import { SmartFormService } from "./services/smartform.service";
import { SmartFormWidgetDirection } from "./smartform.model";
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
    submitForm() {
        if (this.form.status === "VALID") {
            return this.service.toSmartForm(this.form, this.smartForm);
        }
        else {
            throw new Error(`The form status is ${this.form.status}.`);
        }
    }
}
SmartformComponent.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartformComponent, deps: [{ token: i1.SmartFormService }], target: i0.ɵɵFactoryTarget.Component });
SmartformComponent.ɵcmp = i0.ɵɵngDeclareComponent({ minVersion: "12.0.0", version: "13.2.7", type: SmartformComponent, selector: "smartform", inputs: { smartForm: "smartForm" }, providers: [SmartFormService], ngImport: i0, template: "<form [formGroup]=\"form\" class=\"flex form\">\n    <h2>\n        {{ smartForm.name }}\n    </h2>\n    <div [ngClass]=\"smartForm.direction === direction.ROW ? 'row' : 'col'\">\n        <smartformwidget\n            *ngFor=\"let widget of smartForm.widgets\"\n            [widgetInstance]=\"widget\"\n            [form]=\"form\"\n            class=\"grid-item\"\n        ></smartformwidget>\n    </div>\n</form>\n", styles: [".col{display:flex;flex-direction:column;padding:1em}.row{display:flex;flex-wrap:wrap;padding:1em}.grid-item{padding:.25em;text-align:left}\n"], components: [{ type: i2.SmartformwidgetComponent, selector: "smartformwidget", inputs: ["form", "widgetInstance"] }], directives: [{ type: i3.ɵNgNoValidate, selector: "form:not([ngNoForm]):not([ngNativeValidate])" }, { type: i3.NgControlStatusGroup, selector: "[formGroupName],[formArrayName],[ngModelGroup],[formGroup],form:not([ngNoForm]),[ngForm]" }, { type: i3.FormGroupDirective, selector: "[formGroup]", inputs: ["formGroup"], outputs: ["ngSubmit"], exportAs: ["ngForm"] }, { type: i4.NgClass, selector: "[ngClass]", inputs: ["class", "ngClass"] }, { type: i4.NgForOf, selector: "[ngFor][ngForOf]", inputs: ["ngForOf", "ngForTrackBy", "ngForTemplate"] }] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartformComponent, decorators: [{
            type: Component,
            args: [{ selector: "smartform", providers: [SmartFormService], template: "<form [formGroup]=\"form\" class=\"flex form\">\n    <h2>\n        {{ smartForm.name }}\n    </h2>\n    <div [ngClass]=\"smartForm.direction === direction.ROW ? 'row' : 'col'\">\n        <smartformwidget\n            *ngFor=\"let widget of smartForm.widgets\"\n            [widgetInstance]=\"widget\"\n            [form]=\"form\"\n            class=\"grid-item\"\n        ></smartformwidget>\n    </div>\n</form>\n", styles: [".col{display:flex;flex-direction:column;padding:1em}.row{display:flex;flex-wrap:wrap;padding:1em}.grid-item{padding:.25em;text-align:left}\n"] }]
        }], ctorParameters: function () { return [{ type: i1.SmartFormService }]; }, propDecorators: { smartForm: [{
                type: Input
            }] } });
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnRmb3JtLmNvbXBvbmVudC5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uLy4uLy4uL3Byb2plY3RzL3NtYXJ0Zm9ybS9zcmMvbGliL3NtYXJ0Zm9ybS5jb21wb25lbnQudHMiLCIuLi8uLi8uLi8uLi9wcm9qZWN0cy9zbWFydGZvcm0vc3JjL2xpYi9zbWFydGZvcm0uY29tcG9uZW50Lmh0bWwiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUEsT0FBTyxFQUFFLFNBQVMsRUFBRSxLQUFLLEVBQVUsTUFBTSxlQUFlLENBQUM7QUFFekQsT0FBTyxFQUFFLGdCQUFnQixFQUFFLE1BQU0sOEJBQThCLENBQUM7QUFDaEUsT0FBTyxFQUFhLHdCQUF3QixFQUFFLE1BQU0sbUJBQW1CLENBQUM7Ozs7OztBQVF4RSxNQUFNLE9BQU8sa0JBQWtCO0lBTzNCLFlBQW9CLE9BQXlCO1FBQXpCLFlBQU8sR0FBUCxPQUFPLENBQWtCO1FBRjdDLGNBQVMsR0FBRyx3QkFBd0IsQ0FBQztJQUVXLENBQUM7SUFFakQsUUFBUTtRQUNKLElBQUksQ0FBQyxJQUFJLEdBQUcsSUFBSSxDQUFDLE9BQU8sQ0FBQyxXQUFXLENBQUMsSUFBSSxDQUFDLFNBQVMsQ0FBQyxDQUFDO0lBQ3pELENBQUM7SUFFRCxPQUFPO1FBQ0gsT0FBTyxJQUFJLENBQUMsSUFBSSxDQUFDO0lBQ3JCLENBQUM7SUFFRCxVQUFVO1FBQ04sSUFBSSxJQUFJLENBQUMsSUFBSSxDQUFDLE1BQU0sS0FBSyxPQUFPLEVBQUU7WUFDOUIsT0FBTyxJQUFJLENBQUMsT0FBTyxDQUFDLFdBQVcsQ0FBQyxJQUFJLENBQUMsSUFBSSxFQUFFLElBQUksQ0FBQyxTQUFTLENBQUMsQ0FBQztTQUM5RDthQUFNO1lBQ0gsTUFBTSxJQUFJLEtBQUssQ0FBQyxzQkFBc0IsSUFBSSxDQUFDLElBQUksQ0FBQyxNQUFNLEdBQUcsQ0FBQyxDQUFDO1NBQzlEO0lBQ0wsQ0FBQzs7K0dBdkJRLGtCQUFrQjttR0FBbEIsa0JBQWtCLHdFQUZoQixDQUFDLGdCQUFnQixDQUFDLDBCQ1RqQyxnYUFhQTsyRkRGYSxrQkFBa0I7a0JBTjlCLFNBQVM7K0JBQ0ksV0FBVyxhQUdWLENBQUMsZ0JBQWdCLENBQUM7dUdBR3BCLFNBQVM7c0JBQWpCLEtBQUsiLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQgeyBDb21wb25lbnQsIElucHV0LCBPbkluaXQgfSBmcm9tIFwiQGFuZ3VsYXIvY29yZVwiO1xuaW1wb3J0IHsgRm9ybUdyb3VwIH0gZnJvbSBcIkBhbmd1bGFyL2Zvcm1zXCI7XG5pbXBvcnQgeyBTbWFydEZvcm1TZXJ2aWNlIH0gZnJvbSBcIi4vc2VydmljZXMvc21hcnRmb3JtLnNlcnZpY2VcIjtcbmltcG9ydCB7IFNtYXJ0Rm9ybSwgU21hcnRGb3JtV2lkZ2V0RGlyZWN0aW9uIH0gZnJvbSBcIi4vc21hcnRmb3JtLm1vZGVsXCI7XG5cbkBDb21wb25lbnQoe1xuICAgIHNlbGVjdG9yOiBcInNtYXJ0Zm9ybVwiLFxuICAgIHRlbXBsYXRlVXJsOiBcIi4vc21hcnRmb3JtLmNvbXBvbmVudC5odG1sXCIsXG4gICAgc3R5bGVVcmxzOiBbXCIuL3NtYXJ0Zm9ybS5jb21wb25lbnQuY3NzXCJdLFxuICAgIHByb3ZpZGVyczogW1NtYXJ0Rm9ybVNlcnZpY2VdLFxufSlcbmV4cG9ydCBjbGFzcyBTbWFydGZvcm1Db21wb25lbnQgaW1wbGVtZW50cyBPbkluaXQge1xuICAgIEBJbnB1dCgpIHNtYXJ0Rm9ybSE6IFNtYXJ0Rm9ybTtcblxuICAgIGZvcm0hOiBGb3JtR3JvdXA7XG5cbiAgICBkaXJlY3Rpb24gPSBTbWFydEZvcm1XaWRnZXREaXJlY3Rpb247XG5cbiAgICBjb25zdHJ1Y3Rvcihwcml2YXRlIHNlcnZpY2U6IFNtYXJ0Rm9ybVNlcnZpY2UpIHt9XG5cbiAgICBuZ09uSW5pdCgpOiB2b2lkIHtcbiAgICAgICAgdGhpcy5mb3JtID0gdGhpcy5zZXJ2aWNlLnRvRm9ybUdyb3VwKHRoaXMuc21hcnRGb3JtKTtcbiAgICB9XG5cbiAgICBnZXRGb3JtKCk6IEZvcm1Hcm91cCB7XG4gICAgICAgIHJldHVybiB0aGlzLmZvcm07XG4gICAgfVxuXG4gICAgc3VibWl0Rm9ybSgpOiBTbWFydEZvcm0ge1xuICAgICAgICBpZiAodGhpcy5mb3JtLnN0YXR1cyA9PT0gXCJWQUxJRFwiKSB7XG4gICAgICAgICAgICByZXR1cm4gdGhpcy5zZXJ2aWNlLnRvU21hcnRGb3JtKHRoaXMuZm9ybSwgdGhpcy5zbWFydEZvcm0pO1xuICAgICAgICB9IGVsc2Uge1xuICAgICAgICAgICAgdGhyb3cgbmV3IEVycm9yKGBUaGUgZm9ybSBzdGF0dXMgaXMgJHt0aGlzLmZvcm0uc3RhdHVzfS5gKTtcbiAgICAgICAgfVxuICAgIH1cbn1cbiIsIjxmb3JtIFtmb3JtR3JvdXBdPVwiZm9ybVwiIGNsYXNzPVwiZmxleCBmb3JtXCI+XG4gICAgPGgyPlxuICAgICAgICB7eyBzbWFydEZvcm0ubmFtZSB9fVxuICAgIDwvaDI+XG4gICAgPGRpdiBbbmdDbGFzc109XCJzbWFydEZvcm0uZGlyZWN0aW9uID09PSBkaXJlY3Rpb24uUk9XID8gJ3JvdycgOiAnY29sJ1wiPlxuICAgICAgICA8c21hcnRmb3Jtd2lkZ2V0XG4gICAgICAgICAgICAqbmdGb3I9XCJsZXQgd2lkZ2V0IG9mIHNtYXJ0Rm9ybS53aWRnZXRzXCJcbiAgICAgICAgICAgIFt3aWRnZXRJbnN0YW5jZV09XCJ3aWRnZXRcIlxuICAgICAgICAgICAgW2Zvcm1dPVwiZm9ybVwiXG4gICAgICAgICAgICBjbGFzcz1cImdyaWQtaXRlbVwiXG4gICAgICAgID48L3NtYXJ0Zm9ybXdpZGdldD5cbiAgICA8L2Rpdj5cbjwvZm9ybT5cbiJdfQ==