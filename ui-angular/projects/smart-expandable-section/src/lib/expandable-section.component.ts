import { Component, ComponentFactoryResolver, ComponentRef, Input, OnInit, ViewChild, ViewContainerRef, ViewEncapsulation } from '@angular/core';
import { SmartForm } from 'src/app/npms/smartform/smartform.model';
import { SmartTable } from 'src/app/npms/smarttable/smarttable.model';
import { ComponentFactoryService } from 'src/app/services/ComponentFactory';
import { ExpandableSection } from './expandable-section.model';

@Component({
	selector: 'smart-expandable-section',
	templateUrl: './expandable-section.component.html',
	styleUrls: ['./expandable-section.component.css'],
	encapsulation: ViewEncapsulation.None
})
export class ExpandableSectionComponent implements OnInit {
	@Input() data!: ExpandableSection;
	@Input() smartTable?: SmartTable<any>;
	@Input() smartForm?: SmartForm;


	@ViewChild('renderComponent', { read: ViewContainerRef })
	vcRef?: ViewContainerRef;

	constructor(
		private resolver: ComponentFactoryResolver,
		private cfService: ComponentFactoryService
	) { }

	ngOnInit(): void { }

	ngAfterViewInit() {
		this.cfService.createComponent(this.vcRef!, this.data.customComponent, new Map<string, any>([["smartTable", this.smartTable], ["smartForm", this.smartForm]]));
	}

	loadTableData(ref: ComponentRef<any>) {
		ref.instance.smartTable = this.smartTable;
	}

	loadFormData(ref: ComponentRef<any>) {
		ref.instance.smartForm = this.smartForm;
	}
}
