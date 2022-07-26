import * as i0 from '@angular/core';
import { Injectable, Component, Input, ViewContainerRef, ViewChild, NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
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

class SmartToolbarButtonComponent {
    // label!: string;
    // icon?: string;
    // btnAction!: Function;
    // btnStyle?: any;
    constructor() {
    }
    ngOnInit() {
        console.log(this.button);
    }
}
SmartToolbarButtonComponent.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarButtonComponent, deps: [], target: i0.ɵɵFactoryTarget.Component });
SmartToolbarButtonComponent.ɵcmp = i0.ɵɵngDeclareComponent({ minVersion: "12.0.0", version: "13.2.7", type: SmartToolbarButtonComponent, selector: "smart-toolbar-button", inputs: { button: "button" }, ngImport: i0, template: "<div class=\"button\">\n\t<button mat-raised-button (click)=\"button.btnAction()\" [ngStyle]=\"button.btnStyle\">\n\t\t{{ button.label }} <mat-icon>{{ button.icon }}</mat-icon>\n\t</button>\n</div>\n", styles: [".button{padding:1em}\n"], directives: [{ type: i1.NgStyle, selector: "[ngStyle]", inputs: ["ngStyle"] }] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarButtonComponent, decorators: [{
            type: Component,
            args: [{ selector: 'smart-toolbar-button', template: "<div class=\"button\">\n\t<button mat-raised-button (click)=\"button.btnAction()\" [ngStyle]=\"button.btnStyle\">\n\t\t{{ button.label }} <mat-icon>{{ button.icon }}</mat-icon>\n\t</button>\n</div>\n", styles: [".button{padding:1em}\n"] }]
        }], ctorParameters: function () { return []; }, propDecorators: { button: [{
                type: Input
            }] } });

class SmartToolbarComponent {
    constructor(resolver) {
        this.resolver = resolver;
        this.direction = ToolbarDirection.ROW;
        this.toolbarDirection = ToolbarDirection;
    }
    ngOnInit() {
    }
    ngAfterViewInit() {
        for (let btn of this.buttons) {
            const factory = this.resolver.resolveComponentFactory(SmartToolbarButtonComponent);
            const ref = this.vcRef.createComponent(factory);
            console.log(btn);
            ref.instance.button = btn;
            ref.changeDetectorRef.detectChanges();
        }
    }
}
SmartToolbarComponent.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarComponent, deps: [{ token: i0.ComponentFactoryResolver }], target: i0.ɵɵFactoryTarget.Component });
SmartToolbarComponent.ɵcmp = i0.ɵɵngDeclareComponent({ minVersion: "12.0.0", version: "13.2.7", type: SmartToolbarComponent, selector: "smart-toolbar", inputs: { buttons: "buttons", direction: "direction" }, viewQueries: [{ propertyName: "vcRef", first: true, predicate: ["renderToolbar"], descendants: true, read: ViewContainerRef }], ngImport: i0, template: "<div [ngClass]=\"direction === toolbarDirection.ROW ? 'row' : 'col'\">\n\t<ng-template #renderToolbar></ng-template>\n</div>\n", styles: [".col{display:flex;flex-direction:column;padding:1em}.row{display:flex;flex-wrap:wrap;padding:1em}\n"], directives: [{ type: i1.NgClass, selector: "[ngClass]", inputs: ["class", "ngClass"] }] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarComponent, decorators: [{
            type: Component,
            args: [{ selector: 'smart-toolbar', template: "<div [ngClass]=\"direction === toolbarDirection.ROW ? 'row' : 'col'\">\n\t<ng-template #renderToolbar></ng-template>\n</div>\n", styles: [".col{display:flex;flex-direction:column;padding:1em}.row{display:flex;flex-wrap:wrap;padding:1em}\n"] }]
        }], ctorParameters: function () { return [{ type: i0.ComponentFactoryResolver }]; }, propDecorators: { buttons: [{
                type: Input
            }], direction: [{
                type: Input
            }], vcRef: [{
                type: ViewChild,
                args: ['renderToolbar', { read: ViewContainerRef }]
            }] } });
var ToolbarDirection;
(function (ToolbarDirection) {
    ToolbarDirection[ToolbarDirection["COL"] = 0] = "COL";
    ToolbarDirection[ToolbarDirection["ROW"] = 1] = "ROW";
})(ToolbarDirection || (ToolbarDirection = {}));

class SmartToolbarModule {
}
SmartToolbarModule.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarModule, deps: [], target: i0.ɵɵFactoryTarget.NgModule });
SmartToolbarModule.ɵmod = i0.ɵɵngDeclareNgModule({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarModule, declarations: [SmartToolbarComponent,
        SmartToolbarButtonComponent], imports: [CommonModule], exports: [SmartToolbarComponent] });
SmartToolbarModule.ɵinj = i0.ɵɵngDeclareInjector({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarModule, imports: [[
            CommonModule
        ]] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarModule, decorators: [{
            type: NgModule,
            args: [{
                    declarations: [
                        SmartToolbarComponent,
                        SmartToolbarButtonComponent,
                    ],
                    imports: [
                        CommonModule
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
