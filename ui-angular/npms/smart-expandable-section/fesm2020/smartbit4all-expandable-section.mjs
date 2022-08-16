import * as i0 from '@angular/core';
import { Injectable, ViewContainerRef, Component, ViewEncapsulation, Input, ViewChild, NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import * as i1 from '@smartbit4all/component-factory-service';
import { ComponentFactoryServiceModule } from '@smartbit4all/component-factory-service';
import * as i2 from '@angular/material/expansion';
import { MatExpansionModule } from '@angular/material/expansion';
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
        this.cfService.createComponent(this.vcRef, this.data.customComponent, new Map([[this.data.inputName ?? "", this.data.data]]));
    }
}
ExpandableSectionComponent.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: ExpandableSectionComponent, deps: [{ token: i1.ComponentFactoryService }], target: i0.ɵɵFactoryTarget.Component });
ExpandableSectionComponent.ɵcmp = i0.ɵɵngDeclareComponent({ minVersion: "12.0.0", version: "13.2.7", type: ExpandableSectionComponent, selector: "smart-expandable-section", inputs: { data: "data" }, viewQueries: [{ propertyName: "vcRef", first: true, predicate: ["renderComponent"], descendants: true, read: ViewContainerRef }], ngImport: i0, template: "<div class=\"section-container\">\n\t<mat-expansion-panel (opened)=\"(true)\" (closed)=\"(false)\">\n\t\t<mat-expansion-panel-header>\n\t\t\t<mat-panel-title> {{ data.title }} </mat-panel-title>\n\t\t</mat-expansion-panel-header>\n\t\t<ng-template #renderComponent></ng-template>\n\t</mat-expansion-panel>\n</div>\n", styles: [".section-container{margin-bottom:50px}.mat-expansion-panel-header{background:var(--primary-lighter-color)!important}.mat-expansion-panel-header-title{color:var(--primary-color)}\n"], components: [{ type: i2.MatExpansionPanel, selector: "mat-expansion-panel", inputs: ["disabled", "expanded", "hideToggle", "togglePosition"], outputs: ["opened", "closed", "expandedChange", "afterExpand", "afterCollapse"], exportAs: ["matExpansionPanel"] }, { type: i2.MatExpansionPanelHeader, selector: "mat-expansion-panel-header", inputs: ["tabIndex", "expandedHeight", "collapsedHeight"] }], directives: [{ type: i2.MatExpansionPanelTitle, selector: "mat-panel-title" }], encapsulation: i0.ViewEncapsulation.None });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: ExpandableSectionComponent, decorators: [{
            type: Component,
            args: [{ selector: "smart-expandable-section", encapsulation: ViewEncapsulation.None, template: "<div class=\"section-container\">\n\t<mat-expansion-panel (opened)=\"(true)\" (closed)=\"(false)\">\n\t\t<mat-expansion-panel-header>\n\t\t\t<mat-panel-title> {{ data.title }} </mat-panel-title>\n\t\t</mat-expansion-panel-header>\n\t\t<ng-template #renderComponent></ng-template>\n\t</mat-expansion-panel>\n</div>\n", styles: [".section-container{margin-bottom:50px}.mat-expansion-panel-header{background:var(--primary-lighter-color)!important}.mat-expansion-panel-header-title{color:var(--primary-color)}\n"] }]
        }], ctorParameters: function () { return [{ type: i1.ComponentFactoryService }]; }, propDecorators: { data: [{
                type: Input
            }], vcRef: [{
                type: ViewChild,
                args: ["renderComponent", { read: ViewContainerRef }]
            }] } });

class SmartExpandableSectionModule {
}
SmartExpandableSectionModule.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartExpandableSectionModule, deps: [], target: i0.ɵɵFactoryTarget.NgModule });
SmartExpandableSectionModule.ɵmod = i0.ɵɵngDeclareNgModule({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartExpandableSectionModule, declarations: [ExpandableSectionComponent], imports: [BrowserModule, MatCommonModule, MatExpansionModule, ComponentFactoryServiceModule], exports: [ExpandableSectionComponent] });
SmartExpandableSectionModule.ɵinj = i0.ɵɵngDeclareInjector({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartExpandableSectionModule, imports: [[BrowserModule, MatCommonModule, MatExpansionModule, ComponentFactoryServiceModule]] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartExpandableSectionModule, decorators: [{
            type: NgModule,
            args: [{
                    declarations: [ExpandableSectionComponent],
                    imports: [BrowserModule, MatCommonModule, MatExpansionModule, ComponentFactoryServiceModule],
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
