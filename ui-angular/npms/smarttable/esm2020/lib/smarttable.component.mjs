import { Component, Input } from '@angular/core';
import { SmartTableType } from './smarttable.model';
import * as i0 from "@angular/core";
import * as i1 from "@angular/material/table";
import * as i2 from "@angular/material/icon";
import * as i3 from "@angular/material/button";
import * as i4 from "@angular/material/menu";
import * as i5 from "@angular/material/checkbox";
import * as i6 from "@angular/common";
export class SmarttableComponent {
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
SmarttableComponent.ɵcmp = i0.ɵɵngDeclareComponent({ minVersion: "12.0.0", version: "13.2.7", type: SmarttableComponent, selector: "smarttable", inputs: { smartTable: "smartTable" }, ngImport: i0, template: "<table mat-table [dataSource]=\"smartTable.tableRows\" class=\"full-width\">\n  <!--- Note that these columns can be defined in any order.\n            The actual rendered columns are set as a property on the row definition\" -->\n\n  <!-- Column Descriptor -->\n  <div *ngIf=\"smartTable.tableType === tableType.INHERITED\">\n    <ng-container\n      *ngFor=\"let header of smartTable.tableHeaders; let i = index\"\n      matColumnDef=\"{{ header }}\"\n    >\n      <th mat-header-cell *matHeaderCellDef>\n        <div\n          *ngIf=\"\n            header === 'icon' ||\n            header === 'img' ||\n            header === 'options' ||\n            header === 'button'\n          \"\n        ></div>\n        <div\n          *ngIf=\"\n            header !== 'icon' &&\n            header !== 'img' &&\n            header !== 'options' &&\n            header !== 'button'\n          \"\n        >\n          {{ smartTable.customTableHeaders[i] }}\n        </div>\n      </th>\n      <td mat-cell *matCellDef=\"let element\">\n        <mat-icon *ngIf=\"header === 'icon'\"> {{ element[header] }} </mat-icon>\n        <img\n          *ngIf=\"header === 'img'\"\n          [src]=\"element[header]\"\n          alt=\"\"\n          class=\"smarttableImg\"\n        />\n        <div *ngIf=\"header === 'options'\">\n          <button\n            mat-icon-button\n            [matMenuTriggerFor]=\"menu\"\n            aria-label=\"options\"\n          >\n            <mat-icon>more_vert</mat-icon>\n          </button>\n          <mat-menu #menu=\"matMenu\">\n            <button\n              *ngFor=\"let option of smartTable.options\"\n              mat-menu-item\n              (click)=\"option.callback(element)\"\n            >\n              <mat-icon *ngIf=\"option.icon\">\n                {{ option.icon }}\n              </mat-icon>\n              <span>\n                {{ option.label }}\n              </span>\n            </button>\n          </mat-menu>\n        </div>\n        <div\n          *ngIf=\"\n            header !== 'icon' &&\n            header !== 'img' &&\n            header !== 'option' &&\n            header !== 'button'\n          \"\n        >\n          {{ element[header] }}\n        </div>\n        <div *ngIf=\"header === 'button'\">\n          <button (click)=\"element[header].onClick()\" mat-raised-button>\n            {{ element[header].label }}\n            <mat-icon>{{ element[header].icon }}</mat-icon>\n          </button>\n        </div>\n      </td>\n    </ng-container>\n\n    <tr\n      mat-header-row\n      *matHeaderRowDef=\"smartTable.tableHeaders; sticky: true\"\n    ></tr>\n    <tr mat-row *matRowDef=\"let row; columns: smartTable.tableHeaders\"></tr>\n  </div>\n  <div *ngIf=\"smartTable.tableType !== tableType.INHERITED\">\n    <!-- Checkbox Column -->\n    <ng-container matColumnDef=\"select\">\n      <th mat-header-cell *matHeaderCellDef>\n        <mat-checkbox\n          (change)=\"$event ? toggleAllRows() : null\"\n          [checked]=\"smartTable.selection!.hasValue() && isAllSelected()\"\n          [indeterminate]=\"smartTable.selection!.hasValue() && !isAllSelected()\"\n          [aria-label]=\"checkboxLabel()\"\n        >\n        </mat-checkbox>\n      </th>\n      <td mat-cell *matCellDef=\"let row\">\n        <mat-checkbox\n          (click)=\"$event.stopPropagation()\"\n          (change)=\"$event ? smartTable.selection!.toggle(row) : null\"\n          [checked]=\"smartTable.selection!.isSelected(row)\"\n          [aria-label]=\"checkboxLabel(row)\"\n        >\n        </mat-checkbox>\n      </td>\n    </ng-container>\n\n    <!-- Column Descriptor -->\n    <div *ngFor=\"let header of smartTable.tableHeaders; let i = index\">\n      <ng-container *ngIf=\"header !== 'select'\" matColumnDef=\"{{ header }}\">\n        <th mat-header-cell *matHeaderCellDef>\n          <div\n            *ngIf=\"\n              header === 'icon' || header === 'img' || header === 'options'\n            \"\n          ></div>\n          <div\n            *ngIf=\"\n              header !== 'icon' && header !== 'img' && header !== 'options'\n            \"\n          >\n            {{ smartTable.customTableHeaders[i] }}\n          </div>\n        </th>\n        <td mat-cell *matCellDef=\"let element\">\n          <mat-icon *ngIf=\"header === 'icon'\"> {{ element[header] }} </mat-icon>\n          <img\n            *ngIf=\"header === 'img'\"\n            [src]=\"element[header]\"\n            alt=\"\"\n            class=\"smarttableImg\"\n          />\n          <div *ngIf=\"header === 'options'\">\n            <button\n              mat-icon-button\n              [matMenuTriggerFor]=\"menu\"\n              aria-label=\"options\"\n            >\n              <mat-icon>more_vert</mat-icon>\n            </button>\n            <mat-menu #menu=\"matMenu\">\n              <button\n                *ngFor=\"let option of smartTable.options\"\n                mat-menu-item\n                (click)=\"option.callback(element)\"\n              >\n                <mat-icon *ngIf=\"option.icon\">\n                  {{ option.icon }}\n                </mat-icon>\n                <span>\n                  {{ option.label }}\n                </span>\n              </button>\n            </mat-menu>\n          </div>\n          <div *ngIf=\"header !== 'icon' && header !== 'img'\">\n            {{ element[header] }}\n          </div>\n        </td>\n        <!-- <td mat-cell *matCellDef=\"let element\">{{ element[header] }}</td> -->\n      </ng-container>\n    </div>\n\n    <tr mat-header-row *matHeaderRowDef=\"smartTable.tableHeaders\"></tr>\n    <tr\n      mat-row\n      *matRowDef=\"let row; columns: smartTable.tableHeaders\"\n      (click)=\"smartTable.selection!.toggle(row)\"\n    ></tr>\n  </div>\n</table>\n", styles: [".full-width{width:100%}.smarttableImg{width:25px}\n"], components: [{ type: i1.MatTable, selector: "mat-table, table[mat-table]", exportAs: ["matTable"] }, { type: i2.MatIcon, selector: "mat-icon", inputs: ["color", "inline", "svgIcon", "fontSet", "fontIcon"], exportAs: ["matIcon"] }, { type: i3.MatButton, selector: "button[mat-button], button[mat-raised-button], button[mat-icon-button],             button[mat-fab], button[mat-mini-fab], button[mat-stroked-button],             button[mat-flat-button]", inputs: ["disabled", "disableRipple", "color"], exportAs: ["matButton"] }, { type: i4.MatMenu, selector: "mat-menu", exportAs: ["matMenu"] }, { type: i4.MatMenuItem, selector: "[mat-menu-item]", inputs: ["disabled", "disableRipple", "role"], exportAs: ["matMenuItem"] }, { type: i1.MatHeaderRow, selector: "mat-header-row, tr[mat-header-row]", exportAs: ["matHeaderRow"] }, { type: i1.MatRow, selector: "mat-row, tr[mat-row]", exportAs: ["matRow"] }, { type: i5.MatCheckbox, selector: "mat-checkbox", inputs: ["disableRipple", "color", "tabIndex", "aria-label", "aria-labelledby", "aria-describedby", "id", "required", "labelPosition", "name", "value", "checked", "disabled", "indeterminate"], outputs: ["change", "indeterminateChange"], exportAs: ["matCheckbox"] }], directives: [{ type: i6.NgIf, selector: "[ngIf]", inputs: ["ngIf", "ngIfThen", "ngIfElse"] }, { type: i6.NgForOf, selector: "[ngFor][ngForOf]", inputs: ["ngForOf", "ngForTrackBy", "ngForTemplate"] }, { type: i1.MatColumnDef, selector: "[matColumnDef]", inputs: ["sticky", "matColumnDef"] }, { type: i1.MatHeaderCellDef, selector: "[matHeaderCellDef]" }, { type: i1.MatHeaderCell, selector: "mat-header-cell, th[mat-header-cell]" }, { type: i1.MatCellDef, selector: "[matCellDef]" }, { type: i1.MatCell, selector: "mat-cell, td[mat-cell]" }, { type: i4.MatMenuTrigger, selector: "[mat-menu-trigger-for], [matMenuTriggerFor]", exportAs: ["matMenuTrigger"] }, { type: i1.MatHeaderRowDef, selector: "[matHeaderRowDef]", inputs: ["matHeaderRowDef", "matHeaderRowDefSticky"] }, { type: i1.MatRowDef, selector: "[matRowDef]", inputs: ["matRowDefColumns", "matRowDefWhen"] }] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmarttableComponent, decorators: [{
            type: Component,
            args: [{ selector: 'smarttable', template: "<table mat-table [dataSource]=\"smartTable.tableRows\" class=\"full-width\">\n  <!--- Note that these columns can be defined in any order.\n            The actual rendered columns are set as a property on the row definition\" -->\n\n  <!-- Column Descriptor -->\n  <div *ngIf=\"smartTable.tableType === tableType.INHERITED\">\n    <ng-container\n      *ngFor=\"let header of smartTable.tableHeaders; let i = index\"\n      matColumnDef=\"{{ header }}\"\n    >\n      <th mat-header-cell *matHeaderCellDef>\n        <div\n          *ngIf=\"\n            header === 'icon' ||\n            header === 'img' ||\n            header === 'options' ||\n            header === 'button'\n          \"\n        ></div>\n        <div\n          *ngIf=\"\n            header !== 'icon' &&\n            header !== 'img' &&\n            header !== 'options' &&\n            header !== 'button'\n          \"\n        >\n          {{ smartTable.customTableHeaders[i] }}\n        </div>\n      </th>\n      <td mat-cell *matCellDef=\"let element\">\n        <mat-icon *ngIf=\"header === 'icon'\"> {{ element[header] }} </mat-icon>\n        <img\n          *ngIf=\"header === 'img'\"\n          [src]=\"element[header]\"\n          alt=\"\"\n          class=\"smarttableImg\"\n        />\n        <div *ngIf=\"header === 'options'\">\n          <button\n            mat-icon-button\n            [matMenuTriggerFor]=\"menu\"\n            aria-label=\"options\"\n          >\n            <mat-icon>more_vert</mat-icon>\n          </button>\n          <mat-menu #menu=\"matMenu\">\n            <button\n              *ngFor=\"let option of smartTable.options\"\n              mat-menu-item\n              (click)=\"option.callback(element)\"\n            >\n              <mat-icon *ngIf=\"option.icon\">\n                {{ option.icon }}\n              </mat-icon>\n              <span>\n                {{ option.label }}\n              </span>\n            </button>\n          </mat-menu>\n        </div>\n        <div\n          *ngIf=\"\n            header !== 'icon' &&\n            header !== 'img' &&\n            header !== 'option' &&\n            header !== 'button'\n          \"\n        >\n          {{ element[header] }}\n        </div>\n        <div *ngIf=\"header === 'button'\">\n          <button (click)=\"element[header].onClick()\" mat-raised-button>\n            {{ element[header].label }}\n            <mat-icon>{{ element[header].icon }}</mat-icon>\n          </button>\n        </div>\n      </td>\n    </ng-container>\n\n    <tr\n      mat-header-row\n      *matHeaderRowDef=\"smartTable.tableHeaders; sticky: true\"\n    ></tr>\n    <tr mat-row *matRowDef=\"let row; columns: smartTable.tableHeaders\"></tr>\n  </div>\n  <div *ngIf=\"smartTable.tableType !== tableType.INHERITED\">\n    <!-- Checkbox Column -->\n    <ng-container matColumnDef=\"select\">\n      <th mat-header-cell *matHeaderCellDef>\n        <mat-checkbox\n          (change)=\"$event ? toggleAllRows() : null\"\n          [checked]=\"smartTable.selection!.hasValue() && isAllSelected()\"\n          [indeterminate]=\"smartTable.selection!.hasValue() && !isAllSelected()\"\n          [aria-label]=\"checkboxLabel()\"\n        >\n        </mat-checkbox>\n      </th>\n      <td mat-cell *matCellDef=\"let row\">\n        <mat-checkbox\n          (click)=\"$event.stopPropagation()\"\n          (change)=\"$event ? smartTable.selection!.toggle(row) : null\"\n          [checked]=\"smartTable.selection!.isSelected(row)\"\n          [aria-label]=\"checkboxLabel(row)\"\n        >\n        </mat-checkbox>\n      </td>\n    </ng-container>\n\n    <!-- Column Descriptor -->\n    <div *ngFor=\"let header of smartTable.tableHeaders; let i = index\">\n      <ng-container *ngIf=\"header !== 'select'\" matColumnDef=\"{{ header }}\">\n        <th mat-header-cell *matHeaderCellDef>\n          <div\n            *ngIf=\"\n              header === 'icon' || header === 'img' || header === 'options'\n            \"\n          ></div>\n          <div\n            *ngIf=\"\n              header !== 'icon' && header !== 'img' && header !== 'options'\n            \"\n          >\n            {{ smartTable.customTableHeaders[i] }}\n          </div>\n        </th>\n        <td mat-cell *matCellDef=\"let element\">\n          <mat-icon *ngIf=\"header === 'icon'\"> {{ element[header] }} </mat-icon>\n          <img\n            *ngIf=\"header === 'img'\"\n            [src]=\"element[header]\"\n            alt=\"\"\n            class=\"smarttableImg\"\n          />\n          <div *ngIf=\"header === 'options'\">\n            <button\n              mat-icon-button\n              [matMenuTriggerFor]=\"menu\"\n              aria-label=\"options\"\n            >\n              <mat-icon>more_vert</mat-icon>\n            </button>\n            <mat-menu #menu=\"matMenu\">\n              <button\n                *ngFor=\"let option of smartTable.options\"\n                mat-menu-item\n                (click)=\"option.callback(element)\"\n              >\n                <mat-icon *ngIf=\"option.icon\">\n                  {{ option.icon }}\n                </mat-icon>\n                <span>\n                  {{ option.label }}\n                </span>\n              </button>\n            </mat-menu>\n          </div>\n          <div *ngIf=\"header !== 'icon' && header !== 'img'\">\n            {{ element[header] }}\n          </div>\n        </td>\n        <!-- <td mat-cell *matCellDef=\"let element\">{{ element[header] }}</td> -->\n      </ng-container>\n    </div>\n\n    <tr mat-header-row *matHeaderRowDef=\"smartTable.tableHeaders\"></tr>\n    <tr\n      mat-row\n      *matRowDef=\"let row; columns: smartTable.tableHeaders\"\n      (click)=\"smartTable.selection!.toggle(row)\"\n    ></tr>\n  </div>\n</table>\n", styles: [".full-width{width:100%}.smarttableImg{width:25px}\n"] }]
        }], ctorParameters: function () { return []; }, propDecorators: { smartTable: [{
                type: Input
            }] } });
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnR0YWJsZS5jb21wb25lbnQuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi8uLi9wcm9qZWN0cy9zbWFydHRhYmxlL3NyYy9saWIvc21hcnR0YWJsZS5jb21wb25lbnQudHMiLCIuLi8uLi8uLi8uLi9wcm9qZWN0cy9zbWFydHRhYmxlL3NyYy9saWIvc21hcnR0YWJsZS5jb21wb25lbnQuaHRtbCJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQSxPQUFPLEVBQUUsU0FBUyxFQUFFLEtBQUssRUFBVSxNQUFNLGVBQWUsQ0FBQztBQUN6RCxPQUFPLEVBQWMsY0FBYyxFQUFFLE1BQU0sb0JBQW9CLENBQUM7Ozs7Ozs7O0FBT2hFLE1BQU0sT0FBTyxtQkFBbUI7SUFJL0I7UUFGQSxjQUFTLEdBQUcsY0FBYyxDQUFDO0lBRVosQ0FBQztJQUVoQixRQUFRLEtBQVUsQ0FBQztJQUVuQixhQUFhO1FBQ1osTUFBTSxXQUFXLEdBQUcsSUFBSSxDQUFDLFVBQVUsQ0FBQyxTQUFVLENBQUMsUUFBUSxDQUFDLE1BQU0sQ0FBQztRQUMvRCxNQUFNLE9BQU8sR0FBRyxJQUFJLENBQUMsVUFBVSxDQUFDLFNBQVMsQ0FBQyxNQUFNLENBQUM7UUFDakQsT0FBTyxXQUFXLEtBQUssT0FBTyxDQUFDO0lBQ2hDLENBQUM7SUFFRCxnRkFBZ0Y7SUFDaEYsYUFBYTtRQUNaLElBQUksSUFBSSxDQUFDLGFBQWEsRUFBRSxFQUFFO1lBQ3pCLElBQUksQ0FBQyxVQUFVLENBQUMsU0FBVSxDQUFDLEtBQUssRUFBRSxDQUFDO1lBQ25DLE9BQU87U0FDUDtRQUVELElBQUksQ0FBQyxVQUFVLENBQUMsU0FBVSxDQUFDLE1BQU0sQ0FBQyxHQUFHLElBQUksQ0FBQyxVQUFVLENBQUMsU0FBUyxDQUFDLENBQUM7SUFDakUsQ0FBQztJQUVELG1EQUFtRDtJQUNuRCxhQUFhLENBQUMsR0FBUztRQUN0QixJQUFJLENBQUMsR0FBRyxFQUFFO1lBQ1QsT0FBTyxHQUFHLElBQUksQ0FBQyxhQUFhLEVBQUUsQ0FBQyxDQUFDLENBQUMsVUFBVSxDQUFDLENBQUMsQ0FBQyxRQUFRLE1BQU0sQ0FBQztTQUM3RDtRQUNELE9BQU8sR0FBRyxJQUFJLENBQUMsVUFBVSxDQUFDLFNBQVUsQ0FBQyxVQUFVLENBQUMsR0FBRyxDQUFDLENBQUMsQ0FBQyxDQUFDLFVBQVUsQ0FBQyxDQUFDLENBQUMsUUFBUSxRQUMzRSxHQUFHLENBQUMsUUFBUSxHQUFHLENBQ2hCLEVBQUUsQ0FBQztJQUNKLENBQUM7O2dIQWhDVyxtQkFBbUI7b0dBQW5CLG1CQUFtQix3RkNSaEMsZ3JMQTZLQTsyRkRyS2EsbUJBQW1CO2tCQUwvQixTQUFTOytCQUNDLFlBQVk7MEVBS2IsVUFBVTtzQkFBbEIsS0FBSyIsInNvdXJjZXNDb250ZW50IjpbImltcG9ydCB7IENvbXBvbmVudCwgSW5wdXQsIE9uSW5pdCB9IGZyb20gJ0Bhbmd1bGFyL2NvcmUnO1xuaW1wb3J0IHsgU21hcnRUYWJsZSwgU21hcnRUYWJsZVR5cGUgfSBmcm9tICcuL3NtYXJ0dGFibGUubW9kZWwnO1xuXG5AQ29tcG9uZW50KHtcblx0c2VsZWN0b3I6ICdzbWFydHRhYmxlJyxcblx0dGVtcGxhdGVVcmw6ICcuL3NtYXJ0dGFibGUuY29tcG9uZW50Lmh0bWwnLFxuXHRzdHlsZVVybHM6IFsnLi9zbWFydHRhYmxlLmNvbXBvbmVudC5jc3MnXVxufSlcbmV4cG9ydCBjbGFzcyBTbWFydHRhYmxlQ29tcG9uZW50IGltcGxlbWVudHMgT25Jbml0IHtcblx0QElucHV0KCkgc21hcnRUYWJsZSE6IFNtYXJ0VGFibGU8YW55Pjtcblx0dGFibGVUeXBlID0gU21hcnRUYWJsZVR5cGU7XG5cblx0Y29uc3RydWN0b3IoKSB7fVxuXG5cdG5nT25Jbml0KCk6IHZvaWQge31cblxuXHRpc0FsbFNlbGVjdGVkKCkge1xuXHRcdGNvbnN0IG51bVNlbGVjdGVkID0gdGhpcy5zbWFydFRhYmxlLnNlbGVjdGlvbiEuc2VsZWN0ZWQubGVuZ3RoO1xuXHRcdGNvbnN0IG51bVJvd3MgPSB0aGlzLnNtYXJ0VGFibGUudGFibGVSb3dzLmxlbmd0aDtcblx0XHRyZXR1cm4gbnVtU2VsZWN0ZWQgPT09IG51bVJvd3M7XG5cdH1cblxuXHQvKiogU2VsZWN0cyBhbGwgcm93cyBpZiB0aGV5IGFyZSBub3QgYWxsIHNlbGVjdGVkOyBvdGhlcndpc2UgY2xlYXIgc2VsZWN0aW9uLiAqL1xuXHR0b2dnbGVBbGxSb3dzKCkge1xuXHRcdGlmICh0aGlzLmlzQWxsU2VsZWN0ZWQoKSkge1xuXHRcdFx0dGhpcy5zbWFydFRhYmxlLnNlbGVjdGlvbiEuY2xlYXIoKTtcblx0XHRcdHJldHVybjtcblx0XHR9XG5cblx0XHR0aGlzLnNtYXJ0VGFibGUuc2VsZWN0aW9uIS5zZWxlY3QoLi4udGhpcy5zbWFydFRhYmxlLnRhYmxlUm93cyk7XG5cdH1cblxuXHQvKiogVGhlIGxhYmVsIGZvciB0aGUgY2hlY2tib3ggb24gdGhlIHBhc3NlZCByb3cgKi9cblx0Y2hlY2tib3hMYWJlbChyb3c/OiBhbnkpOiBzdHJpbmcge1xuXHRcdGlmICghcm93KSB7XG5cdFx0XHRyZXR1cm4gYCR7dGhpcy5pc0FsbFNlbGVjdGVkKCkgPyAnZGVzZWxlY3QnIDogJ3NlbGVjdCd9IGFsbGA7XG5cdFx0fVxuXHRcdHJldHVybiBgJHt0aGlzLnNtYXJ0VGFibGUuc2VsZWN0aW9uIS5pc1NlbGVjdGVkKHJvdykgPyAnZGVzZWxlY3QnIDogJ3NlbGVjdCd9IHJvdyAke1xuXHRcdFx0cm93LnBvc2l0aW9uICsgMVxuXHRcdH1gO1xuXHR9XG59XG4iLCI8dGFibGUgbWF0LXRhYmxlIFtkYXRhU291cmNlXT1cInNtYXJ0VGFibGUudGFibGVSb3dzXCIgY2xhc3M9XCJmdWxsLXdpZHRoXCI+XG4gIDwhLS0tIE5vdGUgdGhhdCB0aGVzZSBjb2x1bW5zIGNhbiBiZSBkZWZpbmVkIGluIGFueSBvcmRlci5cbiAgICAgICAgICAgIFRoZSBhY3R1YWwgcmVuZGVyZWQgY29sdW1ucyBhcmUgc2V0IGFzIGEgcHJvcGVydHkgb24gdGhlIHJvdyBkZWZpbml0aW9uXCIgLS0+XG5cbiAgPCEtLSBDb2x1bW4gRGVzY3JpcHRvciAtLT5cbiAgPGRpdiAqbmdJZj1cInNtYXJ0VGFibGUudGFibGVUeXBlID09PSB0YWJsZVR5cGUuSU5IRVJJVEVEXCI+XG4gICAgPG5nLWNvbnRhaW5lclxuICAgICAgKm5nRm9yPVwibGV0IGhlYWRlciBvZiBzbWFydFRhYmxlLnRhYmxlSGVhZGVyczsgbGV0IGkgPSBpbmRleFwiXG4gICAgICBtYXRDb2x1bW5EZWY9XCJ7eyBoZWFkZXIgfX1cIlxuICAgID5cbiAgICAgIDx0aCBtYXQtaGVhZGVyLWNlbGwgKm1hdEhlYWRlckNlbGxEZWY+XG4gICAgICAgIDxkaXZcbiAgICAgICAgICAqbmdJZj1cIlxuICAgICAgICAgICAgaGVhZGVyID09PSAnaWNvbicgfHxcbiAgICAgICAgICAgIGhlYWRlciA9PT0gJ2ltZycgfHxcbiAgICAgICAgICAgIGhlYWRlciA9PT0gJ29wdGlvbnMnIHx8XG4gICAgICAgICAgICBoZWFkZXIgPT09ICdidXR0b24nXG4gICAgICAgICAgXCJcbiAgICAgICAgPjwvZGl2PlxuICAgICAgICA8ZGl2XG4gICAgICAgICAgKm5nSWY9XCJcbiAgICAgICAgICAgIGhlYWRlciAhPT0gJ2ljb24nICYmXG4gICAgICAgICAgICBoZWFkZXIgIT09ICdpbWcnICYmXG4gICAgICAgICAgICBoZWFkZXIgIT09ICdvcHRpb25zJyAmJlxuICAgICAgICAgICAgaGVhZGVyICE9PSAnYnV0dG9uJ1xuICAgICAgICAgIFwiXG4gICAgICAgID5cbiAgICAgICAgICB7eyBzbWFydFRhYmxlLmN1c3RvbVRhYmxlSGVhZGVyc1tpXSB9fVxuICAgICAgICA8L2Rpdj5cbiAgICAgIDwvdGg+XG4gICAgICA8dGQgbWF0LWNlbGwgKm1hdENlbGxEZWY9XCJsZXQgZWxlbWVudFwiPlxuICAgICAgICA8bWF0LWljb24gKm5nSWY9XCJoZWFkZXIgPT09ICdpY29uJ1wiPiB7eyBlbGVtZW50W2hlYWRlcl0gfX0gPC9tYXQtaWNvbj5cbiAgICAgICAgPGltZ1xuICAgICAgICAgICpuZ0lmPVwiaGVhZGVyID09PSAnaW1nJ1wiXG4gICAgICAgICAgW3NyY109XCJlbGVtZW50W2hlYWRlcl1cIlxuICAgICAgICAgIGFsdD1cIlwiXG4gICAgICAgICAgY2xhc3M9XCJzbWFydHRhYmxlSW1nXCJcbiAgICAgICAgLz5cbiAgICAgICAgPGRpdiAqbmdJZj1cImhlYWRlciA9PT0gJ29wdGlvbnMnXCI+XG4gICAgICAgICAgPGJ1dHRvblxuICAgICAgICAgICAgbWF0LWljb24tYnV0dG9uXG4gICAgICAgICAgICBbbWF0TWVudVRyaWdnZXJGb3JdPVwibWVudVwiXG4gICAgICAgICAgICBhcmlhLWxhYmVsPVwib3B0aW9uc1wiXG4gICAgICAgICAgPlxuICAgICAgICAgICAgPG1hdC1pY29uPm1vcmVfdmVydDwvbWF0LWljb24+XG4gICAgICAgICAgPC9idXR0b24+XG4gICAgICAgICAgPG1hdC1tZW51ICNtZW51PVwibWF0TWVudVwiPlxuICAgICAgICAgICAgPGJ1dHRvblxuICAgICAgICAgICAgICAqbmdGb3I9XCJsZXQgb3B0aW9uIG9mIHNtYXJ0VGFibGUub3B0aW9uc1wiXG4gICAgICAgICAgICAgIG1hdC1tZW51LWl0ZW1cbiAgICAgICAgICAgICAgKGNsaWNrKT1cIm9wdGlvbi5jYWxsYmFjayhlbGVtZW50KVwiXG4gICAgICAgICAgICA+XG4gICAgICAgICAgICAgIDxtYXQtaWNvbiAqbmdJZj1cIm9wdGlvbi5pY29uXCI+XG4gICAgICAgICAgICAgICAge3sgb3B0aW9uLmljb24gfX1cbiAgICAgICAgICAgICAgPC9tYXQtaWNvbj5cbiAgICAgICAgICAgICAgPHNwYW4+XG4gICAgICAgICAgICAgICAge3sgb3B0aW9uLmxhYmVsIH19XG4gICAgICAgICAgICAgIDwvc3Bhbj5cbiAgICAgICAgICAgIDwvYnV0dG9uPlxuICAgICAgICAgIDwvbWF0LW1lbnU+XG4gICAgICAgIDwvZGl2PlxuICAgICAgICA8ZGl2XG4gICAgICAgICAgKm5nSWY9XCJcbiAgICAgICAgICAgIGhlYWRlciAhPT0gJ2ljb24nICYmXG4gICAgICAgICAgICBoZWFkZXIgIT09ICdpbWcnICYmXG4gICAgICAgICAgICBoZWFkZXIgIT09ICdvcHRpb24nICYmXG4gICAgICAgICAgICBoZWFkZXIgIT09ICdidXR0b24nXG4gICAgICAgICAgXCJcbiAgICAgICAgPlxuICAgICAgICAgIHt7IGVsZW1lbnRbaGVhZGVyXSB9fVxuICAgICAgICA8L2Rpdj5cbiAgICAgICAgPGRpdiAqbmdJZj1cImhlYWRlciA9PT0gJ2J1dHRvbidcIj5cbiAgICAgICAgICA8YnV0dG9uIChjbGljayk9XCJlbGVtZW50W2hlYWRlcl0ub25DbGljaygpXCIgbWF0LXJhaXNlZC1idXR0b24+XG4gICAgICAgICAgICB7eyBlbGVtZW50W2hlYWRlcl0ubGFiZWwgfX1cbiAgICAgICAgICAgIDxtYXQtaWNvbj57eyBlbGVtZW50W2hlYWRlcl0uaWNvbiB9fTwvbWF0LWljb24+XG4gICAgICAgICAgPC9idXR0b24+XG4gICAgICAgIDwvZGl2PlxuICAgICAgPC90ZD5cbiAgICA8L25nLWNvbnRhaW5lcj5cblxuICAgIDx0clxuICAgICAgbWF0LWhlYWRlci1yb3dcbiAgICAgICptYXRIZWFkZXJSb3dEZWY9XCJzbWFydFRhYmxlLnRhYmxlSGVhZGVyczsgc3RpY2t5OiB0cnVlXCJcbiAgICA+PC90cj5cbiAgICA8dHIgbWF0LXJvdyAqbWF0Um93RGVmPVwibGV0IHJvdzsgY29sdW1uczogc21hcnRUYWJsZS50YWJsZUhlYWRlcnNcIj48L3RyPlxuICA8L2Rpdj5cbiAgPGRpdiAqbmdJZj1cInNtYXJ0VGFibGUudGFibGVUeXBlICE9PSB0YWJsZVR5cGUuSU5IRVJJVEVEXCI+XG4gICAgPCEtLSBDaGVja2JveCBDb2x1bW4gLS0+XG4gICAgPG5nLWNvbnRhaW5lciBtYXRDb2x1bW5EZWY9XCJzZWxlY3RcIj5cbiAgICAgIDx0aCBtYXQtaGVhZGVyLWNlbGwgKm1hdEhlYWRlckNlbGxEZWY+XG4gICAgICAgIDxtYXQtY2hlY2tib3hcbiAgICAgICAgICAoY2hhbmdlKT1cIiRldmVudCA/IHRvZ2dsZUFsbFJvd3MoKSA6IG51bGxcIlxuICAgICAgICAgIFtjaGVja2VkXT1cInNtYXJ0VGFibGUuc2VsZWN0aW9uIS5oYXNWYWx1ZSgpICYmIGlzQWxsU2VsZWN0ZWQoKVwiXG4gICAgICAgICAgW2luZGV0ZXJtaW5hdGVdPVwic21hcnRUYWJsZS5zZWxlY3Rpb24hLmhhc1ZhbHVlKCkgJiYgIWlzQWxsU2VsZWN0ZWQoKVwiXG4gICAgICAgICAgW2FyaWEtbGFiZWxdPVwiY2hlY2tib3hMYWJlbCgpXCJcbiAgICAgICAgPlxuICAgICAgICA8L21hdC1jaGVja2JveD5cbiAgICAgIDwvdGg+XG4gICAgICA8dGQgbWF0LWNlbGwgKm1hdENlbGxEZWY9XCJsZXQgcm93XCI+XG4gICAgICAgIDxtYXQtY2hlY2tib3hcbiAgICAgICAgICAoY2xpY2spPVwiJGV2ZW50LnN0b3BQcm9wYWdhdGlvbigpXCJcbiAgICAgICAgICAoY2hhbmdlKT1cIiRldmVudCA/IHNtYXJ0VGFibGUuc2VsZWN0aW9uIS50b2dnbGUocm93KSA6IG51bGxcIlxuICAgICAgICAgIFtjaGVja2VkXT1cInNtYXJ0VGFibGUuc2VsZWN0aW9uIS5pc1NlbGVjdGVkKHJvdylcIlxuICAgICAgICAgIFthcmlhLWxhYmVsXT1cImNoZWNrYm94TGFiZWwocm93KVwiXG4gICAgICAgID5cbiAgICAgICAgPC9tYXQtY2hlY2tib3g+XG4gICAgICA8L3RkPlxuICAgIDwvbmctY29udGFpbmVyPlxuXG4gICAgPCEtLSBDb2x1bW4gRGVzY3JpcHRvciAtLT5cbiAgICA8ZGl2ICpuZ0Zvcj1cImxldCBoZWFkZXIgb2Ygc21hcnRUYWJsZS50YWJsZUhlYWRlcnM7IGxldCBpID0gaW5kZXhcIj5cbiAgICAgIDxuZy1jb250YWluZXIgKm5nSWY9XCJoZWFkZXIgIT09ICdzZWxlY3QnXCIgbWF0Q29sdW1uRGVmPVwie3sgaGVhZGVyIH19XCI+XG4gICAgICAgIDx0aCBtYXQtaGVhZGVyLWNlbGwgKm1hdEhlYWRlckNlbGxEZWY+XG4gICAgICAgICAgPGRpdlxuICAgICAgICAgICAgKm5nSWY9XCJcbiAgICAgICAgICAgICAgaGVhZGVyID09PSAnaWNvbicgfHwgaGVhZGVyID09PSAnaW1nJyB8fCBoZWFkZXIgPT09ICdvcHRpb25zJ1xuICAgICAgICAgICAgXCJcbiAgICAgICAgICA+PC9kaXY+XG4gICAgICAgICAgPGRpdlxuICAgICAgICAgICAgKm5nSWY9XCJcbiAgICAgICAgICAgICAgaGVhZGVyICE9PSAnaWNvbicgJiYgaGVhZGVyICE9PSAnaW1nJyAmJiBoZWFkZXIgIT09ICdvcHRpb25zJ1xuICAgICAgICAgICAgXCJcbiAgICAgICAgICA+XG4gICAgICAgICAgICB7eyBzbWFydFRhYmxlLmN1c3RvbVRhYmxlSGVhZGVyc1tpXSB9fVxuICAgICAgICAgIDwvZGl2PlxuICAgICAgICA8L3RoPlxuICAgICAgICA8dGQgbWF0LWNlbGwgKm1hdENlbGxEZWY9XCJsZXQgZWxlbWVudFwiPlxuICAgICAgICAgIDxtYXQtaWNvbiAqbmdJZj1cImhlYWRlciA9PT0gJ2ljb24nXCI+IHt7IGVsZW1lbnRbaGVhZGVyXSB9fSA8L21hdC1pY29uPlxuICAgICAgICAgIDxpbWdcbiAgICAgICAgICAgICpuZ0lmPVwiaGVhZGVyID09PSAnaW1nJ1wiXG4gICAgICAgICAgICBbc3JjXT1cImVsZW1lbnRbaGVhZGVyXVwiXG4gICAgICAgICAgICBhbHQ9XCJcIlxuICAgICAgICAgICAgY2xhc3M9XCJzbWFydHRhYmxlSW1nXCJcbiAgICAgICAgICAvPlxuICAgICAgICAgIDxkaXYgKm5nSWY9XCJoZWFkZXIgPT09ICdvcHRpb25zJ1wiPlxuICAgICAgICAgICAgPGJ1dHRvblxuICAgICAgICAgICAgICBtYXQtaWNvbi1idXR0b25cbiAgICAgICAgICAgICAgW21hdE1lbnVUcmlnZ2VyRm9yXT1cIm1lbnVcIlxuICAgICAgICAgICAgICBhcmlhLWxhYmVsPVwib3B0aW9uc1wiXG4gICAgICAgICAgICA+XG4gICAgICAgICAgICAgIDxtYXQtaWNvbj5tb3JlX3ZlcnQ8L21hdC1pY29uPlxuICAgICAgICAgICAgPC9idXR0b24+XG4gICAgICAgICAgICA8bWF0LW1lbnUgI21lbnU9XCJtYXRNZW51XCI+XG4gICAgICAgICAgICAgIDxidXR0b25cbiAgICAgICAgICAgICAgICAqbmdGb3I9XCJsZXQgb3B0aW9uIG9mIHNtYXJ0VGFibGUub3B0aW9uc1wiXG4gICAgICAgICAgICAgICAgbWF0LW1lbnUtaXRlbVxuICAgICAgICAgICAgICAgIChjbGljayk9XCJvcHRpb24uY2FsbGJhY2soZWxlbWVudClcIlxuICAgICAgICAgICAgICA+XG4gICAgICAgICAgICAgICAgPG1hdC1pY29uICpuZ0lmPVwib3B0aW9uLmljb25cIj5cbiAgICAgICAgICAgICAgICAgIHt7IG9wdGlvbi5pY29uIH19XG4gICAgICAgICAgICAgICAgPC9tYXQtaWNvbj5cbiAgICAgICAgICAgICAgICA8c3Bhbj5cbiAgICAgICAgICAgICAgICAgIHt7IG9wdGlvbi5sYWJlbCB9fVxuICAgICAgICAgICAgICAgIDwvc3Bhbj5cbiAgICAgICAgICAgICAgPC9idXR0b24+XG4gICAgICAgICAgICA8L21hdC1tZW51PlxuICAgICAgICAgIDwvZGl2PlxuICAgICAgICAgIDxkaXYgKm5nSWY9XCJoZWFkZXIgIT09ICdpY29uJyAmJiBoZWFkZXIgIT09ICdpbWcnXCI+XG4gICAgICAgICAgICB7eyBlbGVtZW50W2hlYWRlcl0gfX1cbiAgICAgICAgICA8L2Rpdj5cbiAgICAgICAgPC90ZD5cbiAgICAgICAgPCEtLSA8dGQgbWF0LWNlbGwgKm1hdENlbGxEZWY9XCJsZXQgZWxlbWVudFwiPnt7IGVsZW1lbnRbaGVhZGVyXSB9fTwvdGQ+IC0tPlxuICAgICAgPC9uZy1jb250YWluZXI+XG4gICAgPC9kaXY+XG5cbiAgICA8dHIgbWF0LWhlYWRlci1yb3cgKm1hdEhlYWRlclJvd0RlZj1cInNtYXJ0VGFibGUudGFibGVIZWFkZXJzXCI+PC90cj5cbiAgICA8dHJcbiAgICAgIG1hdC1yb3dcbiAgICAgICptYXRSb3dEZWY9XCJsZXQgcm93OyBjb2x1bW5zOiBzbWFydFRhYmxlLnRhYmxlSGVhZGVyc1wiXG4gICAgICAoY2xpY2spPVwic21hcnRUYWJsZS5zZWxlY3Rpb24hLnRvZ2dsZShyb3cpXCJcbiAgICA+PC90cj5cbiAgPC9kaXY+XG48L3RhYmxlPlxuIl19