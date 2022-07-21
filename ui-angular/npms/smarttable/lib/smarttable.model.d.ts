import { SelectionModel } from '@angular/cdk/collections';
export declare enum SmartTableType {
    INHERITED = 0,
    CHECK_BOX = 1
}
export interface SmartTableOption {
    label: string;
    icon?: string;
    callback: (id: string) => any;
}
export interface SmartTableInterface<T> {
    title?: string;
    tableHeaders: string[];
    customTableHeaders: string[];
    tableRows: T[];
    tableType: SmartTableType;
    hideCols?: number[];
    options?: SmartTableOption[];
}
export declare class SmartTable<T> implements SmartTableInterface<T> {
    title?: string | undefined;
    tableHeaders: string[];
    customTableHeaders: string[];
    tableRows: T[];
    tableType: SmartTableType;
    hideCols?: number[] | undefined;
    selection?: SelectionModel<T>;
    isMultiple?: boolean;
    options?: SmartTableOption[];
    constructor(tableRows: T[], tableType: SmartTableType, customTableHeaders?: string[], title?: string, hideCols?: number[], isMultiple?: boolean, options?: SmartTableOption[]);
}
