import { SelectionModel } from "@angular/cdk/collections";
export declare enum SmartTableType {
    INHERITED = 0,
    CHECK_BOX = 1
}
export interface SmartTableOption {
    label: string;
    icon?: string;
    callback: (id: string) => any;
}
export interface SmartTableHeader {
    propertyName: string;
    label: string;
    isHidden?: boolean;
}
export interface SmartTableButton {
    lable: string;
    icon?: string;
    onClick?: (args?: any[]) => void;
}
export interface SmartTableInterface<T> {
    title?: string;
    tableHeaders: string[];
    customSmartTableHeaders?: SmartTableHeader[];
    customTableHeaders: string[];
    tableRows: T[];
    tableType: SmartTableType;
    options?: SmartTableOption[];
}
export declare class SmartTable<T> implements SmartTableInterface<T> {
    title?: string | undefined;
    tableHeaders: string[];
    customSmartTableHeaders?: SmartTableHeader[];
    customTableHeaders: string[];
    tableRows: T[];
    tableType: SmartTableType;
    selection?: SelectionModel<T>;
    isMultiple?: boolean;
    options?: SmartTableOption[];
    constructor(tableRows: T[], tableType: SmartTableType, customSmartTableHeaders?: SmartTableHeader[], title?: string, isMultiple?: boolean, options?: SmartTableOption[]);
    setTableSelectionType(tableType: SmartTableType): void;
    pushOptions(): void;
    setTableHeaders(): void;
}
