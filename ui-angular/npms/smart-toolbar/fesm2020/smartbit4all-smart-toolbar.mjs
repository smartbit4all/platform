import * as i0 from '@angular/core';
import { Injectable, Component, Input, NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import * as i1 from '@angular/material/button';
import { MatButtonModule } from '@angular/material/button';
import * as i2 from '@angular/material/icon';
import { MatIconModule } from '@angular/material/icon';
import * as i3 from '@angular/common';
import { CommonModule } from '@angular/common';
import { BrowserModule } from '@angular/platform-browser';

class SmartToolbarService {
    constructor() { }
}
SmartToolbarService.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarService, deps: [], target: i0.ɵɵFactoryTarget.Injectable });
SmartToolbarService.ɵprov = i0.ɵɵngDeclareInjectable({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarService, providedIn: 'root' });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarService, decorators: [{
            type: Injectable,
            args: [{
                    providedIn: 'root'
                }]
        }], ctorParameters: function () { return []; } });

class SmartToolbarComponent {
    constructor() {
        this.toolbarDirection = ToolbarDirection;
    }
    ngOnInit() {
    }
}
SmartToolbarComponent.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarComponent, deps: [], target: i0.ɵɵFactoryTarget.Component });
SmartToolbarComponent.ɵcmp = i0.ɵɵngDeclareComponent({ minVersion: "12.0.0", version: "13.2.7", type: SmartToolbarComponent, selector: "smart-toolbar", inputs: { toolbar: "toolbar" }, ngImport: i0, template: "<div\n\tclass=\"button-container\"\n\t[ngClass]=\"toolbar.direction === toolbarDirection.ROW ? 'row' : 'col'\"\n>\n\t<button\n\t\tmat-raised-button\n\t\t*ngFor=\"let button of toolbar.buttons\"\n\t\t(click)=\"button.btnAction()\"\n\t\t[ngClass]=\"button.btnStyle\"\n\t>\n\t\t{{ button.label }} <mat-icon>{{ button.icon }}</mat-icon>\n\t</button>\n</div>\n", styles: [".button-container{background-color:transparent}.col{display:flex;flex-direction:column;padding:1em}.row{display:flex;flex-wrap:row;padding:1em}.tree-toolbar-btn{color:#fff;border-radius:6px;background-color:#1b5498;margin:1em}.document-details-dark-btn{color:#fff;border-radius:6px;background-color:#0c2645;margin:1em}.document-details-light-btn{color:#0c2645;border-radius:6px;background-color:#fff;margin:1em;border:1px solid #0C2645}\n"], components: [{ type: i1.MatButton, selector: "button[mat-button], button[mat-raised-button], button[mat-icon-button],             button[mat-fab], button[mat-mini-fab], button[mat-stroked-button],             button[mat-flat-button]", inputs: ["disabled", "disableRipple", "color"], exportAs: ["matButton"] }, { type: i2.MatIcon, selector: "mat-icon", inputs: ["color", "inline", "svgIcon", "fontSet", "fontIcon"], exportAs: ["matIcon"] }], directives: [{ type: i3.NgClass, selector: "[ngClass]", inputs: ["class", "ngClass"] }, { type: i3.NgForOf, selector: "[ngFor][ngForOf]", inputs: ["ngForOf", "ngForTrackBy", "ngForTemplate"] }] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarComponent, decorators: [{
            type: Component,
            args: [{ selector: 'smart-toolbar', template: "<div\n\tclass=\"button-container\"\n\t[ngClass]=\"toolbar.direction === toolbarDirection.ROW ? 'row' : 'col'\"\n>\n\t<button\n\t\tmat-raised-button\n\t\t*ngFor=\"let button of toolbar.buttons\"\n\t\t(click)=\"button.btnAction()\"\n\t\t[ngClass]=\"button.btnStyle\"\n\t>\n\t\t{{ button.label }} <mat-icon>{{ button.icon }}</mat-icon>\n\t</button>\n</div>\n", styles: [".button-container{background-color:transparent}.col{display:flex;flex-direction:column;padding:1em}.row{display:flex;flex-wrap:row;padding:1em}.tree-toolbar-btn{color:#fff;border-radius:6px;background-color:#1b5498;margin:1em}.document-details-dark-btn{color:#fff;border-radius:6px;background-color:#0c2645;margin:1em}.document-details-light-btn{color:#0c2645;border-radius:6px;background-color:#fff;margin:1em;border:1px solid #0C2645}\n"] }]
        }], ctorParameters: function () { return []; }, propDecorators: { toolbar: [{
                type: Input
            }] } });
var ToolbarDirection;
(function (ToolbarDirection) {
    ToolbarDirection[ToolbarDirection["COL"] = 0] = "COL";
    ToolbarDirection[ToolbarDirection["ROW"] = 1] = "ROW";
})(ToolbarDirection || (ToolbarDirection = {}));

class SmartToolbarModule {
}
SmartToolbarModule.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarModule, deps: [], target: i0.ɵɵFactoryTarget.NgModule });
SmartToolbarModule.ɵmod = i0.ɵɵngDeclareNgModule({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarModule, declarations: [SmartToolbarComponent], imports: [CommonModule,
        BrowserModule,
        MatButtonModule,
        MatIconModule], exports: [SmartToolbarComponent] });
SmartToolbarModule.ɵinj = i0.ɵɵngDeclareInjector({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarModule, imports: [[
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
                    schemas: [CUSTOM_ELEMENTS_SCHEMA],
                }]
        }] });

/*
 * Public API Surface of smart-toolbar
 */

/**
 * Generated bundle index. Do not edit.
 */

export { SmartToolbarComponent, SmartToolbarModule, SmartToolbarService, ToolbarDirection };
//# sourceMappingURL=smartbit4all-smart-toolbar.mjs.map
