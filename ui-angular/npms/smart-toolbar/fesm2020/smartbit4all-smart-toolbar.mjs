import * as i0 from '@angular/core';
import { Injectable, Component, Input, NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import * as i1 from '@angular/router';
import * as i2 from '@angular/material/button';
import { MatButtonModule } from '@angular/material/button';
import * as i3 from '@angular/material/icon';
import { MatIconModule } from '@angular/material/icon';
import * as i4 from '@angular/common';
import { CommonModule } from '@angular/common';
import { BrowserModule } from '@angular/platform-browser';

var ToolbarDirection;
(function (ToolbarDirection) {
    ToolbarDirection[ToolbarDirection["COL"] = 0] = "COL";
    ToolbarDirection[ToolbarDirection["ROW"] = 1] = "ROW";
})(ToolbarDirection || (ToolbarDirection = {}));
var CommandType;
(function (CommandType) {
    CommandType["NAVIGATION"] = "NAVIGATION";
})(CommandType || (CommandType = {}));
var ToolbarButtonStyle;
(function (ToolbarButtonStyle) {
    ToolbarButtonStyle["MAT_RAISED_BUTTON"] = "MAT_RAISED_BUTTON";
    ToolbarButtonStyle["MAT_BUTTON"] = "MAT_BUTTON";
})(ToolbarButtonStyle || (ToolbarButtonStyle = {}));

class SmartToolbarService {
    constructor(router) {
        this.router = router;
    }
    executeCommand(button) {
        if (button.btnAction.commandType === CommandType.NAVIGATION) {
            let params = button.btnAction.objectUri ? { queryParams: { uri: button.btnAction.objectUri } } : {};
            this.router.navigate([button.btnAction.url], params);
        }
    }
}
SmartToolbarService.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarService, deps: [{ token: i1.Router }], target: i0.ɵɵFactoryTarget.Injectable });
SmartToolbarService.ɵprov = i0.ɵɵngDeclareInjectable({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarService, providedIn: 'root' });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarService, decorators: [{
            type: Injectable,
            args: [{
                    providedIn: 'root'
                }]
        }], ctorParameters: function () { return [{ type: i1.Router }]; } });

class SmartToolbarComponent {
    constructor(service) {
        this.service = service;
        this.toolbarDirection = ToolbarDirection;
        this.ToolbarButtonStyle = ToolbarButtonStyle;
    }
    ngOnInit() { }
    executeCommand(button) {
        this.service.executeCommand(button);
    }
}
SmartToolbarComponent.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarComponent, deps: [{ token: SmartToolbarService }], target: i0.ɵɵFactoryTarget.Component });
SmartToolbarComponent.ɵcmp = i0.ɵɵngDeclareComponent({ minVersion: "12.0.0", version: "13.2.7", type: SmartToolbarComponent, selector: "smart-toolbar", inputs: { toolbar: "toolbar" }, ngImport: i0, template: "<div\n  class=\"button-container\"\n  [ngClass]=\"toolbar.direction === toolbarDirection.ROW ? 'row' : 'col'\"\n>\n  <div *ngFor=\"let button of toolbar.buttons\">\n    <button\n      *ngIf=\"button.btnStyle === ToolbarButtonStyle.MAT_RAISED_BUTTON\"\n      mat-raised-button\n      (click)=\"executeCommand(button)\"\n    >\n      {{ button.label }} <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n    <button\n      *ngIf=\"button.btnStyle === ToolbarButtonStyle.MAT_BUTTON\"\n      mat-button\n      (click)=\"executeCommand(button)\"\n    >\n      {{ button.label }} <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n  </div>\n</div>\n", styles: [".button-container{background-color:transparent}.col{display:flex;flex-direction:column;padding:1em}.row{display:flex;flex-wrap:row;padding:1em}.button-container button{margin:1em}\n"], components: [{ type: i2.MatButton, selector: "button[mat-button], button[mat-raised-button], button[mat-icon-button],             button[mat-fab], button[mat-mini-fab], button[mat-stroked-button],             button[mat-flat-button]", inputs: ["disabled", "disableRipple", "color"], exportAs: ["matButton"] }, { type: i3.MatIcon, selector: "mat-icon", inputs: ["color", "inline", "svgIcon", "fontSet", "fontIcon"], exportAs: ["matIcon"] }], directives: [{ type: i4.NgClass, selector: "[ngClass]", inputs: ["class", "ngClass"] }, { type: i4.NgForOf, selector: "[ngFor][ngForOf]", inputs: ["ngForOf", "ngForTrackBy", "ngForTemplate"] }, { type: i4.NgIf, selector: "[ngIf]", inputs: ["ngIf", "ngIfThen", "ngIfElse"] }] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarComponent, decorators: [{
            type: Component,
            args: [{ selector: "smart-toolbar", template: "<div\n  class=\"button-container\"\n  [ngClass]=\"toolbar.direction === toolbarDirection.ROW ? 'row' : 'col'\"\n>\n  <div *ngFor=\"let button of toolbar.buttons\">\n    <button\n      *ngIf=\"button.btnStyle === ToolbarButtonStyle.MAT_RAISED_BUTTON\"\n      mat-raised-button\n      (click)=\"executeCommand(button)\"\n    >\n      {{ button.label }} <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n    <button\n      *ngIf=\"button.btnStyle === ToolbarButtonStyle.MAT_BUTTON\"\n      mat-button\n      (click)=\"executeCommand(button)\"\n    >\n      {{ button.label }} <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n  </div>\n</div>\n", styles: [".button-container{background-color:transparent}.col{display:flex;flex-direction:column;padding:1em}.row{display:flex;flex-wrap:row;padding:1em}.button-container button{margin:1em}\n"] }]
        }], ctorParameters: function () { return [{ type: SmartToolbarService }]; }, propDecorators: { toolbar: [{
                type: Input
            }] } });

class SmartToolbarModule {
}
SmartToolbarModule.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarModule, deps: [], target: i0.ɵɵFactoryTarget.NgModule });
SmartToolbarModule.ɵmod = i0.ɵɵngDeclareNgModule({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarModule, declarations: [SmartToolbarComponent], imports: [CommonModule,
        BrowserModule,
        MatButtonModule,
        MatIconModule], exports: [SmartToolbarComponent] });
SmartToolbarModule.ɵinj = i0.ɵɵngDeclareInjector({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarModule, providers: [SmartToolbarService], imports: [[
            CommonModule,
            BrowserModule,
            MatButtonModule,
            MatIconModule
        ]] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarModule, decorators: [{
            type: NgModule,
            args: [{
                    declarations: [
                        SmartToolbarComponent
                    ],
                    imports: [
                        CommonModule,
                        BrowserModule,
                        MatButtonModule,
                        MatIconModule
                    ],
                    exports: [
                        SmartToolbarComponent
                    ],
                    providers: [SmartToolbarService],
                    schemas: [CUSTOM_ELEMENTS_SCHEMA],
                }]
        }] });

/*
 * Public API Surface of smart-toolbar
 */

/**
 * Generated bundle index. Do not edit.
 */

export { CommandType, SmartToolbarComponent, SmartToolbarModule, SmartToolbarService, ToolbarButtonStyle, ToolbarDirection };
//# sourceMappingURL=smartbit4all-smart-toolbar.mjs.map
