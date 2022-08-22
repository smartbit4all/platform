import * as i0 from '@angular/core';
import { Injectable, Component, ViewEncapsulation, Input, NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import * as i9 from '@angular/forms';
import { FormGroup, FormControl, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ENTER, COMMA } from '@angular/cdk/keycodes';
import * as i1 from '@angular/material/form-field';
import { MatFormFieldModule } from '@angular/material/form-field';
import * as i2 from '@angular/material/icon';
import { MatIconModule } from '@angular/material/icon';
import * as i3 from '@angular/material/chips';
import { MatChipsModule } from '@angular/material/chips';
import * as i4 from '@angular/material/checkbox';
import { MatCheckboxModule } from '@angular/material/checkbox';
import * as i5 from '@angular/material/radio';
import { MatRadioModule } from '@angular/material/radio';
import * as i6 from '@angular/material/datepicker';
import { MatDatepickerModule } from '@angular/material/datepicker';
import * as i7 from '@angular/material/select';
import { MatSelectModule } from '@angular/material/select';
import * as i8 from '@angular/material/core';
import { MatCommonModule, MatNativeDateModule } from '@angular/material/core';
import * as i10 from '@angular/common';
import * as i11 from '@angular/material/input';
import { MatInputModule } from '@angular/material/input';
import * as i2$1 from '@angular/material/button';
import { MatButtonModule } from '@angular/material/button';
import { BrowserModule } from '@angular/platform-browser';

/**
 * This enum containes the available types of the form widgets.
 *
 * @author Roland Fényes
 */
var SmartFormWidgetType;
(function (SmartFormWidgetType) {
    SmartFormWidgetType[SmartFormWidgetType["TEXT_FIELD"] = 0] = "TEXT_FIELD";
    SmartFormWidgetType[SmartFormWidgetType["TEXT_FIELD_NUMBER"] = 1] = "TEXT_FIELD_NUMBER";
    SmartFormWidgetType[SmartFormWidgetType["TEXT_FIELD_CHIPS"] = 2] = "TEXT_FIELD_CHIPS";
    SmartFormWidgetType[SmartFormWidgetType["TEXT_BOX"] = 3] = "TEXT_BOX";
    SmartFormWidgetType[SmartFormWidgetType["SELECT"] = 4] = "SELECT";
    SmartFormWidgetType[SmartFormWidgetType["SELECT_MULTIPLE"] = 5] = "SELECT_MULTIPLE";
    SmartFormWidgetType[SmartFormWidgetType["CHECK_BOX"] = 6] = "CHECK_BOX";
    SmartFormWidgetType[SmartFormWidgetType["CHECK_BOX_TABLE"] = 7] = "CHECK_BOX_TABLE";
    SmartFormWidgetType[SmartFormWidgetType["RADIO_BUTTON"] = 8] = "RADIO_BUTTON";
    SmartFormWidgetType[SmartFormWidgetType["DATE_PICKER"] = 9] = "DATE_PICKER";
    SmartFormWidgetType[SmartFormWidgetType["FILE_UPLOAD"] = 10] = "FILE_UPLOAD";
    SmartFormWidgetType[SmartFormWidgetType["ITEM"] = 11] = "ITEM";
    SmartFormWidgetType[SmartFormWidgetType["CONTAINER"] = 12] = "CONTAINER";
})(SmartFormWidgetType || (SmartFormWidgetType = {}));
var SmartFormWidgetDirection;
(function (SmartFormWidgetDirection) {
    SmartFormWidgetDirection[SmartFormWidgetDirection["COL"] = 0] = "COL";
    SmartFormWidgetDirection[SmartFormWidgetDirection["ROW"] = 1] = "ROW";
})(SmartFormWidgetDirection || (SmartFormWidgetDirection = {}));
var SmartFormWidgetWidth;
(function (SmartFormWidgetWidth) {
    SmartFormWidgetWidth[SmartFormWidgetWidth["SMALL"] = 150] = "SMALL";
    SmartFormWidgetWidth[SmartFormWidgetWidth["MEDIUM"] = 250] = "MEDIUM";
    SmartFormWidgetWidth[SmartFormWidgetWidth["LARGE"] = 350] = "LARGE";
    SmartFormWidgetWidth[SmartFormWidgetWidth["EXTRA_LARGE"] = 450] = "EXTRA_LARGE";
})(SmartFormWidgetWidth || (SmartFormWidgetWidth = {}));

class SmartFormService {
    constructor() {
        this.group = {};
    }
    toFormGroup(smartForm) {
        this.createFormControls(smartForm.widgets);
        return new FormGroup(this.group);
    }
    createFormControls(widgets) {
        widgets.forEach((widget) => {
            var _a;
            let formControl = new FormControl(widget.value || "", widget.isRequired ? Validators.required : undefined);
            if (widget.isDisabled) {
                formControl.disable();
            }
            this.group[widget.key] = formControl;
            if (widget.type === SmartFormWidgetType.CONTAINER && ((_a = widget.valueList) === null || _a === void 0 ? void 0 : _a.length)) {
                this.createFormControls(widget.valueList);
            }
        });
    }
}
SmartFormService.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartFormService, deps: [], target: i0.ɵɵFactoryTarget.Injectable });
SmartFormService.ɵprov = i0.ɵɵngDeclareInjectable({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartFormService });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartFormService, decorators: [{
            type: Injectable
        }] });

class SmartformwidgetComponent {
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
SmartformwidgetComponent.ɵcmp = i0.ɵɵngDeclareComponent({ minVersion: "12.0.0", version: "13.2.7", type: SmartformwidgetComponent, selector: "smartformwidget", inputs: { form: "form", widgetInstance: "widgetInstance" }, ngImport: i0, template: "<div class=\"container\" [formGroup]=\"form\">\n    <div [ngSwitch]=\"widgetInstance.type\" class=\"container\">\n        <div *ngSwitchCase=\"smartFormWidgetType.CONTAINER\" [ngClass]=\"getDirection()\">\n            <smartformwidget\n                *ngFor=\"let widget of widgetInstance.valueList\"\n                [form]=\"form\"\n                [widgetInstance]=\"widget\"\n            ></smartformwidget>\n        </div>\n\n        <div *ngSwitchCase=\"smartFormWidgetType.TEXT_FIELD\">\n            <div *ngIf=\"widgetInstance.showLabel\">\n                <h2>{{ widgetInstance.label }}</h2>\n            </div>\n            <mat-form-field\n                class=\"input textField\"\n                [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n                appearance=\"outline\"\n            >\n                <mat-label *ngIf=\"!widgetInstance.showLabel\">\n                    {{ widgetInstance.label }}\n                </mat-label>\n                <input\n                    [formControlName]=\"widgetInstance.key\"\n                    [id]=\"widgetInstance.key\"\n                    [type]=\"'string'\"\n                    [value]=\"widgetInstance.value\"\n                    placeholder=\"{{ widgetInstance.placeholder }}\"\n                    matInput\n                />\n                <span matPrefix>\n                    {{ widgetInstance.prefix }}\n                </span>\n                <span matSuffix>\n                    {{ widgetInstance.suffix }}\n                </span>\n                <mat-icon *ngIf=\"widgetInstance.icon\" matSuffix>\n                    {{ widgetInstance.icon }}\n                </mat-icon>\n            </mat-form-field>\n        </div>\n\n        <div *ngSwitchCase=\"smartFormWidgetType.TEXT_FIELD_NUMBER\">\n            <div *ngIf=\"widgetInstance.showLabel\">\n                <h2>{{ widgetInstance.label }}</h2>\n            </div>\n            <mat-form-field\n                class=\"input textField\"\n                [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n                appearance=\"outline\"\n            >\n                <mat-label *ngIf=\"!widgetInstance.showLabel\">\n                    {{ widgetInstance.label }}\n                </mat-label>\n                <input\n                    [formControlName]=\"widgetInstance.key\"\n                    [id]=\"widgetInstance.key\"\n                    [value]=\"widgetInstance.value\"\n                    type=\"number\"\n                    placeholder=\"{{ widgetInstance.placeholder }}\"\n                    min=\"10\"\n                    matInput\n                />\n                <span matPrefix>\n                    {{ widgetInstance.prefix }}\n                </span>\n                <span matSuffix>\n                    {{ widgetInstance.suffix }}\n                </span>\n                <mat-icon *ngIf=\"widgetInstance.icon\" matSuffix>\n                    {{ widgetInstance.icon }}\n                </mat-icon>\n            </mat-form-field>\n        </div>\n\n        <div *ngSwitchCase=\"smartFormWidgetType.TEXT_FIELD_CHIPS\">\n            <div *ngIf=\"widgetInstance.showLabel\">\n                <h2>{{ widgetInstance.label }}</h2>\n            </div>\n            <mat-form-field\n                class=\"input\"\n                [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n                appearance=\"outline\"\n            >\n                <mat-label *ngIf=\"!widgetInstance.showLabel\">\n                    {{ widgetInstance.label }}\n                </mat-label>\n                <mat-chip-list #chipList aria-label=\"{{ widgetInstance.label }}\">\n                    <mat-chip\n                        *ngFor=\"let value of widgetInstance.valueList\"\n                        (removed)=\"remove(value)\"\n                    >\n                        {{ value.label }}\n                        <button matChipRemove>\n                            <mat-icon>cancel</mat-icon>\n                        </button>\n                    </mat-chip>\n                    <input\n                        placeholder=\"{{ widgetInstance.placeholder }}\"\n                        [matChipInputFor]=\"chipList\"\n                        [matChipInputSeparatorKeyCodes]=\"separatorKeysCodes\"\n                        [matChipInputAddOnBlur]=\"addOnBlur\"\n                        (matChipInputTokenEnd)=\"add($event)\"\n                    />\n                </mat-chip-list>\n            </mat-form-field>\n        </div>\n\n        <div *ngSwitchCase=\"smartFormWidgetType.TEXT_BOX\">\n            <div *ngIf=\"widgetInstance.showLabel\">\n                <h2>{{ widgetInstance.label }}</h2>\n            </div>\n            <mat-form-field\n                class=\"input\"\n                [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n                appearance=\"outline\"\n            >\n                <mat-label *ngIf=\"!widgetInstance.showLabel\">\n                    {{ widgetInstance.label }}\n                </mat-label>\n                <textarea\n                    [formControlName]=\"widgetInstance.key\"\n                    [id]=\"widgetInstance.key\"\n                    [type]=\"'string'\"\n                    [value]=\"widgetInstance.value\"\n                    placeholder=\"{{ widgetInstance.value }}\"\n                    matInput\n                ></textarea>\n            </mat-form-field>\n        </div>\n\n        <div *ngSwitchCase=\"smartFormWidgetType.CHECK_BOX\">\n            <div *ngIf=\"widgetInstance.showLabel\">\n                <h2>{{ widgetInstance.label }}</h2>\n            </div>\n            <div\n                class=\"input checkbox\"\n                [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n                appearance=\"outline\"\n            >\n                <mat-label class=\"radioLabel\" *ngIf=\"!widgetInstance.showLabel\">\n                    {{ widgetInstance.label }}\n                </mat-label>\n                <section class=\"checkbox-section\" [ngClass]=\"getDirection()\">\n                    <mat-checkbox\n                        *ngFor=\"let checkbox of widgetInstance.valueList\"\n                        class=\"selecatbleObject\"\n                        formControlName=\"{{ widgetInstance.key }}\"\n                    >\n                        {{ checkbox.label }}\n                    </mat-checkbox>\n                </section>\n            </div>\n        </div>\n\n        <div *ngSwitchCase=\"smartFormWidgetType.RADIO_BUTTON\">\n            <div *ngIf=\"widgetInstance.showLabel\">\n                <h2>{{ widgetInstance.label }}</h2>\n            </div>\n            <mat-radio-group\n                class=\"input radio-section\"\n                [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n                aria-label=\"{{ widgetInstance.label }}\"\n                appearance=\"outline\"\n                formControlName=\"{{ widgetInstance.key }}\"\n            >\n                <mat-label class=\"radioLabel\" *ngIf=\"!widgetInstance.showLabel\">\n                    {{ widgetInstance.label }}\n                </mat-label>\n                <div [ngClass]=\"getDirection()\">\n                    <mat-radio-button\n                        class=\"selecatbleObject\"\n                        *ngFor=\"let radio of widgetInstance.valueList\"\n                        value=\"{{ radio.value }}\"\n                        [ngClass]=\"getDirection()\"\n                    >\n                        {{ radio.label }}\n                    </mat-radio-button>\n                </div>\n            </mat-radio-group>\n        </div>\n\n        <div *ngSwitchCase=\"smartFormWidgetType.DATE_PICKER\">\n            <div *ngIf=\"widgetInstance.showLabel\">\n                <h2>{{ widgetInstance.label }}</h2>\n            </div>\n            <mat-form-field\n                class=\"input\"\n                [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n                appearance=\"outline\"\n            >\n                <mat-label *ngIf=\"!widgetInstance.showLabel\">\n                    {{ widgetInstance.label }}\n                </mat-label>\n                <mat-datepicker-toggle matSuffix [for]=\"picker\"></mat-datepicker-toggle>\n                <mat-datepicker #picker></mat-datepicker>\n                <input\n                    [formControlName]=\"widgetInstance.key\"\n                    [id]=\"widgetInstance.key\"\n                    [value]=\"widgetInstance.value\"\n                    placeholder=\"{{ widgetInstance.value }}\"\n                    matInput\n                    [matDatepicker]=\"picker\"\n                />\n                <mat-hint>MM/DD/YYYY</mat-hint>\n            </mat-form-field>\n        </div>\n\n        <div *ngSwitchCase=\"smartFormWidgetType.SELECT\">\n            <div *ngIf=\"widgetInstance.showLabel\">\n                <h2>{{ widgetInstance.label }}</h2>\n            </div>\n            <mat-form-field\n                class=\"input\"\n                [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n                appearance=\"outline\"\n            >\n                <mat-label *ngIf=\"!widgetInstance.showLabel\">\n                    {{ widgetInstance.label }}\n                </mat-label>\n                <mat-select [formControlName]=\"widgetInstance.key\">\n                    <mat-option\n                        *ngFor=\"let option of widgetInstance.valueList\"\n                        [value]=\"option.key\"\n                        >{{ option.value }}</mat-option\n                    >\n                </mat-select>\n            </mat-form-field>\n        </div>\n\n        <div *ngSwitchCase=\"smartFormWidgetType.SELECT_MULTIPLE\">\n            <div *ngIf=\"widgetInstance.showLabel\">\n                <h2>{{ widgetInstance.label }}</h2>\n            </div>\n            <div\n                class=\"input\"\n                [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n                appearance=\"outline\"\n            >\n                <mat-label *ngIf=\"!widgetInstance.showLabel\">\n                    {{ widgetInstance.label }}\n                </mat-label>\n                <mat-select [formControlName]=\"widgetInstance.key\" multiple>\n                    <mat-option\n                        *ngFor=\"let option of widgetInstance.valueList\"\n                        [value]=\"option.key\"\n                        >{{ option.value }}</mat-option\n                    >\n                </mat-select>\n            </div>\n        </div>\n    </div>\n</div>\n", styles: [".checkbox-section,.radio-section{display:flex;flex-direction:column}.input{width:100%}.direction-col{display:flex;flex-direction:column}.direction-row{display:flex;flex-direction:row}.selecatbleObject{margin:.5em}.radioLabel{color:var(--primary-color);text-align:left!important}.container{height:100%}.input{height:100%;display:flex;flex-direction:column}.checkbox{flex-direction:column}.mat-form-field-wrapper{padding-bottom:0!important}.input .mat-standard-chip.mat-chip-with-trailing-icon{padding-right:12px}\n"], components: [{ type: SmartformwidgetComponent, selector: "smartformwidget", inputs: ["form", "widgetInstance"] }, { type: i1.MatFormField, selector: "mat-form-field", inputs: ["color", "appearance", "hideRequiredMarker", "hintLabel", "floatLabel"], exportAs: ["matFormField"] }, { type: i2.MatIcon, selector: "mat-icon", inputs: ["color", "inline", "svgIcon", "fontSet", "fontIcon"], exportAs: ["matIcon"] }, { type: i3.MatChipList, selector: "mat-chip-list", inputs: ["errorStateMatcher", "multiple", "compareWith", "value", "required", "placeholder", "disabled", "aria-orientation", "selectable", "tabIndex"], outputs: ["change", "valueChange"], exportAs: ["matChipList"] }, { type: i4.MatCheckbox, selector: "mat-checkbox", inputs: ["disableRipple", "color", "tabIndex", "aria-label", "aria-labelledby", "aria-describedby", "id", "required", "labelPosition", "name", "value", "checked", "disabled", "indeterminate"], outputs: ["change", "indeterminateChange"], exportAs: ["matCheckbox"] }, { type: i5.MatRadioButton, selector: "mat-radio-button", inputs: ["disableRipple", "tabIndex"], exportAs: ["matRadioButton"] }, { type: i6.MatDatepickerToggle, selector: "mat-datepicker-toggle", inputs: ["for", "tabIndex", "aria-label", "disabled", "disableRipple"], exportAs: ["matDatepickerToggle"] }, { type: i6.MatDatepicker, selector: "mat-datepicker", exportAs: ["matDatepicker"] }, { type: i7.MatSelect, selector: "mat-select", inputs: ["disabled", "disableRipple", "tabIndex"], exportAs: ["matSelect"] }, { type: i8.MatOption, selector: "mat-option", exportAs: ["matOption"] }], directives: [{ type: i9.NgControlStatusGroup, selector: "[formGroupName],[formArrayName],[ngModelGroup],[formGroup],form:not([ngNoForm]),[ngForm]" }, { type: i9.FormGroupDirective, selector: "[formGroup]", inputs: ["formGroup"], outputs: ["ngSubmit"], exportAs: ["ngForm"] }, { type: i10.NgSwitch, selector: "[ngSwitch]", inputs: ["ngSwitch"] }, { type: i10.NgSwitchCase, selector: "[ngSwitchCase]", inputs: ["ngSwitchCase"] }, { type: i10.NgClass, selector: "[ngClass]", inputs: ["class", "ngClass"] }, { type: i10.NgForOf, selector: "[ngFor][ngForOf]", inputs: ["ngForOf", "ngForTrackBy", "ngForTemplate"] }, { type: i10.NgIf, selector: "[ngIf]", inputs: ["ngIf", "ngIfThen", "ngIfElse"] }, { type: i10.NgStyle, selector: "[ngStyle]", inputs: ["ngStyle"] }, { type: i1.MatLabel, selector: "mat-label" }, { type: i11.MatInput, selector: "input[matInput], textarea[matInput], select[matNativeControl],      input[matNativeControl], textarea[matNativeControl]", inputs: ["disabled", "id", "placeholder", "name", "required", "type", "errorStateMatcher", "aria-describedby", "value", "readonly"], exportAs: ["matInput"] }, { type: i9.DefaultValueAccessor, selector: "input:not([type=checkbox])[formControlName],textarea[formControlName],input:not([type=checkbox])[formControl],textarea[formControl],input:not([type=checkbox])[ngModel],textarea[ngModel],[ngDefaultControl]" }, { type: i9.NgControlStatus, selector: "[formControlName],[ngModel],[formControl]" }, { type: i9.FormControlName, selector: "[formControlName]", inputs: ["formControlName", "disabled", "ngModel"], outputs: ["ngModelChange"] }, { type: i1.MatPrefix, selector: "[matPrefix]" }, { type: i1.MatSuffix, selector: "[matSuffix]" }, { type: i9.MinValidator, selector: "input[type=number][min][formControlName],input[type=number][min][formControl],input[type=number][min][ngModel]", inputs: ["min"] }, { type: i9.NumberValueAccessor, selector: "input[type=number][formControlName],input[type=number][formControl],input[type=number][ngModel]" }, { type: i3.MatChip, selector: "mat-basic-chip, [mat-basic-chip], mat-chip, [mat-chip]", inputs: ["color", "disableRipple", "tabIndex", "selected", "value", "selectable", "disabled", "removable"], outputs: ["selectionChange", "destroyed", "removed"], exportAs: ["matChip"] }, { type: i3.MatChipRemove, selector: "[matChipRemove]" }, { type: i3.MatChipInput, selector: "input[matChipInputFor]", inputs: ["matChipInputFor", "matChipInputAddOnBlur", "matChipInputSeparatorKeyCodes", "placeholder", "id", "disabled"], outputs: ["matChipInputTokenEnd"], exportAs: ["matChipInput", "matChipInputFor"] }, { type: i5.MatRadioGroup, selector: "mat-radio-group", exportAs: ["matRadioGroup"] }, { type: i6.MatDatepickerInput, selector: "input[matDatepicker]", inputs: ["matDatepicker", "min", "max", "matDatepickerFilter"], exportAs: ["matDatepickerInput"] }, { type: i1.MatHint, selector: "mat-hint", inputs: ["align", "id"] }], encapsulation: i0.ViewEncapsulation.None });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartformwidgetComponent, decorators: [{
            type: Component,
            args: [{ selector: "smartformwidget", encapsulation: ViewEncapsulation.None, template: "<div class=\"container\" [formGroup]=\"form\">\n    <div [ngSwitch]=\"widgetInstance.type\" class=\"container\">\n        <div *ngSwitchCase=\"smartFormWidgetType.CONTAINER\" [ngClass]=\"getDirection()\">\n            <smartformwidget\n                *ngFor=\"let widget of widgetInstance.valueList\"\n                [form]=\"form\"\n                [widgetInstance]=\"widget\"\n            ></smartformwidget>\n        </div>\n\n        <div *ngSwitchCase=\"smartFormWidgetType.TEXT_FIELD\">\n            <div *ngIf=\"widgetInstance.showLabel\">\n                <h2>{{ widgetInstance.label }}</h2>\n            </div>\n            <mat-form-field\n                class=\"input textField\"\n                [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n                appearance=\"outline\"\n            >\n                <mat-label *ngIf=\"!widgetInstance.showLabel\">\n                    {{ widgetInstance.label }}\n                </mat-label>\n                <input\n                    [formControlName]=\"widgetInstance.key\"\n                    [id]=\"widgetInstance.key\"\n                    [type]=\"'string'\"\n                    [value]=\"widgetInstance.value\"\n                    placeholder=\"{{ widgetInstance.placeholder }}\"\n                    matInput\n                />\n                <span matPrefix>\n                    {{ widgetInstance.prefix }}\n                </span>\n                <span matSuffix>\n                    {{ widgetInstance.suffix }}\n                </span>\n                <mat-icon *ngIf=\"widgetInstance.icon\" matSuffix>\n                    {{ widgetInstance.icon }}\n                </mat-icon>\n            </mat-form-field>\n        </div>\n\n        <div *ngSwitchCase=\"smartFormWidgetType.TEXT_FIELD_NUMBER\">\n            <div *ngIf=\"widgetInstance.showLabel\">\n                <h2>{{ widgetInstance.label }}</h2>\n            </div>\n            <mat-form-field\n                class=\"input textField\"\n                [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n                appearance=\"outline\"\n            >\n                <mat-label *ngIf=\"!widgetInstance.showLabel\">\n                    {{ widgetInstance.label }}\n                </mat-label>\n                <input\n                    [formControlName]=\"widgetInstance.key\"\n                    [id]=\"widgetInstance.key\"\n                    [value]=\"widgetInstance.value\"\n                    type=\"number\"\n                    placeholder=\"{{ widgetInstance.placeholder }}\"\n                    min=\"10\"\n                    matInput\n                />\n                <span matPrefix>\n                    {{ widgetInstance.prefix }}\n                </span>\n                <span matSuffix>\n                    {{ widgetInstance.suffix }}\n                </span>\n                <mat-icon *ngIf=\"widgetInstance.icon\" matSuffix>\n                    {{ widgetInstance.icon }}\n                </mat-icon>\n            </mat-form-field>\n        </div>\n\n        <div *ngSwitchCase=\"smartFormWidgetType.TEXT_FIELD_CHIPS\">\n            <div *ngIf=\"widgetInstance.showLabel\">\n                <h2>{{ widgetInstance.label }}</h2>\n            </div>\n            <mat-form-field\n                class=\"input\"\n                [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n                appearance=\"outline\"\n            >\n                <mat-label *ngIf=\"!widgetInstance.showLabel\">\n                    {{ widgetInstance.label }}\n                </mat-label>\n                <mat-chip-list #chipList aria-label=\"{{ widgetInstance.label }}\">\n                    <mat-chip\n                        *ngFor=\"let value of widgetInstance.valueList\"\n                        (removed)=\"remove(value)\"\n                    >\n                        {{ value.label }}\n                        <button matChipRemove>\n                            <mat-icon>cancel</mat-icon>\n                        </button>\n                    </mat-chip>\n                    <input\n                        placeholder=\"{{ widgetInstance.placeholder }}\"\n                        [matChipInputFor]=\"chipList\"\n                        [matChipInputSeparatorKeyCodes]=\"separatorKeysCodes\"\n                        [matChipInputAddOnBlur]=\"addOnBlur\"\n                        (matChipInputTokenEnd)=\"add($event)\"\n                    />\n                </mat-chip-list>\n            </mat-form-field>\n        </div>\n\n        <div *ngSwitchCase=\"smartFormWidgetType.TEXT_BOX\">\n            <div *ngIf=\"widgetInstance.showLabel\">\n                <h2>{{ widgetInstance.label }}</h2>\n            </div>\n            <mat-form-field\n                class=\"input\"\n                [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n                appearance=\"outline\"\n            >\n                <mat-label *ngIf=\"!widgetInstance.showLabel\">\n                    {{ widgetInstance.label }}\n                </mat-label>\n                <textarea\n                    [formControlName]=\"widgetInstance.key\"\n                    [id]=\"widgetInstance.key\"\n                    [type]=\"'string'\"\n                    [value]=\"widgetInstance.value\"\n                    placeholder=\"{{ widgetInstance.value }}\"\n                    matInput\n                ></textarea>\n            </mat-form-field>\n        </div>\n\n        <div *ngSwitchCase=\"smartFormWidgetType.CHECK_BOX\">\n            <div *ngIf=\"widgetInstance.showLabel\">\n                <h2>{{ widgetInstance.label }}</h2>\n            </div>\n            <div\n                class=\"input checkbox\"\n                [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n                appearance=\"outline\"\n            >\n                <mat-label class=\"radioLabel\" *ngIf=\"!widgetInstance.showLabel\">\n                    {{ widgetInstance.label }}\n                </mat-label>\n                <section class=\"checkbox-section\" [ngClass]=\"getDirection()\">\n                    <mat-checkbox\n                        *ngFor=\"let checkbox of widgetInstance.valueList\"\n                        class=\"selecatbleObject\"\n                        formControlName=\"{{ widgetInstance.key }}\"\n                    >\n                        {{ checkbox.label }}\n                    </mat-checkbox>\n                </section>\n            </div>\n        </div>\n\n        <div *ngSwitchCase=\"smartFormWidgetType.RADIO_BUTTON\">\n            <div *ngIf=\"widgetInstance.showLabel\">\n                <h2>{{ widgetInstance.label }}</h2>\n            </div>\n            <mat-radio-group\n                class=\"input radio-section\"\n                [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n                aria-label=\"{{ widgetInstance.label }}\"\n                appearance=\"outline\"\n                formControlName=\"{{ widgetInstance.key }}\"\n            >\n                <mat-label class=\"radioLabel\" *ngIf=\"!widgetInstance.showLabel\">\n                    {{ widgetInstance.label }}\n                </mat-label>\n                <div [ngClass]=\"getDirection()\">\n                    <mat-radio-button\n                        class=\"selecatbleObject\"\n                        *ngFor=\"let radio of widgetInstance.valueList\"\n                        value=\"{{ radio.value }}\"\n                        [ngClass]=\"getDirection()\"\n                    >\n                        {{ radio.label }}\n                    </mat-radio-button>\n                </div>\n            </mat-radio-group>\n        </div>\n\n        <div *ngSwitchCase=\"smartFormWidgetType.DATE_PICKER\">\n            <div *ngIf=\"widgetInstance.showLabel\">\n                <h2>{{ widgetInstance.label }}</h2>\n            </div>\n            <mat-form-field\n                class=\"input\"\n                [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n                appearance=\"outline\"\n            >\n                <mat-label *ngIf=\"!widgetInstance.showLabel\">\n                    {{ widgetInstance.label }}\n                </mat-label>\n                <mat-datepicker-toggle matSuffix [for]=\"picker\"></mat-datepicker-toggle>\n                <mat-datepicker #picker></mat-datepicker>\n                <input\n                    [formControlName]=\"widgetInstance.key\"\n                    [id]=\"widgetInstance.key\"\n                    [value]=\"widgetInstance.value\"\n                    placeholder=\"{{ widgetInstance.value }}\"\n                    matInput\n                    [matDatepicker]=\"picker\"\n                />\n                <mat-hint>MM/DD/YYYY</mat-hint>\n            </mat-form-field>\n        </div>\n\n        <div *ngSwitchCase=\"smartFormWidgetType.SELECT\">\n            <div *ngIf=\"widgetInstance.showLabel\">\n                <h2>{{ widgetInstance.label }}</h2>\n            </div>\n            <mat-form-field\n                class=\"input\"\n                [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n                appearance=\"outline\"\n            >\n                <mat-label *ngIf=\"!widgetInstance.showLabel\">\n                    {{ widgetInstance.label }}\n                </mat-label>\n                <mat-select [formControlName]=\"widgetInstance.key\">\n                    <mat-option\n                        *ngFor=\"let option of widgetInstance.valueList\"\n                        [value]=\"option.key\"\n                        >{{ option.value }}</mat-option\n                    >\n                </mat-select>\n            </mat-form-field>\n        </div>\n\n        <div *ngSwitchCase=\"smartFormWidgetType.SELECT_MULTIPLE\">\n            <div *ngIf=\"widgetInstance.showLabel\">\n                <h2>{{ widgetInstance.label }}</h2>\n            </div>\n            <div\n                class=\"input\"\n                [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\n                appearance=\"outline\"\n            >\n                <mat-label *ngIf=\"!widgetInstance.showLabel\">\n                    {{ widgetInstance.label }}\n                </mat-label>\n                <mat-select [formControlName]=\"widgetInstance.key\" multiple>\n                    <mat-option\n                        *ngFor=\"let option of widgetInstance.valueList\"\n                        [value]=\"option.key\"\n                        >{{ option.value }}</mat-option\n                    >\n                </mat-select>\n            </div>\n        </div>\n    </div>\n</div>\n", styles: [".checkbox-section,.radio-section{display:flex;flex-direction:column}.input{width:100%}.direction-col{display:flex;flex-direction:column}.direction-row{display:flex;flex-direction:row}.selecatbleObject{margin:.5em}.radioLabel{color:var(--primary-color);text-align:left!important}.container{height:100%}.input{height:100%;display:flex;flex-direction:column}.checkbox{flex-direction:column}.mat-form-field-wrapper{padding-bottom:0!important}.input .mat-standard-chip.mat-chip-with-trailing-icon{padding-right:12px}\n"] }]
        }], ctorParameters: function () { return []; }, propDecorators: { form: [{
                type: Input
            }], widgetInstance: [{
                type: Input
            }] } });

class SmartformComponent {
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
SmartformComponent.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartformComponent, deps: [{ token: SmartFormService }], target: i0.ɵɵFactoryTarget.Component });
SmartformComponent.ɵcmp = i0.ɵɵngDeclareComponent({ minVersion: "12.0.0", version: "13.2.7", type: SmartformComponent, selector: "smartform", inputs: { smartForm: "smartForm" }, providers: [SmartFormService], ngImport: i0, template: "<form [formGroup]=\"form\" class=\"flex form\">\n    <h2>\n        {{ smartForm.name }}\n    </h2>\n    <div [ngClass]=\"smartForm.direction === direction.ROW ? 'row' : 'col'\">\n        <smartformwidget\n            *ngFor=\"let widget of smartForm.widgets\"\n            [widgetInstance]=\"widget\"\n            [form]=\"form\"\n            class=\"grid-item\"\n        ></smartformwidget>\n    </div>\n</form>\n", styles: [".col{display:flex;flex-direction:column;padding:1em}.row{display:flex;flex-wrap:wrap;padding:1em}.grid-item{padding:.25em;text-align:left}\n"], components: [{ type: SmartformwidgetComponent, selector: "smartformwidget", inputs: ["form", "widgetInstance"] }], directives: [{ type: i9.ɵNgNoValidate, selector: "form:not([ngNoForm]):not([ngNativeValidate])" }, { type: i9.NgControlStatusGroup, selector: "[formGroupName],[formArrayName],[ngModelGroup],[formGroup],form:not([ngNoForm]),[ngForm]" }, { type: i9.FormGroupDirective, selector: "[formGroup]", inputs: ["formGroup"], outputs: ["ngSubmit"], exportAs: ["ngForm"] }, { type: i10.NgClass, selector: "[ngClass]", inputs: ["class", "ngClass"] }, { type: i10.NgForOf, selector: "[ngFor][ngForOf]", inputs: ["ngForOf", "ngForTrackBy", "ngForTemplate"] }] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartformComponent, decorators: [{
            type: Component,
            args: [{ selector: 'smartform', providers: [SmartFormService], template: "<form [formGroup]=\"form\" class=\"flex form\">\n    <h2>\n        {{ smartForm.name }}\n    </h2>\n    <div [ngClass]=\"smartForm.direction === direction.ROW ? 'row' : 'col'\">\n        <smartformwidget\n            *ngFor=\"let widget of smartForm.widgets\"\n            [widgetInstance]=\"widget\"\n            [form]=\"form\"\n            class=\"grid-item\"\n        ></smartformwidget>\n    </div>\n</form>\n", styles: [".col{display:flex;flex-direction:column;padding:1em}.row{display:flex;flex-wrap:wrap;padding:1em}.grid-item{padding:.25em;text-align:left}\n"] }]
        }], ctorParameters: function () { return [{ type: SmartFormService }]; }, propDecorators: { smartForm: [{
                type: Input
            }] } });

class SmartfileuploaderComponent {
    constructor() {
        this.files = [];
        this.fileFormats = [];
        this.i18n = {
            addFile: 'dokumentum hozzáadása',
            browseOrDrag: 'tallózás vagy behúzás',
            maxSize: 'max. 25 MB',
            formats: 'PDF, docx, xls formátum'
        };
    }
    ngOnInit() {
        console.log(this.fileFormats);
    }
    getFile(event) {
        if (event.target.files && event.target.files.length) {
            this.files = [...this.files, ...event.target.files];
        }
    }
    remove(index) {
        this.files.splice(index, 1);
    }
    uploadFile() {
        this.uploadCallback(this.files);
    }
}
SmartfileuploaderComponent.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartfileuploaderComponent, deps: [], target: i0.ɵɵFactoryTarget.Component });
SmartfileuploaderComponent.ɵcmp = i0.ɵɵngDeclareComponent({ minVersion: "12.0.0", version: "13.2.7", type: SmartfileuploaderComponent, selector: "smartfileuploader", inputs: { uploadCallback: "uploadCallback", fileFormats: "fileFormats", i18n: "i18n" }, ngImport: i0, template: "<div class=\"container\">\n    <div class=\"fileContainer\">\n        <div class=\"fileInnerContainer\">\n            <input\n                id=\"addFile\"\n                placeholder=\"fileInput\"\n                type=\"file\"\n                (change)=\"getFile($event)\"\n                class=\"file\"\n                accept=\"{{ fileFormats.join(', ') }}\"\n                multiple\n            />\n            <div class=\"fileUploadContentContainer\">\n                <mat-label class=\"addFileButton\">\n                    <mat-icon color=\"primary\" class=\"addCircle\">add_circle</mat-icon>\n                </mat-label>\n                <mat-label class=\"label primary\"> {{ i18n.addFile }} </mat-label>\n                <mat-label class=\"label secondary\"> {{ i18n.browseOrDrag }} </mat-label>\n                <mat-label class=\"subLabel primary\"> {{ i18n.maxSize }} </mat-label>\n                <mat-label class=\"subLabel primary\"> {{ i18n.formats }} </mat-label>\n            </div>\n        </div>\n    </div>\n    <div *ngIf=\"files.length\" class=\"uploadedFilesContainer\">\n        <div *ngFor=\"let file of files; let i = index\" class=\"uploadedFile\">\n            <mat-label class=\"fileNameLabel\"> {{ file.name }} </mat-label>\n            <button mat-icon-button (click)=\"remove(i)\">\n                <mat-icon>close</mat-icon>\n            </button>\n        </div>\n        <button class=\"uploadButton\" mat-raised-button color=\"primary\" (click)=\"uploadFile()\">\n            Felt\u00F6lt\u00E9s\n        </button>\n    </div>\n</div>\n", styles: [".container{width:100%;height:max-content;margin:1em;border-radius:5px;border:2px dashed var(--light-gray-60)}.fileContainer,.uploadedFilesContainer{background-color:var(--primary-lighter-color)}.fileInnerContainer{min-height:250px}.uploadedFilesContainer{border-top:1px solid var(--light-gray-60);display:flex;flex-direction:column}.fileContainer{position:relative;height:250px}.uploadedFile{padding:1em;display:flex;flex-direction:row;color:var(--primary-color)}.fileNameLabel{flex:1;margin-top:auto;margin-bottom:auto}.label{font-size:small}.subLabel{font-size:smaller}.file{position:absolute;left:0;top:0;opacity:0;width:100%;height:100%;z-index:120;cursor:pointer}.addFileButton{z-index:150;cursor:pointer}.addCircle{font-size:50px;width:50px;height:50px}.fileUploadContentContainer{position:absolute;top:50%;left:0;transform:translateY(-50%);width:100%;display:flex;flex-direction:column;text-align:center}.fileUploadContentContainer .secondary{margin-bottom:1em}.inputLabel{flex:1}.uploadButton{margin-left:auto;margin-right:auto;margin-bottom:.5em}\n"], components: [{ type: i2.MatIcon, selector: "mat-icon", inputs: ["color", "inline", "svgIcon", "fontSet", "fontIcon"], exportAs: ["matIcon"] }, { type: i2$1.MatButton, selector: "button[mat-button], button[mat-raised-button], button[mat-icon-button],             button[mat-fab], button[mat-mini-fab], button[mat-stroked-button],             button[mat-flat-button]", inputs: ["disabled", "disableRipple", "color"], exportAs: ["matButton"] }], directives: [{ type: i1.MatLabel, selector: "mat-label" }, { type: i10.NgIf, selector: "[ngIf]", inputs: ["ngIf", "ngIfThen", "ngIfElse"] }, { type: i10.NgForOf, selector: "[ngFor][ngForOf]", inputs: ["ngForOf", "ngForTrackBy", "ngForTemplate"] }] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartfileuploaderComponent, decorators: [{
            type: Component,
            args: [{ selector: 'smartfileuploader', template: "<div class=\"container\">\n    <div class=\"fileContainer\">\n        <div class=\"fileInnerContainer\">\n            <input\n                id=\"addFile\"\n                placeholder=\"fileInput\"\n                type=\"file\"\n                (change)=\"getFile($event)\"\n                class=\"file\"\n                accept=\"{{ fileFormats.join(', ') }}\"\n                multiple\n            />\n            <div class=\"fileUploadContentContainer\">\n                <mat-label class=\"addFileButton\">\n                    <mat-icon color=\"primary\" class=\"addCircle\">add_circle</mat-icon>\n                </mat-label>\n                <mat-label class=\"label primary\"> {{ i18n.addFile }} </mat-label>\n                <mat-label class=\"label secondary\"> {{ i18n.browseOrDrag }} </mat-label>\n                <mat-label class=\"subLabel primary\"> {{ i18n.maxSize }} </mat-label>\n                <mat-label class=\"subLabel primary\"> {{ i18n.formats }} </mat-label>\n            </div>\n        </div>\n    </div>\n    <div *ngIf=\"files.length\" class=\"uploadedFilesContainer\">\n        <div *ngFor=\"let file of files; let i = index\" class=\"uploadedFile\">\n            <mat-label class=\"fileNameLabel\"> {{ file.name }} </mat-label>\n            <button mat-icon-button (click)=\"remove(i)\">\n                <mat-icon>close</mat-icon>\n            </button>\n        </div>\n        <button class=\"uploadButton\" mat-raised-button color=\"primary\" (click)=\"uploadFile()\">\n            Felt\u00F6lt\u00E9s\n        </button>\n    </div>\n</div>\n", styles: [".container{width:100%;height:max-content;margin:1em;border-radius:5px;border:2px dashed var(--light-gray-60)}.fileContainer,.uploadedFilesContainer{background-color:var(--primary-lighter-color)}.fileInnerContainer{min-height:250px}.uploadedFilesContainer{border-top:1px solid var(--light-gray-60);display:flex;flex-direction:column}.fileContainer{position:relative;height:250px}.uploadedFile{padding:1em;display:flex;flex-direction:row;color:var(--primary-color)}.fileNameLabel{flex:1;margin-top:auto;margin-bottom:auto}.label{font-size:small}.subLabel{font-size:smaller}.file{position:absolute;left:0;top:0;opacity:0;width:100%;height:100%;z-index:120;cursor:pointer}.addFileButton{z-index:150;cursor:pointer}.addCircle{font-size:50px;width:50px;height:50px}.fileUploadContentContainer{position:absolute;top:50%;left:0;transform:translateY(-50%);width:100%;display:flex;flex-direction:column;text-align:center}.fileUploadContentContainer .secondary{margin-bottom:1em}.inputLabel{flex:1}.uploadButton{margin-left:auto;margin-right:auto;margin-bottom:.5em}\n"] }]
        }], ctorParameters: function () { return []; }, propDecorators: { uploadCallback: [{
                type: Input
            }], fileFormats: [{
                type: Input
            }], i18n: [{
                type: Input
            }] } });

class SmartformModule {
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

/*
 * Public API Surface of smartform
 */

/**
 * Generated bundle index. Do not edit.
 */

export { SmartFormWidgetDirection, SmartFormWidgetType, SmartFormWidgetWidth, SmartfileuploaderComponent, SmartformComponent, SmartformModule, SmartformwidgetComponent };
//# sourceMappingURL=smartbit4all-form.mjs.map
