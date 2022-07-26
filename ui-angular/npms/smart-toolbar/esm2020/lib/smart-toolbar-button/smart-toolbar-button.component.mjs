import { Component, Input } from '@angular/core';
import * as i0 from "@angular/core";
import * as i1 from "@angular/common";
export class SmartToolbarButtonComponent {
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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnQtdG9vbGJhci1idXR0b24uY29tcG9uZW50LmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vLi4vLi4vcHJvamVjdHMvc21hcnQtdG9vbGJhci9zcmMvbGliL3NtYXJ0LXRvb2xiYXItYnV0dG9uL3NtYXJ0LXRvb2xiYXItYnV0dG9uLmNvbXBvbmVudC50cyIsIi4uLy4uLy4uLy4uLy4uL3Byb2plY3RzL3NtYXJ0LXRvb2xiYXIvc3JjL2xpYi9zbWFydC10b29sYmFyLWJ1dHRvbi9zbWFydC10b29sYmFyLWJ1dHRvbi5jb21wb25lbnQuaHRtbCJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQSxPQUFPLEVBQUUsU0FBUyxFQUFFLEtBQUssRUFBVSxNQUFNLGVBQWUsQ0FBQzs7O0FBUXpELE1BQU0sT0FBTywyQkFBMkI7SUFHdEMsa0JBQWtCO0lBQ2xCLGlCQUFpQjtJQUNqQix3QkFBd0I7SUFDeEIsa0JBQWtCO0lBRWxCO0lBQ0EsQ0FBQztJQUVELFFBQVE7UUFDTixPQUFPLENBQUMsR0FBRyxDQUFDLElBQUksQ0FBQyxNQUFNLENBQUMsQ0FBQztJQUMzQixDQUFDOzt3SEFiVSwyQkFBMkI7NEdBQTNCLDJCQUEyQiwwRkNSeEMseU1BS0E7MkZER2EsMkJBQTJCO2tCQUx2QyxTQUFTOytCQUNFLHNCQUFzQjswRUFLdkIsTUFBTTtzQkFBZCxLQUFLIiwic291cmNlc0NvbnRlbnQiOlsiaW1wb3J0IHsgQ29tcG9uZW50LCBJbnB1dCwgT25Jbml0IH0gZnJvbSAnQGFuZ3VsYXIvY29yZSc7XG5pbXBvcnQgeyBTbWFydFRvb2xiYXJCdXR0b24gfSBmcm9tICcuL3NtYXJ0LXRvb2xiYXItYnV0dG9uLm1vZGVsJztcblxuQENvbXBvbmVudCh7XG4gIHNlbGVjdG9yOiAnc21hcnQtdG9vbGJhci1idXR0b24nLFxuICB0ZW1wbGF0ZVVybDogJy4vc21hcnQtdG9vbGJhci1idXR0b24uY29tcG9uZW50Lmh0bWwnLFxuICBzdHlsZVVybHM6IFsnLi9zbWFydC10b29sYmFyLWJ1dHRvbi5jb21wb25lbnQuY3NzJ11cbn0pXG5leHBvcnQgY2xhc3MgU21hcnRUb29sYmFyQnV0dG9uQ29tcG9uZW50IGltcGxlbWVudHMgT25Jbml0IHtcbiAgQElucHV0KCkgYnV0dG9uITogU21hcnRUb29sYmFyQnV0dG9uO1xuXG4gIC8vIGxhYmVsITogc3RyaW5nO1xuICAvLyBpY29uPzogc3RyaW5nO1xuICAvLyBidG5BY3Rpb24hOiBGdW5jdGlvbjtcbiAgLy8gYnRuU3R5bGU/OiBhbnk7XG5cbiAgY29uc3RydWN0b3IoKSB7XG4gIH1cblxuICBuZ09uSW5pdCgpOiB2b2lkIHtcbiAgICBjb25zb2xlLmxvZyh0aGlzLmJ1dHRvbik7XG4gIH1cblxufVxuIiwiPGRpdiBjbGFzcz1cImJ1dHRvblwiPlxuXHQ8YnV0dG9uIG1hdC1yYWlzZWQtYnV0dG9uIChjbGljayk9XCJidXR0b24uYnRuQWN0aW9uKClcIiBbbmdTdHlsZV09XCJidXR0b24uYnRuU3R5bGVcIj5cblx0XHR7eyBidXR0b24ubGFiZWwgfX0gPG1hdC1pY29uPnt7IGJ1dHRvbi5pY29uIH19PC9tYXQtaWNvbj5cblx0PC9idXR0b24+XG48L2Rpdj5cbiJdfQ==