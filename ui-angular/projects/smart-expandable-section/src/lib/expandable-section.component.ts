import {
    Component,
    Input,
    OnInit,
    ViewChild,
    ViewContainerRef,
    ViewEncapsulation,
} from "@angular/core";
import { ComponentFactoryService } from "@smartbit4all/component-factory-service";
import { ExpandableSection, ExpandableSectionButton } from "./expandable-section.model";

@Component({
    selector: "smart-expandable-section",
    templateUrl: "./expandable-section.component.html",
    styleUrls: ["./expandable-section.component.css"],
    encapsulation: ViewEncapsulation.None,
})
export class ExpandableSectionComponent implements OnInit {
    @Input() data!: ExpandableSection<any>;

    @ViewChild("renderComponent", { read: ViewContainerRef })
    vcRef?: ViewContainerRef;

    constructor(private cfService: ComponentFactoryService) { }

    ngOnInit(): void { }

    ngAfterViewInit() {
        this.cfService.createComponent(
            this.vcRef!,
            this.data.customComponent,
            new Map<string, any>([[this.data.inputName ?? "", this.data.data]])
        );
    }

    action(button: ExpandableSectionButton, event: any) {
        event.stopPropagation();
        button.onClick();
    }
}
