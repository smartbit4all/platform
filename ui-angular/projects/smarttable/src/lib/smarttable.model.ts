import { SelectionModel } from "@angular/cdk/collections";

export enum SmartTableType {
    INHERITED,
    CHECK_BOX,
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

export class SmartTable<T> implements SmartTableInterface<T> {
    title?: string | undefined;
    tableHeaders: string[];
    customSmartTableHeaders?: SmartTableHeader[];
    customTableHeaders!: string[];
    tableRows: T[];
    tableType: SmartTableType;
    selection?: SelectionModel<T>;
    isMultiple?: boolean;
    options?: SmartTableOption[];

    constructor(
        tableRows: T[],
        tableType: SmartTableType,
        customSmartTableHeaders?: SmartTableHeader[],
        title?: string,
        isMultiple?: boolean,
        options?: SmartTableOption[]
    ) {
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

    setTableSelectionType(tableType: SmartTableType): void {
        if (tableType !== SmartTableType.INHERITED) {
            this.tableHeaders = ["select"].concat(this.tableHeaders);
            this.customTableHeaders = ["select"].concat(this.customTableHeaders);
            this.selection = new SelectionModel<T>(this.isMultiple, []);
        }
    }

    pushOptions(): void {
        if (this.options && this.options.length) {
            this.tableHeaders.push("options");
        }
    }

    setTableHeaders(): void {
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
