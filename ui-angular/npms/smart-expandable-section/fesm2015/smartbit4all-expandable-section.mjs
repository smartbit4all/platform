import * as i0 from '@angular/core';
import { Injectable, ViewContainerRef, Component, ViewEncapsulation, Input, ViewChild, NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import * as i1 from '@smartbit4all/component-factory-service';
import { ComponentFactoryServiceModule } from '@smartbit4all/component-factory-service';
import * as i2 from '@angular/material/expansion';
import { MatExpansionModule } from '@angular/material/expansion';
import * as i3 from '@angular/material/button';
import { MatButtonModule } from '@angular/material/button';
import * as i4 from '@angular/material/icon';
import { MatIconModule } from '@angular/material/icon';
import * as i5 from '@angular/common';
import { MatCommonModule } from '@angular/material/core';
import { BrowserModule } from '@angular/platform-browser';

class SmartExpandableSectionService {
    constructor() { }
}
SmartExpandableSectionService.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartExpandableSectionService, deps: [], target: i0.ɵɵFactoryTarget.Injectable });
SmartExpandableSectionService.ɵprov = i0.ɵɵngDeclareInjectable({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartExpandableSectionService, providedIn: 'root' });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartExpandableSectionService, decorators: [{
            type: Injectable,
            args: [{
                    providedIn: 'root'
                }]
        }], ctorParameters: function () { return []; } });

class ExpandableSectionComponent {
    constructor(cfService) {
        this.cfService = cfService;
    }
    ngOnInit() { }
    ngAfterViewInit() {
        var _a;
        this.cfService.createComponent(this.vcRef, this.data.customComponent, new Map([[(_a = this.data.inputName) !== null && _a !== void 0 ? _a : "", this.data.data]]));
    }
    action(button, event) {
        event.stopPropagation();
        button.onClick();
    }
}
ExpandableSectionComponent.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: ExpandableSectionComponent, deps: [{ token: i1.ComponentFactoryService }], target: i0.ɵɵFactoryTarget.Component });
ExpandableSectionComponent.ɵcmp = i0.ɵɵngDeclareComponent({ minVersion: "12.0.0", version: "13.2.7", type: ExpandableSectionComponent, selector: "smart-expandable-section", inputs: { data: "data" }, viewQueries: [{ propertyName: "vcRef", first: true, predicate: ["renderComponent"], descendants: true, read: ViewContainerRef }], ngImport: i0, template: "<div class=\"section-container\">\r\n  <mat-expansion-panel (opened)=\"(true)\" (closed)=\"(false)\">\r\n    <mat-expansion-panel-header>\r\n      <mat-panel-title> {{ data.title }} </mat-panel-title>\r\n      <div class=\"btn-container\" *ngIf=\"data.button\">\r\n        <button\r\n          class=\"btn\"\r\n          (click)=\"action(data.button, $event)\"\r\n          mat-stroked-button\r\n        >\r\n          {{ data.button.label }}\r\n          <mat-icon>{{ data.button.icon }}</mat-icon>\r\n        </button>\r\n      </div>\r\n    </mat-expansion-panel-header>\r\n    <ng-template #renderComponent></ng-template>\r\n  </mat-expansion-panel>\r\n</div>\r\n", styles: [".section-container{margin-bottom:50px}.mat-expansion-panel-header{background:var(--primary-lighter-color)!important}.mat-expansion-panel-header-title{color:var(--primary-color)}.btn-container{margin:1em 3em 1em 1em}.btn{border-radius:24px}\n"], components: [{ type: i2.MatExpansionPanel, selector: "mat-expansion-panel", inputs: ["disabled", "expanded", "hideToggle", "togglePosition"], outputs: ["opened", "closed", "expandedChange", "afterExpand", "afterCollapse"], exportAs: ["matExpansionPanel"] }, { type: i2.MatExpansionPanelHeader, selector: "mat-expansion-panel-header", inputs: ["tabIndex", "expandedHeight", "collapsedHeight"] }, { type: i3.MatButton, selector: "button[mat-button], button[mat-raised-button], button[mat-icon-button],             button[mat-fab], button[mat-mini-fab], button[mat-stroked-button],             button[mat-flat-button]", inputs: ["disabled", "disableRipple", "color"], exportAs: ["matButton"] }, { type: i4.MatIcon, selector: "mat-icon", inputs: ["color", "inline", "svgIcon", "fontSet", "fontIcon"], exportAs: ["matIcon"] }], directives: [{ type: i2.MatExpansionPanelTitle, selector: "mat-panel-title" }, { type: i5.NgIf, selector: "[ngIf]", inputs: ["ngIf", "ngIfThen", "ngIfElse"] }], encapsulation: i0.ViewEncapsulation.None });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: ExpandableSectionComponent, decorators: [{
            type: Component,
            args: [{ selector: "smart-expandable-section", encapsulation: ViewEncapsulation.None, template: "<div class=\"section-container\">\r\n  <mat-expansion-panel (opened)=\"(true)\" (closed)=\"(false)\">\r\n    <mat-expansion-panel-header>\r\n      <mat-panel-title> {{ data.title }} </mat-panel-title>\r\n      <div class=\"btn-container\" *ngIf=\"data.button\">\r\n        <button\r\n          class=\"btn\"\r\n          (click)=\"action(data.button, $event)\"\r\n          mat-stroked-button\r\n        >\r\n          {{ data.button.label }}\r\n          <mat-icon>{{ data.button.icon }}</mat-icon>\r\n        </button>\r\n      </div>\r\n    </mat-expansion-panel-header>\r\n    <ng-template #renderComponent></ng-template>\r\n  </mat-expansion-panel>\r\n</div>\r\n", styles: [".section-container{margin-bottom:50px}.mat-expansion-panel-header{background:var(--primary-lighter-color)!important}.mat-expansion-panel-header-title{color:var(--primary-color)}.btn-container{margin:1em 3em 1em 1em}.btn{border-radius:24px}\n"] }]
        }], ctorParameters: function () { return [{ type: i1.ComponentFactoryService }]; }, propDecorators: { data: [{
                type: Input
            }], vcRef: [{
                type: ViewChild,
                args: ["renderComponent", { read: ViewContainerRef }]
            }] } });

class SmartExpandableSectionModule {
}
SmartExpandableSectionModule.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartExpandableSectionModule, deps: [], target: i0.ɵɵFactoryTarget.NgModule });
SmartExpandableSectionModule.ɵmod = i0.ɵɵngDeclareNgModule({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartExpandableSectionModule, declarations: [ExpandableSectionComponent], imports: [BrowserModule, MatCommonModule, MatExpansionModule, ComponentFactoryServiceModule, MatIconModule, MatButtonModule], exports: [ExpandableSectionComponent] });
SmartExpandableSectionModule.ɵinj = i0.ɵɵngDeclareInjector({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartExpandableSectionModule, imports: [[BrowserModule, MatCommonModule, MatExpansionModule, ComponentFactoryServiceModule, MatIconModule, MatButtonModule]] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartExpandableSectionModule, decorators: [{
            type: NgModule,
            args: [{
                    declarations: [ExpandableSectionComponent],
                    imports: [BrowserModule, MatCommonModule, MatExpansionModule, ComponentFactoryServiceModule, MatIconModule, MatButtonModule],
                    exports: [ExpandableSectionComponent],
                    schemas: [CUSTOM_ELEMENTS_SCHEMA],
                }]
        }] });

/*
 * Public API Surface of smart-expandable-section
 */

/**
 * Generated bundle index. Do not edit.
 */

export { ExpandableSectionComponent, SmartExpandableSectionModule, SmartExpandableSectionService };
//# sourceMappingURL=smartbit4all-expandable-section.mjs.map
