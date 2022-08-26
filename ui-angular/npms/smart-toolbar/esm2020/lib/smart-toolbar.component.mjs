import { Component, Input } from "@angular/core";
import { ToolbarButtonStyle, ToolbarDirection } from "./smart-toolbar.model";
import * as i0 from "@angular/core";
import * as i1 from "@angular/material/button";
import * as i2 from "@angular/material/icon";
import * as i3 from "@angular/common";
export class SmartToolbarComponent {
    constructor() {
        this.toolbarDirection = ToolbarDirection;
        this.ToolbarButtonStyle = ToolbarButtonStyle;
    }
    ngOnInit() { }
}
SmartToolbarComponent.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarComponent, deps: [], target: i0.ɵɵFactoryTarget.Component });
SmartToolbarComponent.ɵcmp = i0.ɵɵngDeclareComponent({ minVersion: "12.0.0", version: "13.2.7", type: SmartToolbarComponent, selector: "smart-toolbar", inputs: { toolbar: "toolbar" }, ngImport: i0, template: "<div\n  class=\"button-container\"\n  [ngClass]=\"toolbar.direction === toolbarDirection.ROW ? 'row' : 'col'\"\n>\n  <div *ngFor=\"let button of toolbar.buttons\">\n    <!-- MAT RAISED BUTTON -->\n    <button\n      *ngIf=\"button.style === ToolbarButtonStyle.MAT_RAISED_BUTTON\"\n      mat-raised-button\n      [ngClass]=\"{ 'btn-round': button.rounded }\"\n      color=\"{{ button.color }}\"\n      [disabled]=\"button.disabled\"\n      (click)=\"button.btnAction.execute()\"\n    >\n      {{ button.label }} <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n    <!-- MAT BUTTON -->\n    <button\n      *ngIf=\"button.style === ToolbarButtonStyle.MAT_BUTTON\"\n      mat-button\n      [ngClass]=\"{ 'btn-round': button.rounded }\"\n      color=\"{{ button.color }}\"\n      [disabled]=\"button.disabled\"\n      (click)=\"button.btnAction.execute()\"\n    >\n      {{ button.label }} <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n\n    <!-- MAT STROKED BUTTON -->\n    <button\n      *ngIf=\"button.style === ToolbarButtonStyle.MAT_STROKED_BUTTON\"\n      mat-stroked-button\n      [ngClass]=\"{ 'btn-round': button.rounded }\"\n      color=\"{{ button.color }}\"\n      [disabled]=\"button.disabled\"\n      (click)=\"button.btnAction.execute()\"\n    >\n      {{ button.label }} <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n\n    <!-- MAT FLAT BUTTON -->\n    <button\n      *ngIf=\"button.style === ToolbarButtonStyle.MAT_FLAT_BUTTON\"\n      mat-flat-button\n      [ngClass]=\"{ 'btn-round': button.rounded }\"\n      color=\"{{ button.color }}\"\n      [disabled]=\"button.disabled\"\n      (click)=\"button.btnAction.execute()\"\n    >\n      {{ button.label }} <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n\n    <!-- MAT ICON BUTTON -->\n    <button\n      *ngIf=\"button.style === ToolbarButtonStyle.MAT_ICON_BUTTON\"\n      mat-icon-button\n      color=\"{{ button.color }}\"\n      [disabled]=\"button.disabled\"\n      (click)=\"button.btnAction.execute()\"\n    >\n      <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n\n    <!-- MAT FAB -->\n    <button\n      *ngIf=\"button.style === ToolbarButtonStyle.MAT_FAB\"\n      mat-fab\n      color=\"{{ button.color }}\"\n      [disabled]=\"button.disabled\"\n      (click)=\"button.btnAction.execute()\"\n    >\n      <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n\n    <!-- MAT MINI FAB -->\n    <button\n      *ngIf=\"button.style === ToolbarButtonStyle.MAT_MINI_FAB\"\n      mat-mini-fab\n      color=\"{{ button.color }}\"\n      [disabled]=\"button.disabled\"\n      (click)=\"button.btnAction.execute()\"\n    >\n      <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n  </div>\n</div>\n", styles: [".button-container{background-color:transparent}.col{display:flex;flex-direction:column;padding:1em}.row{display:flex;flex-wrap:row;padding:1em}.button-container button{margin:1em}.btn-round{border-radius:25px}\n"], components: [{ type: i1.MatButton, selector: "button[mat-button], button[mat-raised-button], button[mat-icon-button],             button[mat-fab], button[mat-mini-fab], button[mat-stroked-button],             button[mat-flat-button]", inputs: ["disabled", "disableRipple", "color"], exportAs: ["matButton"] }, { type: i2.MatIcon, selector: "mat-icon", inputs: ["color", "inline", "svgIcon", "fontSet", "fontIcon"], exportAs: ["matIcon"] }], directives: [{ type: i3.NgClass, selector: "[ngClass]", inputs: ["class", "ngClass"] }, { type: i3.NgForOf, selector: "[ngFor][ngForOf]", inputs: ["ngForOf", "ngForTrackBy", "ngForTemplate"] }, { type: i3.NgIf, selector: "[ngIf]", inputs: ["ngIf", "ngIfThen", "ngIfElse"] }] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarComponent, decorators: [{
            type: Component,
            args: [{ selector: "smart-toolbar", template: "<div\n  class=\"button-container\"\n  [ngClass]=\"toolbar.direction === toolbarDirection.ROW ? 'row' : 'col'\"\n>\n  <div *ngFor=\"let button of toolbar.buttons\">\n    <!-- MAT RAISED BUTTON -->\n    <button\n      *ngIf=\"button.style === ToolbarButtonStyle.MAT_RAISED_BUTTON\"\n      mat-raised-button\n      [ngClass]=\"{ 'btn-round': button.rounded }\"\n      color=\"{{ button.color }}\"\n      [disabled]=\"button.disabled\"\n      (click)=\"button.btnAction.execute()\"\n    >\n      {{ button.label }} <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n    <!-- MAT BUTTON -->\n    <button\n      *ngIf=\"button.style === ToolbarButtonStyle.MAT_BUTTON\"\n      mat-button\n      [ngClass]=\"{ 'btn-round': button.rounded }\"\n      color=\"{{ button.color }}\"\n      [disabled]=\"button.disabled\"\n      (click)=\"button.btnAction.execute()\"\n    >\n      {{ button.label }} <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n\n    <!-- MAT STROKED BUTTON -->\n    <button\n      *ngIf=\"button.style === ToolbarButtonStyle.MAT_STROKED_BUTTON\"\n      mat-stroked-button\n      [ngClass]=\"{ 'btn-round': button.rounded }\"\n      color=\"{{ button.color }}\"\n      [disabled]=\"button.disabled\"\n      (click)=\"button.btnAction.execute()\"\n    >\n      {{ button.label }} <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n\n    <!-- MAT FLAT BUTTON -->\n    <button\n      *ngIf=\"button.style === ToolbarButtonStyle.MAT_FLAT_BUTTON\"\n      mat-flat-button\n      [ngClass]=\"{ 'btn-round': button.rounded }\"\n      color=\"{{ button.color }}\"\n      [disabled]=\"button.disabled\"\n      (click)=\"button.btnAction.execute()\"\n    >\n      {{ button.label }} <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n\n    <!-- MAT ICON BUTTON -->\n    <button\n      *ngIf=\"button.style === ToolbarButtonStyle.MAT_ICON_BUTTON\"\n      mat-icon-button\n      color=\"{{ button.color }}\"\n      [disabled]=\"button.disabled\"\n      (click)=\"button.btnAction.execute()\"\n    >\n      <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n\n    <!-- MAT FAB -->\n    <button\n      *ngIf=\"button.style === ToolbarButtonStyle.MAT_FAB\"\n      mat-fab\n      color=\"{{ button.color }}\"\n      [disabled]=\"button.disabled\"\n      (click)=\"button.btnAction.execute()\"\n    >\n      <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n\n    <!-- MAT MINI FAB -->\n    <button\n      *ngIf=\"button.style === ToolbarButtonStyle.MAT_MINI_FAB\"\n      mat-mini-fab\n      color=\"{{ button.color }}\"\n      [disabled]=\"button.disabled\"\n      (click)=\"button.btnAction.execute()\"\n    >\n      <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n  </div>\n</div>\n", styles: [".button-container{background-color:transparent}.col{display:flex;flex-direction:column;padding:1em}.row{display:flex;flex-wrap:row;padding:1em}.button-container button{margin:1em}.btn-round{border-radius:25px}\n"] }]
        }], ctorParameters: function () { return []; }, propDecorators: { toolbar: [{
                type: Input
            }] } });
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnQtdG9vbGJhci5jb21wb25lbnQuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi8uLi9wcm9qZWN0cy9zbWFydC10b29sYmFyL3NyYy9saWIvc21hcnQtdG9vbGJhci5jb21wb25lbnQudHMiLCIuLi8uLi8uLi8uLi9wcm9qZWN0cy9zbWFydC10b29sYmFyL3NyYy9saWIvc21hcnQtdG9vbGJhci5jb21wb25lbnQuaHRtbCJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQSxPQUFPLEVBQ0gsU0FBUyxFQUNULEtBQUssRUFFUixNQUFNLGVBQWUsQ0FBQztBQUN2QixPQUFPLEVBQWdCLGtCQUFrQixFQUFFLGdCQUFnQixFQUFFLE1BQU0sdUJBQXVCLENBQUM7Ozs7O0FBTzNGLE1BQU0sT0FBTyxxQkFBcUI7SUFLOUI7UUFIQSxxQkFBZ0IsR0FBRyxnQkFBZ0IsQ0FBQztRQUNwQyx1QkFBa0IsR0FBRyxrQkFBa0IsQ0FBQztJQUV4QixDQUFDO0lBRWpCLFFBQVEsS0FBVyxDQUFDOztrSEFQWCxxQkFBcUI7c0dBQXJCLHFCQUFxQixxRkNabEMsdXBGQXNGQTsyRkQxRWEscUJBQXFCO2tCQUxqQyxTQUFTOytCQUNJLGVBQWU7MEVBS2hCLE9BQU87c0JBQWYsS0FBSyIsInNvdXJjZXNDb250ZW50IjpbImltcG9ydCB7XHJcbiAgICBDb21wb25lbnQsXHJcbiAgICBJbnB1dCxcclxuICAgIE9uSW5pdFxyXG59IGZyb20gXCJAYW5ndWxhci9jb3JlXCI7XHJcbmltcG9ydCB7IFNtYXJ0VG9vbGJhciwgVG9vbGJhckJ1dHRvblN0eWxlLCBUb29sYmFyRGlyZWN0aW9uIH0gZnJvbSBcIi4vc21hcnQtdG9vbGJhci5tb2RlbFwiO1xyXG5cclxuQENvbXBvbmVudCh7XHJcbiAgICBzZWxlY3RvcjogXCJzbWFydC10b29sYmFyXCIsXHJcbiAgICB0ZW1wbGF0ZVVybDogXCIuL3NtYXJ0LXRvb2xiYXIuY29tcG9uZW50Lmh0bWxcIixcclxuICAgIHN0eWxlVXJsczogW1wiLi9zbWFydC10b29sYmFyLmNvbXBvbmVudC5jc3NcIl0sXHJcbn0pXHJcbmV4cG9ydCBjbGFzcyBTbWFydFRvb2xiYXJDb21wb25lbnQgaW1wbGVtZW50cyBPbkluaXQge1xyXG4gICAgQElucHV0KCkgdG9vbGJhciE6IFNtYXJ0VG9vbGJhcjtcclxuICAgIHRvb2xiYXJEaXJlY3Rpb24gPSBUb29sYmFyRGlyZWN0aW9uO1xyXG4gICAgVG9vbGJhckJ1dHRvblN0eWxlID0gVG9vbGJhckJ1dHRvblN0eWxlO1xyXG5cclxuICAgIGNvbnN0cnVjdG9yKCkgeyB9XHJcblxyXG4gICAgbmdPbkluaXQoKTogdm9pZCB7IH1cclxufVxyXG4iLCI8ZGl2XG4gIGNsYXNzPVwiYnV0dG9uLWNvbnRhaW5lclwiXG4gIFtuZ0NsYXNzXT1cInRvb2xiYXIuZGlyZWN0aW9uID09PSB0b29sYmFyRGlyZWN0aW9uLlJPVyA/ICdyb3cnIDogJ2NvbCdcIlxuPlxuICA8ZGl2ICpuZ0Zvcj1cImxldCBidXR0b24gb2YgdG9vbGJhci5idXR0b25zXCI+XG4gICAgPCEtLSBNQVQgUkFJU0VEIEJVVFRPTiAtLT5cbiAgICA8YnV0dG9uXG4gICAgICAqbmdJZj1cImJ1dHRvbi5zdHlsZSA9PT0gVG9vbGJhckJ1dHRvblN0eWxlLk1BVF9SQUlTRURfQlVUVE9OXCJcbiAgICAgIG1hdC1yYWlzZWQtYnV0dG9uXG4gICAgICBbbmdDbGFzc109XCJ7ICdidG4tcm91bmQnOiBidXR0b24ucm91bmRlZCB9XCJcbiAgICAgIGNvbG9yPVwie3sgYnV0dG9uLmNvbG9yIH19XCJcbiAgICAgIFtkaXNhYmxlZF09XCJidXR0b24uZGlzYWJsZWRcIlxuICAgICAgKGNsaWNrKT1cImJ1dHRvbi5idG5BY3Rpb24uZXhlY3V0ZSgpXCJcbiAgICA+XG4gICAgICB7eyBidXR0b24ubGFiZWwgfX0gPG1hdC1pY29uPnt7IGJ1dHRvbi5pY29uIH19PC9tYXQtaWNvbj5cbiAgICA8L2J1dHRvbj5cbiAgICA8IS0tIE1BVCBCVVRUT04gLS0+XG4gICAgPGJ1dHRvblxuICAgICAgKm5nSWY9XCJidXR0b24uc3R5bGUgPT09IFRvb2xiYXJCdXR0b25TdHlsZS5NQVRfQlVUVE9OXCJcbiAgICAgIG1hdC1idXR0b25cbiAgICAgIFtuZ0NsYXNzXT1cInsgJ2J0bi1yb3VuZCc6IGJ1dHRvbi5yb3VuZGVkIH1cIlxuICAgICAgY29sb3I9XCJ7eyBidXR0b24uY29sb3IgfX1cIlxuICAgICAgW2Rpc2FibGVkXT1cImJ1dHRvbi5kaXNhYmxlZFwiXG4gICAgICAoY2xpY2spPVwiYnV0dG9uLmJ0bkFjdGlvbi5leGVjdXRlKClcIlxuICAgID5cbiAgICAgIHt7IGJ1dHRvbi5sYWJlbCB9fSA8bWF0LWljb24+e3sgYnV0dG9uLmljb24gfX08L21hdC1pY29uPlxuICAgIDwvYnV0dG9uPlxuXG4gICAgPCEtLSBNQVQgU1RST0tFRCBCVVRUT04gLS0+XG4gICAgPGJ1dHRvblxuICAgICAgKm5nSWY9XCJidXR0b24uc3R5bGUgPT09IFRvb2xiYXJCdXR0b25TdHlsZS5NQVRfU1RST0tFRF9CVVRUT05cIlxuICAgICAgbWF0LXN0cm9rZWQtYnV0dG9uXG4gICAgICBbbmdDbGFzc109XCJ7ICdidG4tcm91bmQnOiBidXR0b24ucm91bmRlZCB9XCJcbiAgICAgIGNvbG9yPVwie3sgYnV0dG9uLmNvbG9yIH19XCJcbiAgICAgIFtkaXNhYmxlZF09XCJidXR0b24uZGlzYWJsZWRcIlxuICAgICAgKGNsaWNrKT1cImJ1dHRvbi5idG5BY3Rpb24uZXhlY3V0ZSgpXCJcbiAgICA+XG4gICAgICB7eyBidXR0b24ubGFiZWwgfX0gPG1hdC1pY29uPnt7IGJ1dHRvbi5pY29uIH19PC9tYXQtaWNvbj5cbiAgICA8L2J1dHRvbj5cblxuICAgIDwhLS0gTUFUIEZMQVQgQlVUVE9OIC0tPlxuICAgIDxidXR0b25cbiAgICAgICpuZ0lmPVwiYnV0dG9uLnN0eWxlID09PSBUb29sYmFyQnV0dG9uU3R5bGUuTUFUX0ZMQVRfQlVUVE9OXCJcbiAgICAgIG1hdC1mbGF0LWJ1dHRvblxuICAgICAgW25nQ2xhc3NdPVwieyAnYnRuLXJvdW5kJzogYnV0dG9uLnJvdW5kZWQgfVwiXG4gICAgICBjb2xvcj1cInt7IGJ1dHRvbi5jb2xvciB9fVwiXG4gICAgICBbZGlzYWJsZWRdPVwiYnV0dG9uLmRpc2FibGVkXCJcbiAgICAgIChjbGljayk9XCJidXR0b24uYnRuQWN0aW9uLmV4ZWN1dGUoKVwiXG4gICAgPlxuICAgICAge3sgYnV0dG9uLmxhYmVsIH19IDxtYXQtaWNvbj57eyBidXR0b24uaWNvbiB9fTwvbWF0LWljb24+XG4gICAgPC9idXR0b24+XG5cbiAgICA8IS0tIE1BVCBJQ09OIEJVVFRPTiAtLT5cbiAgICA8YnV0dG9uXG4gICAgICAqbmdJZj1cImJ1dHRvbi5zdHlsZSA9PT0gVG9vbGJhckJ1dHRvblN0eWxlLk1BVF9JQ09OX0JVVFRPTlwiXG4gICAgICBtYXQtaWNvbi1idXR0b25cbiAgICAgIGNvbG9yPVwie3sgYnV0dG9uLmNvbG9yIH19XCJcbiAgICAgIFtkaXNhYmxlZF09XCJidXR0b24uZGlzYWJsZWRcIlxuICAgICAgKGNsaWNrKT1cImJ1dHRvbi5idG5BY3Rpb24uZXhlY3V0ZSgpXCJcbiAgICA+XG4gICAgICA8bWF0LWljb24+e3sgYnV0dG9uLmljb24gfX08L21hdC1pY29uPlxuICAgIDwvYnV0dG9uPlxuXG4gICAgPCEtLSBNQVQgRkFCIC0tPlxuICAgIDxidXR0b25cbiAgICAgICpuZ0lmPVwiYnV0dG9uLnN0eWxlID09PSBUb29sYmFyQnV0dG9uU3R5bGUuTUFUX0ZBQlwiXG4gICAgICBtYXQtZmFiXG4gICAgICBjb2xvcj1cInt7IGJ1dHRvbi5jb2xvciB9fVwiXG4gICAgICBbZGlzYWJsZWRdPVwiYnV0dG9uLmRpc2FibGVkXCJcbiAgICAgIChjbGljayk9XCJidXR0b24uYnRuQWN0aW9uLmV4ZWN1dGUoKVwiXG4gICAgPlxuICAgICAgPG1hdC1pY29uPnt7IGJ1dHRvbi5pY29uIH19PC9tYXQtaWNvbj5cbiAgICA8L2J1dHRvbj5cblxuICAgIDwhLS0gTUFUIE1JTkkgRkFCIC0tPlxuICAgIDxidXR0b25cbiAgICAgICpuZ0lmPVwiYnV0dG9uLnN0eWxlID09PSBUb29sYmFyQnV0dG9uU3R5bGUuTUFUX01JTklfRkFCXCJcbiAgICAgIG1hdC1taW5pLWZhYlxuICAgICAgY29sb3I9XCJ7eyBidXR0b24uY29sb3IgfX1cIlxuICAgICAgW2Rpc2FibGVkXT1cImJ1dHRvbi5kaXNhYmxlZFwiXG4gICAgICAoY2xpY2spPVwiYnV0dG9uLmJ0bkFjdGlvbi5leGVjdXRlKClcIlxuICAgID5cbiAgICAgIDxtYXQtaWNvbj57eyBidXR0b24uaWNvbiB9fTwvbWF0LWljb24+XG4gICAgPC9idXR0b24+XG4gIDwvZGl2PlxuPC9kaXY+XG4iXX0=