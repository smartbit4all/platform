import { ComponentType } from "@angular/cdk/portal";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { Router } from "@angular/router";
import { SmartDialogData } from "./smartdialog.model";
import * as i0 from "@angular/core";
export declare class SmartdialogService {
    protected dialog: MatDialog;
    protected router: Router;
    dialogRef?: MatDialogRef<any, any>;
    dialogData?: SmartDialogData;
    constructor(dialog: MatDialog, router: Router);
    createDialog(smartDialog: SmartDialogData, component: ComponentType<any>): void;
    handleAfterClosed(result: any): void;
    closeDialog(): Promise<void>;
    static ɵfac: i0.ɵɵFactoryDeclaration<SmartdialogService, never>;
    static ɵprov: i0.ɵɵInjectableDeclaration<SmartdialogService>;
}
