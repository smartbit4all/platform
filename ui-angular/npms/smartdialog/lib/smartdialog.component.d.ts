import { AfterViewInit, ComponentFactoryResolver, ComponentRef, ViewContainerRef } from "@angular/core";
import { MatDialogRef } from "@angular/material/dialog";
import { SmartformComponent } from "@smartbit4all/form";
import { SmartDialogData } from "./smartdialog.model";
import * as i0 from "@angular/core";
export declare class SmartDialog implements AfterViewInit {
    dialogRef: MatDialogRef<SmartDialog>;
    data: SmartDialogData;
    private resolver;
    smartFormComponent: SmartformComponent;
    vcRef?: ViewContainerRef;
    componentRef?: ComponentRef<any>;
    constructor(dialogRef: MatDialogRef<SmartDialog>, data: SmartDialogData, resolver: ComponentFactoryResolver);
    ngAfterViewInit(): void;
    onActionClick(): void;
    onNoClick(): void;
    static ɵfac: i0.ɵɵFactoryDeclaration<SmartDialog, never>;
    static ɵcmp: i0.ɵɵComponentDeclaration<SmartDialog, "smartdialog", never, {}, {}, never, never>;
}
