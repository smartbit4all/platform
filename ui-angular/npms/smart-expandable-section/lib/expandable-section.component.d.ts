import { OnInit, ViewContainerRef } from "@angular/core";
import { ComponentFactoryService } from "@smartbit4all/component-factory-service";
import { ExpandableSection } from "./expandable-section.model";
import * as i0 from "@angular/core";
export declare class ExpandableSectionComponent implements OnInit {
    private cfService;
    data: ExpandableSection<any>;
    vcRef?: ViewContainerRef;
    constructor(cfService: ComponentFactoryService);
    ngOnInit(): void;
    ngAfterViewInit(): void;
    static ɵfac: i0.ɵɵFactoryDeclaration<ExpandableSectionComponent, never>;
    static ɵcmp: i0.ɵɵComponentDeclaration<ExpandableSectionComponent, "smart-expandable-section", never, { "data": "data"; }, {}, never, never>;
}
