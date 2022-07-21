import { Component, Input, OnInit } from '@angular/core';
import { SmartTable, SmartTableType } from './smarttable.model';

@Component({
	selector: 'smarttable',
	templateUrl: './smarttable.component.html',
	styleUrls: ['./smarttable.component.css']
})
export class SmarttableComponent implements OnInit {
	@Input() smartTable!: SmartTable<any>;
	tableType = SmartTableType;

	constructor() {}

	ngOnInit(): void {}

	isAllSelected() {
		const numSelected = this.smartTable.selection!.selected.length;
		const numRows = this.smartTable.tableRows.length;
		return numSelected === numRows;
	}

	/** Selects all rows if they are not all selected; otherwise clear selection. */
	toggleAllRows() {
		if (this.isAllSelected()) {
			this.smartTable.selection!.clear();
			return;
		}

		this.smartTable.selection!.select(...this.smartTable.tableRows);
	}

	/** The label for the checkbox on the passed row */
	checkboxLabel(row?: any): string {
		if (!row) {
			return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
		}
		return `${this.smartTable.selection!.isSelected(row) ? 'deselect' : 'select'} row ${
			row.position + 1
		}`;
	}
}
