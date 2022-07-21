import { SelectionModel } from '@angular/cdk/collections';

export enum SmartTableType {
	INHERITED,
	CHECK_BOX
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

export class SmartTable<T> implements SmartTableInterface<T> {
	title?: string | undefined;
	tableHeaders: string[];
	customTableHeaders: string[];
	tableRows: T[];
	tableType: SmartTableType;
	hideCols?: number[] | undefined;
	selection?: SelectionModel<T>;
	isMultiple?: boolean;
	options?: SmartTableOption[];

	constructor(
		tableRows: T[],
		tableType: SmartTableType,
		customTableHeaders?: string[],
		title?: string,
		hideCols?: number[],
		isMultiple?: boolean,
		options?: SmartTableOption[]
	) {
		this.tableHeaders = Object.getOwnPropertyNames(tableRows[0]);
		if (customTableHeaders) {
			this.customTableHeaders = customTableHeaders;
		} else {
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
			this.selection = new SelectionModel<T>(this.isMultiple, []);
		}
	}
}
