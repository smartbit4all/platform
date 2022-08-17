import * as i0 from '@angular/core';
import { Injectable, Component, Input, NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { SelectionModel } from '@angular/cdk/collections';
import * as i1 from '@angular/material/table';
import { MatTableModule } from '@angular/material/table';
import * as i2 from '@angular/material/icon';
import { MatIconModule } from '@angular/material/icon';
import * as i3 from '@angular/material/button';
import { MatButtonModule } from '@angular/material/button';
import * as i4 from '@angular/material/menu';
import { MatMenuModule } from '@angular/material/menu';
import * as i5 from '@angular/material/checkbox';
import { MatCheckboxModule } from '@angular/material/checkbox';
import * as i6 from '@angular/common';
import { MatCommonModule } from '@angular/material/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { BrowserModule } from '@angular/platform-browser';

class SmarttableService {
    constructor() { }
}
SmarttableService.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmarttableService, deps: [], target: i0.ɵɵFactoryTarget.Injectable });
SmarttableService.ɵprov = i0.ɵɵngDeclareInjectable({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmarttableService, providedIn: 'root' });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmarttableService, decorators: [{
            type: Injectable,
            args: [{
                    providedIn: 'root'
                }]
        }], ctorParameters: function () { return []; } });

var SmartTableType;
(function (SmartTableType) {
    SmartTableType[SmartTableType["INHERITED"] = 0] = "INHERITED";
    SmartTableType[SmartTableType["CHECK_BOX"] = 1] = "CHECK_BOX";
})(SmartTableType || (SmartTableType = {}));
class SmartTable {
    constructor(tableRows, tableType, customTableHeaders, title, hideCols, isMultiple, options) {
        this.tableHeaders = Object.getOwnPropertyNames(tableRows[0]);
        if (customTableHeaders) {
            this.customTableHeaders = customTableHeaders;
        }
        else {
            this.customTableHeaders = this.tableHeaders;
        }
        this.tableRows = tableRows;
        this.tableType = tableType;
        this.title = title;
        this.hideCols = hideCols;
        this.isMultiple = isMultiple;
        this.options = options;
        if (this.options && this.options.length) {
            this.tableHeaders.push('options');
        }
        if (this.hideCols !== undefined && this.hideCols.length > 0) {
            this.hideCols.forEach((hideCol, index) => {
                hideCol -= index;
                this.tableHeaders.splice(hideCol, 1);
            });
        }
        if (tableType !== SmartTableType.INHERITED) {
            this.tableHeaders = ['select'].concat(this.tableHeaders);
            this.selection = new SelectionModel(this.isMultiple, []);
        }
    }
}

class SmarttableComponent {
    constructor() {
        this.tableType = SmartTableType;
    }
    ngOnInit() { }
    isAllSelected() {
        const numSelected = this.smartTable.selection.selected.length;
        const numRows = this.smartTable.tableRows.length;
        return numSelected === numRows;
    }
    /** Selects all rows if they are not all selected; otherwise clear selection. */
    toggleAllRows() {
        if (this.isAllSelected()) {
            this.smartTable.selection.clear();
            return;
        }
        this.smartTable.selection.select(...this.smartTable.tableRows);
    }
    /** The label for the checkbox on the passed row */
    checkboxLabel(row) {
        if (!row) {
            return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
        }
        return `${this.smartTable.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.position + 1}`;
    }
}
SmarttableComponent.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmarttableComponent, deps: [], target: i0.ɵɵFactoryTarget.Component });
SmarttableComponent.ɵcmp = i0.ɵɵngDeclareComponent({ minVersion: "12.0.0", version: "13.2.7", type: SmarttableComponent, selector: "smarttable", inputs: { smartTable: "smartTable" }, ngImport: i0, template: "<table mat-table [dataSource]=\"smartTable.tableRows\" class=\"full-width\">\n    <!--- Note that these columns can be defined in any order.\n          The actual rendered columns are set as a property on the row definition\" -->\n\n    <!-- Column Descriptor -->\n    <div *ngIf=\"smartTable.tableType === tableType.INHERITED\">\n        <ng-container\n            *ngFor=\"let header of smartTable.tableHeaders; let i = index\"\n            matColumnDef=\"{{ header }}\"\n        >\n            <th mat-header-cell *matHeaderCellDef>\n                <div *ngIf=\"header === 'icon' || header === 'img' || header === 'options'\"></div>\n                <div *ngIf=\"header !== 'icon' && header !== 'img' && header !== 'options'\">\n                    {{ smartTable.customTableHeaders[i] }}\n                </div>\n            </th>\n            <td mat-cell *matCellDef=\"let element\">\n                <mat-icon *ngIf=\"header === 'icon'\"> {{ element[header] }} </mat-icon>\n                <img\n                    *ngIf=\"header === 'img'\"\n                    [src]=\"element[header]\"\n                    alt=\"\"\n                    class=\"smarttableImg\"\n                />\n                <div *ngIf=\"header === 'options'\">\n                    <button mat-icon-button [matMenuTriggerFor]=\"menu\" aria-label=\"options\">\n                        <mat-icon>more_vert</mat-icon>\n                    </button>\n                    <mat-menu #menu=\"matMenu\">\n                        <button\n                            *ngFor=\"let option of smartTable.options\"\n                            mat-menu-item\n                            (click)=\"option.callback(element)\"\n                        >\n                            <mat-icon *ngIf=\"option.icon\">\n                                {{ option.icon }}\n                            </mat-icon>\n                            <span>\n                                {{ option.label }}\n                            </span>\n                        </button>\n                    </mat-menu>\n                </div>\n                <div *ngIf=\"header !== 'icon' && header !== 'img'\">\n                    {{ element[header] }}\n                </div>\n            </td>\n        </ng-container>\n\n        <tr mat-header-row *matHeaderRowDef=\"smartTable.tableHeaders; sticky: true\"></tr>\n        <tr mat-row *matRowDef=\"let row; columns: smartTable.tableHeaders\"></tr>\n    </div>\n    <div *ngIf=\"smartTable.tableType !== tableType.INHERITED\">\n        <!-- Checkbox Column -->\n        <ng-container matColumnDef=\"select\">\n            <th mat-header-cell *matHeaderCellDef>\n                <mat-checkbox\n                    (change)=\"$event ? toggleAllRows() : null\"\n                    [checked]=\"smartTable.selection!.hasValue() && isAllSelected()\"\n                    [indeterminate]=\"smartTable.selection!.hasValue() && !isAllSelected()\"\n                    [aria-label]=\"checkboxLabel()\"\n                >\n                </mat-checkbox>\n            </th>\n            <td mat-cell *matCellDef=\"let row\">\n                <mat-checkbox\n                    (click)=\"$event.stopPropagation()\"\n                    (change)=\"$event ? smartTable.selection!.toggle(row) : null\"\n                    [checked]=\"smartTable.selection!.isSelected(row)\"\n                    [aria-label]=\"checkboxLabel(row)\"\n                >\n                </mat-checkbox>\n            </td>\n        </ng-container>\n\n        <!-- Column Descriptor -->\n        <div *ngFor=\"let header of smartTable.tableHeaders; let i = index\">\n            <ng-container *ngIf=\"header !== 'select'\" matColumnDef=\"{{ header }}\">\n                <th mat-header-cell *matHeaderCellDef>\n                    <div\n                        *ngIf=\"header === 'icon' || header === 'img' || header === 'options'\"\n                    ></div>\n                    <div *ngIf=\"header !== 'icon' && header !== 'img' && header !== 'options'\">\n                        {{ smartTable.customTableHeaders[i] }}\n                    </div>\n                </th>\n                <td mat-cell *matCellDef=\"let element\">\n                    <mat-icon *ngIf=\"header === 'icon'\"> {{ element[header] }} </mat-icon>\n                    <img\n                        *ngIf=\"header === 'img'\"\n                        [src]=\"element[header]\"\n                        alt=\"\"\n                        class=\"smarttableImg\"\n                    />\n                    <div *ngIf=\"header === 'options'\">\n                        <button mat-icon-button [matMenuTriggerFor]=\"menu\" aria-label=\"options\">\n                            <mat-icon>more_vert</mat-icon>\n                        </button>\n                        <mat-menu #menu=\"matMenu\">\n                            <button\n                                *ngFor=\"let option of smartTable.options\"\n                                mat-menu-item\n                                (click)=\"option.callback(element)\"\n                            >\n                                <mat-icon *ngIf=\"option.icon\">\n                                    {{ option.icon }}\n                                </mat-icon>\n                                <span>\n                                    {{ option.label }}\n                                </span>\n                            </button>\n                        </mat-menu>\n                    </div>\n                    <div *ngIf=\"header !== 'icon' && header !== 'img'\">\n                        {{ element[header] }}\n                    </div>\n                </td>\n                <!-- <td mat-cell *matCellDef=\"let element\">{{ element[header] }}</td> -->\n            </ng-container>\n        </div>\n\n        <tr mat-header-row *matHeaderRowDef=\"smartTable.tableHeaders\"></tr>\n        <tr\n            mat-row\n            *matRowDef=\"let row; columns: smartTable.tableHeaders\"\n            (click)=\"smartTable.selection!.toggle(row)\"\n        ></tr>\n    </div>\n</table>\n", styles: [".full-width{width:100%}.smarttableImg{width:25px}\n"], components: [{ type: i1.MatTable, selector: "mat-table, table[mat-table]", exportAs: ["matTable"] }, { type: i2.MatIcon, selector: "mat-icon", inputs: ["color", "inline", "svgIcon", "fontSet", "fontIcon"], exportAs: ["matIcon"] }, { type: i3.MatButton, selector: "button[mat-button], button[mat-raised-button], button[mat-icon-button],             button[mat-fab], button[mat-mini-fab], button[mat-stroked-button],             button[mat-flat-button]", inputs: ["disabled", "disableRipple", "color"], exportAs: ["matButton"] }, { type: i4.MatMenu, selector: "mat-menu", exportAs: ["matMenu"] }, { type: i4.MatMenuItem, selector: "[mat-menu-item]", inputs: ["disabled", "disableRipple", "role"], exportAs: ["matMenuItem"] }, { type: i1.MatHeaderRow, selector: "mat-header-row, tr[mat-header-row]", exportAs: ["matHeaderRow"] }, { type: i1.MatRow, selector: "mat-row, tr[mat-row]", exportAs: ["matRow"] }, { type: i5.MatCheckbox, selector: "mat-checkbox", inputs: ["disableRipple", "color", "tabIndex", "aria-label", "aria-labelledby", "aria-describedby", "id", "required", "labelPosition", "name", "value", "checked", "disabled", "indeterminate"], outputs: ["change", "indeterminateChange"], exportAs: ["matCheckbox"] }], directives: [{ type: i6.NgIf, selector: "[ngIf]", inputs: ["ngIf", "ngIfThen", "ngIfElse"] }, { type: i6.NgForOf, selector: "[ngFor][ngForOf]", inputs: ["ngForOf", "ngForTrackBy", "ngForTemplate"] }, { type: i1.MatColumnDef, selector: "[matColumnDef]", inputs: ["sticky", "matColumnDef"] }, { type: i1.MatHeaderCellDef, selector: "[matHeaderCellDef]" }, { type: i1.MatHeaderCell, selector: "mat-header-cell, th[mat-header-cell]" }, { type: i1.MatCellDef, selector: "[matCellDef]" }, { type: i1.MatCell, selector: "mat-cell, td[mat-cell]" }, { type: i4.MatMenuTrigger, selector: "[mat-menu-trigger-for], [matMenuTriggerFor]", exportAs: ["matMenuTrigger"] }, { type: i1.MatHeaderRowDef, selector: "[matHeaderRowDef]", inputs: ["matHeaderRowDef", "matHeaderRowDefSticky"] }, { type: i1.MatRowDef, selector: "[matRowDef]", inputs: ["matRowDefColumns", "matRowDefWhen"] }] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmarttableComponent, decorators: [{
            type: Component,
            args: [{ selector: 'smarttable', template: "<table mat-table [dataSource]=\"smartTable.tableRows\" class=\"full-width\">\n    <!--- Note that these columns can be defined in any order.\n          The actual rendered columns are set as a property on the row definition\" -->\n\n    <!-- Column Descriptor -->\n    <div *ngIf=\"smartTable.tableType === tableType.INHERITED\">\n        <ng-container\n            *ngFor=\"let header of smartTable.tableHeaders; let i = index\"\n            matColumnDef=\"{{ header }}\"\n        >\n            <th mat-header-cell *matHeaderCellDef>\n                <div *ngIf=\"header === 'icon' || header === 'img' || header === 'options'\"></div>\n                <div *ngIf=\"header !== 'icon' && header !== 'img' && header !== 'options'\">\n                    {{ smartTable.customTableHeaders[i] }}\n                </div>\n            </th>\n            <td mat-cell *matCellDef=\"let element\">\n                <mat-icon *ngIf=\"header === 'icon'\"> {{ element[header] }} </mat-icon>\n                <img\n                    *ngIf=\"header === 'img'\"\n                    [src]=\"element[header]\"\n                    alt=\"\"\n                    class=\"smarttableImg\"\n                />\n                <div *ngIf=\"header === 'options'\">\n                    <button mat-icon-button [matMenuTriggerFor]=\"menu\" aria-label=\"options\">\n                        <mat-icon>more_vert</mat-icon>\n                    </button>\n                    <mat-menu #menu=\"matMenu\">\n                        <button\n                            *ngFor=\"let option of smartTable.options\"\n                            mat-menu-item\n                            (click)=\"option.callback(element)\"\n                        >\n                            <mat-icon *ngIf=\"option.icon\">\n                                {{ option.icon }}\n                            </mat-icon>\n                            <span>\n                                {{ option.label }}\n                            </span>\n                        </button>\n                    </mat-menu>\n                </div>\n                <div *ngIf=\"header !== 'icon' && header !== 'img'\">\n                    {{ element[header] }}\n                </div>\n            </td>\n        </ng-container>\n\n        <tr mat-header-row *matHeaderRowDef=\"smartTable.tableHeaders; sticky: true\"></tr>\n        <tr mat-row *matRowDef=\"let row; columns: smartTable.tableHeaders\"></tr>\n    </div>\n    <div *ngIf=\"smartTable.tableType !== tableType.INHERITED\">\n        <!-- Checkbox Column -->\n        <ng-container matColumnDef=\"select\">\n            <th mat-header-cell *matHeaderCellDef>\n                <mat-checkbox\n                    (change)=\"$event ? toggleAllRows() : null\"\n                    [checked]=\"smartTable.selection!.hasValue() && isAllSelected()\"\n                    [indeterminate]=\"smartTable.selection!.hasValue() && !isAllSelected()\"\n                    [aria-label]=\"checkboxLabel()\"\n                >\n                </mat-checkbox>\n            </th>\n            <td mat-cell *matCellDef=\"let row\">\n                <mat-checkbox\n                    (click)=\"$event.stopPropagation()\"\n                    (change)=\"$event ? smartTable.selection!.toggle(row) : null\"\n                    [checked]=\"smartTable.selection!.isSelected(row)\"\n                    [aria-label]=\"checkboxLabel(row)\"\n                >\n                </mat-checkbox>\n            </td>\n        </ng-container>\n\n        <!-- Column Descriptor -->\n        <div *ngFor=\"let header of smartTable.tableHeaders; let i = index\">\n            <ng-container *ngIf=\"header !== 'select'\" matColumnDef=\"{{ header }}\">\n                <th mat-header-cell *matHeaderCellDef>\n                    <div\n                        *ngIf=\"header === 'icon' || header === 'img' || header === 'options'\"\n                    ></div>\n                    <div *ngIf=\"header !== 'icon' && header !== 'img' && header !== 'options'\">\n                        {{ smartTable.customTableHeaders[i] }}\n                    </div>\n                </th>\n                <td mat-cell *matCellDef=\"let element\">\n                    <mat-icon *ngIf=\"header === 'icon'\"> {{ element[header] }} </mat-icon>\n                    <img\n                        *ngIf=\"header === 'img'\"\n                        [src]=\"element[header]\"\n                        alt=\"\"\n                        class=\"smarttableImg\"\n                    />\n                    <div *ngIf=\"header === 'options'\">\n                        <button mat-icon-button [matMenuTriggerFor]=\"menu\" aria-label=\"options\">\n                            <mat-icon>more_vert</mat-icon>\n                        </button>\n                        <mat-menu #menu=\"matMenu\">\n                            <button\n                                *ngFor=\"let option of smartTable.options\"\n                                mat-menu-item\n                                (click)=\"option.callback(element)\"\n                            >\n                                <mat-icon *ngIf=\"option.icon\">\n                                    {{ option.icon }}\n                                </mat-icon>\n                                <span>\n                                    {{ option.label }}\n                                </span>\n                            </button>\n                        </mat-menu>\n                    </div>\n                    <div *ngIf=\"header !== 'icon' && header !== 'img'\">\n                        {{ element[header] }}\n                    </div>\n                </td>\n                <!-- <td mat-cell *matCellDef=\"let element\">{{ element[header] }}</td> -->\n            </ng-container>\n        </div>\n\n        <tr mat-header-row *matHeaderRowDef=\"smartTable.tableHeaders\"></tr>\n        <tr\n            mat-row\n            *matRowDef=\"let row; columns: smartTable.tableHeaders\"\n            (click)=\"smartTable.selection!.toggle(row)\"\n        ></tr>\n    </div>\n</table>\n", styles: [".full-width{width:100%}.smarttableImg{width:25px}\n"] }]
        }], ctorParameters: function () { return []; }, propDecorators: { smartTable: [{
                type: Input
            }] } });

class SmarttableModule {
}
SmarttableModule.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmarttableModule, deps: [], target: i0.ɵɵFactoryTarget.NgModule });
SmarttableModule.ɵmod = i0.ɵɵngDeclareNgModule({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmarttableModule, declarations: [SmarttableComponent], imports: [BrowserModule,
        MatCommonModule,
        FormsModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatCheckboxModule,
        MatButtonModule,
        MatTableModule,
        MatIconModule,
        MatMenuModule], exports: [SmarttableComponent] });
SmarttableModule.ɵinj = i0.ɵɵngDeclareInjector({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmarttableModule, imports: [[
            BrowserModule,
            MatCommonModule,
            FormsModule,
            ReactiveFormsModule,
            MatFormFieldModule,
            MatCheckboxModule,
            MatButtonModule,
            MatTableModule,
            MatIconModule,
            MatMenuModule,
        ]] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmarttableModule, decorators: [{
            type: NgModule,
            args: [{
                    declarations: [SmarttableComponent],
                    imports: [
                        BrowserModule,
                        MatCommonModule,
                        FormsModule,
                        ReactiveFormsModule,
                        MatFormFieldModule,
                        MatCheckboxModule,
                        MatButtonModule,
                        MatTableModule,
                        MatIconModule,
                        MatMenuModule,
                    ],
                    exports: [SmarttableComponent],
                    schemas: [CUSTOM_ELEMENTS_SCHEMA],
                }]
        }] });

/*
 * Public API Surface of smarttable
 */

/**
 * Generated bundle index. Do not edit.
 */

export { SmartTable, SmartTableType, SmarttableComponent, SmarttableModule, SmarttableService };
//# sourceMappingURL=smartbit4all-table.mjs.map
