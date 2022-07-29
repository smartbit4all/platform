import * as i0 from '@angular/core';
import { Injectable, Component, ViewEncapsulation, Input, NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import * as i9 from '@angular/forms';
import { FormControl, Validators, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
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

class SmartFormService {
    toFormGroup(smartForm) {
        const group = {};
        smartForm.widgets.forEach((widget) => {
            group[widget.key] = new FormControl(widget.value || '', widget.isRequired ? Validators.required : undefined);
        });
        return new FormGroup(group);
    }
}
SmartFormService.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartFormService, deps: [], target: i0.ɵɵFactoryTarget.Injectable });
SmartFormService.ɵprov = i0.ɵɵngDeclareInjectable({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartFormService });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartFormService, decorators: [{
            type: Injectable
        }] });

/**
 * This enum containes the available types of the form widgets.
 *
 * @author Roland Fényes
 */
var SmartFormWidgetType;
(function (SmartFormWidgetType) {
    SmartFormWidgetType[SmartFormWidgetType["TEXT_FIELD"] = 0] = "TEXT_FIELD";
    SmartFormWidgetType[SmartFormWidgetType["TEXT_FIELD_CHIPS"] = 1] = "TEXT_FIELD_CHIPS";
    SmartFormWidgetType[SmartFormWidgetType["TEXT_BOX"] = 2] = "TEXT_BOX";
    SmartFormWidgetType[SmartFormWidgetType["SELECT"] = 3] = "SELECT";
    SmartFormWidgetType[SmartFormWidgetType["SELECT_MULTIPLE"] = 4] = "SELECT_MULTIPLE";
    SmartFormWidgetType[SmartFormWidgetType["CHECK_BOX"] = 5] = "CHECK_BOX";
    SmartFormWidgetType[SmartFormWidgetType["CHECK_BOX_TABLE"] = 6] = "CHECK_BOX_TABLE";
    SmartFormWidgetType[SmartFormWidgetType["RADIO_BUTTON"] = 7] = "RADIO_BUTTON";
    SmartFormWidgetType[SmartFormWidgetType["DATE_PICKER"] = 8] = "DATE_PICKER";
    SmartFormWidgetType[SmartFormWidgetType["FILE_UPLOAD"] = 9] = "FILE_UPLOAD";
    SmartFormWidgetType[SmartFormWidgetType["ITEM"] = 10] = "ITEM";
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
SmartformwidgetComponent.ɵcmp = i0.ɵɵngDeclareComponent({ minVersion: "12.0.0", version: "13.2.7", type: SmartformwidgetComponent, selector: "smartformwidget", inputs: { form: "form", widgetInstance: "widgetInstance" }, ngImport: i0, template: "<div class=\"container\" [formGroup]=\"form\">\r\n  <div [ngSwitch]=\"widgetInstance.type\" class=\"container\">\r\n    <div *ngSwitchCase=\"smartFormWidgetType.TEXT_FIELD\">\r\n      <div *ngIf=\"widgetInstance.showLabel\">\r\n        <h2>{{ widgetInstance.label }}</h2>\r\n      </div>\r\n      <mat-form-field\r\n        class=\"input textField\"\r\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\r\n        appearance=\"outline\"\r\n      >\r\n        <mat-label *ngIf=\"!widgetInstance.showLabel\">\r\n          {{ widgetInstance.label }}\r\n        </mat-label>\r\n        <input\r\n          [formControlName]=\"widgetInstance.key\"\r\n          [id]=\"widgetInstance.key\"\r\n          [type]=\"'string'\"\r\n          [value]=\"widgetInstance.value\"\r\n          placeholder=\"{{ widgetInstance.placeholder }}\"\r\n          matInput\r\n        />\r\n        <mat-icon *ngIf=\"widgetInstance.icon\" matSuffix>\r\n          {{ widgetInstance.icon }}\r\n        </mat-icon>\r\n      </mat-form-field>\r\n    </div>\r\n\r\n    <div *ngSwitchCase=\"smartFormWidgetType.TEXT_FIELD_CHIPS\">\r\n      <div *ngIf=\"widgetInstance.showLabel\">\r\n        <h2>{{ widgetInstance.label }}</h2>\r\n      </div>\r\n      <mat-form-field\r\n        class=\"input\"\r\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\r\n        appearance=\"outline\"\r\n      >\r\n        <mat-label *ngIf=\"!widgetInstance.showLabel\">\r\n          {{ widgetInstance.label }}\r\n        </mat-label>\r\n        <mat-chip-list #chipList aria-label=\"{{ widgetInstance.label }}\">\r\n          <mat-chip\r\n            *ngFor=\"let value of widgetInstance.valueList\"\r\n            (removed)=\"remove(value)\"\r\n          >\r\n            {{ value.label }}\r\n            <button matChipRemove>\r\n              <mat-icon>cancel</mat-icon>\r\n            </button>\r\n          </mat-chip>\r\n          <input\r\n            placeholder=\"{{ widgetInstance.placeholder }}\"\r\n            [matChipInputFor]=\"chipList\"\r\n            [matChipInputSeparatorKeyCodes]=\"separatorKeysCodes\"\r\n            [matChipInputAddOnBlur]=\"addOnBlur\"\r\n            (matChipInputTokenEnd)=\"add($event)\"\r\n          />\r\n        </mat-chip-list>\r\n      </mat-form-field>\r\n    </div>\r\n\r\n    <div *ngSwitchCase=\"smartFormWidgetType.TEXT_BOX\">\r\n      <div *ngIf=\"widgetInstance.showLabel\">\r\n        <h2>{{ widgetInstance.label }}</h2>\r\n      </div>\r\n      <mat-form-field\r\n        class=\"input\"\r\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\r\n        appearance=\"outline\"\r\n      >\r\n        <mat-label *ngIf=\"!widgetInstance.showLabel\">\r\n          {{ widgetInstance.label }}\r\n        </mat-label>\r\n        <textarea\r\n          [formControlName]=\"widgetInstance.key\"\r\n          [id]=\"widgetInstance.key\"\r\n          [type]=\"'string'\"\r\n          [value]=\"widgetInstance.value\"\r\n          placeholder=\"{{ widgetInstance.value }}\"\r\n          matInput\r\n        ></textarea>\r\n      </mat-form-field>\r\n    </div>\r\n\r\n    <div *ngSwitchCase=\"smartFormWidgetType.CHECK_BOX\">\r\n      <div *ngIf=\"widgetInstance.showLabel\">\r\n        <h2>{{ widgetInstance.label }}</h2>\r\n      </div>\r\n      <div\r\n        class=\"input checkbox\"\r\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\r\n        appearance=\"outline\"\r\n      >\r\n        <mat-label *ngIf=\"!widgetInstance.showLabel\">\r\n          {{ widgetInstance.label }}\r\n        </mat-label>\r\n        <section class=\"checkbox-section\" [ngClass]=\"getDirection()\">\r\n          <mat-checkbox\r\n            *ngFor=\"let checkbox of widgetInstance.valueList\"\r\n            class=\"selecatbleObject\"\r\n            formControlName=\"{{ widgetInstance.key }}\"\r\n          >\r\n            {{ checkbox.label }}\r\n          </mat-checkbox>\r\n        </section>\r\n      </div>\r\n    </div>\r\n\r\n    <div *ngSwitchCase=\"smartFormWidgetType.RADIO_BUTTON\">\r\n      <div *ngIf=\"widgetInstance.showLabel\">\r\n        <h2>{{ widgetInstance.label }}</h2>\r\n      </div>\r\n      <mat-radio-group\r\n        class=\"input radio-section\"\r\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\r\n        aria-label=\"{{ widgetInstance.label }}\"\r\n        appearance=\"outline\"\r\n        formControlName=\"{{ widgetInstance.key }}\"\r\n      >\r\n        <mat-label class=\"radioLabel\" *ngIf=\"!widgetInstance.showLabel\">\r\n          {{ widgetInstance.label }}\r\n        </mat-label>\r\n        <div [ngClass]=\"getDirection()\">\r\n          <mat-radio-button\r\n            class=\"selecatbleObject\"\r\n            *ngFor=\"let radio of widgetInstance.valueList\"\r\n            value=\"{{ radio.value }}\"\r\n            [ngClass]=\"getDirection()\"\r\n          >\r\n            {{ radio.label }}\r\n          </mat-radio-button>\r\n        </div>\r\n      </mat-radio-group>\r\n    </div>\r\n\r\n    <div *ngSwitchCase=\"smartFormWidgetType.DATE_PICKER\">\r\n      <div *ngIf=\"widgetInstance.showLabel\">\r\n        <h2>{{ widgetInstance.label }}</h2>\r\n      </div>\r\n      <mat-form-field\r\n        class=\"input\"\r\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\r\n        appearance=\"outline\"\r\n      >\r\n        <mat-label *ngIf=\"!widgetInstance.showLabel\">\r\n          {{ widgetInstance.label }}\r\n        </mat-label>\r\n        <mat-datepicker-toggle matSuffix [for]=\"picker\"></mat-datepicker-toggle>\r\n        <mat-datepicker #picker></mat-datepicker>\r\n        <input\r\n          [formControlName]=\"widgetInstance.key\"\r\n          [id]=\"widgetInstance.key\"\r\n          [value]=\"widgetInstance.value\"\r\n          placeholder=\"{{ widgetInstance.value }}\"\r\n          matInput\r\n          [matDatepicker]=\"picker\"\r\n        />\r\n        <mat-hint>MM/DD/YYYY</mat-hint>\r\n      </mat-form-field>\r\n    </div>\r\n\r\n    <div *ngSwitchCase=\"smartFormWidgetType.SELECT\">\r\n      <div *ngIf=\"widgetInstance.showLabel\">\r\n        <h2>{{ widgetInstance.label }}</h2>\r\n      </div>\r\n      <mat-form-field\r\n        class=\"input\"\r\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\r\n        appearance=\"outline\"\r\n      >\r\n        <mat-label *ngIf=\"!widgetInstance.showLabel\">\r\n          {{ widgetInstance.label }}\r\n        </mat-label>\r\n        <mat-select [formControlName]=\"widgetInstance.key\">\r\n          <mat-option\r\n            *ngFor=\"let option of widgetInstance.valueList\"\r\n            [value]=\"option.key\"\r\n            >{{ option.value }}</mat-option\r\n          >\r\n        </mat-select>\r\n      </mat-form-field>\r\n    </div>\r\n\r\n    <div *ngSwitchCase=\"smartFormWidgetType.SELECT_MULTIPLE\">\r\n      <div *ngIf=\"widgetInstance.showLabel\">\r\n        <h2>{{ widgetInstance.label }}</h2>\r\n      </div>\r\n      <div\r\n        class=\"input\"\r\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\r\n        appearance=\"outline\"\r\n      >\r\n        <mat-label *ngIf=\"!widgetInstance.showLabel\">\r\n          {{ widgetInstance.label }}\r\n        </mat-label>\r\n        <mat-select [formControlName]=\"widgetInstance.key\" multiple>\r\n          <mat-option\r\n            *ngFor=\"let option of widgetInstance.valueList\"\r\n            [value]=\"option.key\"\r\n            >{{ option.value }}</mat-option\r\n          >\r\n        </mat-select>\r\n      </div>\r\n    </div>\r\n  </div>\r\n</div>\r\n", styles: [".checkbox-section,.radio-section{display:flex;flex-direction:column}.input{width:100%}.direction-col{display:flex;flex-direction:column}.direction-row{display:flex;flex-direction:row}.selecatbleObject{margin:.5em}.radioLabel{color:var(--primary-color);text-align:left!important}.container{height:100%}.input{height:100%;display:flex;flex-direction:column}.checkbox{flex-direction:column-reverse}.mat-form-field-wrapper{padding-bottom:0!important}.input .mat-standard-chip.mat-chip-with-trailing-icon{padding-right:12px}\n"], components: [{ type: i1.MatFormField, selector: "mat-form-field", inputs: ["color", "appearance", "hideRequiredMarker", "hintLabel", "floatLabel"], exportAs: ["matFormField"] }, { type: i2.MatIcon, selector: "mat-icon", inputs: ["color", "inline", "svgIcon", "fontSet", "fontIcon"], exportAs: ["matIcon"] }, { type: i3.MatChipList, selector: "mat-chip-list", inputs: ["errorStateMatcher", "multiple", "compareWith", "value", "required", "placeholder", "disabled", "aria-orientation", "selectable", "tabIndex"], outputs: ["change", "valueChange"], exportAs: ["matChipList"] }, { type: i4.MatCheckbox, selector: "mat-checkbox", inputs: ["disableRipple", "color", "tabIndex", "aria-label", "aria-labelledby", "aria-describedby", "id", "required", "labelPosition", "name", "value", "checked", "disabled", "indeterminate"], outputs: ["change", "indeterminateChange"], exportAs: ["matCheckbox"] }, { type: i5.MatRadioButton, selector: "mat-radio-button", inputs: ["disableRipple", "tabIndex"], exportAs: ["matRadioButton"] }, { type: i6.MatDatepickerToggle, selector: "mat-datepicker-toggle", inputs: ["for", "tabIndex", "aria-label", "disabled", "disableRipple"], exportAs: ["matDatepickerToggle"] }, { type: i6.MatDatepicker, selector: "mat-datepicker", exportAs: ["matDatepicker"] }, { type: i7.MatSelect, selector: "mat-select", inputs: ["disabled", "disableRipple", "tabIndex"], exportAs: ["matSelect"] }, { type: i8.MatOption, selector: "mat-option", exportAs: ["matOption"] }], directives: [{ type: i9.NgControlStatusGroup, selector: "[formGroupName],[formArrayName],[ngModelGroup],[formGroup],form:not([ngNoForm]),[ngForm]" }, { type: i9.FormGroupDirective, selector: "[formGroup]", inputs: ["formGroup"], outputs: ["ngSubmit"], exportAs: ["ngForm"] }, { type: i10.NgSwitch, selector: "[ngSwitch]", inputs: ["ngSwitch"] }, { type: i10.NgSwitchCase, selector: "[ngSwitchCase]", inputs: ["ngSwitchCase"] }, { type: i10.NgIf, selector: "[ngIf]", inputs: ["ngIf", "ngIfThen", "ngIfElse"] }, { type: i10.NgStyle, selector: "[ngStyle]", inputs: ["ngStyle"] }, { type: i1.MatLabel, selector: "mat-label" }, { type: i11.MatInput, selector: "input[matInput], textarea[matInput], select[matNativeControl],      input[matNativeControl], textarea[matNativeControl]", inputs: ["disabled", "id", "placeholder", "name", "required", "type", "errorStateMatcher", "aria-describedby", "value", "readonly"], exportAs: ["matInput"] }, { type: i9.DefaultValueAccessor, selector: "input:not([type=checkbox])[formControlName],textarea[formControlName],input:not([type=checkbox])[formControl],textarea[formControl],input:not([type=checkbox])[ngModel],textarea[ngModel],[ngDefaultControl]" }, { type: i9.NgControlStatus, selector: "[formControlName],[ngModel],[formControl]" }, { type: i9.FormControlName, selector: "[formControlName]", inputs: ["formControlName", "disabled", "ngModel"], outputs: ["ngModelChange"] }, { type: i1.MatSuffix, selector: "[matSuffix]" }, { type: i10.NgForOf, selector: "[ngFor][ngForOf]", inputs: ["ngForOf", "ngForTrackBy", "ngForTemplate"] }, { type: i3.MatChip, selector: "mat-basic-chip, [mat-basic-chip], mat-chip, [mat-chip]", inputs: ["color", "disableRipple", "tabIndex", "selected", "value", "selectable", "disabled", "removable"], outputs: ["selectionChange", "destroyed", "removed"], exportAs: ["matChip"] }, { type: i3.MatChipRemove, selector: "[matChipRemove]" }, { type: i3.MatChipInput, selector: "input[matChipInputFor]", inputs: ["matChipInputFor", "matChipInputAddOnBlur", "matChipInputSeparatorKeyCodes", "placeholder", "id", "disabled"], outputs: ["matChipInputTokenEnd"], exportAs: ["matChipInput", "matChipInputFor"] }, { type: i10.NgClass, selector: "[ngClass]", inputs: ["class", "ngClass"] }, { type: i5.MatRadioGroup, selector: "mat-radio-group", exportAs: ["matRadioGroup"] }, { type: i6.MatDatepickerInput, selector: "input[matDatepicker]", inputs: ["matDatepicker", "min", "max", "matDatepickerFilter"], exportAs: ["matDatepickerInput"] }, { type: i1.MatHint, selector: "mat-hint", inputs: ["align", "id"] }], encapsulation: i0.ViewEncapsulation.None });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartformwidgetComponent, decorators: [{
            type: Component,
            args: [{ selector: "smartformwidget", encapsulation: ViewEncapsulation.None, template: "<div class=\"container\" [formGroup]=\"form\">\r\n  <div [ngSwitch]=\"widgetInstance.type\" class=\"container\">\r\n    <div *ngSwitchCase=\"smartFormWidgetType.TEXT_FIELD\">\r\n      <div *ngIf=\"widgetInstance.showLabel\">\r\n        <h2>{{ widgetInstance.label }}</h2>\r\n      </div>\r\n      <mat-form-field\r\n        class=\"input textField\"\r\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\r\n        appearance=\"outline\"\r\n      >\r\n        <mat-label *ngIf=\"!widgetInstance.showLabel\">\r\n          {{ widgetInstance.label }}\r\n        </mat-label>\r\n        <input\r\n          [formControlName]=\"widgetInstance.key\"\r\n          [id]=\"widgetInstance.key\"\r\n          [type]=\"'string'\"\r\n          [value]=\"widgetInstance.value\"\r\n          placeholder=\"{{ widgetInstance.placeholder }}\"\r\n          matInput\r\n        />\r\n        <mat-icon *ngIf=\"widgetInstance.icon\" matSuffix>\r\n          {{ widgetInstance.icon }}\r\n        </mat-icon>\r\n      </mat-form-field>\r\n    </div>\r\n\r\n    <div *ngSwitchCase=\"smartFormWidgetType.TEXT_FIELD_CHIPS\">\r\n      <div *ngIf=\"widgetInstance.showLabel\">\r\n        <h2>{{ widgetInstance.label }}</h2>\r\n      </div>\r\n      <mat-form-field\r\n        class=\"input\"\r\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\r\n        appearance=\"outline\"\r\n      >\r\n        <mat-label *ngIf=\"!widgetInstance.showLabel\">\r\n          {{ widgetInstance.label }}\r\n        </mat-label>\r\n        <mat-chip-list #chipList aria-label=\"{{ widgetInstance.label }}\">\r\n          <mat-chip\r\n            *ngFor=\"let value of widgetInstance.valueList\"\r\n            (removed)=\"remove(value)\"\r\n          >\r\n            {{ value.label }}\r\n            <button matChipRemove>\r\n              <mat-icon>cancel</mat-icon>\r\n            </button>\r\n          </mat-chip>\r\n          <input\r\n            placeholder=\"{{ widgetInstance.placeholder }}\"\r\n            [matChipInputFor]=\"chipList\"\r\n            [matChipInputSeparatorKeyCodes]=\"separatorKeysCodes\"\r\n            [matChipInputAddOnBlur]=\"addOnBlur\"\r\n            (matChipInputTokenEnd)=\"add($event)\"\r\n          />\r\n        </mat-chip-list>\r\n      </mat-form-field>\r\n    </div>\r\n\r\n    <div *ngSwitchCase=\"smartFormWidgetType.TEXT_BOX\">\r\n      <div *ngIf=\"widgetInstance.showLabel\">\r\n        <h2>{{ widgetInstance.label }}</h2>\r\n      </div>\r\n      <mat-form-field\r\n        class=\"input\"\r\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\r\n        appearance=\"outline\"\r\n      >\r\n        <mat-label *ngIf=\"!widgetInstance.showLabel\">\r\n          {{ widgetInstance.label }}\r\n        </mat-label>\r\n        <textarea\r\n          [formControlName]=\"widgetInstance.key\"\r\n          [id]=\"widgetInstance.key\"\r\n          [type]=\"'string'\"\r\n          [value]=\"widgetInstance.value\"\r\n          placeholder=\"{{ widgetInstance.value }}\"\r\n          matInput\r\n        ></textarea>\r\n      </mat-form-field>\r\n    </div>\r\n\r\n    <div *ngSwitchCase=\"smartFormWidgetType.CHECK_BOX\">\r\n      <div *ngIf=\"widgetInstance.showLabel\">\r\n        <h2>{{ widgetInstance.label }}</h2>\r\n      </div>\r\n      <div\r\n        class=\"input checkbox\"\r\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\r\n        appearance=\"outline\"\r\n      >\r\n        <mat-label *ngIf=\"!widgetInstance.showLabel\">\r\n          {{ widgetInstance.label }}\r\n        </mat-label>\r\n        <section class=\"checkbox-section\" [ngClass]=\"getDirection()\">\r\n          <mat-checkbox\r\n            *ngFor=\"let checkbox of widgetInstance.valueList\"\r\n            class=\"selecatbleObject\"\r\n            formControlName=\"{{ widgetInstance.key }}\"\r\n          >\r\n            {{ checkbox.label }}\r\n          </mat-checkbox>\r\n        </section>\r\n      </div>\r\n    </div>\r\n\r\n    <div *ngSwitchCase=\"smartFormWidgetType.RADIO_BUTTON\">\r\n      <div *ngIf=\"widgetInstance.showLabel\">\r\n        <h2>{{ widgetInstance.label }}</h2>\r\n      </div>\r\n      <mat-radio-group\r\n        class=\"input radio-section\"\r\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\r\n        aria-label=\"{{ widgetInstance.label }}\"\r\n        appearance=\"outline\"\r\n        formControlName=\"{{ widgetInstance.key }}\"\r\n      >\r\n        <mat-label class=\"radioLabel\" *ngIf=\"!widgetInstance.showLabel\">\r\n          {{ widgetInstance.label }}\r\n        </mat-label>\r\n        <div [ngClass]=\"getDirection()\">\r\n          <mat-radio-button\r\n            class=\"selecatbleObject\"\r\n            *ngFor=\"let radio of widgetInstance.valueList\"\r\n            value=\"{{ radio.value }}\"\r\n            [ngClass]=\"getDirection()\"\r\n          >\r\n            {{ radio.label }}\r\n          </mat-radio-button>\r\n        </div>\r\n      </mat-radio-group>\r\n    </div>\r\n\r\n    <div *ngSwitchCase=\"smartFormWidgetType.DATE_PICKER\">\r\n      <div *ngIf=\"widgetInstance.showLabel\">\r\n        <h2>{{ widgetInstance.label }}</h2>\r\n      </div>\r\n      <mat-form-field\r\n        class=\"input\"\r\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\r\n        appearance=\"outline\"\r\n      >\r\n        <mat-label *ngIf=\"!widgetInstance.showLabel\">\r\n          {{ widgetInstance.label }}\r\n        </mat-label>\r\n        <mat-datepicker-toggle matSuffix [for]=\"picker\"></mat-datepicker-toggle>\r\n        <mat-datepicker #picker></mat-datepicker>\r\n        <input\r\n          [formControlName]=\"widgetInstance.key\"\r\n          [id]=\"widgetInstance.key\"\r\n          [value]=\"widgetInstance.value\"\r\n          placeholder=\"{{ widgetInstance.value }}\"\r\n          matInput\r\n          [matDatepicker]=\"picker\"\r\n        />\r\n        <mat-hint>MM/DD/YYYY</mat-hint>\r\n      </mat-form-field>\r\n    </div>\r\n\r\n    <div *ngSwitchCase=\"smartFormWidgetType.SELECT\">\r\n      <div *ngIf=\"widgetInstance.showLabel\">\r\n        <h2>{{ widgetInstance.label }}</h2>\r\n      </div>\r\n      <mat-form-field\r\n        class=\"input\"\r\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\r\n        appearance=\"outline\"\r\n      >\r\n        <mat-label *ngIf=\"!widgetInstance.showLabel\">\r\n          {{ widgetInstance.label }}\r\n        </mat-label>\r\n        <mat-select [formControlName]=\"widgetInstance.key\">\r\n          <mat-option\r\n            *ngFor=\"let option of widgetInstance.valueList\"\r\n            [value]=\"option.key\"\r\n            >{{ option.value }}</mat-option\r\n          >\r\n        </mat-select>\r\n      </mat-form-field>\r\n    </div>\r\n\r\n    <div *ngSwitchCase=\"smartFormWidgetType.SELECT_MULTIPLE\">\r\n      <div *ngIf=\"widgetInstance.showLabel\">\r\n        <h2>{{ widgetInstance.label }}</h2>\r\n      </div>\r\n      <div\r\n        class=\"input\"\r\n        [ngStyle]=\"{ width: widgetInstance.minWidth + 'px' }\"\r\n        appearance=\"outline\"\r\n      >\r\n        <mat-label *ngIf=\"!widgetInstance.showLabel\">\r\n          {{ widgetInstance.label }}\r\n        </mat-label>\r\n        <mat-select [formControlName]=\"widgetInstance.key\" multiple>\r\n          <mat-option\r\n            *ngFor=\"let option of widgetInstance.valueList\"\r\n            [value]=\"option.key\"\r\n            >{{ option.value }}</mat-option\r\n          >\r\n        </mat-select>\r\n      </div>\r\n    </div>\r\n  </div>\r\n</div>\r\n", styles: [".checkbox-section,.radio-section{display:flex;flex-direction:column}.input{width:100%}.direction-col{display:flex;flex-direction:column}.direction-row{display:flex;flex-direction:row}.selecatbleObject{margin:.5em}.radioLabel{color:var(--primary-color);text-align:left!important}.container{height:100%}.input{height:100%;display:flex;flex-direction:column}.checkbox{flex-direction:column-reverse}.mat-form-field-wrapper{padding-bottom:0!important}.input .mat-standard-chip.mat-chip-with-trailing-icon{padding-right:12px}\n"] }]
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
SmartformComponent.ɵcmp = i0.ɵɵngDeclareComponent({ minVersion: "12.0.0", version: "13.2.7", type: SmartformComponent, selector: "smartform", inputs: { smartForm: "smartForm" }, providers: [SmartFormService], ngImport: i0, template: "<form [formGroup]=\"form\" class=\"flex form\">\r\n    <h2>\r\n        {{ smartForm.name }}\r\n    </h2>\r\n    <div [ngClass]=\"smartForm.direction === direction.ROW ? 'row' : 'col'\">\r\n        <smartformwidget\r\n            *ngFor=\"let widget of smartForm.widgets\"\r\n            [widgetInstance]=\"widget\"\r\n            [form]=\"form\"\r\n            class=\"grid-item\"\r\n        ></smartformwidget>\r\n    </div>\r\n</form>\r\n", styles: [".col{display:flex;flex-direction:column;padding:1em}.row{display:flex;flex-wrap:wrap;padding:1em}.grid-item{padding:.25em;text-align:left}\n"], components: [{ type: SmartformwidgetComponent, selector: "smartformwidget", inputs: ["form", "widgetInstance"] }], directives: [{ type: i9.ɵNgNoValidate, selector: "form:not([ngNoForm]):not([ngNativeValidate])" }, { type: i9.NgControlStatusGroup, selector: "[formGroupName],[formArrayName],[ngModelGroup],[formGroup],form:not([ngNoForm]),[ngForm]" }, { type: i9.FormGroupDirective, selector: "[formGroup]", inputs: ["formGroup"], outputs: ["ngSubmit"], exportAs: ["ngForm"] }, { type: i10.NgClass, selector: "[ngClass]", inputs: ["class", "ngClass"] }, { type: i10.NgForOf, selector: "[ngFor][ngForOf]", inputs: ["ngForOf", "ngForTrackBy", "ngForTemplate"] }] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartformComponent, decorators: [{
            type: Component,
            args: [{ selector: 'smartform', providers: [SmartFormService], template: "<form [formGroup]=\"form\" class=\"flex form\">\r\n    <h2>\r\n        {{ smartForm.name }}\r\n    </h2>\r\n    <div [ngClass]=\"smartForm.direction === direction.ROW ? 'row' : 'col'\">\r\n        <smartformwidget\r\n            *ngFor=\"let widget of smartForm.widgets\"\r\n            [widgetInstance]=\"widget\"\r\n            [form]=\"form\"\r\n            class=\"grid-item\"\r\n        ></smartformwidget>\r\n    </div>\r\n</form>\r\n", styles: [".col{display:flex;flex-direction:column;padding:1em}.row{display:flex;flex-wrap:wrap;padding:1em}.grid-item{padding:.25em;text-align:left}\n"] }]
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
SmartfileuploaderComponent.ɵcmp = i0.ɵɵngDeclareComponent({ minVersion: "12.0.0", version: "13.2.7", type: SmartfileuploaderComponent, selector: "smartfileuploader", inputs: { uploadCallback: "uploadCallback", fileFormats: "fileFormats", i18n: "i18n" }, ngImport: i0, template: "<div class=\"container\">\r\n    <div class=\"fileContainer\">\r\n        <div class=\"fileInnerContainer\">\r\n            <input\r\n                id=\"addFile\"\r\n                placeholder=\"fileInput\"\r\n                type=\"file\"\r\n                (change)=\"getFile($event)\"\r\n                class=\"file\"\r\n                accept=\"{{ fileFormats.join(', ') }}\"\r\n                multiple\r\n            />\r\n            <div class=\"fileUploadContentContainer\">\r\n                <mat-label class=\"addFileButton\">\r\n                    <mat-icon color=\"primary\" class=\"addCircle\">add_circle</mat-icon>\r\n                </mat-label>\r\n                <mat-label class=\"label primary\"> {{ i18n.addFile }} </mat-label>\r\n                <mat-label class=\"label secondary\"> {{ i18n.browseOrDrag }} </mat-label>\r\n                <mat-label class=\"subLabel primary\"> {{ i18n.maxSize }} </mat-label>\r\n                <mat-label class=\"subLabel primary\"> {{ i18n.formats }} </mat-label>\r\n            </div>\r\n        </div>\r\n    </div>\r\n    <div *ngIf=\"files.length\" class=\"uploadedFilesContainer\">\r\n        <div *ngFor=\"let file of files; let i = index\" class=\"uploadedFile\">\r\n            <mat-label class=\"fileNameLabel\"> {{ file.name }} </mat-label>\r\n            <button mat-icon-button (click)=\"remove(i)\">\r\n                <mat-icon>close</mat-icon>\r\n            </button>\r\n        </div>\r\n        <button class=\"uploadButton\" mat-raised-button color=\"primary\" (click)=\"uploadFile()\">\r\n            Felt\u00F6lt\u00E9s\r\n        </button>\r\n    </div>\r\n</div>\r\n", styles: [".container{width:100%;height:max-content;margin:1em;border-radius:5px;border:2px dashed var(--light-gray-60)}.fileContainer,.uploadedFilesContainer{background-color:var(--primary-lighter-color)}.fileInnerContainer{min-height:250px}.uploadedFilesContainer{border-top:1px solid var(--light-gray-60);display:flex;flex-direction:column}.fileContainer{position:relative;height:250px}.uploadedFile{padding:1em;display:flex;flex-direction:row;color:var(--primary-color)}.fileNameLabel{flex:1;margin-top:auto;margin-bottom:auto}.label{font-size:small}.subLabel{font-size:smaller}.file{position:absolute;left:0;top:0;opacity:0;width:100%;height:100%;z-index:120;cursor:pointer}.addFileButton{z-index:150;cursor:pointer}.addCircle{font-size:50px;width:50px;height:50px}.fileUploadContentContainer{position:absolute;top:50%;left:0;transform:translateY(-50%);width:100%;display:flex;flex-direction:column;text-align:center}.fileUploadContentContainer .secondary{margin-bottom:1em}.inputLabel{flex:1}.uploadButton{margin-left:auto;margin-right:auto;margin-bottom:.5em}\n"], components: [{ type: i2.MatIcon, selector: "mat-icon", inputs: ["color", "inline", "svgIcon", "fontSet", "fontIcon"], exportAs: ["matIcon"] }, { type: i2$1.MatButton, selector: "button[mat-button], button[mat-raised-button], button[mat-icon-button],             button[mat-fab], button[mat-mini-fab], button[mat-stroked-button],             button[mat-flat-button]", inputs: ["disabled", "disableRipple", "color"], exportAs: ["matButton"] }], directives: [{ type: i1.MatLabel, selector: "mat-label" }, { type: i10.NgIf, selector: "[ngIf]", inputs: ["ngIf", "ngIfThen", "ngIfElse"] }, { type: i10.NgForOf, selector: "[ngFor][ngForOf]", inputs: ["ngForOf", "ngForTrackBy", "ngForTemplate"] }] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartfileuploaderComponent, decorators: [{
            type: Component,
            args: [{ selector: 'smartfileuploader', template: "<div class=\"container\">\r\n    <div class=\"fileContainer\">\r\n        <div class=\"fileInnerContainer\">\r\n            <input\r\n                id=\"addFile\"\r\n                placeholder=\"fileInput\"\r\n                type=\"file\"\r\n                (change)=\"getFile($event)\"\r\n                class=\"file\"\r\n                accept=\"{{ fileFormats.join(', ') }}\"\r\n                multiple\r\n            />\r\n            <div class=\"fileUploadContentContainer\">\r\n                <mat-label class=\"addFileButton\">\r\n                    <mat-icon color=\"primary\" class=\"addCircle\">add_circle</mat-icon>\r\n                </mat-label>\r\n                <mat-label class=\"label primary\"> {{ i18n.addFile }} </mat-label>\r\n                <mat-label class=\"label secondary\"> {{ i18n.browseOrDrag }} </mat-label>\r\n                <mat-label class=\"subLabel primary\"> {{ i18n.maxSize }} </mat-label>\r\n                <mat-label class=\"subLabel primary\"> {{ i18n.formats }} </mat-label>\r\n            </div>\r\n        </div>\r\n    </div>\r\n    <div *ngIf=\"files.length\" class=\"uploadedFilesContainer\">\r\n        <div *ngFor=\"let file of files; let i = index\" class=\"uploadedFile\">\r\n            <mat-label class=\"fileNameLabel\"> {{ file.name }} </mat-label>\r\n            <button mat-icon-button (click)=\"remove(i)\">\r\n                <mat-icon>close</mat-icon>\r\n            </button>\r\n        </div>\r\n        <button class=\"uploadButton\" mat-raised-button color=\"primary\" (click)=\"uploadFile()\">\r\n            Felt\u00F6lt\u00E9s\r\n        </button>\r\n    </div>\r\n</div>\r\n", styles: [".container{width:100%;height:max-content;margin:1em;border-radius:5px;border:2px dashed var(--light-gray-60)}.fileContainer,.uploadedFilesContainer{background-color:var(--primary-lighter-color)}.fileInnerContainer{min-height:250px}.uploadedFilesContainer{border-top:1px solid var(--light-gray-60);display:flex;flex-direction:column}.fileContainer{position:relative;height:250px}.uploadedFile{padding:1em;display:flex;flex-direction:row;color:var(--primary-color)}.fileNameLabel{flex:1;margin-top:auto;margin-bottom:auto}.label{font-size:small}.subLabel{font-size:smaller}.file{position:absolute;left:0;top:0;opacity:0;width:100%;height:100%;z-index:120;cursor:pointer}.addFileButton{z-index:150;cursor:pointer}.addCircle{font-size:50px;width:50px;height:50px}.fileUploadContentContainer{position:absolute;top:50%;left:0;transform:translateY(-50%);width:100%;display:flex;flex-direction:column;text-align:center}.fileUploadContentContainer .secondary{margin-bottom:1em}.inputLabel{flex:1}.uploadButton{margin-left:auto;margin-right:auto;margin-bottom:.5em}\n"] }]
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
