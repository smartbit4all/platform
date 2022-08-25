import {
    AfterViewInit,
    Component,
    ComponentFactory,
    ComponentFactoryResolver,
    ComponentRef,
    Inject,
    ViewChild,
    ViewContainerRef,
} from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { SmartformComponent } from "@smartbit4all/form";
import { SmartDialogData } from "./smartdialog.model";

@Component({
    selector: "smartdialog",
    templateUrl: "./smartdialog.component.html",
    styleUrls: ["./smartdialog.component.css"],
})
export class SmartDialog implements AfterViewInit {
    @ViewChild(SmartformComponent) smartFormComponent!: SmartformComponent;

    @ViewChild("renderComponentHere", { read: ViewContainerRef })
    vcRef?: ViewContainerRef;

    componentRef?: ComponentRef<any>;

    constructor(
        public dialogRef: MatDialogRef<SmartDialog>,
        @Inject(MAT_DIALOG_DATA) public data: SmartDialogData,
        private resolver: ComponentFactoryResolver
    ) {}

    ngAfterViewInit() {
        const factory = this.resolver.resolveComponentFactory(this.data.customComponent);
        const ref = this.vcRef!.createComponent(factory);
        ref.changeDetectorRef.detectChanges();
    }

    onActionClick(): void {
        const controls = this.smartFormComponent.getForm().controls;

        for (const control in controls) {
            if (controls[control].valid) {
                const widget = this.data.form!.widgets.find((k: any) => k.key === control);
                if (widget) {
                    widget.value = controls[control].value;
                }
            }
        }

        if (this.data.actionCallback) {
            this.data.actionCallback([this.data]);
        }
    }

    onNoClick(): void {
        this.dialogRef.close();
    }
}
