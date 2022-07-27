import { ComponentFactory, ComponentFactoryResolver, ComponentRef, ViewContainerRef } from '@angular/core';
import * as i0 from "@angular/core";
export declare class ComponentFactoryService {
    private resolver;
    factory?: ComponentFactory<any>;
    constructor(resolver: ComponentFactoryResolver);
    createComponent(vcRef: ViewContainerRef, componentType: any, inputs?: Map<string, any>): ComponentRef<any>;
    destroyComponent(componentRef: ComponentRef<any>): void;
    private setComponentFactory;
    private setComponentInputParameters;
    static ɵfac: i0.ɵɵFactoryDeclaration<ComponentFactoryService, never>;
    static ɵprov: i0.ɵɵInjectableDeclaration<ComponentFactoryService>;
}
