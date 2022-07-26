import * as i0 from '@angular/core';
import { Injectable, Component, Input, NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import * as i1 from '@angular/common';
import { CommonModule } from '@angular/common';

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
    constructor(resolver) {
        this.resolver = resolver;
        this.toolbarDirection = ToolbarDirection;
    }
    ngOnInit() {
    }
}
SmartToolbarComponent.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarComponent, deps: [{ token: i0.ComponentFactoryResolver }], target: i0.ɵɵFactoryTarget.Component });
SmartToolbarComponent.ɵcmp = i0.ɵɵngDeclareComponent({ minVersion: "12.0.0", version: "13.2.7", type: SmartToolbarComponent, selector: "smart-toolbar", inputs: { toolbar: "toolbar" }, ngImport: i0, template: "<div [ngClass]=\"toolbar.direction === toolbarDirection.ROW ? 'row' : 'col'\">\n  <button\n    mat-raised-button\n    *ngFor=\"let button of toolbar.buttons\"\n    (click)=\"button.btnAction()\"\n    [ngClass]=\"button.btnStyle\"\n  >\n    {{ button.label }} <mat-icon>{{ button.icon }}</mat-icon>\n  </button>\n</div>\n", styles: [".button{margin:1em}.col{display:flex;flex-direction:column;padding:1em}.row{display:flex;flex-wrap:wrap;padding:1em}.save{color:#fff;border-radius:24px;background-color:green;margin:1em}.discard{color:#fff;border-radius:24px;background-color:red;margin:1em}.back,.ok{color:#fff;border-radius:24px;background-color:#00f;margin:1em}\n"], directives: [{ type: i1.NgClass, selector: "[ngClass]", inputs: ["class", "ngClass"] }, { type: i1.NgForOf, selector: "[ngFor][ngForOf]", inputs: ["ngForOf", "ngForTrackBy", "ngForTemplate"] }] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarComponent, decorators: [{
            type: Component,
            args: [{ selector: 'smart-toolbar', template: "<div [ngClass]=\"toolbar.direction === toolbarDirection.ROW ? 'row' : 'col'\">\n  <button\n    mat-raised-button\n    *ngFor=\"let button of toolbar.buttons\"\n    (click)=\"button.btnAction()\"\n    [ngClass]=\"button.btnStyle\"\n  >\n    {{ button.label }} <mat-icon>{{ button.icon }}</mat-icon>\n  </button>\n</div>\n", styles: [".button{margin:1em}.col{display:flex;flex-direction:column;padding:1em}.row{display:flex;flex-wrap:wrap;padding:1em}.save{color:#fff;border-radius:24px;background-color:green;margin:1em}.discard{color:#fff;border-radius:24px;background-color:red;margin:1em}.back,.ok{color:#fff;border-radius:24px;background-color:#00f;margin:1em}\n"] }]
        }], ctorParameters: function () { return [{ type: i0.ComponentFactoryResolver }]; }, propDecorators: { toolbar: [{
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
SmartToolbarModule.ɵmod = i0.ɵɵngDeclareNgModule({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarModule, declarations: [SmartToolbarComponent], imports: [CommonModule], exports: [SmartToolbarComponent] });
SmartToolbarModule.ɵinj = i0.ɵɵngDeclareInjector({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarModule, imports: [[
            CommonModule,
        ]] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarModule, decorators: [{
            type: NgModule,
            args: [{
                    declarations: [
                        SmartToolbarComponent
                    ],
                    imports: [
                        CommonModule,
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
