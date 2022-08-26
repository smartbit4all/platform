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
    ngOnInit() {}
    executeCommand(button) {
        this.service.executeCommand(button);
    }
}
SmartToolbarComponent.ɵfac = i0.ɵɵngDeclareFactory({
    minVersion: "12.0.0",
    version: "13.2.7",
    ngImport: i0,
    type: SmartToolbarComponent,
    deps: [{ token: i1.SmartToolbarService }],
    target: i0.ɵɵFactoryTarget.Component,
});
SmartToolbarComponent.ɵcmp = i0.ɵɵngDeclareComponent({
    minVersion: "12.0.0",
    version: "13.2.7",
    type: SmartToolbarComponent,
    selector: "smart-toolbar",
    inputs: { toolbar: "toolbar" },
    ngImport: i0,
    template:
        '<div\n  class="button-container"\n  [ngClass]="toolbar.direction === toolbarDirection.ROW ? \'row\' : \'col\'"\n>\n  <div *ngFor="let button of toolbar.buttons">\n    <!-- MAT RAISED BUTTON -->\n    <button\n      *ngIf="button.style === ToolbarButtonStyle.MAT_RAISED_BUTTON"\n      mat-raised-button\n      [ngClass]="{ \'btn-round\': button.rounded }"\n      color="{{ button.color }}"\n      [disabled]="button.disabled"\n      (click)="executeCommand(button)"\n    >\n      {{ button.label }} <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n    <!-- MAT BUTTON -->\n    <button\n      *ngIf="button.style === ToolbarButtonStyle.MAT_BUTTON"\n      mat-button\n      [ngClass]="{ \'btn-round\': button.rounded }"\n      color="{{ button.color }}"\n      [disabled]="button.disabled"\n      (click)="executeCommand(button)"\n    >\n      {{ button.label }} <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n\n    <!-- MAT STROKED BUTTON -->\n    <button\n      *ngIf="button.style === ToolbarButtonStyle.MAT_STROKED_BUTTON"\n      mat-stroked-button\n      [ngClass]="{ \'btn-round\': button.rounded }"\n      color="{{ button.color }}"\n      [disabled]="button.disabled"\n      (click)="executeCommand(button)"\n    >\n      {{ button.label }} <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n\n    <!-- MAT FLAT BUTTON -->\n    <button\n      *ngIf="button.style === ToolbarButtonStyle.MAT_FLAT_BUTTON"\n      mat-flat-button\n      [ngClass]="{ \'btn-round\': button.rounded }"\n      color="{{ button.color }}"\n      [disabled]="button.disabled"\n      (click)="executeCommand(button)"\n    >\n      {{ button.label }} <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n\n    <!-- MAT ICON BUTTON -->\n    <button\n      *ngIf="button.style === ToolbarButtonStyle.MAT_ICON_BUTTON"\n      mat-icon-button\n      color="{{ button.color }}"\n      [disabled]="button.disabled"\n      (click)="executeCommand(button)"\n    >\n      <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n\n    <!-- MAT FAB -->\n    <button\n      *ngIf="button.style === ToolbarButtonStyle.MAT_FAB"\n      mat-fab\n      color="{{ button.color }}"\n      [disabled]="button.disabled"\n      (click)="executeCommand(button)"\n    >\n      <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n\n    <!-- MAT MINI FAB -->\n    <button\n      *ngIf="button.style === ToolbarButtonStyle.MAT_MINI_FAB"\n      mat-mini-fab\n      color="{{ button.color }}"\n      [disabled]="button.disabled"\n      (click)="executeCommand(button)"\n    >\n      <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n  </div>\n</div>\n',
    styles: [
        ".button-container{background-color:transparent}.col{display:flex;flex-direction:column;padding:1em}.row{display:flex;flex-wrap:row;padding:1em}.button-container button{margin:1em}.btn-round{border-radius:25px}\n",
    ],
    components: [
        {
            type: i2.MatButton,
            selector:
                "button[mat-button], button[mat-raised-button], button[mat-icon-button],             button[mat-fab], button[mat-mini-fab], button[mat-stroked-button],             button[mat-flat-button]",
            inputs: ["disabled", "disableRipple", "color"],
            exportAs: ["matButton"],
        },
        {
            type: i3.MatIcon,
            selector: "mat-icon",
            inputs: ["color", "inline", "svgIcon", "fontSet", "fontIcon"],
            exportAs: ["matIcon"],
        },
    ],
    directives: [
        { type: i4.NgClass, selector: "[ngClass]", inputs: ["class", "ngClass"] },
        {
            type: i4.NgForOf,
            selector: "[ngFor][ngForOf]",
            inputs: ["ngForOf", "ngForTrackBy", "ngForTemplate"],
        },
        { type: i4.NgIf, selector: "[ngIf]", inputs: ["ngIf", "ngIfThen", "ngIfElse"] },
    ],
});
i0.ɵɵngDeclareClassMetadata({
    minVersion: "12.0.0",
    version: "13.2.7",
    ngImport: i0,
    type: SmartToolbarComponent,
    decorators: [
        {
            type: Component,
            args: [
                {
                    selector: "smart-toolbar",
                    template:
                        '<div\n  class="button-container"\n  [ngClass]="toolbar.direction === toolbarDirection.ROW ? \'row\' : \'col\'"\n>\n  <div *ngFor="let button of toolbar.buttons">\n    <!-- MAT RAISED BUTTON -->\n    <button\n      *ngIf="button.style === ToolbarButtonStyle.MAT_RAISED_BUTTON"\n      mat-raised-button\n      [ngClass]="{ \'btn-round\': button.rounded }"\n      color="{{ button.color }}"\n      [disabled]="button.disabled"\n      (click)="executeCommand(button)"\n    >\n      {{ button.label }} <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n    <!-- MAT BUTTON -->\n    <button\n      *ngIf="button.style === ToolbarButtonStyle.MAT_BUTTON"\n      mat-button\n      [ngClass]="{ \'btn-round\': button.rounded }"\n      color="{{ button.color }}"\n      [disabled]="button.disabled"\n      (click)="executeCommand(button)"\n    >\n      {{ button.label }} <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n\n    <!-- MAT STROKED BUTTON -->\n    <button\n      *ngIf="button.style === ToolbarButtonStyle.MAT_STROKED_BUTTON"\n      mat-stroked-button\n      [ngClass]="{ \'btn-round\': button.rounded }"\n      color="{{ button.color }}"\n      [disabled]="button.disabled"\n      (click)="executeCommand(button)"\n    >\n      {{ button.label }} <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n\n    <!-- MAT FLAT BUTTON -->\n    <button\n      *ngIf="button.style === ToolbarButtonStyle.MAT_FLAT_BUTTON"\n      mat-flat-button\n      [ngClass]="{ \'btn-round\': button.rounded }"\n      color="{{ button.color }}"\n      [disabled]="button.disabled"\n      (click)="executeCommand(button)"\n    >\n      {{ button.label }} <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n\n    <!-- MAT ICON BUTTON -->\n    <button\n      *ngIf="button.style === ToolbarButtonStyle.MAT_ICON_BUTTON"\n      mat-icon-button\n      color="{{ button.color }}"\n      [disabled]="button.disabled"\n      (click)="executeCommand(button)"\n    >\n      <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n\n    <!-- MAT FAB -->\n    <button\n      *ngIf="button.style === ToolbarButtonStyle.MAT_FAB"\n      mat-fab\n      color="{{ button.color }}"\n      [disabled]="button.disabled"\n      (click)="executeCommand(button)"\n    >\n      <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n\n    <!-- MAT MINI FAB -->\n    <button\n      *ngIf="button.style === ToolbarButtonStyle.MAT_MINI_FAB"\n      mat-mini-fab\n      color="{{ button.color }}"\n      [disabled]="button.disabled"\n      (click)="executeCommand(button)"\n    >\n      <mat-icon>{{ button.icon }}</mat-icon>\n    </button>\n  </div>\n</div>\n',
                    styles: [
                        ".button-container{background-color:transparent}.col{display:flex;flex-direction:column;padding:1em}.row{display:flex;flex-wrap:row;padding:1em}.button-container button{margin:1em}.btn-round{border-radius:25px}\n",
                    ],
                },
            ],
        },
    ],
    ctorParameters: function () {
        return [{ type: i1.SmartToolbarService }];
    },
    propDecorators: {
        toolbar: [
            {
                type: Input,
            },
        ],
    },
});
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnQtdG9vbGJhci5jb21wb25lbnQuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi8uLi9wcm9qZWN0cy9zbWFydC10b29sYmFyL3NyYy9saWIvc21hcnQtdG9vbGJhci5jb21wb25lbnQudHMiLCIuLi8uLi8uLi8uLi9wcm9qZWN0cy9zbWFydC10b29sYmFyL3NyYy9saWIvc21hcnQtdG9vbGJhci5jb21wb25lbnQuaHRtbCJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQSxPQUFPLEVBQ0gsU0FBUyxFQUNULEtBQUssRUFFUixNQUFNLGVBQWUsQ0FBQztBQUN2QixPQUFPLEVBQW9DLGtCQUFrQixFQUFFLGdCQUFnQixFQUFFLE1BQU0sdUJBQXVCLENBQUM7Ozs7OztBQVEvRyxNQUFNLE9BQU8scUJBQXFCO0lBSzlCLFlBQW9CLE9BQTRCO1FBQTVCLFlBQU8sR0FBUCxPQUFPLENBQXFCO1FBSGhELHFCQUFnQixHQUFHLGdCQUFnQixDQUFDO1FBQ3BDLHVCQUFrQixHQUFHLGtCQUFrQixDQUFDO0lBRVksQ0FBQztJQUVyRCxRQUFRLEtBQVcsQ0FBQztJQUVwQixjQUFjLENBQUMsTUFBMEI7UUFDckMsSUFBSSxDQUFDLE9BQU8sQ0FBQyxjQUFjLENBQUMsTUFBTSxDQUFDLENBQUM7SUFDeEMsQ0FBQzs7a0hBWFEscUJBQXFCO3NHQUFyQixxQkFBcUIscUZDYmxDLDJuRkFzRkE7MkZEekVhLHFCQUFxQjtrQkFMakMsU0FBUzsrQkFDSSxlQUFlOzBHQUtoQixPQUFPO3NCQUFmLEtBQUsiLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQge1xyXG4gICAgQ29tcG9uZW50LFxyXG4gICAgSW5wdXQsXHJcbiAgICBPbkluaXRcclxufSBmcm9tIFwiQGFuZ3VsYXIvY29yZVwiO1xyXG5pbXBvcnQgeyBTbWFydFRvb2xiYXIsIFNtYXJ0VG9vbGJhckJ1dHRvbiwgVG9vbGJhckJ1dHRvblN0eWxlLCBUb29sYmFyRGlyZWN0aW9uIH0gZnJvbSBcIi4vc21hcnQtdG9vbGJhci5tb2RlbFwiO1xyXG5pbXBvcnQgeyBTbWFydFRvb2xiYXJTZXJ2aWNlIH0gZnJvbSBcIi4vc21hcnQtdG9vbGJhci5zZXJ2aWNlXCI7XHJcblxyXG5AQ29tcG9uZW50KHtcclxuICAgIHNlbGVjdG9yOiBcInNtYXJ0LXRvb2xiYXJcIixcclxuICAgIHRlbXBsYXRlVXJsOiBcIi4vc21hcnQtdG9vbGJhci5jb21wb25lbnQuaHRtbFwiLFxyXG4gICAgc3R5bGVVcmxzOiBbXCIuL3NtYXJ0LXRvb2xiYXIuY29tcG9uZW50LmNzc1wiXSxcclxufSlcclxuZXhwb3J0IGNsYXNzIFNtYXJ0VG9vbGJhckNvbXBvbmVudCBpbXBsZW1lbnRzIE9uSW5pdCB7XHJcbiAgICBASW5wdXQoKSB0b29sYmFyITogU21hcnRUb29sYmFyO1xyXG4gICAgdG9vbGJhckRpcmVjdGlvbiA9IFRvb2xiYXJEaXJlY3Rpb247XHJcbiAgICBUb29sYmFyQnV0dG9uU3R5bGUgPSBUb29sYmFyQnV0dG9uU3R5bGU7XHJcblxyXG4gICAgY29uc3RydWN0b3IocHJpdmF0ZSBzZXJ2aWNlOiBTbWFydFRvb2xiYXJTZXJ2aWNlKSB7IH1cclxuXHJcbiAgICBuZ09uSW5pdCgpOiB2b2lkIHsgfVxyXG5cclxuICAgIGV4ZWN1dGVDb21tYW5kKGJ1dHRvbjogU21hcnRUb29sYmFyQnV0dG9uKSB7XHJcbiAgICAgICAgdGhpcy5zZXJ2aWNlLmV4ZWN1dGVDb21tYW5kKGJ1dHRvbik7XHJcbiAgICB9XHJcbn1cclxuIiwiPGRpdlxuICBjbGFzcz1cImJ1dHRvbi1jb250YWluZXJcIlxuICBbbmdDbGFzc109XCJ0b29sYmFyLmRpcmVjdGlvbiA9PT0gdG9vbGJhckRpcmVjdGlvbi5ST1cgPyAncm93JyA6ICdjb2wnXCJcbj5cbiAgPGRpdiAqbmdGb3I9XCJsZXQgYnV0dG9uIG9mIHRvb2xiYXIuYnV0dG9uc1wiPlxuICAgIDwhLS0gTUFUIFJBSVNFRCBCVVRUT04gLS0+XG4gICAgPGJ1dHRvblxuICAgICAgKm5nSWY9XCJidXR0b24uc3R5bGUgPT09IFRvb2xiYXJCdXR0b25TdHlsZS5NQVRfUkFJU0VEX0JVVFRPTlwiXG4gICAgICBtYXQtcmFpc2VkLWJ1dHRvblxuICAgICAgW25nQ2xhc3NdPVwieyAnYnRuLXJvdW5kJzogYnV0dG9uLnJvdW5kZWQgfVwiXG4gICAgICBjb2xvcj1cInt7IGJ1dHRvbi5jb2xvciB9fVwiXG4gICAgICBbZGlzYWJsZWRdPVwiYnV0dG9uLmRpc2FibGVkXCJcbiAgICAgIChjbGljayk9XCJleGVjdXRlQ29tbWFuZChidXR0b24pXCJcbiAgICA+XG4gICAgICB7eyBidXR0b24ubGFiZWwgfX0gPG1hdC1pY29uPnt7IGJ1dHRvbi5pY29uIH19PC9tYXQtaWNvbj5cbiAgICA8L2J1dHRvbj5cbiAgICA8IS0tIE1BVCBCVVRUT04gLS0+XG4gICAgPGJ1dHRvblxuICAgICAgKm5nSWY9XCJidXR0b24uc3R5bGUgPT09IFRvb2xiYXJCdXR0b25TdHlsZS5NQVRfQlVUVE9OXCJcbiAgICAgIG1hdC1idXR0b25cbiAgICAgIFtuZ0NsYXNzXT1cInsgJ2J0bi1yb3VuZCc6IGJ1dHRvbi5yb3VuZGVkIH1cIlxuICAgICAgY29sb3I9XCJ7eyBidXR0b24uY29sb3IgfX1cIlxuICAgICAgW2Rpc2FibGVkXT1cImJ1dHRvbi5kaXNhYmxlZFwiXG4gICAgICAoY2xpY2spPVwiZXhlY3V0ZUNvbW1hbmQoYnV0dG9uKVwiXG4gICAgPlxuICAgICAge3sgYnV0dG9uLmxhYmVsIH19IDxtYXQtaWNvbj57eyBidXR0b24uaWNvbiB9fTwvbWF0LWljb24+XG4gICAgPC9idXR0b24+XG5cbiAgICA8IS0tIE1BVCBTVFJPS0VEIEJVVFRPTiAtLT5cbiAgICA8YnV0dG9uXG4gICAgICAqbmdJZj1cImJ1dHRvbi5zdHlsZSA9PT0gVG9vbGJhckJ1dHRvblN0eWxlLk1BVF9TVFJPS0VEX0JVVFRPTlwiXG4gICAgICBtYXQtc3Ryb2tlZC1idXR0b25cbiAgICAgIFtuZ0NsYXNzXT1cInsgJ2J0bi1yb3VuZCc6IGJ1dHRvbi5yb3VuZGVkIH1cIlxuICAgICAgY29sb3I9XCJ7eyBidXR0b24uY29sb3IgfX1cIlxuICAgICAgW2Rpc2FibGVkXT1cImJ1dHRvbi5kaXNhYmxlZFwiXG4gICAgICAoY2xpY2spPVwiZXhlY3V0ZUNvbW1hbmQoYnV0dG9uKVwiXG4gICAgPlxuICAgICAge3sgYnV0dG9uLmxhYmVsIH19IDxtYXQtaWNvbj57eyBidXR0b24uaWNvbiB9fTwvbWF0LWljb24+XG4gICAgPC9idXR0b24+XG5cbiAgICA8IS0tIE1BVCBGTEFUIEJVVFRPTiAtLT5cbiAgICA8YnV0dG9uXG4gICAgICAqbmdJZj1cImJ1dHRvbi5zdHlsZSA9PT0gVG9vbGJhckJ1dHRvblN0eWxlLk1BVF9GTEFUX0JVVFRPTlwiXG4gICAgICBtYXQtZmxhdC1idXR0b25cbiAgICAgIFtuZ0NsYXNzXT1cInsgJ2J0bi1yb3VuZCc6IGJ1dHRvbi5yb3VuZGVkIH1cIlxuICAgICAgY29sb3I9XCJ7eyBidXR0b24uY29sb3IgfX1cIlxuICAgICAgW2Rpc2FibGVkXT1cImJ1dHRvbi5kaXNhYmxlZFwiXG4gICAgICAoY2xpY2spPVwiZXhlY3V0ZUNvbW1hbmQoYnV0dG9uKVwiXG4gICAgPlxuICAgICAge3sgYnV0dG9uLmxhYmVsIH19IDxtYXQtaWNvbj57eyBidXR0b24uaWNvbiB9fTwvbWF0LWljb24+XG4gICAgPC9idXR0b24+XG5cbiAgICA8IS0tIE1BVCBJQ09OIEJVVFRPTiAtLT5cbiAgICA8YnV0dG9uXG4gICAgICAqbmdJZj1cImJ1dHRvbi5zdHlsZSA9PT0gVG9vbGJhckJ1dHRvblN0eWxlLk1BVF9JQ09OX0JVVFRPTlwiXG4gICAgICBtYXQtaWNvbi1idXR0b25cbiAgICAgIGNvbG9yPVwie3sgYnV0dG9uLmNvbG9yIH19XCJcbiAgICAgIFtkaXNhYmxlZF09XCJidXR0b24uZGlzYWJsZWRcIlxuICAgICAgKGNsaWNrKT1cImV4ZWN1dGVDb21tYW5kKGJ1dHRvbilcIlxuICAgID5cbiAgICAgIDxtYXQtaWNvbj57eyBidXR0b24uaWNvbiB9fTwvbWF0LWljb24+XG4gICAgPC9idXR0b24+XG5cbiAgICA8IS0tIE1BVCBGQUIgLS0+XG4gICAgPGJ1dHRvblxuICAgICAgKm5nSWY9XCJidXR0b24uc3R5bGUgPT09IFRvb2xiYXJCdXR0b25TdHlsZS5NQVRfRkFCXCJcbiAgICAgIG1hdC1mYWJcbiAgICAgIGNvbG9yPVwie3sgYnV0dG9uLmNvbG9yIH19XCJcbiAgICAgIFtkaXNhYmxlZF09XCJidXR0b24uZGlzYWJsZWRcIlxuICAgICAgKGNsaWNrKT1cImV4ZWN1dGVDb21tYW5kKGJ1dHRvbilcIlxuICAgID5cbiAgICAgIDxtYXQtaWNvbj57eyBidXR0b24uaWNvbiB9fTwvbWF0LWljb24+XG4gICAgPC9idXR0b24+XG5cbiAgICA8IS0tIE1BVCBNSU5JIEZBQiAtLT5cbiAgICA8YnV0dG9uXG4gICAgICAqbmdJZj1cImJ1dHRvbi5zdHlsZSA9PT0gVG9vbGJhckJ1dHRvblN0eWxlLk1BVF9NSU5JX0ZBQlwiXG4gICAgICBtYXQtbWluaS1mYWJcbiAgICAgIGNvbG9yPVwie3sgYnV0dG9uLmNvbG9yIH19XCJcbiAgICAgIFtkaXNhYmxlZF09XCJidXR0b24uZGlzYWJsZWRcIlxuICAgICAgKGNsaWNrKT1cImV4ZWN1dGVDb21tYW5kKGJ1dHRvbilcIlxuICAgID5cbiAgICAgIDxtYXQtaWNvbj57eyBidXR0b24uaWNvbiB9fTwvbWF0LWljb24+XG4gICAgPC9idXR0b24+XG4gIDwvZGl2PlxuPC9kaXY+XG4iXX0=
