import { Component, Input } from "@angular/core";
import { SmartTableType } from "./smarttable.model";
import * as i0 from "@angular/core";
import * as i1 from "@angular/material/table";
import * as i2 from "@angular/material/icon";
import * as i3 from "@angular/material/button";
import * as i4 from "@angular/material/menu";
import * as i5 from "@smartbit4all/smart-toolbar";
import * as i6 from "@angular/material/checkbox";
import * as i7 from "@angular/common";
export class SmarttableComponent {
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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnR0YWJsZS5jb21wb25lbnQuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi8uLi9wcm9qZWN0cy9zbWFydHRhYmxlL3NyYy9saWIvc21hcnR0YWJsZS5jb21wb25lbnQudHMiLCIuLi8uLi8uLi8uLi9wcm9qZWN0cy9zbWFydHRhYmxlL3NyYy9saWIvc21hcnR0YWJsZS5jb21wb25lbnQuaHRtbCJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQSxPQUFPLEVBQUUsU0FBUyxFQUFFLEtBQUssRUFBVSxNQUFNLGVBQWUsQ0FBQztBQUN6RCxPQUFPLEVBQWMsY0FBYyxFQUFFLE1BQU0sb0JBQW9CLENBQUM7Ozs7Ozs7OztBQU9oRSxNQUFNLE9BQU8sbUJBQW1CO0lBSS9CO1FBRkEsY0FBUyxHQUFHLGNBQWMsQ0FBQztJQUVaLENBQUM7SUFFaEIsUUFBUSxLQUFVLENBQUM7SUFFbkIsYUFBYTtRQUNaLE1BQU0sV0FBVyxHQUFHLElBQUksQ0FBQyxVQUFVLENBQUMsU0FBVSxDQUFDLFFBQVEsQ0FBQyxNQUFNLENBQUM7UUFDL0QsTUFBTSxPQUFPLEdBQUcsSUFBSSxDQUFDLFVBQVUsQ0FBQyxTQUFTLENBQUMsTUFBTSxDQUFDO1FBQ2pELE9BQU8sV0FBVyxLQUFLLE9BQU8sQ0FBQztJQUNoQyxDQUFDO0lBRUQsZ0ZBQWdGO0lBQ2hGLGFBQWE7UUFDWixJQUFJLElBQUksQ0FBQyxhQUFhLEVBQUUsRUFBRTtZQUN6QixJQUFJLENBQUMsVUFBVSxDQUFDLFNBQVUsQ0FBQyxLQUFLLEVBQUUsQ0FBQztZQUNuQyxPQUFPO1NBQ1A7UUFFRCxJQUFJLENBQUMsVUFBVSxDQUFDLFNBQVUsQ0FBQyxNQUFNLENBQUMsR0FBRyxJQUFJLENBQUMsVUFBVSxDQUFDLFNBQVMsQ0FBQyxDQUFDO0lBQ2pFLENBQUM7SUFFRCxtREFBbUQ7SUFDbkQsYUFBYSxDQUFDLEdBQVM7UUFDdEIsSUFBSSxDQUFDLEdBQUcsRUFBRTtZQUNULE9BQU8sR0FBRyxJQUFJLENBQUMsYUFBYSxFQUFFLENBQUMsQ0FBQyxDQUFDLFVBQVUsQ0FBQyxDQUFDLENBQUMsUUFBUSxNQUFNLENBQUM7U0FDN0Q7UUFDRCxPQUFPLEdBQUcsSUFBSSxDQUFDLFVBQVUsQ0FBQyxTQUFVLENBQUMsVUFBVSxDQUFDLEdBQUcsQ0FBQyxDQUFDLENBQUMsQ0FBQyxVQUFVLENBQUMsQ0FBQyxDQUFDLFFBQVEsUUFDM0UsR0FBRyxDQUFDLFFBQVEsR0FBRyxDQUNoQixFQUFFLENBQUM7SUFDSixDQUFDOztnSEFoQ1csbUJBQW1CO29HQUFuQixtQkFBbUIsd0ZDUmhDLDhsTUE4S0E7MkZEdEthLG1CQUFtQjtrQkFML0IsU0FBUzsrQkFDQyxZQUFZOzBFQUtiLFVBQVU7c0JBQWxCLEtBQUsiLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQgeyBDb21wb25lbnQsIElucHV0LCBPbkluaXQgfSBmcm9tICdAYW5ndWxhci9jb3JlJztcclxuaW1wb3J0IHsgU21hcnRUYWJsZSwgU21hcnRUYWJsZVR5cGUgfSBmcm9tICcuL3NtYXJ0dGFibGUubW9kZWwnO1xyXG5cclxuQENvbXBvbmVudCh7XHJcblx0c2VsZWN0b3I6ICdzbWFydHRhYmxlJyxcclxuXHR0ZW1wbGF0ZVVybDogJy4vc21hcnR0YWJsZS5jb21wb25lbnQuaHRtbCcsXHJcblx0c3R5bGVVcmxzOiBbJy4vc21hcnR0YWJsZS5jb21wb25lbnQuY3NzJ11cclxufSlcclxuZXhwb3J0IGNsYXNzIFNtYXJ0dGFibGVDb21wb25lbnQgaW1wbGVtZW50cyBPbkluaXQge1xyXG5cdEBJbnB1dCgpIHNtYXJ0VGFibGUhOiBTbWFydFRhYmxlPGFueT47XHJcblx0dGFibGVUeXBlID0gU21hcnRUYWJsZVR5cGU7XHJcblxyXG5cdGNvbnN0cnVjdG9yKCkge31cclxuXHJcblx0bmdPbkluaXQoKTogdm9pZCB7fVxyXG5cclxuXHRpc0FsbFNlbGVjdGVkKCkge1xyXG5cdFx0Y29uc3QgbnVtU2VsZWN0ZWQgPSB0aGlzLnNtYXJ0VGFibGUuc2VsZWN0aW9uIS5zZWxlY3RlZC5sZW5ndGg7XHJcblx0XHRjb25zdCBudW1Sb3dzID0gdGhpcy5zbWFydFRhYmxlLnRhYmxlUm93cy5sZW5ndGg7XHJcblx0XHRyZXR1cm4gbnVtU2VsZWN0ZWQgPT09IG51bVJvd3M7XHJcblx0fVxyXG5cclxuXHQvKiogU2VsZWN0cyBhbGwgcm93cyBpZiB0aGV5IGFyZSBub3QgYWxsIHNlbGVjdGVkOyBvdGhlcndpc2UgY2xlYXIgc2VsZWN0aW9uLiAqL1xyXG5cdHRvZ2dsZUFsbFJvd3MoKSB7XHJcblx0XHRpZiAodGhpcy5pc0FsbFNlbGVjdGVkKCkpIHtcclxuXHRcdFx0dGhpcy5zbWFydFRhYmxlLnNlbGVjdGlvbiEuY2xlYXIoKTtcclxuXHRcdFx0cmV0dXJuO1xyXG5cdFx0fVxyXG5cclxuXHRcdHRoaXMuc21hcnRUYWJsZS5zZWxlY3Rpb24hLnNlbGVjdCguLi50aGlzLnNtYXJ0VGFibGUudGFibGVSb3dzKTtcclxuXHR9XHJcblxyXG5cdC8qKiBUaGUgbGFiZWwgZm9yIHRoZSBjaGVja2JveCBvbiB0aGUgcGFzc2VkIHJvdyAqL1xyXG5cdGNoZWNrYm94TGFiZWwocm93PzogYW55KTogc3RyaW5nIHtcclxuXHRcdGlmICghcm93KSB7XHJcblx0XHRcdHJldHVybiBgJHt0aGlzLmlzQWxsU2VsZWN0ZWQoKSA/ICdkZXNlbGVjdCcgOiAnc2VsZWN0J30gYWxsYDtcclxuXHRcdH1cclxuXHRcdHJldHVybiBgJHt0aGlzLnNtYXJ0VGFibGUuc2VsZWN0aW9uIS5pc1NlbGVjdGVkKHJvdykgPyAnZGVzZWxlY3QnIDogJ3NlbGVjdCd9IHJvdyAke1xyXG5cdFx0XHRyb3cucG9zaXRpb24gKyAxXHJcblx0XHR9YDtcclxuXHR9XHJcbn1cclxuIiwiPHRhYmxlIG1hdC10YWJsZSBbZGF0YVNvdXJjZV09XCJzbWFydFRhYmxlLnRhYmxlUm93c1wiIGNsYXNzPVwiZnVsbC13aWR0aFwiPlxyXG4gIDwhLS0tIE5vdGUgdGhhdCB0aGVzZSBjb2x1bW5zIGNhbiBiZSBkZWZpbmVkIGluIGFueSBvcmRlci5cclxuICAgICAgICAgICAgVGhlIGFjdHVhbCByZW5kZXJlZCBjb2x1bW5zIGFyZSBzZXQgYXMgYSBwcm9wZXJ0eSBvbiB0aGUgcm93IGRlZmluaXRpb25cIiAtLT5cclxuXHJcbiAgPCEtLSBDb2x1bW4gRGVzY3JpcHRvciAtLT5cclxuICA8ZGl2ICpuZ0lmPVwic21hcnRUYWJsZS50YWJsZVR5cGUgPT09IHRhYmxlVHlwZS5JTkhFUklURURcIj5cclxuICAgIDxuZy1jb250YWluZXJcclxuICAgICAgKm5nRm9yPVwibGV0IGhlYWRlciBvZiBzbWFydFRhYmxlLnRhYmxlSGVhZGVyczsgbGV0IGkgPSBpbmRleFwiXHJcbiAgICAgIG1hdENvbHVtbkRlZj1cInt7IGhlYWRlciB9fVwiXHJcbiAgICA+XHJcbiAgICAgIDx0aCBtYXQtaGVhZGVyLWNlbGwgKm1hdEhlYWRlckNlbGxEZWY+XHJcbiAgICAgICAgPGRpdlxyXG4gICAgICAgICAgKm5nSWY9XCJcclxuICAgICAgICAgICAgaGVhZGVyID09PSAnaWNvbicgfHxcclxuICAgICAgICAgICAgaGVhZGVyID09PSAnaW1nJyB8fFxyXG4gICAgICAgICAgICBoZWFkZXIgPT09ICdvcHRpb25zJyB8fFxyXG4gICAgICAgICAgICBoZWFkZXIgPT09ICdidXR0b24nXHJcbiAgICAgICAgICBcIlxyXG4gICAgICAgID48L2Rpdj5cclxuICAgICAgICA8ZGl2XHJcbiAgICAgICAgICAqbmdJZj1cIlxyXG4gICAgICAgICAgICBoZWFkZXIgIT09ICdpY29uJyAmJlxyXG4gICAgICAgICAgICBoZWFkZXIgIT09ICdpbWcnICYmXHJcbiAgICAgICAgICAgIGhlYWRlciAhPT0gJ29wdGlvbnMnICYmXHJcbiAgICAgICAgICAgIGhlYWRlciAhPT0gJ2J1dHRvbidcclxuICAgICAgICAgIFwiXHJcbiAgICAgICAgPlxyXG4gICAgICAgICAge3sgc21hcnRUYWJsZS5jdXN0b21UYWJsZUhlYWRlcnNbaV0gfX1cclxuICAgICAgICA8L2Rpdj5cclxuICAgICAgPC90aD5cclxuICAgICAgPHRkIG1hdC1jZWxsICptYXRDZWxsRGVmPVwibGV0IGVsZW1lbnRcIj5cclxuICAgICAgICA8bWF0LWljb24gKm5nSWY9XCJoZWFkZXIgPT09ICdpY29uJ1wiPiB7eyBlbGVtZW50W2hlYWRlcl0gfX0gPC9tYXQtaWNvbj5cclxuICAgICAgICA8aW1nXHJcbiAgICAgICAgICAqbmdJZj1cImhlYWRlciA9PT0gJ2ltZydcIlxyXG4gICAgICAgICAgW3NyY109XCJlbGVtZW50W2hlYWRlcl1cIlxyXG4gICAgICAgICAgYWx0PVwiXCJcclxuICAgICAgICAgIGNsYXNzPVwic21hcnR0YWJsZUltZ1wiXHJcbiAgICAgICAgLz5cclxuICAgICAgICA8ZGl2ICpuZ0lmPVwiaGVhZGVyID09PSAnb3B0aW9ucydcIj5cclxuICAgICAgICAgIDxidXR0b25cclxuICAgICAgICAgICAgbWF0LWljb24tYnV0dG9uXHJcbiAgICAgICAgICAgIFttYXRNZW51VHJpZ2dlckZvcl09XCJtZW51XCJcclxuICAgICAgICAgICAgYXJpYS1sYWJlbD1cIm9wdGlvbnNcIlxyXG4gICAgICAgICAgPlxyXG4gICAgICAgICAgICA8bWF0LWljb24+bW9yZV92ZXJ0PC9tYXQtaWNvbj5cclxuICAgICAgICAgIDwvYnV0dG9uPlxyXG4gICAgICAgICAgPG1hdC1tZW51ICNtZW51PVwibWF0TWVudVwiPlxyXG4gICAgICAgICAgICA8YnV0dG9uXHJcbiAgICAgICAgICAgICAgKm5nRm9yPVwibGV0IG9wdGlvbiBvZiBzbWFydFRhYmxlLm9wdGlvbnNcIlxyXG4gICAgICAgICAgICAgIG1hdC1tZW51LWl0ZW1cclxuICAgICAgICAgICAgICAoY2xpY2spPVwib3B0aW9uLmNhbGxiYWNrKGVsZW1lbnQpXCJcclxuICAgICAgICAgICAgPlxyXG4gICAgICAgICAgICAgIDxtYXQtaWNvbiAqbmdJZj1cIm9wdGlvbi5pY29uXCI+XHJcbiAgICAgICAgICAgICAgICB7eyBvcHRpb24uaWNvbiB9fVxyXG4gICAgICAgICAgICAgIDwvbWF0LWljb24+XHJcbiAgICAgICAgICAgICAgPHNwYW4+XHJcbiAgICAgICAgICAgICAgICB7eyBvcHRpb24ubGFiZWwgfX1cclxuICAgICAgICAgICAgICA8L3NwYW4+XHJcbiAgICAgICAgICAgIDwvYnV0dG9uPlxyXG4gICAgICAgICAgPC9tYXQtbWVudT5cclxuICAgICAgICA8L2Rpdj5cclxuICAgICAgICA8ZGl2XHJcbiAgICAgICAgICAqbmdJZj1cIlxyXG4gICAgICAgICAgICBoZWFkZXIgIT09ICdpY29uJyAmJlxyXG4gICAgICAgICAgICBoZWFkZXIgIT09ICdpbWcnICYmXHJcbiAgICAgICAgICAgIGhlYWRlciAhPT0gJ29wdGlvbicgJiZcclxuICAgICAgICAgICAgaGVhZGVyICE9PSAnYnV0dG9uJ1xyXG4gICAgICAgICAgXCJcclxuICAgICAgICA+XHJcbiAgICAgICAgICB7eyBlbGVtZW50W2hlYWRlcl0gfX1cclxuICAgICAgICA8L2Rpdj5cclxuICAgICAgICA8ZGl2ICpuZ0lmPVwiaGVhZGVyID09PSAnYnV0dG9uJ1wiPlxyXG4gICAgICAgICAgPCEtLSA8YnV0dG9uIChjbGljayk9XCJlbGVtZW50W2hlYWRlcl0ub25DbGljaygpXCIgbWF0LXJhaXNlZC1idXR0b24+XHJcbiAgICAgICAgICAgIHt7IGVsZW1lbnRbaGVhZGVyXS5sYWJlbCB9fVxyXG4gICAgICAgICAgICA8bWF0LWljb24+e3sgZWxlbWVudFtoZWFkZXJdLmljb24gfX08L21hdC1pY29uPlxyXG4gICAgICAgICAgPC9idXR0b24+IC0tPlxyXG4gICAgICAgICAgPHNtYXJ0LXRvb2xiYXIgW3Rvb2xiYXJdPVwiZWxlbWVudFtoZWFkZXJdXCI+PC9zbWFydC10b29sYmFyPlxyXG4gICAgICAgIDwvZGl2PlxyXG4gICAgICA8L3RkPlxyXG4gICAgPC9uZy1jb250YWluZXI+XHJcblxyXG4gICAgPHRyXHJcbiAgICAgIG1hdC1oZWFkZXItcm93XHJcbiAgICAgICptYXRIZWFkZXJSb3dEZWY9XCJzbWFydFRhYmxlLnRhYmxlSGVhZGVyczsgc3RpY2t5OiB0cnVlXCJcclxuICAgID48L3RyPlxyXG4gICAgPHRyIG1hdC1yb3cgKm1hdFJvd0RlZj1cImxldCByb3c7IGNvbHVtbnM6IHNtYXJ0VGFibGUudGFibGVIZWFkZXJzXCI+PC90cj5cclxuICA8L2Rpdj5cclxuICA8ZGl2ICpuZ0lmPVwic21hcnRUYWJsZS50YWJsZVR5cGUgIT09IHRhYmxlVHlwZS5JTkhFUklURURcIj5cclxuICAgIDwhLS0gQ2hlY2tib3ggQ29sdW1uIC0tPlxyXG4gICAgPG5nLWNvbnRhaW5lciBtYXRDb2x1bW5EZWY9XCJzZWxlY3RcIj5cclxuICAgICAgPHRoIG1hdC1oZWFkZXItY2VsbCAqbWF0SGVhZGVyQ2VsbERlZj5cclxuICAgICAgICA8bWF0LWNoZWNrYm94XHJcbiAgICAgICAgICAoY2hhbmdlKT1cIiRldmVudCA/IHRvZ2dsZUFsbFJvd3MoKSA6IG51bGxcIlxyXG4gICAgICAgICAgW2NoZWNrZWRdPVwic21hcnRUYWJsZS5zZWxlY3Rpb24hLmhhc1ZhbHVlKCkgJiYgaXNBbGxTZWxlY3RlZCgpXCJcclxuICAgICAgICAgIFtpbmRldGVybWluYXRlXT1cInNtYXJ0VGFibGUuc2VsZWN0aW9uIS5oYXNWYWx1ZSgpICYmICFpc0FsbFNlbGVjdGVkKClcIlxyXG4gICAgICAgICAgW2FyaWEtbGFiZWxdPVwiY2hlY2tib3hMYWJlbCgpXCJcclxuICAgICAgICA+XHJcbiAgICAgICAgPC9tYXQtY2hlY2tib3g+XHJcbiAgICAgIDwvdGg+XHJcbiAgICAgIDx0ZCBtYXQtY2VsbCAqbWF0Q2VsbERlZj1cImxldCByb3dcIj5cclxuICAgICAgICA8bWF0LWNoZWNrYm94XHJcbiAgICAgICAgICAoY2xpY2spPVwiJGV2ZW50LnN0b3BQcm9wYWdhdGlvbigpXCJcclxuICAgICAgICAgIChjaGFuZ2UpPVwiJGV2ZW50ID8gc21hcnRUYWJsZS5zZWxlY3Rpb24hLnRvZ2dsZShyb3cpIDogbnVsbFwiXHJcbiAgICAgICAgICBbY2hlY2tlZF09XCJzbWFydFRhYmxlLnNlbGVjdGlvbiEuaXNTZWxlY3RlZChyb3cpXCJcclxuICAgICAgICAgIFthcmlhLWxhYmVsXT1cImNoZWNrYm94TGFiZWwocm93KVwiXHJcbiAgICAgICAgPlxyXG4gICAgICAgIDwvbWF0LWNoZWNrYm94PlxyXG4gICAgICA8L3RkPlxyXG4gICAgPC9uZy1jb250YWluZXI+XHJcblxyXG4gICAgPCEtLSBDb2x1bW4gRGVzY3JpcHRvciAtLT5cclxuICAgIDxkaXYgKm5nRm9yPVwibGV0IGhlYWRlciBvZiBzbWFydFRhYmxlLnRhYmxlSGVhZGVyczsgbGV0IGkgPSBpbmRleFwiPlxyXG4gICAgICA8bmctY29udGFpbmVyICpuZ0lmPVwiaGVhZGVyICE9PSAnc2VsZWN0J1wiIG1hdENvbHVtbkRlZj1cInt7IGhlYWRlciB9fVwiPlxyXG4gICAgICAgIDx0aCBtYXQtaGVhZGVyLWNlbGwgKm1hdEhlYWRlckNlbGxEZWY+XHJcbiAgICAgICAgICA8ZGl2XHJcbiAgICAgICAgICAgICpuZ0lmPVwiXHJcbiAgICAgICAgICAgICAgaGVhZGVyID09PSAnaWNvbicgfHwgaGVhZGVyID09PSAnaW1nJyB8fCBoZWFkZXIgPT09ICdvcHRpb25zJ1xyXG4gICAgICAgICAgICBcIlxyXG4gICAgICAgICAgPjwvZGl2PlxyXG4gICAgICAgICAgPGRpdlxyXG4gICAgICAgICAgICAqbmdJZj1cIlxyXG4gICAgICAgICAgICAgIGhlYWRlciAhPT0gJ2ljb24nICYmIGhlYWRlciAhPT0gJ2ltZycgJiYgaGVhZGVyICE9PSAnb3B0aW9ucydcclxuICAgICAgICAgICAgXCJcclxuICAgICAgICAgID5cclxuICAgICAgICAgICAge3sgc21hcnRUYWJsZS5jdXN0b21UYWJsZUhlYWRlcnNbaV0gfX1cclxuICAgICAgICAgIDwvZGl2PlxyXG4gICAgICAgIDwvdGg+XHJcbiAgICAgICAgPHRkIG1hdC1jZWxsICptYXRDZWxsRGVmPVwibGV0IGVsZW1lbnRcIj5cclxuICAgICAgICAgIDxtYXQtaWNvbiAqbmdJZj1cImhlYWRlciA9PT0gJ2ljb24nXCI+IHt7IGVsZW1lbnRbaGVhZGVyXSB9fSA8L21hdC1pY29uPlxyXG4gICAgICAgICAgPGltZ1xyXG4gICAgICAgICAgICAqbmdJZj1cImhlYWRlciA9PT0gJ2ltZydcIlxyXG4gICAgICAgICAgICBbc3JjXT1cImVsZW1lbnRbaGVhZGVyXVwiXHJcbiAgICAgICAgICAgIGFsdD1cIlwiXHJcbiAgICAgICAgICAgIGNsYXNzPVwic21hcnR0YWJsZUltZ1wiXHJcbiAgICAgICAgICAvPlxyXG4gICAgICAgICAgPGRpdiAqbmdJZj1cImhlYWRlciA9PT0gJ29wdGlvbnMnXCI+XHJcbiAgICAgICAgICAgIDxidXR0b25cclxuICAgICAgICAgICAgICBtYXQtaWNvbi1idXR0b25cclxuICAgICAgICAgICAgICBbbWF0TWVudVRyaWdnZXJGb3JdPVwibWVudVwiXHJcbiAgICAgICAgICAgICAgYXJpYS1sYWJlbD1cIm9wdGlvbnNcIlxyXG4gICAgICAgICAgICA+XHJcbiAgICAgICAgICAgICAgPG1hdC1pY29uPm1vcmVfdmVydDwvbWF0LWljb24+XHJcbiAgICAgICAgICAgIDwvYnV0dG9uPlxyXG4gICAgICAgICAgICA8bWF0LW1lbnUgI21lbnU9XCJtYXRNZW51XCI+XHJcbiAgICAgICAgICAgICAgPGJ1dHRvblxyXG4gICAgICAgICAgICAgICAgKm5nRm9yPVwibGV0IG9wdGlvbiBvZiBzbWFydFRhYmxlLm9wdGlvbnNcIlxyXG4gICAgICAgICAgICAgICAgbWF0LW1lbnUtaXRlbVxyXG4gICAgICAgICAgICAgICAgKGNsaWNrKT1cIm9wdGlvbi5jYWxsYmFjayhlbGVtZW50KVwiXHJcbiAgICAgICAgICAgICAgPlxyXG4gICAgICAgICAgICAgICAgPG1hdC1pY29uICpuZ0lmPVwib3B0aW9uLmljb25cIj5cclxuICAgICAgICAgICAgICAgICAge3sgb3B0aW9uLmljb24gfX1cclxuICAgICAgICAgICAgICAgIDwvbWF0LWljb24+XHJcbiAgICAgICAgICAgICAgICA8c3Bhbj5cclxuICAgICAgICAgICAgICAgICAge3sgb3B0aW9uLmxhYmVsIH19XHJcbiAgICAgICAgICAgICAgICA8L3NwYW4+XHJcbiAgICAgICAgICAgICAgPC9idXR0b24+XHJcbiAgICAgICAgICAgIDwvbWF0LW1lbnU+XHJcbiAgICAgICAgICA8L2Rpdj5cclxuICAgICAgICAgIDxkaXYgKm5nSWY9XCJoZWFkZXIgIT09ICdpY29uJyAmJiBoZWFkZXIgIT09ICdpbWcnXCI+XHJcbiAgICAgICAgICAgIHt7IGVsZW1lbnRbaGVhZGVyXSB9fVxyXG4gICAgICAgICAgPC9kaXY+XHJcbiAgICAgICAgPC90ZD5cclxuICAgICAgICA8IS0tIDx0ZCBtYXQtY2VsbCAqbWF0Q2VsbERlZj1cImxldCBlbGVtZW50XCI+e3sgZWxlbWVudFtoZWFkZXJdIH19PC90ZD4gLS0+XHJcbiAgICAgIDwvbmctY29udGFpbmVyPlxyXG4gICAgPC9kaXY+XHJcblxyXG4gICAgPHRyIG1hdC1oZWFkZXItcm93ICptYXRIZWFkZXJSb3dEZWY9XCJzbWFydFRhYmxlLnRhYmxlSGVhZGVyc1wiPjwvdHI+XHJcbiAgICA8dHJcclxuICAgICAgbWF0LXJvd1xyXG4gICAgICAqbWF0Um93RGVmPVwibGV0IHJvdzsgY29sdW1uczogc21hcnRUYWJsZS50YWJsZUhlYWRlcnNcIlxyXG4gICAgICAoY2xpY2spPVwic21hcnRUYWJsZS5zZWxlY3Rpb24hLnRvZ2dsZShyb3cpXCJcclxuICAgID48L3RyPlxyXG4gIDwvZGl2PlxyXG48L3RhYmxlPlxyXG4iXX0=
