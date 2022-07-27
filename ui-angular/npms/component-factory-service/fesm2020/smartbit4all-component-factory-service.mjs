import * as i0 from '@angular/core';
import { Injectable, NgModule } from '@angular/core';

class ComponentFactoryService {
    constructor(resolver) {
        this.resolver = resolver;
    }
    createComponent(vcRef, componentType, inputs) {
        this.setComponentFactory(componentType);
        const componentRef = vcRef.createComponent(this.factory);
        if (inputs)
            this.setComponentInputParameters(componentRef, inputs);
        componentRef.changeDetectorRef.detectChanges();
        return componentRef;
    }
    destroyComponent(componentRef) {
        componentRef.destroy();
    }
    setComponentFactory(componentType) {
        this.factory = this.resolver.resolveComponentFactory(componentType);
    }
    setComponentInputParameters(ref, inputs) {
        for (let [key, value] of inputs)
            if (value)
                ref.instance[key] = value;
    }
}
ComponentFactoryService.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: ComponentFactoryService, deps: [{ token: i0.ComponentFactoryResolver }], target: i0.ɵɵFactoryTarget.Injectable });
ComponentFactoryService.ɵprov = i0.ɵɵngDeclareInjectable({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: ComponentFactoryService, providedIn: 'root' });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: ComponentFactoryService, decorators: [{
            type: Injectable,
            args: [{
                    providedIn: 'root'
                }]
        }], ctorParameters: function () { return [{ type: i0.ComponentFactoryResolver }]; } });

class ComponentFactoryServiceModule {
}
ComponentFactoryServiceModule.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: ComponentFactoryServiceModule, deps: [], target: i0.ɵɵFactoryTarget.NgModule });
ComponentFactoryServiceModule.ɵmod = i0.ɵɵngDeclareNgModule({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: ComponentFactoryServiceModule });
ComponentFactoryServiceModule.ɵinj = i0.ɵɵngDeclareInjector({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: ComponentFactoryServiceModule, providers: [ComponentFactoryService], imports: [[]] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: ComponentFactoryServiceModule, decorators: [{
            type: NgModule,
            args: [{
                    declarations: [],
                    imports: [],
                    providers: [ComponentFactoryService]
                }]
        }] });

/*
 * Public API Surface of component-factory-service
 */

/**
 * Generated bundle index. Do not edit.
 */

export { ComponentFactoryService, ComponentFactoryServiceModule };
//# sourceMappingURL=smartbit4all-component-factory-service.mjs.map
