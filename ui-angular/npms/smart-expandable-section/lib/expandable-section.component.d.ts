import { ComponentFactoryResolver, ComponentRef, OnInit, ViewContainerRef } from "@angular/core";
import { SmartForm } from "@smartbit4all/form";
import { SmartTable } from "@smartbit4all/table";
import { ExpandableSection } from "./expandable-section.model";
import * as i0 from "@angular/core";
export declare class ExpandableSectionComponent implements OnInit {
    private resolver;
    data: ExpandableSection;
    smartTable?: SmartTable<any>;
    smartForm?: SmartForm;
    vcRef?: ViewContainerRef;
    constructor(resolver: ComponentFactoryResolver);
    ngOnInit(): void;
    ngAfterViewInit(): void;
    loadTableData(ref: ComponentRef<any>): void;
    loadFormData(ref: ComponentRef<any>): void;
    static ɵfac: i0.ɵɵFactoryDeclaration<ExpandableSectionComponent, never>;
    static ɵcmp: i0.ɵɵComponentDeclaration<ExpandableSectionComponent, "smart-expandable-section", never, { "data": "data"; "smartTable": "smartTable"; "smartForm": "smartForm"; }, {}, never, never>;
}
