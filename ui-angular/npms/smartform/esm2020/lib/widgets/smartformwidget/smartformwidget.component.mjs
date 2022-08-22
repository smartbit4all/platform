import { COMMA, ENTER } from "@angular/cdk/keycodes";
import { Component, Input, ViewEncapsulation } from "@angular/core";
import { SmartFormWidgetDirection, SmartFormWidgetType, } from "../../smartform.model";
import * as i0 from "@angular/core";
import * as i1 from "@angular/material/form-field";
import * as i2 from "@angular/material/icon";
import * as i3 from "@angular/material/chips";
import * as i4 from "@angular/material/checkbox";
import * as i5 from "@angular/material/radio";
import * as i6 from "@angular/material/datepicker";
import * as i7 from "@angular/material/select";
import * as i8 from "@angular/material/core";
import * as i9 from "@angular/forms";
import * as i10 from "@angular/common";
import * as i11 from "@angular/material/input";
export class SmartformwidgetComponent {
    constructor() {
        this.smartFormWidgetType = SmartFormWidgetType;
        this.addOnBlur = true;
        this.separatorKeysCodes = [ENTER, COMMA];
    }
    add(event) {
        const value = (event.value || "").trim();
        if (value) {
            if ((this.widgetInstance.maxValues &&
                this.widgetInstance.valueList.length < this.widgetInstance.maxValues) ||
                !this.widgetInstance.maxValues) {
                this.widgetInstance.valueList.push({
                    key: value,
                    label: value,
                    type: SmartFormWidgetType.ITEM,
                    value: value,
                    callback: this.widgetInstance.callback,
                });
            }
        }
        // Clear the input value
        event.chipInput.clear();
    }
    remove(value) {
        const index = this.widgetInstance.valueList.indexOf(value);
        if (index >= 0) {
            this.widgetInstance.valueList.splice(index, 1);
        }
    }
    getDirection() {
        if (this.widgetInstance.direction === undefined) {
            return "";
        }
        else if (this.widgetInstance.direction === SmartFormWidgetDirection.COL) {
            return "direction-col";
        }
        else {
            return "direction-row";
        }
    }
}
SmartformwidgetComponent.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartformwidgetComponent, deps: [], target: i0.ɵɵFactoryTarget.Component });
SmartformwidgetComponent.ɵcmp = i0.ɵɵngDeclareComponent({ minVersion: "12.0.0", version: "13.2.7", type: SmartformwidgetComponent, selector: "smartformwidget", inputs: { form: "form", widgetInstance: "widgetInstance" }, ngImport: i0, template: "<div class=\"container\" [formGroup]=\"form\">\n  <div [ngSwitch]=\"widgetInstance.type\" class=\"container\">\n    <div *ngSwitchCase=\"smartFormWidgetType.TEXT_FIELD\">\n      <div *ngIf=\"widgetInstance.showLabel\">\n        <h2>{{ widgetInstance.label }}</h2>\n      </div>\n      <mat-form-field\n        class=\"input textField\"\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n        appearance=\"outline\"\n      >\n        <mat-label *ngIf=\"!widgetInstance.showLabel\">\n          {{ widgetInstance.label }}\n        </mat-label>\n        <input\n          [formControlName]=\"widgetInstance.key\"\n          [id]=\"widgetInstance.key\"\n          [type]=\"'string'\"\n          [value]=\"widgetInstance.value\"\n          placeholder=\"{{ widgetInstance.placeholder }}\"\n          matInput\n        />\n        <mat-icon *ngIf=\"widgetInstance.icon\" matSuffix>\n          {{ widgetInstance.icon }}\n        </mat-icon>\n      </mat-form-field>\n    </div>\n\n    <div *ngSwitchCase=\"smartFormWidgetType.TEXT_FIELD_CHIPS\">\n      <div *ngIf=\"widgetInstance.showLabel\">\n        <h2>{{ widgetInstance.label }}</h2>\n      </div>\n      <mat-form-field\n        class=\"input\"\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n        appearance=\"outline\"\n      >\n        <mat-label *ngIf=\"!widgetInstance.showLabel\">\n          {{ widgetInstance.label }}\n        </mat-label>\n        <mat-chip-list #chipList aria-label=\"{{ widgetInstance.label }}\">\n          <mat-chip\n            *ngFor=\"let value of widgetInstance.valueList\"\n            (removed)=\"remove(value)\"\n          >\n            {{ value.label }}\n            <button matChipRemove>\n              <mat-icon>cancel</mat-icon>\n            </button>\n          </mat-chip>\n          <input\n            placeholder=\"{{ widgetInstance.placeholder }}\"\n            [matChipInputFor]=\"chipList\"\n            [matChipInputSeparatorKeyCodes]=\"separatorKeysCodes\"\n            [matChipInputAddOnBlur]=\"addOnBlur\"\n            (matChipInputTokenEnd)=\"add($event)\"\n          />\n        </mat-chip-list>\n      </mat-form-field>\n    </div>\n\n    <div *ngSwitchCase=\"smartFormWidgetType.TEXT_BOX\">\n      <div *ngIf=\"widgetInstance.showLabel\">\n        <h2>{{ widgetInstance.label }}</h2>\n      </div>\n      <mat-form-field\n        class=\"input\"\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n        appearance=\"outline\"\n      >\n        <mat-label *ngIf=\"!widgetInstance.showLabel\">\n          {{ widgetInstance.label }}\n        </mat-label>\n        <textarea\n          [formControlName]=\"widgetInstance.key\"\n          [id]=\"widgetInstance.key\"\n          [type]=\"'string'\"\n          [value]=\"widgetInstance.value\"\n          placeholder=\"{{ widgetInstance.value }}\"\n          matInput\n        ></textarea>\n      </mat-form-field>\n    </div>\n\n    <div *ngSwitchCase=\"smartFormWidgetType.CHECK_BOX\">\n      <div *ngIf=\"widgetInstance.showLabel\">\n        <h2>{{ widgetInstance.label }}</h2>\n      </div>\n      <div\n        class=\"input checkbox\"\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n        appearance=\"outline\"\n      >\n        <mat-label *ngIf=\"!widgetInstance.showLabel\">\n          {{ widgetInstance.label }}\n        </mat-label>\n        <section class=\"checkbox-section\" [ngClass]=\"getDirection()\">\n          <mat-checkbox\n            *ngFor=\"let checkbox of widgetInstance.valueList\"\n            class=\"selecatbleObject\"\n            formControlName=\"{{ widgetInstance.key }}\"\n          >\n            {{ checkbox.label }}\n          </mat-checkbox>\n        </section>\n      </div>\n    </div>\n\n    <div *ngSwitchCase=\"smartFormWidgetType.RADIO_BUTTON\">\n      <div *ngIf=\"widgetInstance.showLabel\">\n        <h2>{{ widgetInstance.label }}</h2>\n      </div>\n      <mat-radio-group\n        class=\"input radio-section\"\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n        aria-label=\"{{ widgetInstance.label }}\"\n        appearance=\"outline\"\n        formControlName=\"{{ widgetInstance.key }}\"\n      >\n        <mat-label class=\"radioLabel\" *ngIf=\"!widgetInstance.showLabel\">\n          {{ widgetInstance.label }}\n        </mat-label>\n        <div [ngClass]=\"getDirection()\">\n          <mat-radio-button\n            class=\"selecatbleObject\"\n            *ngFor=\"let radio of widgetInstance.valueList\"\n            value=\"{{ radio.value }}\"\n            [ngClass]=\"getDirection()\"\n          >\n            {{ radio.label }}\n          </mat-radio-button>\n        </div>\n      </mat-radio-group>\n    </div>\n\n    <div *ngSwitchCase=\"smartFormWidgetType.DATE_PICKER\">\n      <div *ngIf=\"widgetInstance.showLabel\">\n        <h2>{{ widgetInstance.label }}</h2>\n      </div>\n      <mat-form-field\n        class=\"input\"\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n        appearance=\"outline\"\n      >\n        <mat-label *ngIf=\"!widgetInstance.showLabel\">\n          {{ widgetInstance.label }}\n        </mat-label>\n        <mat-datepicker-toggle matSuffix [for]=\"picker\"></mat-datepicker-toggle>\n        <mat-datepicker #picker></mat-datepicker>\n        <input\n          [formControlName]=\"widgetInstance.key\"\n          [id]=\"widgetInstance.key\"\n          [value]=\"widgetInstance.value\"\n          placeholder=\"{{ widgetInstance.value }}\"\n          matInput\n          [matDatepicker]=\"picker\"\n        />\n        <mat-hint>MM/DD/YYYY</mat-hint>\n      </mat-form-field>\n    </div>\n\n    <div *ngSwitchCase=\"smartFormWidgetType.SELECT\">\n      <div *ngIf=\"widgetInstance.showLabel\">\n        <h2>{{ widgetInstance.label }}</h2>\n      </div>\n      <mat-form-field\n        class=\"input\"\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n        appearance=\"outline\"\n      >\n        <mat-label *ngIf=\"!widgetInstance.showLabel\">\n          {{ widgetInstance.label }}\n        </mat-label>\n        <mat-select [formControlName]=\"widgetInstance.key\">\n          <mat-option\n            *ngFor=\"let option of widgetInstance.valueList\"\n            [value]=\"option.key\"\n            >{{ option.value }}</mat-option\n          >\n        </mat-select>\n      </mat-form-field>\n    </div>\n\n    <div *ngSwitchCase=\"smartFormWidgetType.SELECT_MULTIPLE\">\n      <div *ngIf=\"widgetInstance.showLabel\">\n        <h2>{{ widgetInstance.label }}</h2>\n      </div>\n      <div\n        class=\"input\"\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n        appearance=\"outline\"\n      >\n        <mat-label *ngIf=\"!widgetInstance.showLabel\">\n          {{ widgetInstance.label }}\n        </mat-label>\n        <mat-select [formControlName]=\"widgetInstance.key\" multiple>\n          <mat-option\n            *ngFor=\"let option of widgetInstance.valueList\"\n            [value]=\"option.key\"\n            >{{ option.value }}</mat-option\n          >\n        </mat-select>\n      </div>\n    </div>\n  </div>\n</div>\n", styles: [".checkbox-section,.radio-section{display:flex;flex-direction:column}.input{width:100%}.direction-col{display:flex;flex-direction:column}.direction-row{display:flex;flex-direction:row}.selecatbleObject{margin:.5em}.radioLabel{color:var(--primary-color);text-align:left!important}.container{height:100%}.input{height:100%;display:flex;flex-direction:column}.checkbox{flex-direction:column-reverse}.mat-form-field-wrapper{padding-bottom:0!important}.input .mat-standard-chip.mat-chip-with-trailing-icon{padding-right:12px}\n"], components: [{ type: i1.MatFormField, selector: "mat-form-field", inputs: ["color", "appearance", "hideRequiredMarker", "hintLabel", "floatLabel"], exportAs: ["matFormField"] }, { type: i2.MatIcon, selector: "mat-icon", inputs: ["color", "inline", "svgIcon", "fontSet", "fontIcon"], exportAs: ["matIcon"] }, { type: i3.MatChipList, selector: "mat-chip-list", inputs: ["errorStateMatcher", "multiple", "compareWith", "value", "required", "placeholder", "disabled", "aria-orientation", "selectable", "tabIndex"], outputs: ["change", "valueChange"], exportAs: ["matChipList"] }, { type: i4.MatCheckbox, selector: "mat-checkbox", inputs: ["disableRipple", "color", "tabIndex", "aria-label", "aria-labelledby", "aria-describedby", "id", "required", "labelPosition", "name", "value", "checked", "disabled", "indeterminate"], outputs: ["change", "indeterminateChange"], exportAs: ["matCheckbox"] }, { type: i5.MatRadioButton, selector: "mat-radio-button", inputs: ["disableRipple", "tabIndex"], exportAs: ["matRadioButton"] }, { type: i6.MatDatepickerToggle, selector: "mat-datepicker-toggle", inputs: ["for", "tabIndex", "aria-label", "disabled", "disableRipple"], exportAs: ["matDatepickerToggle"] }, { type: i6.MatDatepicker, selector: "mat-datepicker", exportAs: ["matDatepicker"] }, { type: i7.MatSelect, selector: "mat-select", inputs: ["disabled", "disableRipple", "tabIndex"], exportAs: ["matSelect"] }, { type: i8.MatOption, selector: "mat-option", exportAs: ["matOption"] }], directives: [{ type: i9.NgControlStatusGroup, selector: "[formGroupName],[formArrayName],[ngModelGroup],[formGroup],form:not([ngNoForm]),[ngForm]" }, { type: i9.FormGroupDirective, selector: "[formGroup]", inputs: ["formGroup"], outputs: ["ngSubmit"], exportAs: ["ngForm"] }, { type: i10.NgSwitch, selector: "[ngSwitch]", inputs: ["ngSwitch"] }, { type: i10.NgSwitchCase, selector: "[ngSwitchCase]", inputs: ["ngSwitchCase"] }, { type: i10.NgIf, selector: "[ngIf]", inputs: ["ngIf", "ngIfThen", "ngIfElse"] }, { type: i10.NgStyle, selector: "[ngStyle]", inputs: ["ngStyle"] }, { type: i1.MatLabel, selector: "mat-label" }, { type: i11.MatInput, selector: "input[matInput], textarea[matInput], select[matNativeControl],      input[matNativeControl], textarea[matNativeControl]", inputs: ["disabled", "id", "placeholder", "name", "required", "type", "errorStateMatcher", "aria-describedby", "value", "readonly"], exportAs: ["matInput"] }, { type: i9.DefaultValueAccessor, selector: "input:not([type=checkbox])[formControlName],textarea[formControlName],input:not([type=checkbox])[formControl],textarea[formControl],input:not([type=checkbox])[ngModel],textarea[ngModel],[ngDefaultControl]" }, { type: i9.NgControlStatus, selector: "[formControlName],[ngModel],[formControl]" }, { type: i9.FormControlName, selector: "[formControlName]", inputs: ["formControlName", "disabled", "ngModel"], outputs: ["ngModelChange"] }, { type: i1.MatSuffix, selector: "[matSuffix]" }, { type: i10.NgForOf, selector: "[ngFor][ngForOf]", inputs: ["ngForOf", "ngForTrackBy", "ngForTemplate"] }, { type: i3.MatChip, selector: "mat-basic-chip, [mat-basic-chip], mat-chip, [mat-chip]", inputs: ["color", "disableRipple", "tabIndex", "selected", "value", "selectable", "disabled", "removable"], outputs: ["selectionChange", "destroyed", "removed"], exportAs: ["matChip"] }, { type: i3.MatChipRemove, selector: "[matChipRemove]" }, { type: i3.MatChipInput, selector: "input[matChipInputFor]", inputs: ["matChipInputFor", "matChipInputAddOnBlur", "matChipInputSeparatorKeyCodes", "placeholder", "id", "disabled"], outputs: ["matChipInputTokenEnd"], exportAs: ["matChipInput", "matChipInputFor"] }, { type: i10.NgClass, selector: "[ngClass]", inputs: ["class", "ngClass"] }, { type: i5.MatRadioGroup, selector: "mat-radio-group", exportAs: ["matRadioGroup"] }, { type: i6.MatDatepickerInput, selector: "input[matDatepicker]", inputs: ["matDatepicker", "min", "max", "matDatepickerFilter"], exportAs: ["matDatepickerInput"] }, { type: i1.MatHint, selector: "mat-hint", inputs: ["align", "id"] }], encapsulation: i0.ViewEncapsulation.None });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartformwidgetComponent, decorators: [{
            type: Component,
            args: [{ selector: "smartformwidget", encapsulation: ViewEncapsulation.None, template: "<div class=\"container\" [formGroup]=\"form\">\n  <div [ngSwitch]=\"widgetInstance.type\" class=\"container\">\n    <div *ngSwitchCase=\"smartFormWidgetType.TEXT_FIELD\">\n      <div *ngIf=\"widgetInstance.showLabel\">\n        <h2>{{ widgetInstance.label }}</h2>\n      </div>\n      <mat-form-field\n        class=\"input textField\"\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n        appearance=\"outline\"\n      >\n        <mat-label *ngIf=\"!widgetInstance.showLabel\">\n          {{ widgetInstance.label }}\n        </mat-label>\n        <input\n          [formControlName]=\"widgetInstance.key\"\n          [id]=\"widgetInstance.key\"\n          [type]=\"'string'\"\n          [value]=\"widgetInstance.value\"\n          placeholder=\"{{ widgetInstance.placeholder }}\"\n          matInput\n        />\n        <mat-icon *ngIf=\"widgetInstance.icon\" matSuffix>\n          {{ widgetInstance.icon }}\n        </mat-icon>\n      </mat-form-field>\n    </div>\n\n    <div *ngSwitchCase=\"smartFormWidgetType.TEXT_FIELD_CHIPS\">\n      <div *ngIf=\"widgetInstance.showLabel\">\n        <h2>{{ widgetInstance.label }}</h2>\n      </div>\n      <mat-form-field\n        class=\"input\"\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n        appearance=\"outline\"\n      >\n        <mat-label *ngIf=\"!widgetInstance.showLabel\">\n          {{ widgetInstance.label }}\n        </mat-label>\n        <mat-chip-list #chipList aria-label=\"{{ widgetInstance.label }}\">\n          <mat-chip\n            *ngFor=\"let value of widgetInstance.valueList\"\n            (removed)=\"remove(value)\"\n          >\n            {{ value.label }}\n            <button matChipRemove>\n              <mat-icon>cancel</mat-icon>\n            </button>\n          </mat-chip>\n          <input\n            placeholder=\"{{ widgetInstance.placeholder }}\"\n            [matChipInputFor]=\"chipList\"\n            [matChipInputSeparatorKeyCodes]=\"separatorKeysCodes\"\n            [matChipInputAddOnBlur]=\"addOnBlur\"\n            (matChipInputTokenEnd)=\"add($event)\"\n          />\n        </mat-chip-list>\n      </mat-form-field>\n    </div>\n\n    <div *ngSwitchCase=\"smartFormWidgetType.TEXT_BOX\">\n      <div *ngIf=\"widgetInstance.showLabel\">\n        <h2>{{ widgetInstance.label }}</h2>\n      </div>\n      <mat-form-field\n        class=\"input\"\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n        appearance=\"outline\"\n      >\n        <mat-label *ngIf=\"!widgetInstance.showLabel\">\n          {{ widgetInstance.label }}\n        </mat-label>\n        <textarea\n          [formControlName]=\"widgetInstance.key\"\n          [id]=\"widgetInstance.key\"\n          [type]=\"'string'\"\n          [value]=\"widgetInstance.value\"\n          placeholder=\"{{ widgetInstance.value }}\"\n          matInput\n        ></textarea>\n      </mat-form-field>\n    </div>\n\n    <div *ngSwitchCase=\"smartFormWidgetType.CHECK_BOX\">\n      <div *ngIf=\"widgetInstance.showLabel\">\n        <h2>{{ widgetInstance.label }}</h2>\n      </div>\n      <div\n        class=\"input checkbox\"\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n        appearance=\"outline\"\n      >\n        <mat-label *ngIf=\"!widgetInstance.showLabel\">\n          {{ widgetInstance.label }}\n        </mat-label>\n        <section class=\"checkbox-section\" [ngClass]=\"getDirection()\">\n          <mat-checkbox\n            *ngFor=\"let checkbox of widgetInstance.valueList\"\n            class=\"selecatbleObject\"\n            formControlName=\"{{ widgetInstance.key }}\"\n          >\n            {{ checkbox.label }}\n          </mat-checkbox>\n        </section>\n      </div>\n    </div>\n\n    <div *ngSwitchCase=\"smartFormWidgetType.RADIO_BUTTON\">\n      <div *ngIf=\"widgetInstance.showLabel\">\n        <h2>{{ widgetInstance.label }}</h2>\n      </div>\n      <mat-radio-group\n        class=\"input radio-section\"\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n        aria-label=\"{{ widgetInstance.label }}\"\n        appearance=\"outline\"\n        formControlName=\"{{ widgetInstance.key }}\"\n      >\n        <mat-label class=\"radioLabel\" *ngIf=\"!widgetInstance.showLabel\">\n          {{ widgetInstance.label }}\n        </mat-label>\n        <div [ngClass]=\"getDirection()\">\n          <mat-radio-button\n            class=\"selecatbleObject\"\n            *ngFor=\"let radio of widgetInstance.valueList\"\n            value=\"{{ radio.value }}\"\n            [ngClass]=\"getDirection()\"\n          >\n            {{ radio.label }}\n          </mat-radio-button>\n        </div>\n      </mat-radio-group>\n    </div>\n\n    <div *ngSwitchCase=\"smartFormWidgetType.DATE_PICKER\">\n      <div *ngIf=\"widgetInstance.showLabel\">\n        <h2>{{ widgetInstance.label }}</h2>\n      </div>\n      <mat-form-field\n        class=\"input\"\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n        appearance=\"outline\"\n      >\n        <mat-label *ngIf=\"!widgetInstance.showLabel\">\n          {{ widgetInstance.label }}\n        </mat-label>\n        <mat-datepicker-toggle matSuffix [for]=\"picker\"></mat-datepicker-toggle>\n        <mat-datepicker #picker></mat-datepicker>\n        <input\n          [formControlName]=\"widgetInstance.key\"\n          [id]=\"widgetInstance.key\"\n          [value]=\"widgetInstance.value\"\n          placeholder=\"{{ widgetInstance.value }}\"\n          matInput\n          [matDatepicker]=\"picker\"\n        />\n        <mat-hint>MM/DD/YYYY</mat-hint>\n      </mat-form-field>\n    </div>\n\n    <div *ngSwitchCase=\"smartFormWidgetType.SELECT\">\n      <div *ngIf=\"widgetInstance.showLabel\">\n        <h2>{{ widgetInstance.label }}</h2>\n      </div>\n      <mat-form-field\n        class=\"input\"\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n        appearance=\"outline\"\n      >\n        <mat-label *ngIf=\"!widgetInstance.showLabel\">\n          {{ widgetInstance.label }}\n        </mat-label>\n        <mat-select [formControlName]=\"widgetInstance.key\">\n          <mat-option\n            *ngFor=\"let option of widgetInstance.valueList\"\n            [value]=\"option.key\"\n            >{{ option.value }}</mat-option\n          >\n        </mat-select>\n      </mat-form-field>\n    </div>\n\n    <div *ngSwitchCase=\"smartFormWidgetType.SELECT_MULTIPLE\">\n      <div *ngIf=\"widgetInstance.showLabel\">\n        <h2>{{ widgetInstance.label }}</h2>\n      </div>\n      <div\n        class=\"input\"\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n        appearance=\"outline\"\n      >\n        <mat-label *ngIf=\"!widgetInstance.showLabel\">\n          {{ widgetInstance.label }}\n        </mat-label>\n        <mat-select [formControlName]=\"widgetInstance.key\" multiple>\n          <mat-option\n            *ngFor=\"let option of widgetInstance.valueList\"\n            [value]=\"option.key\"\n            >{{ option.value }}</mat-option\n          >\n        </mat-select>\n      </div>\n    </div>\n  </div>\n</div>\n", styles: [".checkbox-section,.radio-section{display:flex;flex-direction:column}.input{width:100%}.direction-col{display:flex;flex-direction:column}.direction-row{display:flex;flex-direction:row}.selecatbleObject{margin:.5em}.radioLabel{color:var(--primary-color);text-align:left!important}.container{height:100%}.input{height:100%;display:flex;flex-direction:column}.checkbox{flex-direction:column-reverse}.mat-form-field-wrapper{padding-bottom:0!important}.input .mat-standard-chip.mat-chip-with-trailing-icon{padding-right:12px}\n"] }]
        }], ctorParameters: function () { return []; }, propDecorators: { form: [{
                type: Input
            }], widgetInstance: [{
                type: Input
            }] } });
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnRmb3Jtd2lkZ2V0LmNvbXBvbmVudC5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uLy4uLy4uLy4uLy4uL3Byb2plY3RzL3NtYXJ0Zm9ybS9zcmMvbGliL3dpZGdldHMvc21hcnRmb3Jtd2lkZ2V0L3NtYXJ0Zm9ybXdpZGdldC5jb21wb25lbnQudHMiLCIuLi8uLi8uLi8uLi8uLi8uLi9wcm9qZWN0cy9zbWFydGZvcm0vc3JjL2xpYi93aWRnZXRzL3NtYXJ0Zm9ybXdpZGdldC9zbWFydGZvcm13aWRnZXQuY29tcG9uZW50Lmh0bWwiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUEsT0FBTyxFQUFFLEtBQUssRUFBRSxLQUFLLEVBQUUsTUFBTSx1QkFBdUIsQ0FBQztBQUNyRCxPQUFPLEVBQUUsU0FBUyxFQUFFLEtBQUssRUFBRSxpQkFBaUIsRUFBRSxNQUFNLGVBQWUsQ0FBQztBQUdwRSxPQUFPLEVBRUgsd0JBQXdCLEVBQ3hCLG1CQUFtQixHQUN0QixNQUFNLHVCQUF1QixDQUFDOzs7Ozs7Ozs7Ozs7O0FBUS9CLE1BQU0sT0FBTyx3QkFBd0I7SUFNakM7UUFGQSx3QkFBbUIsR0FBRyxtQkFBbUIsQ0FBQztRQUkxQyxjQUFTLEdBQUcsSUFBSSxDQUFDO1FBQ1IsdUJBQWtCLEdBQUcsQ0FBQyxLQUFLLEVBQUUsS0FBSyxDQUFVLENBQUM7SUFIdkMsQ0FBQztJQUtoQixHQUFHLENBQUMsS0FBd0I7UUFDeEIsTUFBTSxLQUFLLEdBQUcsQ0FBQyxLQUFLLENBQUMsS0FBSyxJQUFJLEVBQUUsQ0FBQyxDQUFDLElBQUksRUFBRSxDQUFDO1FBRXpDLElBQUksS0FBSyxFQUFFO1lBQ1AsSUFDSSxDQUFDLElBQUksQ0FBQyxjQUFjLENBQUMsU0FBUztnQkFDMUIsSUFBSSxDQUFDLGNBQWMsQ0FBQyxTQUFVLENBQUMsTUFBTSxHQUFHLElBQUksQ0FBQyxjQUFjLENBQUMsU0FBUyxDQUFDO2dCQUMxRSxDQUFDLElBQUksQ0FBQyxjQUFjLENBQUMsU0FBUyxFQUNoQztnQkFDRSxJQUFJLENBQUMsY0FBYyxDQUFDLFNBQVUsQ0FBQyxJQUFJLENBQUM7b0JBQ2hDLEdBQUcsRUFBRSxLQUFLO29CQUNWLEtBQUssRUFBRSxLQUFLO29CQUNaLElBQUksRUFBRSxtQkFBbUIsQ0FBQyxJQUFJO29CQUM5QixLQUFLLEVBQUUsS0FBSztvQkFDWixRQUFRLEVBQUUsSUFBSSxDQUFDLGNBQWMsQ0FBQyxRQUFRO2lCQUN6QyxDQUFDLENBQUM7YUFDTjtTQUNKO1FBRUQsd0JBQXdCO1FBQ3hCLEtBQUssQ0FBQyxTQUFVLENBQUMsS0FBSyxFQUFFLENBQUM7SUFDN0IsQ0FBQztJQUVELE1BQU0sQ0FBQyxLQUEyQjtRQUM5QixNQUFNLEtBQUssR0FBRyxJQUFJLENBQUMsY0FBYyxDQUFDLFNBQVUsQ0FBQyxPQUFPLENBQUMsS0FBSyxDQUFDLENBQUM7UUFFNUQsSUFBSSxLQUFLLElBQUksQ0FBQyxFQUFFO1lBQ1osSUFBSSxDQUFDLGNBQWMsQ0FBQyxTQUFVLENBQUMsTUFBTSxDQUFDLEtBQUssRUFBRSxDQUFDLENBQUMsQ0FBQztTQUNuRDtJQUNMLENBQUM7SUFFRCxZQUFZO1FBQ1IsSUFBSSxJQUFJLENBQUMsY0FBYyxDQUFDLFNBQVMsS0FBSyxTQUFTLEVBQUU7WUFDN0MsT0FBTyxFQUFFLENBQUM7U0FDYjthQUFNLElBQUksSUFBSSxDQUFDLGNBQWMsQ0FBQyxTQUFTLEtBQUssd0JBQXdCLENBQUMsR0FBRyxFQUFFO1lBQ3ZFLE9BQU8sZUFBZSxDQUFDO1NBQzFCO2FBQU07WUFDSCxPQUFPLGVBQWUsQ0FBQztTQUMxQjtJQUNMLENBQUM7O3FIQWxEUSx3QkFBd0I7eUdBQXhCLHdCQUF3QixtSENoQnJDLDArTkE4TUE7MkZEOUxhLHdCQUF3QjtrQkFOcEMsU0FBUzsrQkFDSSxpQkFBaUIsaUJBR1osaUJBQWlCLENBQUMsSUFBSTswRUFHNUIsSUFBSTtzQkFBWixLQUFLO2dCQUNHLGNBQWM7c0JBQXRCLEtBQUsiLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQgeyBDT01NQSwgRU5URVIgfSBmcm9tIFwiQGFuZ3VsYXIvY2RrL2tleWNvZGVzXCI7XG5pbXBvcnQgeyBDb21wb25lbnQsIElucHV0LCBWaWV3RW5jYXBzdWxhdGlvbiB9IGZyb20gXCJAYW5ndWxhci9jb3JlXCI7XG5pbXBvcnQgeyBGb3JtR3JvdXAgfSBmcm9tIFwiQGFuZ3VsYXIvZm9ybXNcIjtcbmltcG9ydCB7IE1hdENoaXBJbnB1dEV2ZW50IH0gZnJvbSBcIkBhbmd1bGFyL21hdGVyaWFsL2NoaXBzXCI7XG5pbXBvcnQge1xuICAgIFNtYXJ0Rm9ybVdpZGdldCxcbiAgICBTbWFydEZvcm1XaWRnZXREaXJlY3Rpb24sXG4gICAgU21hcnRGb3JtV2lkZ2V0VHlwZSxcbn0gZnJvbSBcIi4uLy4uL3NtYXJ0Zm9ybS5tb2RlbFwiO1xuXG5AQ29tcG9uZW50KHtcbiAgICBzZWxlY3RvcjogXCJzbWFydGZvcm13aWRnZXRcIixcbiAgICB0ZW1wbGF0ZVVybDogXCIuL3NtYXJ0Zm9ybXdpZGdldC5jb21wb25lbnQuaHRtbFwiLFxuICAgIHN0eWxlVXJsczogW1wiLi9zbWFydGZvcm13aWRnZXQuY29tcG9uZW50LmNzc1wiXSxcbiAgICBlbmNhcHN1bGF0aW9uOiBWaWV3RW5jYXBzdWxhdGlvbi5Ob25lLFxufSlcbmV4cG9ydCBjbGFzcyBTbWFydGZvcm13aWRnZXRDb21wb25lbnQge1xuICAgIEBJbnB1dCgpIGZvcm0hOiBGb3JtR3JvdXA7XG4gICAgQElucHV0KCkgd2lkZ2V0SW5zdGFuY2UhOiBTbWFydEZvcm1XaWRnZXQ8YW55PjtcblxuICAgIHNtYXJ0Rm9ybVdpZGdldFR5cGUgPSBTbWFydEZvcm1XaWRnZXRUeXBlO1xuXG4gICAgY29uc3RydWN0b3IoKSB7fVxuXG4gICAgYWRkT25CbHVyID0gdHJ1ZTtcbiAgICByZWFkb25seSBzZXBhcmF0b3JLZXlzQ29kZXMgPSBbRU5URVIsIENPTU1BXSBhcyBjb25zdDtcblxuICAgIGFkZChldmVudDogTWF0Q2hpcElucHV0RXZlbnQpOiB2b2lkIHtcbiAgICAgICAgY29uc3QgdmFsdWUgPSAoZXZlbnQudmFsdWUgfHwgXCJcIikudHJpbSgpO1xuXG4gICAgICAgIGlmICh2YWx1ZSkge1xuICAgICAgICAgICAgaWYgKFxuICAgICAgICAgICAgICAgICh0aGlzLndpZGdldEluc3RhbmNlLm1heFZhbHVlcyAmJlxuICAgICAgICAgICAgICAgICAgICB0aGlzLndpZGdldEluc3RhbmNlLnZhbHVlTGlzdCEubGVuZ3RoIDwgdGhpcy53aWRnZXRJbnN0YW5jZS5tYXhWYWx1ZXMpIHx8XG4gICAgICAgICAgICAgICAgIXRoaXMud2lkZ2V0SW5zdGFuY2UubWF4VmFsdWVzXG4gICAgICAgICAgICApIHtcbiAgICAgICAgICAgICAgICB0aGlzLndpZGdldEluc3RhbmNlLnZhbHVlTGlzdCEucHVzaCh7XG4gICAgICAgICAgICAgICAgICAgIGtleTogdmFsdWUsXG4gICAgICAgICAgICAgICAgICAgIGxhYmVsOiB2YWx1ZSxcbiAgICAgICAgICAgICAgICAgICAgdHlwZTogU21hcnRGb3JtV2lkZ2V0VHlwZS5JVEVNLFxuICAgICAgICAgICAgICAgICAgICB2YWx1ZTogdmFsdWUsXG4gICAgICAgICAgICAgICAgICAgIGNhbGxiYWNrOiB0aGlzLndpZGdldEluc3RhbmNlLmNhbGxiYWNrLFxuICAgICAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgfVxuICAgICAgICB9XG5cbiAgICAgICAgLy8gQ2xlYXIgdGhlIGlucHV0IHZhbHVlXG4gICAgICAgIGV2ZW50LmNoaXBJbnB1dCEuY2xlYXIoKTtcbiAgICB9XG5cbiAgICByZW1vdmUodmFsdWU6IFNtYXJ0Rm9ybVdpZGdldDxhbnk+KTogdm9pZCB7XG4gICAgICAgIGNvbnN0IGluZGV4ID0gdGhpcy53aWRnZXRJbnN0YW5jZS52YWx1ZUxpc3QhLmluZGV4T2YodmFsdWUpO1xuXG4gICAgICAgIGlmIChpbmRleCA+PSAwKSB7XG4gICAgICAgICAgICB0aGlzLndpZGdldEluc3RhbmNlLnZhbHVlTGlzdCEuc3BsaWNlKGluZGV4LCAxKTtcbiAgICAgICAgfVxuICAgIH1cblxuICAgIGdldERpcmVjdGlvbigpOiBzdHJpbmcge1xuICAgICAgICBpZiAodGhpcy53aWRnZXRJbnN0YW5jZS5kaXJlY3Rpb24gPT09IHVuZGVmaW5lZCkge1xuICAgICAgICAgICAgcmV0dXJuIFwiXCI7XG4gICAgICAgIH0gZWxzZSBpZiAodGhpcy53aWRnZXRJbnN0YW5jZS5kaXJlY3Rpb24gPT09IFNtYXJ0Rm9ybVdpZGdldERpcmVjdGlvbi5DT0wpIHtcbiAgICAgICAgICAgIHJldHVybiBcImRpcmVjdGlvbi1jb2xcIjtcbiAgICAgICAgfSBlbHNlIHtcbiAgICAgICAgICAgIHJldHVybiBcImRpcmVjdGlvbi1yb3dcIjtcbiAgICAgICAgfVxuICAgIH1cbn1cbiIsIjxkaXYgY2xhc3M9XCJjb250YWluZXJcIiBbZm9ybUdyb3VwXT1cImZvcm1cIj5cbiAgPGRpdiBbbmdTd2l0Y2hdPVwid2lkZ2V0SW5zdGFuY2UudHlwZVwiIGNsYXNzPVwiY29udGFpbmVyXCI+XG4gICAgPGRpdiAqbmdTd2l0Y2hDYXNlPVwic21hcnRGb3JtV2lkZ2V0VHlwZS5URVhUX0ZJRUxEXCI+XG4gICAgICA8ZGl2ICpuZ0lmPVwid2lkZ2V0SW5zdGFuY2Uuc2hvd0xhYmVsXCI+XG4gICAgICAgIDxoMj57eyB3aWRnZXRJbnN0YW5jZS5sYWJlbCB9fTwvaDI+XG4gICAgICA8L2Rpdj5cbiAgICAgIDxtYXQtZm9ybS1maWVsZFxuICAgICAgICBjbGFzcz1cImlucHV0IHRleHRGaWVsZFwiXG4gICAgICAgIFtuZ1N0eWxlXT1cInsgd2lkdGg6IHdpZGdldEluc3RhbmNlLm1pbldpZHRoICsgJ3B4JyB9XCJcbiAgICAgICAgYXBwZWFyYW5jZT1cIm91dGxpbmVcIlxuICAgICAgPlxuICAgICAgICA8bWF0LWxhYmVsICpuZ0lmPVwiIXdpZGdldEluc3RhbmNlLnNob3dMYWJlbFwiPlxuICAgICAgICAgIHt7IHdpZGdldEluc3RhbmNlLmxhYmVsIH19XG4gICAgICAgIDwvbWF0LWxhYmVsPlxuICAgICAgICA8aW5wdXRcbiAgICAgICAgICBbZm9ybUNvbnRyb2xOYW1lXT1cIndpZGdldEluc3RhbmNlLmtleVwiXG4gICAgICAgICAgW2lkXT1cIndpZGdldEluc3RhbmNlLmtleVwiXG4gICAgICAgICAgW3R5cGVdPVwiJ3N0cmluZydcIlxuICAgICAgICAgIFt2YWx1ZV09XCJ3aWRnZXRJbnN0YW5jZS52YWx1ZVwiXG4gICAgICAgICAgcGxhY2Vob2xkZXI9XCJ7eyB3aWRnZXRJbnN0YW5jZS5wbGFjZWhvbGRlciB9fVwiXG4gICAgICAgICAgbWF0SW5wdXRcbiAgICAgICAgLz5cbiAgICAgICAgPG1hdC1pY29uICpuZ0lmPVwid2lkZ2V0SW5zdGFuY2UuaWNvblwiIG1hdFN1ZmZpeD5cbiAgICAgICAgICB7eyB3aWRnZXRJbnN0YW5jZS5pY29uIH19XG4gICAgICAgIDwvbWF0LWljb24+XG4gICAgICA8L21hdC1mb3JtLWZpZWxkPlxuICAgIDwvZGl2PlxuXG4gICAgPGRpdiAqbmdTd2l0Y2hDYXNlPVwic21hcnRGb3JtV2lkZ2V0VHlwZS5URVhUX0ZJRUxEX0NISVBTXCI+XG4gICAgICA8ZGl2ICpuZ0lmPVwid2lkZ2V0SW5zdGFuY2Uuc2hvd0xhYmVsXCI+XG4gICAgICAgIDxoMj57eyB3aWRnZXRJbnN0YW5jZS5sYWJlbCB9fTwvaDI+XG4gICAgICA8L2Rpdj5cbiAgICAgIDxtYXQtZm9ybS1maWVsZFxuICAgICAgICBjbGFzcz1cImlucHV0XCJcbiAgICAgICAgW25nU3R5bGVdPVwieyB3aWR0aDogd2lkZ2V0SW5zdGFuY2UubWluV2lkdGggKyAncHgnIH1cIlxuICAgICAgICBhcHBlYXJhbmNlPVwib3V0bGluZVwiXG4gICAgICA+XG4gICAgICAgIDxtYXQtbGFiZWwgKm5nSWY9XCIhd2lkZ2V0SW5zdGFuY2Uuc2hvd0xhYmVsXCI+XG4gICAgICAgICAge3sgd2lkZ2V0SW5zdGFuY2UubGFiZWwgfX1cbiAgICAgICAgPC9tYXQtbGFiZWw+XG4gICAgICAgIDxtYXQtY2hpcC1saXN0ICNjaGlwTGlzdCBhcmlhLWxhYmVsPVwie3sgd2lkZ2V0SW5zdGFuY2UubGFiZWwgfX1cIj5cbiAgICAgICAgICA8bWF0LWNoaXBcbiAgICAgICAgICAgICpuZ0Zvcj1cImxldCB2YWx1ZSBvZiB3aWRnZXRJbnN0YW5jZS52YWx1ZUxpc3RcIlxuICAgICAgICAgICAgKHJlbW92ZWQpPVwicmVtb3ZlKHZhbHVlKVwiXG4gICAgICAgICAgPlxuICAgICAgICAgICAge3sgdmFsdWUubGFiZWwgfX1cbiAgICAgICAgICAgIDxidXR0b24gbWF0Q2hpcFJlbW92ZT5cbiAgICAgICAgICAgICAgPG1hdC1pY29uPmNhbmNlbDwvbWF0LWljb24+XG4gICAgICAgICAgICA8L2J1dHRvbj5cbiAgICAgICAgICA8L21hdC1jaGlwPlxuICAgICAgICAgIDxpbnB1dFxuICAgICAgICAgICAgcGxhY2Vob2xkZXI9XCJ7eyB3aWRnZXRJbnN0YW5jZS5wbGFjZWhvbGRlciB9fVwiXG4gICAgICAgICAgICBbbWF0Q2hpcElucHV0Rm9yXT1cImNoaXBMaXN0XCJcbiAgICAgICAgICAgIFttYXRDaGlwSW5wdXRTZXBhcmF0b3JLZXlDb2Rlc109XCJzZXBhcmF0b3JLZXlzQ29kZXNcIlxuICAgICAgICAgICAgW21hdENoaXBJbnB1dEFkZE9uQmx1cl09XCJhZGRPbkJsdXJcIlxuICAgICAgICAgICAgKG1hdENoaXBJbnB1dFRva2VuRW5kKT1cImFkZCgkZXZlbnQpXCJcbiAgICAgICAgICAvPlxuICAgICAgICA8L21hdC1jaGlwLWxpc3Q+XG4gICAgICA8L21hdC1mb3JtLWZpZWxkPlxuICAgIDwvZGl2PlxuXG4gICAgPGRpdiAqbmdTd2l0Y2hDYXNlPVwic21hcnRGb3JtV2lkZ2V0VHlwZS5URVhUX0JPWFwiPlxuICAgICAgPGRpdiAqbmdJZj1cIndpZGdldEluc3RhbmNlLnNob3dMYWJlbFwiPlxuICAgICAgICA8aDI+e3sgd2lkZ2V0SW5zdGFuY2UubGFiZWwgfX08L2gyPlxuICAgICAgPC9kaXY+XG4gICAgICA8bWF0LWZvcm0tZmllbGRcbiAgICAgICAgY2xhc3M9XCJpbnB1dFwiXG4gICAgICAgIFtuZ1N0eWxlXT1cInsgd2lkdGg6IHdpZGdldEluc3RhbmNlLm1pbldpZHRoICsgJ3B4JyB9XCJcbiAgICAgICAgYXBwZWFyYW5jZT1cIm91dGxpbmVcIlxuICAgICAgPlxuICAgICAgICA8bWF0LWxhYmVsICpuZ0lmPVwiIXdpZGdldEluc3RhbmNlLnNob3dMYWJlbFwiPlxuICAgICAgICAgIHt7IHdpZGdldEluc3RhbmNlLmxhYmVsIH19XG4gICAgICAgIDwvbWF0LWxhYmVsPlxuICAgICAgICA8dGV4dGFyZWFcbiAgICAgICAgICBbZm9ybUNvbnRyb2xOYW1lXT1cIndpZGdldEluc3RhbmNlLmtleVwiXG4gICAgICAgICAgW2lkXT1cIndpZGdldEluc3RhbmNlLmtleVwiXG4gICAgICAgICAgW3R5cGVdPVwiJ3N0cmluZydcIlxuICAgICAgICAgIFt2YWx1ZV09XCJ3aWRnZXRJbnN0YW5jZS52YWx1ZVwiXG4gICAgICAgICAgcGxhY2Vob2xkZXI9XCJ7eyB3aWRnZXRJbnN0YW5jZS52YWx1ZSB9fVwiXG4gICAgICAgICAgbWF0SW5wdXRcbiAgICAgICAgPjwvdGV4dGFyZWE+XG4gICAgICA8L21hdC1mb3JtLWZpZWxkPlxuICAgIDwvZGl2PlxuXG4gICAgPGRpdiAqbmdTd2l0Y2hDYXNlPVwic21hcnRGb3JtV2lkZ2V0VHlwZS5DSEVDS19CT1hcIj5cbiAgICAgIDxkaXYgKm5nSWY9XCJ3aWRnZXRJbnN0YW5jZS5zaG93TGFiZWxcIj5cbiAgICAgICAgPGgyPnt7IHdpZGdldEluc3RhbmNlLmxhYmVsIH19PC9oMj5cbiAgICAgIDwvZGl2PlxuICAgICAgPGRpdlxuICAgICAgICBjbGFzcz1cImlucHV0IGNoZWNrYm94XCJcbiAgICAgICAgW25nU3R5bGVdPVwieyB3aWR0aDogd2lkZ2V0SW5zdGFuY2UubWluV2lkdGggKyAncHgnIH1cIlxuICAgICAgICBhcHBlYXJhbmNlPVwib3V0bGluZVwiXG4gICAgICA+XG4gICAgICAgIDxtYXQtbGFiZWwgKm5nSWY9XCIhd2lkZ2V0SW5zdGFuY2Uuc2hvd0xhYmVsXCI+XG4gICAgICAgICAge3sgd2lkZ2V0SW5zdGFuY2UubGFiZWwgfX1cbiAgICAgICAgPC9tYXQtbGFiZWw+XG4gICAgICAgIDxzZWN0aW9uIGNsYXNzPVwiY2hlY2tib3gtc2VjdGlvblwiIFtuZ0NsYXNzXT1cImdldERpcmVjdGlvbigpXCI+XG4gICAgICAgICAgPG1hdC1jaGVja2JveFxuICAgICAgICAgICAgKm5nRm9yPVwibGV0IGNoZWNrYm94IG9mIHdpZGdldEluc3RhbmNlLnZhbHVlTGlzdFwiXG4gICAgICAgICAgICBjbGFzcz1cInNlbGVjYXRibGVPYmplY3RcIlxuICAgICAgICAgICAgZm9ybUNvbnRyb2xOYW1lPVwie3sgd2lkZ2V0SW5zdGFuY2Uua2V5IH19XCJcbiAgICAgICAgICA+XG4gICAgICAgICAgICB7eyBjaGVja2JveC5sYWJlbCB9fVxuICAgICAgICAgIDwvbWF0LWNoZWNrYm94PlxuICAgICAgICA8L3NlY3Rpb24+XG4gICAgICA8L2Rpdj5cbiAgICA8L2Rpdj5cblxuICAgIDxkaXYgKm5nU3dpdGNoQ2FzZT1cInNtYXJ0Rm9ybVdpZGdldFR5cGUuUkFESU9fQlVUVE9OXCI+XG4gICAgICA8ZGl2ICpuZ0lmPVwid2lkZ2V0SW5zdGFuY2Uuc2hvd0xhYmVsXCI+XG4gICAgICAgIDxoMj57eyB3aWRnZXRJbnN0YW5jZS5sYWJlbCB9fTwvaDI+XG4gICAgICA8L2Rpdj5cbiAgICAgIDxtYXQtcmFkaW8tZ3JvdXBcbiAgICAgICAgY2xhc3M9XCJpbnB1dCByYWRpby1zZWN0aW9uXCJcbiAgICAgICAgW25nU3R5bGVdPVwieyB3aWR0aDogd2lkZ2V0SW5zdGFuY2UubWluV2lkdGggKyAncHgnIH1cIlxuICAgICAgICBhcmlhLWxhYmVsPVwie3sgd2lkZ2V0SW5zdGFuY2UubGFiZWwgfX1cIlxuICAgICAgICBhcHBlYXJhbmNlPVwib3V0bGluZVwiXG4gICAgICAgIGZvcm1Db250cm9sTmFtZT1cInt7IHdpZGdldEluc3RhbmNlLmtleSB9fVwiXG4gICAgICA+XG4gICAgICAgIDxtYXQtbGFiZWwgY2xhc3M9XCJyYWRpb0xhYmVsXCIgKm5nSWY9XCIhd2lkZ2V0SW5zdGFuY2Uuc2hvd0xhYmVsXCI+XG4gICAgICAgICAge3sgd2lkZ2V0SW5zdGFuY2UubGFiZWwgfX1cbiAgICAgICAgPC9tYXQtbGFiZWw+XG4gICAgICAgIDxkaXYgW25nQ2xhc3NdPVwiZ2V0RGlyZWN0aW9uKClcIj5cbiAgICAgICAgICA8bWF0LXJhZGlvLWJ1dHRvblxuICAgICAgICAgICAgY2xhc3M9XCJzZWxlY2F0YmxlT2JqZWN0XCJcbiAgICAgICAgICAgICpuZ0Zvcj1cImxldCByYWRpbyBvZiB3aWRnZXRJbnN0YW5jZS52YWx1ZUxpc3RcIlxuICAgICAgICAgICAgdmFsdWU9XCJ7eyByYWRpby52YWx1ZSB9fVwiXG4gICAgICAgICAgICBbbmdDbGFzc109XCJnZXREaXJlY3Rpb24oKVwiXG4gICAgICAgICAgPlxuICAgICAgICAgICAge3sgcmFkaW8ubGFiZWwgfX1cbiAgICAgICAgICA8L21hdC1yYWRpby1idXR0b24+XG4gICAgICAgIDwvZGl2PlxuICAgICAgPC9tYXQtcmFkaW8tZ3JvdXA+XG4gICAgPC9kaXY+XG5cbiAgICA8ZGl2ICpuZ1N3aXRjaENhc2U9XCJzbWFydEZvcm1XaWRnZXRUeXBlLkRBVEVfUElDS0VSXCI+XG4gICAgICA8ZGl2ICpuZ0lmPVwid2lkZ2V0SW5zdGFuY2Uuc2hvd0xhYmVsXCI+XG4gICAgICAgIDxoMj57eyB3aWRnZXRJbnN0YW5jZS5sYWJlbCB9fTwvaDI+XG4gICAgICA8L2Rpdj5cbiAgICAgIDxtYXQtZm9ybS1maWVsZFxuICAgICAgICBjbGFzcz1cImlucHV0XCJcbiAgICAgICAgW25nU3R5bGVdPVwieyB3aWR0aDogd2lkZ2V0SW5zdGFuY2UubWluV2lkdGggKyAncHgnIH1cIlxuICAgICAgICBhcHBlYXJhbmNlPVwib3V0bGluZVwiXG4gICAgICA+XG4gICAgICAgIDxtYXQtbGFiZWwgKm5nSWY9XCIhd2lkZ2V0SW5zdGFuY2Uuc2hvd0xhYmVsXCI+XG4gICAgICAgICAge3sgd2lkZ2V0SW5zdGFuY2UubGFiZWwgfX1cbiAgICAgICAgPC9tYXQtbGFiZWw+XG4gICAgICAgIDxtYXQtZGF0ZXBpY2tlci10b2dnbGUgbWF0U3VmZml4IFtmb3JdPVwicGlja2VyXCI+PC9tYXQtZGF0ZXBpY2tlci10b2dnbGU+XG4gICAgICAgIDxtYXQtZGF0ZXBpY2tlciAjcGlja2VyPjwvbWF0LWRhdGVwaWNrZXI+XG4gICAgICAgIDxpbnB1dFxuICAgICAgICAgIFtmb3JtQ29udHJvbE5hbWVdPVwid2lkZ2V0SW5zdGFuY2Uua2V5XCJcbiAgICAgICAgICBbaWRdPVwid2lkZ2V0SW5zdGFuY2Uua2V5XCJcbiAgICAgICAgICBbdmFsdWVdPVwid2lkZ2V0SW5zdGFuY2UudmFsdWVcIlxuICAgICAgICAgIHBsYWNlaG9sZGVyPVwie3sgd2lkZ2V0SW5zdGFuY2UudmFsdWUgfX1cIlxuICAgICAgICAgIG1hdElucHV0XG4gICAgICAgICAgW21hdERhdGVwaWNrZXJdPVwicGlja2VyXCJcbiAgICAgICAgLz5cbiAgICAgICAgPG1hdC1oaW50Pk1NL0REL1lZWVk8L21hdC1oaW50PlxuICAgICAgPC9tYXQtZm9ybS1maWVsZD5cbiAgICA8L2Rpdj5cblxuICAgIDxkaXYgKm5nU3dpdGNoQ2FzZT1cInNtYXJ0Rm9ybVdpZGdldFR5cGUuU0VMRUNUXCI+XG4gICAgICA8ZGl2ICpuZ0lmPVwid2lkZ2V0SW5zdGFuY2Uuc2hvd0xhYmVsXCI+XG4gICAgICAgIDxoMj57eyB3aWRnZXRJbnN0YW5jZS5sYWJlbCB9fTwvaDI+XG4gICAgICA8L2Rpdj5cbiAgICAgIDxtYXQtZm9ybS1maWVsZFxuICAgICAgICBjbGFzcz1cImlucHV0XCJcbiAgICAgICAgW25nU3R5bGVdPVwieyB3aWR0aDogd2lkZ2V0SW5zdGFuY2UubWluV2lkdGggKyAncHgnIH1cIlxuICAgICAgICBhcHBlYXJhbmNlPVwib3V0bGluZVwiXG4gICAgICA+XG4gICAgICAgIDxtYXQtbGFiZWwgKm5nSWY9XCIhd2lkZ2V0SW5zdGFuY2Uuc2hvd0xhYmVsXCI+XG4gICAgICAgICAge3sgd2lkZ2V0SW5zdGFuY2UubGFiZWwgfX1cbiAgICAgICAgPC9tYXQtbGFiZWw+XG4gICAgICAgIDxtYXQtc2VsZWN0IFtmb3JtQ29udHJvbE5hbWVdPVwid2lkZ2V0SW5zdGFuY2Uua2V5XCI+XG4gICAgICAgICAgPG1hdC1vcHRpb25cbiAgICAgICAgICAgICpuZ0Zvcj1cImxldCBvcHRpb24gb2Ygd2lkZ2V0SW5zdGFuY2UudmFsdWVMaXN0XCJcbiAgICAgICAgICAgIFt2YWx1ZV09XCJvcHRpb24ua2V5XCJcbiAgICAgICAgICAgID57eyBvcHRpb24udmFsdWUgfX08L21hdC1vcHRpb25cbiAgICAgICAgICA+XG4gICAgICAgIDwvbWF0LXNlbGVjdD5cbiAgICAgIDwvbWF0LWZvcm0tZmllbGQ+XG4gICAgPC9kaXY+XG5cbiAgICA8ZGl2ICpuZ1N3aXRjaENhc2U9XCJzbWFydEZvcm1XaWRnZXRUeXBlLlNFTEVDVF9NVUxUSVBMRVwiPlxuICAgICAgPGRpdiAqbmdJZj1cIndpZGdldEluc3RhbmNlLnNob3dMYWJlbFwiPlxuICAgICAgICA8aDI+e3sgd2lkZ2V0SW5zdGFuY2UubGFiZWwgfX08L2gyPlxuICAgICAgPC9kaXY+XG4gICAgICA8ZGl2XG4gICAgICAgIGNsYXNzPVwiaW5wdXRcIlxuICAgICAgICBbbmdTdHlsZV09XCJ7IHdpZHRoOiB3aWRnZXRJbnN0YW5jZS5taW5XaWR0aCArICdweCcgfVwiXG4gICAgICAgIGFwcGVhcmFuY2U9XCJvdXRsaW5lXCJcbiAgICAgID5cbiAgICAgICAgPG1hdC1sYWJlbCAqbmdJZj1cIiF3aWRnZXRJbnN0YW5jZS5zaG93TGFiZWxcIj5cbiAgICAgICAgICB7eyB3aWRnZXRJbnN0YW5jZS5sYWJlbCB9fVxuICAgICAgICA8L21hdC1sYWJlbD5cbiAgICAgICAgPG1hdC1zZWxlY3QgW2Zvcm1Db250cm9sTmFtZV09XCJ3aWRnZXRJbnN0YW5jZS5rZXlcIiBtdWx0aXBsZT5cbiAgICAgICAgICA8bWF0LW9wdGlvblxuICAgICAgICAgICAgKm5nRm9yPVwibGV0IG9wdGlvbiBvZiB3aWRnZXRJbnN0YW5jZS52YWx1ZUxpc3RcIlxuICAgICAgICAgICAgW3ZhbHVlXT1cIm9wdGlvbi5rZXlcIlxuICAgICAgICAgICAgPnt7IG9wdGlvbi52YWx1ZSB9fTwvbWF0LW9wdGlvblxuICAgICAgICAgID5cbiAgICAgICAgPC9tYXQtc2VsZWN0PlxuICAgICAgPC9kaXY+XG4gICAgPC9kaXY+XG4gIDwvZGl2PlxuPC9kaXY+XG4iXX0=