import * as i0 from "@angular/core";
import { Injectable, Component, Input, NgModule, CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { SelectionModel } from "@angular/cdk/collections";
import * as i1 from "@angular/material/table";
import { MatTableModule } from "@angular/material/table";
import * as i2 from "@angular/material/icon";
import { MatIconModule } from "@angular/material/icon";
import * as i3 from "@angular/material/button";
import { MatButtonModule } from "@angular/material/button";
import * as i4 from "@angular/material/menu";
import { MatMenuModule } from "@angular/material/menu";
import * as i5 from "@smartbit4all/smart-toolbar";
import { SmartToolbarModule } from "@smartbit4all/smart-toolbar";
import * as i6 from "@angular/material/checkbox";
import { MatCheckboxModule } from "@angular/material/checkbox";
import * as i7 from "@angular/common";
import { MatCommonModule } from "@angular/material/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatFormFieldModule } from "@angular/material/form-field";
import { BrowserModule } from "@angular/platform-browser";

class SmarttableService {
    constructor() {}
}
SmarttableService.ɵfac = i0.ɵɵngDeclareFactory({
    minVersion: "12.0.0",
    version: "13.2.7",
    ngImport: i0,
    type: SmarttableService,
    deps: [],
    target: i0.ɵɵFactoryTarget.Injectable,
});
SmarttableService.ɵprov = i0.ɵɵngDeclareInjectable({
    minVersion: "12.0.0",
    version: "13.2.7",
    ngImport: i0,
    type: SmarttableService,
    providedIn: "root",
});
i0.ɵɵngDeclareClassMetadata({
    minVersion: "12.0.0",
    version: "13.2.7",
    ngImport: i0,
    type: SmarttableService,
    decorators: [
        {
            type: Injectable,
            args: [
                {
                    providedIn: "root",
                },
            ],
        },
    ],
    ctorParameters: function () {
        return [];
    },
});

var SmartTableType;
(function (SmartTableType) {
    SmartTableType[(SmartTableType["INHERITED"] = 0)] = "INHERITED";
    SmartTableType[(SmartTableType["CHECK_BOX"] = 1)] = "CHECK_BOX";
})(SmartTableType || (SmartTableType = {}));
class SmartTable {
    constructor(tableRows, tableType, customSmartTableHeaders, title, isMultiple, options) {
        this.tableHeaders = Object.getOwnPropertyNames(tableRows[0]);
        this.customSmartTableHeaders = customSmartTableHeaders;
        this.setTableHeaders();
        this.tableRows = tableRows;
        this.tableType = tableType;
        this.title = title;
        this.isMultiple = isMultiple;
        this.options = options;
        this.pushOptions();
        this.setTableSelectionType(tableType);
    }
    setTableSelectionType(tableType) {
        if (tableType !== SmartTableType.INHERITED) {
            this.tableHeaders = ["select"].concat(this.tableHeaders);
            this.customTableHeaders = ["select"].concat(this.customTableHeaders);
            this.selection = new SelectionModel(this.isMultiple, []);
        }
    }
    pushOptions() {
        if (this.options && this.options.length) {
            this.tableHeaders.push("options");
        }
    }
    setTableHeaders() {
        if (this.customSmartTableHeaders && this.customSmartTableHeaders.length) {
            let originalHeaders = this.tableHeaders;
            this.tableHeaders = [];
            this.customTableHeaders = [];
            this.customSmartTableHeaders.forEach((tableHeader) => {
                if (originalHeaders.includes(tableHeader.propertyName) && !tableHeader.isHidden) {
                    this.tableHeaders.push(tableHeader.propertyName);
                    this.customTableHeaders.push(tableHeader.label);
                }
            });
        } else {
            this.customTableHeaders = this.tableHeaders;
        }
    }
}

class SmarttableComponent {
    constructor() {
        this.tableType = SmartTableType;
    }
    ngOnInit() {}
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
            return `${this.isAllSelected() ? "deselect" : "select"} all`;
        }
        return `${this.smartTable.selection.isSelected(row) ? "deselect" : "select"} row ${
            row.position + 1
        }`;
    }
}
SmarttableComponent.ɵfac = i0.ɵɵngDeclareFactory({
    minVersion: "12.0.0",
    version: "13.2.7",
    ngImport: i0,
    type: SmarttableComponent,
    deps: [],
    target: i0.ɵɵFactoryTarget.Component,
});
SmarttableComponent.ɵcmp = i0.ɵɵngDeclareComponent({
    minVersion: "12.0.0",
    version: "13.2.7",
    type: SmarttableComponent,
    selector: "smarttable",
    inputs: { smartTable: "smartTable" },
    ngImport: i0,
    template:
        '<table mat-table [dataSource]="smartTable.tableRows" class="full-width">\r\n  <!--- Note that these columns can be defined in any order.\r\n            The actual rendered columns are set as a property on the row definition" -->\r\n\r\n  <!-- Column Descriptor -->\r\n  <div *ngIf="smartTable.tableType === tableType.INHERITED">\r\n    <ng-container\r\n      *ngFor="let header of smartTable.tableHeaders; let i = index"\r\n      matColumnDef="{{ header }}"\r\n    >\r\n      <th mat-header-cell *matHeaderCellDef>\r\n        <div\r\n          *ngIf="\r\n            header === \'icon\' ||\r\n            header === \'img\' ||\r\n            header === \'options\' ||\r\n            header === \'button\'\r\n          "\r\n        ></div>\r\n        <div\r\n          *ngIf="\r\n            header !== \'icon\' &&\r\n            header !== \'img\' &&\r\n            header !== \'options\' &&\r\n            header !== \'button\'\r\n          "\r\n        >\r\n          {{ smartTable.customTableHeaders[i] }}\r\n        </div>\r\n      </th>\r\n      <td mat-cell *matCellDef="let element">\r\n        <mat-icon *ngIf="header === \'icon\'"> {{ element[header] }} </mat-icon>\r\n        <img\r\n          *ngIf="header === \'img\'"\r\n          [src]="element[header]"\r\n          alt=""\r\n          class="smarttableImg"\r\n        />\r\n        <div *ngIf="header === \'options\'">\r\n          <button\r\n            mat-icon-button\r\n            [matMenuTriggerFor]="menu"\r\n            aria-label="options"\r\n          >\r\n            <mat-icon>more_vert</mat-icon>\r\n          </button>\r\n          <mat-menu #menu="matMenu">\r\n            <button\r\n              *ngFor="let option of smartTable.options"\r\n              mat-menu-item\r\n              (click)="option.callback(element)"\r\n            >\r\n              <mat-icon *ngIf="option.icon">\r\n                {{ option.icon }}\r\n              </mat-icon>\r\n              <span>\r\n                {{ option.label }}\r\n              </span>\r\n            </button>\r\n          </mat-menu>\r\n        </div>\r\n        <div\r\n          *ngIf="\r\n            header !== \'icon\' &&\r\n            header !== \'img\' &&\r\n            header !== \'option\' &&\r\n            header !== \'button\'\r\n          "\r\n        >\r\n          {{ element[header] }}\r\n        </div>\r\n        <div *ngIf="header === \'button\'">\r\n          <!-- <button (click)="element[header].onClick()" mat-raised-button>\r\n            {{ element[header].label }}\r\n            <mat-icon>{{ element[header].icon }}</mat-icon>\r\n          </button> -->\r\n          <smart-toolbar [toolbar]="element[header]"></smart-toolbar>\r\n        </div>\r\n      </td>\r\n    </ng-container>\r\n\r\n    <tr\r\n      mat-header-row\r\n      *matHeaderRowDef="smartTable.tableHeaders; sticky: true"\r\n    ></tr>\r\n    <tr mat-row *matRowDef="let row; columns: smartTable.tableHeaders"></tr>\r\n  </div>\r\n  <div *ngIf="smartTable.tableType !== tableType.INHERITED">\r\n    <!-- Checkbox Column -->\r\n    <ng-container matColumnDef="select">\r\n      <th mat-header-cell *matHeaderCellDef>\r\n        <mat-checkbox\r\n          (change)="$event ? toggleAllRows() : null"\r\n          [checked]="smartTable.selection!.hasValue() && isAllSelected()"\r\n          [indeterminate]="smartTable.selection!.hasValue() && !isAllSelected()"\r\n          [aria-label]="checkboxLabel()"\r\n        >\r\n        </mat-checkbox>\r\n      </th>\r\n      <td mat-cell *matCellDef="let row">\r\n        <mat-checkbox\r\n          (click)="$event.stopPropagation()"\r\n          (change)="$event ? smartTable.selection!.toggle(row) : null"\r\n          [checked]="smartTable.selection!.isSelected(row)"\r\n          [aria-label]="checkboxLabel(row)"\r\n        >\r\n        </mat-checkbox>\r\n      </td>\r\n    </ng-container>\r\n\r\n    <!-- Column Descriptor -->\r\n    <div *ngFor="let header of smartTable.tableHeaders; let i = index">\r\n      <ng-container *ngIf="header !== \'select\'" matColumnDef="{{ header }}">\r\n        <th mat-header-cell *matHeaderCellDef>\r\n          <div\r\n            *ngIf="\r\n              header === \'icon\' || header === \'img\' || header === \'options\'\r\n            "\r\n          ></div>\r\n          <div\r\n            *ngIf="\r\n              header !== \'icon\' && header !== \'img\' && header !== \'options\'\r\n            "\r\n          >\r\n            {{ smartTable.customTableHeaders[i] }}\r\n          </div>\r\n        </th>\r\n        <td mat-cell *matCellDef="let element">\r\n          <mat-icon *ngIf="header === \'icon\'"> {{ element[header] }} </mat-icon>\r\n          <img\r\n            *ngIf="header === \'img\'"\r\n            [src]="element[header]"\r\n            alt=""\r\n            class="smarttableImg"\r\n          />\r\n          <div *ngIf="header === \'options\'">\r\n            <button\r\n              mat-icon-button\r\n              [matMenuTriggerFor]="menu"\r\n              aria-label="options"\r\n            >\r\n              <mat-icon>more_vert</mat-icon>\r\n            </button>\r\n            <mat-menu #menu="matMenu">\r\n              <button\r\n                *ngFor="let option of smartTable.options"\r\n                mat-menu-item\r\n                (click)="option.callback(element)"\r\n              >\r\n                <mat-icon *ngIf="option.icon">\r\n                  {{ option.icon }}\r\n                </mat-icon>\r\n                <span>\r\n                  {{ option.label }}\r\n                </span>\r\n              </button>\r\n            </mat-menu>\r\n          </div>\r\n          <div *ngIf="header !== \'icon\' && header !== \'img\'">\r\n            {{ element[header] }}\r\n          </div>\r\n        </td>\r\n        <!-- <td mat-cell *matCellDef="let element">{{ element[header] }}</td> -->\r\n      </ng-container>\r\n    </div>\r\n\r\n    <tr mat-header-row *matHeaderRowDef="smartTable.tableHeaders"></tr>\r\n    <tr\r\n      mat-row\r\n      *matRowDef="let row; columns: smartTable.tableHeaders"\r\n      (click)="smartTable.selection!.toggle(row)"\r\n    ></tr>\r\n  </div>\r\n</table>\r\n',
    styles: [".full-width{width:100%}.smarttableImg{width:25px}\n"],
    components: [
        { type: i1.MatTable, selector: "mat-table, table[mat-table]", exportAs: ["matTable"] },
        {
            type: i2.MatIcon,
            selector: "mat-icon",
            inputs: ["color", "inline", "svgIcon", "fontSet", "fontIcon"],
            exportAs: ["matIcon"],
        },
        {
            type: i3.MatButton,
            selector:
                "button[mat-button], button[mat-raised-button], button[mat-icon-button],             button[mat-fab], button[mat-mini-fab], button[mat-stroked-button],             button[mat-flat-button]",
            inputs: ["disabled", "disableRipple", "color"],
            exportAs: ["matButton"],
        },
        { type: i4.MatMenu, selector: "mat-menu", exportAs: ["matMenu"] },
        {
            type: i4.MatMenuItem,
            selector: "[mat-menu-item]",
            inputs: ["disabled", "disableRipple", "role"],
            exportAs: ["matMenuItem"],
        },
        { type: i5.SmartToolbarComponent, selector: "smart-toolbar", inputs: ["toolbar"] },
        {
            type: i1.MatHeaderRow,
            selector: "mat-header-row, tr[mat-header-row]",
            exportAs: ["matHeaderRow"],
        },
        { type: i1.MatRow, selector: "mat-row, tr[mat-row]", exportAs: ["matRow"] },
        {
            type: i6.MatCheckbox,
            selector: "mat-checkbox",
            inputs: [
                "disableRipple",
                "color",
                "tabIndex",
                "aria-label",
                "aria-labelledby",
                "aria-describedby",
                "id",
                "required",
                "labelPosition",
                "name",
                "value",
                "checked",
                "disabled",
                "indeterminate",
            ],
            outputs: ["change", "indeterminateChange"],
            exportAs: ["matCheckbox"],
        },
    ],
    directives: [
        { type: i7.NgIf, selector: "[ngIf]", inputs: ["ngIf", "ngIfThen", "ngIfElse"] },
        {
            type: i7.NgForOf,
            selector: "[ngFor][ngForOf]",
            inputs: ["ngForOf", "ngForTrackBy", "ngForTemplate"],
        },
        { type: i1.MatColumnDef, selector: "[matColumnDef]", inputs: ["sticky", "matColumnDef"] },
        { type: i1.MatHeaderCellDef, selector: "[matHeaderCellDef]" },
        { type: i1.MatHeaderCell, selector: "mat-header-cell, th[mat-header-cell]" },
        { type: i1.MatCellDef, selector: "[matCellDef]" },
        { type: i1.MatCell, selector: "mat-cell, td[mat-cell]" },
        {
            type: i4.MatMenuTrigger,
            selector: "[mat-menu-trigger-for], [matMenuTriggerFor]",
            exportAs: ["matMenuTrigger"],
        },
        {
            type: i1.MatHeaderRowDef,
            selector: "[matHeaderRowDef]",
            inputs: ["matHeaderRowDef", "matHeaderRowDefSticky"],
        },
        {
            type: i1.MatRowDef,
            selector: "[matRowDef]",
            inputs: ["matRowDefColumns", "matRowDefWhen"],
        },
    ],
});
i0.ɵɵngDeclareClassMetadata({
    minVersion: "12.0.0",
    version: "13.2.7",
    ngImport: i0,
    type: SmarttableComponent,
    decorators: [
        {
            type: Component,
            args: [
                {
                    selector: "smarttable",
                    template:
                        '<table mat-table [dataSource]="smartTable.tableRows" class="full-width">\r\n  <!--- Note that these columns can be defined in any order.\r\n            The actual rendered columns are set as a property on the row definition" -->\r\n\r\n  <!-- Column Descriptor -->\r\n  <div *ngIf="smartTable.tableType === tableType.INHERITED">\r\n    <ng-container\r\n      *ngFor="let header of smartTable.tableHeaders; let i = index"\r\n      matColumnDef="{{ header }}"\r\n    >\r\n      <th mat-header-cell *matHeaderCellDef>\r\n        <div\r\n          *ngIf="\r\n            header === \'icon\' ||\r\n            header === \'img\' ||\r\n            header === \'options\' ||\r\n            header === \'button\'\r\n          "\r\n        ></div>\r\n        <div\r\n          *ngIf="\r\n            header !== \'icon\' &&\r\n            header !== \'img\' &&\r\n            header !== \'options\' &&\r\n            header !== \'button\'\r\n          "\r\n        >\r\n          {{ smartTable.customTableHeaders[i] }}\r\n        </div>\r\n      </th>\r\n      <td mat-cell *matCellDef="let element">\r\n        <mat-icon *ngIf="header === \'icon\'"> {{ element[header] }} </mat-icon>\r\n        <img\r\n          *ngIf="header === \'img\'"\r\n          [src]="element[header]"\r\n          alt=""\r\n          class="smarttableImg"\r\n        />\r\n        <div *ngIf="header === \'options\'">\r\n          <button\r\n            mat-icon-button\r\n            [matMenuTriggerFor]="menu"\r\n            aria-label="options"\r\n          >\r\n            <mat-icon>more_vert</mat-icon>\r\n          </button>\r\n          <mat-menu #menu="matMenu">\r\n            <button\r\n              *ngFor="let option of smartTable.options"\r\n              mat-menu-item\r\n              (click)="option.callback(element)"\r\n            >\r\n              <mat-icon *ngIf="option.icon">\r\n                {{ option.icon }}\r\n              </mat-icon>\r\n              <span>\r\n                {{ option.label }}\r\n              </span>\r\n            </button>\r\n          </mat-menu>\r\n        </div>\r\n        <div\r\n          *ngIf="\r\n            header !== \'icon\' &&\r\n            header !== \'img\' &&\r\n            header !== \'option\' &&\r\n            header !== \'button\'\r\n          "\r\n        >\r\n          {{ element[header] }}\r\n        </div>\r\n        <div *ngIf="header === \'button\'">\r\n          <!-- <button (click)="element[header].onClick()" mat-raised-button>\r\n            {{ element[header].label }}\r\n            <mat-icon>{{ element[header].icon }}</mat-icon>\r\n          </button> -->\r\n          <smart-toolbar [toolbar]="element[header]"></smart-toolbar>\r\n        </div>\r\n      </td>\r\n    </ng-container>\r\n\r\n    <tr\r\n      mat-header-row\r\n      *matHeaderRowDef="smartTable.tableHeaders; sticky: true"\r\n    ></tr>\r\n    <tr mat-row *matRowDef="let row; columns: smartTable.tableHeaders"></tr>\r\n  </div>\r\n  <div *ngIf="smartTable.tableType !== tableType.INHERITED">\r\n    <!-- Checkbox Column -->\r\n    <ng-container matColumnDef="select">\r\n      <th mat-header-cell *matHeaderCellDef>\r\n        <mat-checkbox\r\n          (change)="$event ? toggleAllRows() : null"\r\n          [checked]="smartTable.selection!.hasValue() && isAllSelected()"\r\n          [indeterminate]="smartTable.selection!.hasValue() && !isAllSelected()"\r\n          [aria-label]="checkboxLabel()"\r\n        >\r\n        </mat-checkbox>\r\n      </th>\r\n      <td mat-cell *matCellDef="let row">\r\n        <mat-checkbox\r\n          (click)="$event.stopPropagation()"\r\n          (change)="$event ? smartTable.selection!.toggle(row) : null"\r\n          [checked]="smartTable.selection!.isSelected(row)"\r\n          [aria-label]="checkboxLabel(row)"\r\n        >\r\n        </mat-checkbox>\r\n      </td>\r\n    </ng-container>\r\n\r\n    <!-- Column Descriptor -->\r\n    <div *ngFor="let header of smartTable.tableHeaders; let i = index">\r\n      <ng-container *ngIf="header !== \'select\'" matColumnDef="{{ header }}">\r\n        <th mat-header-cell *matHeaderCellDef>\r\n          <div\r\n            *ngIf="\r\n              header === \'icon\' || header === \'img\' || header === \'options\'\r\n            "\r\n          ></div>\r\n          <div\r\n            *ngIf="\r\n              header !== \'icon\' && header !== \'img\' && header !== \'options\'\r\n            "\r\n          >\r\n            {{ smartTable.customTableHeaders[i] }}\r\n          </div>\r\n        </th>\r\n        <td mat-cell *matCellDef="let element">\r\n          <mat-icon *ngIf="header === \'icon\'"> {{ element[header] }} </mat-icon>\r\n          <img\r\n            *ngIf="header === \'img\'"\r\n            [src]="element[header]"\r\n            alt=""\r\n            class="smarttableImg"\r\n          />\r\n          <div *ngIf="header === \'options\'">\r\n            <button\r\n              mat-icon-button\r\n              [matMenuTriggerFor]="menu"\r\n              aria-label="options"\r\n            >\r\n              <mat-icon>more_vert</mat-icon>\r\n            </button>\r\n            <mat-menu #menu="matMenu">\r\n              <button\r\n                *ngFor="let option of smartTable.options"\r\n                mat-menu-item\r\n                (click)="option.callback(element)"\r\n              >\r\n                <mat-icon *ngIf="option.icon">\r\n                  {{ option.icon }}\r\n                </mat-icon>\r\n                <span>\r\n                  {{ option.label }}\r\n                </span>\r\n              </button>\r\n            </mat-menu>\r\n          </div>\r\n          <div *ngIf="header !== \'icon\' && header !== \'img\'">\r\n            {{ element[header] }}\r\n          </div>\r\n        </td>\r\n        <!-- <td mat-cell *matCellDef="let element">{{ element[header] }}</td> -->\r\n      </ng-container>\r\n    </div>\r\n\r\n    <tr mat-header-row *matHeaderRowDef="smartTable.tableHeaders"></tr>\r\n    <tr\r\n      mat-row\r\n      *matRowDef="let row; columns: smartTable.tableHeaders"\r\n      (click)="smartTable.selection!.toggle(row)"\r\n    ></tr>\r\n  </div>\r\n</table>\r\n',
                    styles: [".full-width{width:100%}.smarttableImg{width:25px}\n"],
                },
            ],
        },
    ],
    ctorParameters: function () {
        return [];
    },
    propDecorators: {
        smartTable: [
            {
                type: Input,
            },
        ],
    },
});

class SmarttableModule {}
SmarttableModule.ɵfac = i0.ɵɵngDeclareFactory({
    minVersion: "12.0.0",
    version: "13.2.7",
    ngImport: i0,
    type: SmarttableModule,
    deps: [],
    target: i0.ɵɵFactoryTarget.NgModule,
});
SmarttableModule.ɵmod = i0.ɵɵngDeclareNgModule({
    minVersion: "12.0.0",
    version: "13.2.7",
    ngImport: i0,
    type: SmarttableModule,
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
        SmartToolbarModule,
    ],
    exports: [SmarttableComponent],
});
SmarttableModule.ɵinj = i0.ɵɵngDeclareInjector({
    minVersion: "12.0.0",
    version: "13.2.7",
    ngImport: i0,
    type: SmarttableModule,
    imports: [
        [
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
            SmartToolbarModule,
        ],
    ],
});
i0.ɵɵngDeclareClassMetadata({
    minVersion: "12.0.0",
    version: "13.2.7",
    ngImport: i0,
    type: SmarttableModule,
    decorators: [
        {
            type: NgModule,
            args: [
                {
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
                        SmartToolbarModule,
                    ],
                    exports: [SmarttableComponent],
                    schemas: [CUSTOM_ELEMENTS_SCHEMA],
                },
            ],
        },
    ],
});

/*
 * Public API Surface of smarttable
 */

/**
 * Generated bundle index. Do not edit.
 */

export { SmartTable, SmartTableType, SmarttableComponent, SmarttableModule, SmarttableService };
//# sourceMappingURL=smartbit4all-table.mjs.map
