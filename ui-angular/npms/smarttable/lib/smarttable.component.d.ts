import { OnInit } from '@angular/core';
import { SmartTable, SmartTableType } from './smarttable.model';
import * as i0 from "@angular/core";
export declare class SmarttableComponent implements OnInit {
    smartTable: SmartTable<any>;
    tableType: typeof SmartTableType;
    constructor();
    ngOnInit(): void;
    isAllSelected(): boolean;
    /** Selects all rows if they are not all selected; otherwise clear selection. */
    toggleAllRows(): void;
    /** The label for the checkbox on the passed row */
    checkboxLabel(row?: any): string;
    static ɵfac: i0.ɵɵFactoryDeclaration<SmarttableComponent, never>;
    static ɵcmp: i0.ɵɵComponentDeclaration<SmarttableComponent, "smarttable", never, { "smartTable": "smartTable"; }, {}, never, never>;
}
