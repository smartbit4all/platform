import { Component, Input, ViewChild, ViewContainerRef, ViewEncapsulation, } from "@angular/core";
import * as i0 from "@angular/core";
import * as i1 from "@smartbit4all/component-factory-service";
import * as i2 from "@angular/material/expansion";
export class ExpandableSectionComponent {
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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiZXhwYW5kYWJsZS1zZWN0aW9uLmNvbXBvbmVudC5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uLy4uLy4uL3Byb2plY3RzL3NtYXJ0LWV4cGFuZGFibGUtc2VjdGlvbi9zcmMvbGliL2V4cGFuZGFibGUtc2VjdGlvbi5jb21wb25lbnQudHMiLCIuLi8uLi8uLi8uLi9wcm9qZWN0cy9zbWFydC1leHBhbmRhYmxlLXNlY3Rpb24vc3JjL2xpYi9leHBhbmRhYmxlLXNlY3Rpb24uY29tcG9uZW50Lmh0bWwiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUEsT0FBTyxFQUNILFNBQVMsRUFDVCxLQUFLLEVBRUwsU0FBUyxFQUNULGdCQUFnQixFQUNoQixpQkFBaUIsR0FDcEIsTUFBTSxlQUFlLENBQUM7Ozs7QUFVdkIsTUFBTSxPQUFPLDBCQUEwQjtJQU1uQyxZQUFvQixTQUFrQztRQUFsQyxjQUFTLEdBQVQsU0FBUyxDQUF5QjtJQUFHLENBQUM7SUFFMUQsUUFBUSxLQUFVLENBQUM7SUFFbkIsZUFBZTtRQUNYLElBQUksQ0FBQyxTQUFTLENBQUMsZUFBZSxDQUMxQixJQUFJLENBQUMsS0FBTSxFQUNYLElBQUksQ0FBQyxJQUFJLENBQUMsZUFBZSxFQUN6QixJQUFJLEdBQUcsQ0FBYyxDQUFDLENBQUMsSUFBSSxDQUFDLElBQUksQ0FBQyxTQUFTLElBQUksRUFBRSxFQUFFLElBQUksQ0FBQyxJQUFJLENBQUMsSUFBSSxDQUFDLENBQUMsQ0FBQyxDQUN0RSxDQUFDO0lBQ04sQ0FBQzs7dUhBaEJRLDBCQUEwQjsyR0FBMUIsMEJBQTBCLCtLQUdHLGdCQUFnQiw2QkNwQjFELDZUQVFBOzJGRFNhLDBCQUEwQjtrQkFOdEMsU0FBUzsrQkFDSSwwQkFBMEIsaUJBR3JCLGlCQUFpQixDQUFDLElBQUk7OEdBRzVCLElBQUk7c0JBQVosS0FBSztnQkFHTixLQUFLO3NCQURKLFNBQVM7dUJBQUMsaUJBQWlCLEVBQUUsRUFBRSxJQUFJLEVBQUUsZ0JBQWdCLEVBQUUiLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQge1xuICAgIENvbXBvbmVudCxcbiAgICBJbnB1dCxcbiAgICBPbkluaXQsXG4gICAgVmlld0NoaWxkLFxuICAgIFZpZXdDb250YWluZXJSZWYsXG4gICAgVmlld0VuY2Fwc3VsYXRpb24sXG59IGZyb20gXCJAYW5ndWxhci9jb3JlXCI7XG5pbXBvcnQgeyBDb21wb25lbnRGYWN0b3J5U2VydmljZSB9IGZyb20gXCJAc21hcnRiaXQ0YWxsL2NvbXBvbmVudC1mYWN0b3J5LXNlcnZpY2VcIjtcbmltcG9ydCB7IEV4cGFuZGFibGVTZWN0aW9uIH0gZnJvbSBcIi4vZXhwYW5kYWJsZS1zZWN0aW9uLm1vZGVsXCI7XG5cbkBDb21wb25lbnQoe1xuICAgIHNlbGVjdG9yOiBcInNtYXJ0LWV4cGFuZGFibGUtc2VjdGlvblwiLFxuICAgIHRlbXBsYXRlVXJsOiBcIi4vZXhwYW5kYWJsZS1zZWN0aW9uLmNvbXBvbmVudC5odG1sXCIsXG4gICAgc3R5bGVVcmxzOiBbXCIuL2V4cGFuZGFibGUtc2VjdGlvbi5jb21wb25lbnQuY3NzXCJdLFxuICAgIGVuY2Fwc3VsYXRpb246IFZpZXdFbmNhcHN1bGF0aW9uLk5vbmUsXG59KVxuZXhwb3J0IGNsYXNzIEV4cGFuZGFibGVTZWN0aW9uQ29tcG9uZW50IGltcGxlbWVudHMgT25Jbml0IHtcbiAgICBASW5wdXQoKSBkYXRhITogRXhwYW5kYWJsZVNlY3Rpb248YW55PjtcblxuICAgIEBWaWV3Q2hpbGQoXCJyZW5kZXJDb21wb25lbnRcIiwgeyByZWFkOiBWaWV3Q29udGFpbmVyUmVmIH0pXG4gICAgdmNSZWY/OiBWaWV3Q29udGFpbmVyUmVmO1xuXG4gICAgY29uc3RydWN0b3IocHJpdmF0ZSBjZlNlcnZpY2U6IENvbXBvbmVudEZhY3RvcnlTZXJ2aWNlKSB7fVxuXG4gICAgbmdPbkluaXQoKTogdm9pZCB7fVxuXG4gICAgbmdBZnRlclZpZXdJbml0KCkge1xuICAgICAgICB0aGlzLmNmU2VydmljZS5jcmVhdGVDb21wb25lbnQoXG4gICAgICAgICAgICB0aGlzLnZjUmVmISxcbiAgICAgICAgICAgIHRoaXMuZGF0YS5jdXN0b21Db21wb25lbnQsXG4gICAgICAgICAgICBuZXcgTWFwPHN0cmluZywgYW55PihbW3RoaXMuZGF0YS5pbnB1dE5hbWUgPz8gXCJcIiwgdGhpcy5kYXRhLmRhdGFdXSlcbiAgICAgICAgKTtcbiAgICB9XG59XG4iLCI8ZGl2IGNsYXNzPVwic2VjdGlvbi1jb250YWluZXJcIj5cblx0PG1hdC1leHBhbnNpb24tcGFuZWwgKG9wZW5lZCk9XCIodHJ1ZSlcIiAoY2xvc2VkKT1cIihmYWxzZSlcIj5cblx0XHQ8bWF0LWV4cGFuc2lvbi1wYW5lbC1oZWFkZXI+XG5cdFx0XHQ8bWF0LXBhbmVsLXRpdGxlPiB7eyBkYXRhLnRpdGxlIH19IDwvbWF0LXBhbmVsLXRpdGxlPlxuXHRcdDwvbWF0LWV4cGFuc2lvbi1wYW5lbC1oZWFkZXI+XG5cdFx0PG5nLXRlbXBsYXRlICNyZW5kZXJDb21wb25lbnQ+PC9uZy10ZW1wbGF0ZT5cblx0PC9tYXQtZXhwYW5zaW9uLXBhbmVsPlxuPC9kaXY+XG4iXX0=