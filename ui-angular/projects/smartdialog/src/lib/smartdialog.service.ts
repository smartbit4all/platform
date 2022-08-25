import { ComponentType } from "@angular/cdk/portal";
import { Injectable } from "@angular/core";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { Router } from "@angular/router";
import { SmartDialogData } from "./smartdialog.model";

@Injectable({
    providedIn: "root",
})
export class SmartdialogService {
    dialogRef?: MatDialogRef<any, any>;
    dialogData?: SmartDialogData;

    constructor(private dialog: MatDialog, private router: Router) {}

    createDialog(smartDialog: SmartDialogData, component: ComponentType<any>): void {
        this.dialogData = smartDialog;

        this.dialogRef = this.dialog.open(component, { data: this.dialogData });

        this.dialogRef.afterClosed().subscribe((result) => {
            this.handleAfterClosed(result);
            this.closeDialog();
        });
    }

    handleAfterClosed(result: any): void {
        throw new Error("handleAfterClosed function is not implemented!");
    }

    async closeDialog(): Promise<void> {
        if (this.dialogData?.outlets) {
            await this.router.navigate([
                {
                    outlets: this.dialogData.outlets,
                },
            ]);
        }
        this.dialogRef?.close();
    }
}
