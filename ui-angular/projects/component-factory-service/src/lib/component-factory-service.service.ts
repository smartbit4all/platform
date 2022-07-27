import { ComponentFactory, ComponentFactoryResolver, ComponentRef, Injectable, ViewContainerRef } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ComponentFactoryService {

  factory?: ComponentFactory<any>;

  constructor(private resolver: ComponentFactoryResolver) { }

  createComponent(vcRef: ViewContainerRef, componentType: any, inputs?: Map<string, any>): ComponentRef<any> {
    this.setComponentFactory(componentType);
    const componentRef = vcRef.createComponent(this.factory!);
    if (inputs) this.setComponentInputParameters(componentRef, inputs);
    componentRef.changeDetectorRef.detectChanges();
    return componentRef;
  }

  destroyComponent(componentRef: ComponentRef<any>) {
    componentRef.destroy();
  }

  private setComponentFactory(componentType: any) {
    this.factory = this.resolver.resolveComponentFactory(componentType);
  }

  private setComponentInputParameters(ref: ComponentRef<any>, inputs: Map<string, any>) {
    for (let [key, value] of inputs)
      if (value) ref.instance[key] = value
  }
}
