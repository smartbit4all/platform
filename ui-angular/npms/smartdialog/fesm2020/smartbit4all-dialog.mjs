import * as i0 from '@angular/core';
import { Injectable, ViewContainerRef, Component, Inject, ViewChild, NgModule, CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from '@angular/core';
import * as i1 from '@angular/material/dialog';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import * as i2 from '@angular/router';
import * as i3 from '@smartbit4all/form';
import { SmartformComponent, SmartformModule } from '@smartbit4all/form';
import * as i2$1 from '@angular/material/icon';
import { MatIconModule } from '@angular/material/icon';
import * as i4 from '@smartbit4all/table';
import { SmarttableModule } from '@smartbit4all/table';
import * as i5 from '@angular/common';
import { MatCommonModule } from '@angular/material/core';
import { BrowserModule } from '@angular/platform-browser';

class SmartdialogService {
    constructor(dialog, router) {
        this.dialog = dialog;
        this.router = router;
    }
    createDialog(smartDialog, component) {
        this.dialogData = smartDialog;
        this.dialogRef = this.dialog.open(component, { data: this.dialogData });
        this.dialogRef.afterClosed().subscribe((result) => {
            this.handleAfterClosed(result);
            this.closeDialog();
        });
    }
    handleAfterClosed(result) {
        throw new Error("handleAfterClosed function is not implemented!");
    }
    async closeDialog() {
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
SmartdialogService.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartdialogService, deps: [{ token: i1.MatDialog }, { token: i2.Router }], target: i0.ɵɵFactoryTarget.Injectable });
SmartdialogService.ɵprov = i0.ɵɵngDeclareInjectable({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartdialogService, providedIn: "root" });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartdialogService, decorators: [{
            type: Injectable,
            args: [{
                    providedIn: "root",
                }]
        }], ctorParameters: function () { return [{ type: i1.MatDialog }, { type: i2.Router }]; } });

class SmartDialog {
    constructor(dialogRef, data, resolver) {
        this.dialogRef = dialogRef;
        this.data = data;
        this.resolver = resolver;
    }
    ngAfterViewInit() {
        const factory = this.resolver.resolveComponentFactory(this.data.customComponent);
        const ref = this.vcRef.createComponent(factory);
        ref.changeDetectorRef.detectChanges();
    }
    onActionClick() {
        const controls = this.smartFormComponent.getForm().controls;
        for (const control in controls) {
            if (controls[control].valid) {
                const widget = this.data.form.widgets.find((k) => k.key === control);
                if (widget) {
                    widget.value = controls[control].value;
                }
            }
        }
        if (this.data.actionCallback) {
            this.data.actionCallback([this.data]);
        }
    }
    onNoClick() {
        this.dialogRef.close();
    }
}
SmartDialog.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartDialog, deps: [{ token: i1.MatDialogRef }, { token: MAT_DIALOG_DATA }, { token: i0.ComponentFactoryResolver }], target: i0.ɵɵFactoryTarget.Component });
SmartDialog.ɵcmp = i0.ɵɵngDeclareComponent({ minVersion: "12.0.0", version: "13.2.7", type: SmartDialog, selector: "smartdialog", viewQueries: [{ propertyName: "smartFormComponent", first: true, predicate: SmartformComponent, descendants: true }, { propertyName: "vcRef", first: true, predicate: ["renderComponentHere"], descendants: true, read: ViewContainerRef }], ngImport: i0, template: "<div\n    class=\"dialog-container\"\n    [ngClass]=\"data.size.fullScreen ? 'fullScreen' : ''\"\n    [ngStyle]=\"{ width: data.size.width + 'px', height: data.size.height + 'px' }\"\n>\n    <div class=\"dialog-title-container\" mat-dialog-title>\n        <h1 class=\"dialog-title\">\n            {{ data.content.title }}\n        </h1>\n        <button mat-icon-button [mat-dialog-close]=\"\">\n            <mat-icon aria-hidden=\"false\" aria-label=\"Example home icon\">close</mat-icon>\n        </button>\n    </div>\n    <div *ngIf=\"!data.customComponent\" mat-dialog-content class=\"content\">\n        <p>\n            {{ data.content.description }}\n        </p>\n        <smartform *ngIf=\"data.form\" [smartForm]=\"data.form!\"></smartform>\n        <smarttable *ngIf=\"data.table\" [smartTable]=\"data.table!\"></smarttable>\n    </div>\n    <div *ngIf=\"!data.customComponent\" class=\"action-container\" mat-dialog-actions>\n        <button\n            class=\"action-button\"\n            *ngIf=\"data.cancelCallback\"\n            mat-button\n            (click)=\"(data.cancelCallback)\"\n        >\n            Cancel\n        </button>\n        <button\n            class=\"action-button\"\n            *ngIf=\"data.okCallback\"\n            mat-button\n            (click)=\"(data.okCallback)\"\n            cdkFocusInitial\n        >\n            Ok\n        </button>\n        <button\n            class=\"action-button\"\n            *ngIf=\"data.actionCallback\"\n            mat-raised-button\n            (click)=\"onActionClick()\"\n        >\n            {{ data.actionLabel }}\n        </button>\n    </div>\n    <mat-dialog-content>\n        <ng-template #renderComponentHere></ng-template>\n    </mat-dialog-content>\n    <div *ngIf=\"data.customComponent\"></div>\n</div>\n", styles: [".dialog-container{position:relative}.dialog-title-container{display:flex;flex-direction:row;width:100%}.dialog-title-container button{text-align:right!important}.dialog-title{flex:20}.action-container{position:absolute;bottom:0;width:100%;display:flex;flex-direction:row}.action-button{flex:1;max-width:200px;margin-left:auto;margin-right:auto}.content{height:calc(100% - 116px)}.fullScreen{width:100%}\n"], components: [{ type: i2$1.MatIcon, selector: "mat-icon", inputs: ["color", "inline", "svgIcon", "fontSet", "fontIcon"], exportAs: ["matIcon"] }, { type: i3.SmartformComponent, selector: "smartform", inputs: ["smartForm"] }, { type: i4.SmarttableComponent, selector: "smarttable", inputs: ["smartTable"] }], directives: [{ type: i5.NgClass, selector: "[ngClass]", inputs: ["class", "ngClass"] }, { type: i5.NgStyle, selector: "[ngStyle]", inputs: ["ngStyle"] }, { type: i1.MatDialogTitle, selector: "[mat-dialog-title], [matDialogTitle]", inputs: ["id"], exportAs: ["matDialogTitle"] }, { type: i1.MatDialogClose, selector: "[mat-dialog-close], [matDialogClose]", inputs: ["aria-label", "type", "mat-dialog-close", "matDialogClose"], exportAs: ["matDialogClose"] }, { type: i5.NgIf, selector: "[ngIf]", inputs: ["ngIf", "ngIfThen", "ngIfElse"] }, { type: i1.MatDialogContent, selector: "[mat-dialog-content], mat-dialog-content, [matDialogContent]" }, { type: i1.MatDialogActions, selector: "[mat-dialog-actions], mat-dialog-actions, [matDialogActions]" }] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartDialog, decorators: [{
            type: Component,
            args: [{ selector: "smartdialog", template: "<div\n    class=\"dialog-container\"\n    [ngClass]=\"data.size.fullScreen ? 'fullScreen' : ''\"\n    [ngStyle]=\"{ width: data.size.width + 'px', height: data.size.height + 'px' }\"\n>\n    <div class=\"dialog-title-container\" mat-dialog-title>\n        <h1 class=\"dialog-title\">\n            {{ data.content.title }}\n        </h1>\n        <button mat-icon-button [mat-dialog-close]=\"\">\n            <mat-icon aria-hidden=\"false\" aria-label=\"Example home icon\">close</mat-icon>\n        </button>\n    </div>\n    <div *ngIf=\"!data.customComponent\" mat-dialog-content class=\"content\">\n        <p>\n            {{ data.content.description }}\n        </p>\n        <smartform *ngIf=\"data.form\" [smartForm]=\"data.form!\"></smartform>\n        <smarttable *ngIf=\"data.table\" [smartTable]=\"data.table!\"></smarttable>\n    </div>\n    <div *ngIf=\"!data.customComponent\" class=\"action-container\" mat-dialog-actions>\n        <button\n            class=\"action-button\"\n            *ngIf=\"data.cancelCallback\"\n            mat-button\n            (click)=\"(data.cancelCallback)\"\n        >\n            Cancel\n        </button>\n        <button\n            class=\"action-button\"\n            *ngIf=\"data.okCallback\"\n            mat-button\n            (click)=\"(data.okCallback)\"\n            cdkFocusInitial\n        >\n            Ok\n        </button>\n        <button\n            class=\"action-button\"\n            *ngIf=\"data.actionCallback\"\n            mat-raised-button\n            (click)=\"onActionClick()\"\n        >\n            {{ data.actionLabel }}\n        </button>\n    </div>\n    <mat-dialog-content>\n        <ng-template #renderComponentHere></ng-template>\n    </mat-dialog-content>\n    <div *ngIf=\"data.customComponent\"></div>\n</div>\n", styles: [".dialog-container{position:relative}.dialog-title-container{display:flex;flex-direction:row;width:100%}.dialog-title-container button{text-align:right!important}.dialog-title{flex:20}.action-container{position:absolute;bottom:0;width:100%;display:flex;flex-direction:row}.action-button{flex:1;max-width:200px;margin-left:auto;margin-right:auto}.content{height:calc(100% - 116px)}.fullScreen{width:100%}\n"] }]
        }], ctorParameters: function () { return [{ type: i1.MatDialogRef }, { type: undefined, decorators: [{
                    type: Inject,
                    args: [MAT_DIALOG_DATA]
                }] }, { type: i0.ComponentFactoryResolver }]; }, propDecorators: { smartFormComponent: [{
                type: ViewChild,
                args: [SmartformComponent]
            }], vcRef: [{
                type: ViewChild,
                args: ["renderComponentHere", { read: ViewContainerRef }]
            }] } });

class SmartdialogModule {
}
SmartdialogModule.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartdialogModule, deps: [], target: i0.ɵɵFactoryTarget.NgModule });
SmartdialogModule.ɵmod = i0.ɵɵngDeclareNgModule({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartdialogModule, declarations: [SmartDialog], imports: [BrowserModule,
        MatDialogModule,
        MatCommonModule,
        MatIconModule,
        SmartformModule,
        SmarttableModule], exports: [SmartDialog] });
SmartdialogModule.ɵinj = i0.ɵɵngDeclareInjector({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartdialogModule, providers: [SmartdialogService], imports: [[
            BrowserModule,
            MatDialogModule,
            MatCommonModule,
            MatIconModule,
            SmartformModule,
            SmarttableModule,
        ]] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartdialogModule, decorators: [{
            type: NgModule,
            args: [{
                    declarations: [SmartDialog],
                    imports: [
                        BrowserModule,
                        MatDialogModule,
                        MatCommonModule,
                        MatIconModule,
                        SmartformModule,
                        SmarttableModule,
                    ],
                    exports: [SmartDialog],
                    schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
                    providers: [SmartdialogService],
                }]
        }] });

/**
 * This enum helps to define the action of a dialog.
 *
 * @author Roland Fényes
 */
var SmartActionType;
(function (SmartActionType) {
    SmartActionType[SmartActionType["ADD"] = 0] = "ADD";
    SmartActionType[SmartActionType["CREATE"] = 1] = "CREATE";
    SmartActionType[SmartActionType["UPDATE"] = 2] = "UPDATE";
    SmartActionType[SmartActionType["REMOVE"] = 3] = "REMOVE";
    SmartActionType[SmartActionType["DELETE"] = 4] = "DELETE";
    SmartActionType[SmartActionType["SAVE"] = 5] = "SAVE";
    SmartActionType[SmartActionType["OK"] = 6] = "OK";
})(SmartActionType || (SmartActionType = {}));

/*
 * Public API Surface of smartdialog
 */

/**
 * Generated bundle index. Do not edit.
 */

export { SmartActionType, SmartDialog, SmartdialogModule, SmartdialogService };
//# sourceMappingURL=smartbit4all-dialog.mjs.map
