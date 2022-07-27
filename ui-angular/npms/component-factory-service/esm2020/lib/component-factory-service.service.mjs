import { Injectable } from '@angular/core';
import * as i0 from "@angular/core";
export class ComponentFactoryService {
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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiY29tcG9uZW50LWZhY3Rvcnktc2VydmljZS5zZXJ2aWNlLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vLi4vcHJvamVjdHMvY29tcG9uZW50LWZhY3Rvcnktc2VydmljZS9zcmMvbGliL2NvbXBvbmVudC1mYWN0b3J5LXNlcnZpY2Uuc2VydmljZS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQSxPQUFPLEVBQTRELFVBQVUsRUFBb0IsTUFBTSxlQUFlLENBQUM7O0FBS3ZILE1BQU0sT0FBTyx1QkFBdUI7SUFJbEMsWUFBb0IsUUFBa0M7UUFBbEMsYUFBUSxHQUFSLFFBQVEsQ0FBMEI7SUFBSSxDQUFDO0lBRTNELGVBQWUsQ0FBQyxLQUF1QixFQUFFLGFBQWtCLEVBQUUsTUFBeUI7UUFDcEYsSUFBSSxDQUFDLG1CQUFtQixDQUFDLGFBQWEsQ0FBQyxDQUFDO1FBQ3hDLE1BQU0sWUFBWSxHQUFHLEtBQUssQ0FBQyxlQUFlLENBQUMsSUFBSSxDQUFDLE9BQVEsQ0FBQyxDQUFDO1FBQzFELElBQUksTUFBTTtZQUFFLElBQUksQ0FBQywyQkFBMkIsQ0FBQyxZQUFZLEVBQUUsTUFBTSxDQUFDLENBQUM7UUFDbkUsWUFBWSxDQUFDLGlCQUFpQixDQUFDLGFBQWEsRUFBRSxDQUFDO1FBQy9DLE9BQU8sWUFBWSxDQUFDO0lBQ3RCLENBQUM7SUFFRCxnQkFBZ0IsQ0FBQyxZQUErQjtRQUM5QyxZQUFZLENBQUMsT0FBTyxFQUFFLENBQUM7SUFDekIsQ0FBQztJQUVPLG1CQUFtQixDQUFDLGFBQWtCO1FBQzVDLElBQUksQ0FBQyxPQUFPLEdBQUcsSUFBSSxDQUFDLFFBQVEsQ0FBQyx1QkFBdUIsQ0FBQyxhQUFhLENBQUMsQ0FBQztJQUN0RSxDQUFDO0lBRU8sMkJBQTJCLENBQUMsR0FBc0IsRUFBRSxNQUF3QjtRQUNsRixLQUFLLElBQUksQ0FBQyxHQUFHLEVBQUUsS0FBSyxDQUFDLElBQUksTUFBTTtZQUM3QixJQUFJLEtBQUs7Z0JBQUUsR0FBRyxDQUFDLFFBQVEsQ0FBQyxHQUFHLENBQUMsR0FBRyxLQUFLLENBQUE7SUFDeEMsQ0FBQzs7b0hBekJVLHVCQUF1Qjt3SEFBdkIsdUJBQXVCLGNBRnRCLE1BQU07MkZBRVAsdUJBQXVCO2tCQUhuQyxVQUFVO21CQUFDO29CQUNWLFVBQVUsRUFBRSxNQUFNO2lCQUNuQiIsInNvdXJjZXNDb250ZW50IjpbImltcG9ydCB7IENvbXBvbmVudEZhY3RvcnksIENvbXBvbmVudEZhY3RvcnlSZXNvbHZlciwgQ29tcG9uZW50UmVmLCBJbmplY3RhYmxlLCBWaWV3Q29udGFpbmVyUmVmIH0gZnJvbSAnQGFuZ3VsYXIvY29yZSc7XG5cbkBJbmplY3RhYmxlKHtcbiAgcHJvdmlkZWRJbjogJ3Jvb3QnXG59KVxuZXhwb3J0IGNsYXNzIENvbXBvbmVudEZhY3RvcnlTZXJ2aWNlIHtcblxuICBmYWN0b3J5PzogQ29tcG9uZW50RmFjdG9yeTxhbnk+O1xuXG4gIGNvbnN0cnVjdG9yKHByaXZhdGUgcmVzb2x2ZXI6IENvbXBvbmVudEZhY3RvcnlSZXNvbHZlcikgeyB9XG5cbiAgY3JlYXRlQ29tcG9uZW50KHZjUmVmOiBWaWV3Q29udGFpbmVyUmVmLCBjb21wb25lbnRUeXBlOiBhbnksIGlucHV0cz86IE1hcDxzdHJpbmcsIGFueT4pOiBDb21wb25lbnRSZWY8YW55PiB7XG4gICAgdGhpcy5zZXRDb21wb25lbnRGYWN0b3J5KGNvbXBvbmVudFR5cGUpO1xuICAgIGNvbnN0IGNvbXBvbmVudFJlZiA9IHZjUmVmLmNyZWF0ZUNvbXBvbmVudCh0aGlzLmZhY3RvcnkhKTtcbiAgICBpZiAoaW5wdXRzKSB0aGlzLnNldENvbXBvbmVudElucHV0UGFyYW1ldGVycyhjb21wb25lbnRSZWYsIGlucHV0cyk7XG4gICAgY29tcG9uZW50UmVmLmNoYW5nZURldGVjdG9yUmVmLmRldGVjdENoYW5nZXMoKTtcbiAgICByZXR1cm4gY29tcG9uZW50UmVmO1xuICB9XG5cbiAgZGVzdHJveUNvbXBvbmVudChjb21wb25lbnRSZWY6IENvbXBvbmVudFJlZjxhbnk+KSB7XG4gICAgY29tcG9uZW50UmVmLmRlc3Ryb3koKTtcbiAgfVxuXG4gIHByaXZhdGUgc2V0Q29tcG9uZW50RmFjdG9yeShjb21wb25lbnRUeXBlOiBhbnkpIHtcbiAgICB0aGlzLmZhY3RvcnkgPSB0aGlzLnJlc29sdmVyLnJlc29sdmVDb21wb25lbnRGYWN0b3J5KGNvbXBvbmVudFR5cGUpO1xuICB9XG5cbiAgcHJpdmF0ZSBzZXRDb21wb25lbnRJbnB1dFBhcmFtZXRlcnMocmVmOiBDb21wb25lbnRSZWY8YW55PiwgaW5wdXRzOiBNYXA8c3RyaW5nLCBhbnk+KSB7XG4gICAgZm9yIChsZXQgW2tleSwgdmFsdWVdIG9mIGlucHV0cylcbiAgICAgIGlmICh2YWx1ZSkgcmVmLmluc3RhbmNlW2tleV0gPSB2YWx1ZVxuICB9XG59XG4iXX0=