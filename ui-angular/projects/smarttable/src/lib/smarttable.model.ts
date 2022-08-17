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

export interface SmartTableInterface<T> {
    title?: string;
    tableHeaders: string[];
    customSmartTableHeaders?: SmartTableHeader[];
    // tableHeadersOrder?: string[];
    customTableHeaders: string[];
    tableRows: T[];
    tableType: SmartTableType;
    options?: SmartTableOption[];
    equalsIgnoreOrder: (a: string[], b: string[]) => boolean;
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

    equalsIgnoreOrder = (a: string[], b: string[]) => {
        if (a.length !== b.length) return false;
        const uniqueValues = new Set([...a, ...b]);
        for (const v of uniqueValues) {
            const aCount = a.filter((e) => e === v).length;
            const bCount = b.filter((e) => e === v).length;
            if (aCount !== bCount) return false;
        }
        return true;
    };

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
        this.customTableHeaders = [];

        if (
            this.customSmartTableHeaders &&
            this.equalsIgnoreOrder(
                this.tableHeaders,
                this.customSmartTableHeaders.map((tableHeader) => tableHeader.propertyName)
            )
        ) {
            this.tableHeaders = [];
            this.customSmartTableHeaders.forEach((tableHeader) => {
                if (!tableHeader.isHidden) {
                    this.tableHeaders.push(tableHeader.propertyName);
                    this.customTableHeaders.push(tableHeader.label);
                }
            });
        } else {
            this.customTableHeaders = this.tableHeaders;
        }

        this.tableRows = tableRows;
        this.tableType = tableType;
        this.title = title;
        this.isMultiple = isMultiple;
        this.options = options;

        if (this.options && this.options.length) {
            this.tableHeaders.push("options");
        }

        // if (this.hideCols !== undefined && this.hideCols.length > 0) {
        //     this.hideCols.forEach((hideCol, index) => {
        //         hideCol -= index;
        //         this.tableHeaders.splice(hideCol, 1);
        //     });
        // }

        if (tableType !== SmartTableType.INHERITED) {
            this.tableHeaders = ["select"].concat(this.tableHeaders);
            this.selection = new SelectionModel<T>(this.isMultiple, []);
        }
    }
}
