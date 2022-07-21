import * as i0 from '@angular/core';
import { Injectable, ViewContainerRef, Component, ViewEncapsulation, Input, ViewChild, NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import * as i1 from '@angular/material/expansion';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatCommonModule } from '@angular/material/core';
import { BrowserModule } from '@angular/platform-browser';
import { SmarttableModule } from '@smartbit4all/table';
import { SmartformModule } from '@smartbit4all/form';

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
    constructor(resolver) {
        this.resolver = resolver;
    }
    ngOnInit() { }
    ngAfterViewInit() {
        const factory = this.resolver.resolveComponentFactory(this.data.customComponent);
        const ref = this.vcRef.createComponent(factory);
        if (this.smartTable)
            this.loadTableData(ref);
        if (this.smartForm)
            this.loadFormData(ref);
        ref.changeDetectorRef.detectChanges();
    }
    loadTableData(ref) {
        ref.instance.smartTable = this.smartTable;
    }
    loadFormData(ref) {
        ref.instance.smartForm = this.smartForm;
    }
}
ExpandableSectionComponent.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: ExpandableSectionComponent, deps: [{ token: i0.ComponentFactoryResolver }], target: i0.ɵɵFactoryTarget.Component });
ExpandableSectionComponent.ɵcmp = i0.ɵɵngDeclareComponent({ minVersion: "12.0.0", version: "13.2.7", type: ExpandableSectionComponent, selector: "smart-expandable-section", inputs: { data: "data", smartTable: "smartTable", smartForm: "smartForm" }, viewQueries: [{ propertyName: "vcRef", first: true, predicate: ["renderComponent"], descendants: true, read: ViewContainerRef }], ngImport: i0, template: "<div class=\"section-container\">\n    <mat-expansion-panel (opened)=\"(true)\" (closed)=\"(false)\">\n        <mat-expansion-panel-header>\n            <mat-panel-title> {{ data.title }} </mat-panel-title>\n        </mat-expansion-panel-header>\n        <ng-template #renderComponent></ng-template>\n    </mat-expansion-panel>\n</div>\n", styles: [".section-container{margin-bottom:50px}.mat-expansion-panel-header{background:var(--primary-lighter-color)!important}.mat-expansion-panel-header-title{color:var(--primary-color)}\n"], components: [{ type: i1.MatExpansionPanel, selector: "mat-expansion-panel", inputs: ["disabled", "expanded", "hideToggle", "togglePosition"], outputs: ["opened", "closed", "expandedChange", "afterExpand", "afterCollapse"], exportAs: ["matExpansionPanel"] }, { type: i1.MatExpansionPanelHeader, selector: "mat-expansion-panel-header", inputs: ["tabIndex", "expandedHeight", "collapsedHeight"] }], directives: [{ type: i1.MatExpansionPanelTitle, selector: "mat-panel-title" }], encapsulation: i0.ViewEncapsulation.None });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: ExpandableSectionComponent, decorators: [{
            type: Component,
            args: [{ selector: "smart-expandable-section", encapsulation: ViewEncapsulation.None, template: "<div class=\"section-container\">\n    <mat-expansion-panel (opened)=\"(true)\" (closed)=\"(false)\">\n        <mat-expansion-panel-header>\n            <mat-panel-title> {{ data.title }} </mat-panel-title>\n        </mat-expansion-panel-header>\n        <ng-template #renderComponent></ng-template>\n    </mat-expansion-panel>\n</div>\n", styles: [".section-container{margin-bottom:50px}.mat-expansion-panel-header{background:var(--primary-lighter-color)!important}.mat-expansion-panel-header-title{color:var(--primary-color)}\n"] }]
        }], ctorParameters: function () { return [{ type: i0.ComponentFactoryResolver }]; }, propDecorators: { data: [{
                type: Input
            }], smartTable: [{
                type: Input
            }], smartForm: [{
                type: Input
            }], vcRef: [{
                type: ViewChild,
                args: ["renderComponent", { read: ViewContainerRef }]
            }] } });

class SmartExpandableSectionModule {
}
SmartExpandableSectionModule.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartExpandableSectionModule, deps: [], target: i0.ɵɵFactoryTarget.NgModule });
SmartExpandableSectionModule.ɵmod = i0.ɵɵngDeclareNgModule({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartExpandableSectionModule, declarations: [ExpandableSectionComponent], imports: [BrowserModule,
        MatCommonModule,
        MatExpansionModule,
        SmarttableModule,
        SmartformModule], exports: [ExpandableSectionComponent] });
SmartExpandableSectionModule.ɵinj = i0.ɵɵngDeclareInjector({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartExpandableSectionModule, imports: [[
            BrowserModule,
            MatCommonModule,
            MatExpansionModule,
            SmarttableModule,
            SmartformModule,
        ]] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartExpandableSectionModule, decorators: [{
            type: NgModule,
            args: [{
                    declarations: [ExpandableSectionComponent],
                    imports: [
                        BrowserModule,
                        MatCommonModule,
                        MatExpansionModule,
                        SmarttableModule,
                        SmartformModule,
                    ],
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
