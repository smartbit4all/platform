import {
    Component,
    ComponentFactoryResolver,
    ComponentRef,
    Input,
    OnInit,
    ViewChild,
    ViewContainerRef,
    ViewEncapsulation,
} from "@angular/core";
import { SmartForm } from "@smartbit4all/form";
import { SmartTable } from "@smartbit4all/table";
import { ExpandableSection } from "./expandable-section.model";

@Component({
    selector: "smart-expandable-section",
    templateUrl: "./expandable-section.component.html",
    styleUrls: ["./expandable-section.component.css"],
    encapsulation: ViewEncapsulation.None,
})
export class ExpandableSectionComponent implements OnInit {
    @Input() data!: ExpandableSection;
    @Input() smartTable?: SmartTable<any>;
    @Input() smartForm?: SmartForm;

    @ViewChild("renderComponent", { read: ViewContainerRef })
    vcRef?: ViewContainerRef;

    constructor(private resolver: ComponentFactoryResolver) {}

    ngOnInit(): void {}

    ngAfterViewInit() {
        const factory = this.resolver.resolveComponentFactory(this.data.customComponent);
        const ref = this.vcRef!.createComponent(factory);
        if (this.smartTable) this.loadTableData(ref);
        if (this.smartForm) this.loadFormData(ref);
        ref.changeDetectorRef.detectChanges();
    }

    loadTableData(ref: ComponentRef<any>) {
        ref.instance.smartTable = this.smartTable;
    }

    loadFormData(ref: ComponentRef<any>) {
        ref.instance.smartForm = this.smartForm;
    }
}
