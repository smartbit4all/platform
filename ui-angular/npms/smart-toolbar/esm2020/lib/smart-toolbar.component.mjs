import { Component, Input, ViewChild, ViewContainerRef } from '@angular/core';
import { SmartToolbarButtonComponent } from './smart-toolbar-button/smart-toolbar-button.component';
import * as i0 from "@angular/core";
import * as i1 from "@angular/common";
export class SmartToolbarComponent {
    constructor(resolver) {
        this.resolver = resolver;
        this.direction = ToolbarDirection.ROW;
        this.toolbarDirection = ToolbarDirection;
    }
    ngOnInit() {
    }
    ngAfterViewInit() {
        for (let btn of this.buttons) {
            const factory = this.resolver.resolveComponentFactory(SmartToolbarButtonComponent);
            const ref = this.vcRef.createComponent(factory);
            console.log(btn);
            ref.instance.button = btn;
            ref.changeDetectorRef.detectChanges();
        }
    }
}
SmartToolbarComponent.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarComponent, deps: [{ token: i0.ComponentFactoryResolver }], target: i0.ɵɵFactoryTarget.Component });
SmartToolbarComponent.ɵcmp = i0.ɵɵngDeclareComponent({ minVersion: "12.0.0", version: "13.2.7", type: SmartToolbarComponent, selector: "smart-toolbar", inputs: { buttons: "buttons", direction: "direction" }, viewQueries: [{ propertyName: "vcRef", first: true, predicate: ["renderToolbar"], descendants: true, read: ViewContainerRef }], ngImport: i0, template: "<div [ngClass]=\"direction === toolbarDirection.ROW ? 'row' : 'col'\">\n\t<ng-template #renderToolbar></ng-template>\n</div>\n", styles: [".col{display:flex;flex-direction:column;padding:1em}.row{display:flex;flex-wrap:wrap;padding:1em}\n"], directives: [{ type: i1.NgClass, selector: "[ngClass]", inputs: ["class", "ngClass"] }] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartToolbarComponent, decorators: [{
            type: Component,
            args: [{ selector: 'smart-toolbar', template: "<div [ngClass]=\"direction === toolbarDirection.ROW ? 'row' : 'col'\">\n\t<ng-template #renderToolbar></ng-template>\n</div>\n", styles: [".col{display:flex;flex-direction:column;padding:1em}.row{display:flex;flex-wrap:wrap;padding:1em}\n"] }]
        }], ctorParameters: function () { return [{ type: i0.ComponentFactoryResolver }]; }, propDecorators: { buttons: [{
                type: Input
            }], direction: [{
                type: Input
            }], vcRef: [{
                type: ViewChild,
                args: ['renderToolbar', { read: ViewContainerRef }]
            }] } });
export var ToolbarDirection;
(function (ToolbarDirection) {
    ToolbarDirection[ToolbarDirection["COL"] = 0] = "COL";
    ToolbarDirection[ToolbarDirection["ROW"] = 1] = "ROW";
})(ToolbarDirection || (ToolbarDirection = {}));
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnQtdG9vbGJhci5jb21wb25lbnQuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi8uLi9wcm9qZWN0cy9zbWFydC10b29sYmFyL3NyYy9saWIvc21hcnQtdG9vbGJhci5jb21wb25lbnQudHMiLCIuLi8uLi8uLi8uLi9wcm9qZWN0cy9zbWFydC10b29sYmFyL3NyYy9saWIvc21hcnQtdG9vbGJhci5jb21wb25lbnQuaHRtbCJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQSxPQUFPLEVBQUUsU0FBUyxFQUE0QixLQUFLLEVBQVUsU0FBUyxFQUFFLGdCQUFnQixFQUFFLE1BQU0sZUFBZSxDQUFDO0FBQ2hILE9BQU8sRUFBRSwyQkFBMkIsRUFBRSxNQUFNLHVEQUF1RCxDQUFDOzs7QUFRcEcsTUFBTSxPQUFPLHFCQUFxQjtJQVFoQyxZQUFvQixRQUFrQztRQUFsQyxhQUFRLEdBQVIsUUFBUSxDQUEwQjtRQU43QyxjQUFTLEdBQXNCLGdCQUFnQixDQUFDLEdBQUcsQ0FBQztRQUM3RCxxQkFBZ0IsR0FBRyxnQkFBZ0IsQ0FBQztJQUtzQixDQUFDO0lBRTNELFFBQVE7SUFDUixDQUFDO0lBRUQsZUFBZTtRQUNiLEtBQUssSUFBSSxHQUFHLElBQUksSUFBSSxDQUFDLE9BQU8sRUFBRTtZQUM1QixNQUFNLE9BQU8sR0FBRyxJQUFJLENBQUMsUUFBUSxDQUFDLHVCQUF1QixDQUFDLDJCQUEyQixDQUFDLENBQUM7WUFDbkYsTUFBTSxHQUFHLEdBQUcsSUFBSSxDQUFDLEtBQU0sQ0FBQyxlQUFlLENBQUMsT0FBTyxDQUFDLENBQUM7WUFDakQsT0FBTyxDQUFDLEdBQUcsQ0FBQyxHQUFHLENBQUMsQ0FBQztZQUNqQixHQUFHLENBQUMsUUFBUSxDQUFDLE1BQU0sR0FBRyxHQUFHLENBQUM7WUFDMUIsR0FBRyxDQUFDLGlCQUFpQixDQUFDLGFBQWEsRUFBRSxDQUFDO1NBQ3ZDO0lBQ0gsQ0FBQzs7a0hBckJVLHFCQUFxQjtzR0FBckIscUJBQXFCLGdNQUtJLGdCQUFnQiw2QkNkdEQsZ0lBR0E7MkZETWEscUJBQXFCO2tCQUxqQyxTQUFTOytCQUNFLGVBQWU7K0dBS2hCLE9BQU87c0JBQWYsS0FBSztnQkFDRyxTQUFTO3NCQUFqQixLQUFLO2dCQUlOLEtBQUs7c0JBREosU0FBUzt1QkFBQyxlQUFlLEVBQUUsRUFBRSxJQUFJLEVBQUUsZ0JBQWdCLEVBQUU7O0FBb0J4RCxNQUFNLENBQU4sSUFBWSxnQkFFWDtBQUZELFdBQVksZ0JBQWdCO0lBQzFCLHFEQUFHLENBQUE7SUFBRSxxREFBRyxDQUFBO0FBQ1YsQ0FBQyxFQUZXLGdCQUFnQixLQUFoQixnQkFBZ0IsUUFFM0IiLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQgeyBDb21wb25lbnQsIENvbXBvbmVudEZhY3RvcnlSZXNvbHZlciwgSW5wdXQsIE9uSW5pdCwgVmlld0NoaWxkLCBWaWV3Q29udGFpbmVyUmVmIH0gZnJvbSAnQGFuZ3VsYXIvY29yZSc7XG5pbXBvcnQgeyBTbWFydFRvb2xiYXJCdXR0b25Db21wb25lbnQgfSBmcm9tICcuL3NtYXJ0LXRvb2xiYXItYnV0dG9uL3NtYXJ0LXRvb2xiYXItYnV0dG9uLmNvbXBvbmVudCc7XG5pbXBvcnQgeyBTbWFydFRvb2xiYXJCdXR0b24gfSBmcm9tICcuL3NtYXJ0LXRvb2xiYXItYnV0dG9uL3NtYXJ0LXRvb2xiYXItYnV0dG9uLm1vZGVsJztcblxuQENvbXBvbmVudCh7XG4gIHNlbGVjdG9yOiAnc21hcnQtdG9vbGJhcicsXG4gIHRlbXBsYXRlVXJsOiAnLi9zbWFydC10b29sYmFyLmNvbXBvbmVudC5odG1sJyxcbiAgc3R5bGVVcmxzOiBbJy4vc21hcnQtdG9vbGJhci5jb21wb25lbnQuY3NzJ11cbn0pXG5leHBvcnQgY2xhc3MgU21hcnRUb29sYmFyQ29tcG9uZW50IGltcGxlbWVudHMgT25Jbml0IHtcbiAgQElucHV0KCkgYnV0dG9ucyE6IFNtYXJ0VG9vbGJhckJ1dHRvbltdO1xuICBASW5wdXQoKSBkaXJlY3Rpb24/OiBUb29sYmFyRGlyZWN0aW9uID0gVG9vbGJhckRpcmVjdGlvbi5ST1c7XG4gIHRvb2xiYXJEaXJlY3Rpb24gPSBUb29sYmFyRGlyZWN0aW9uO1xuXG4gIEBWaWV3Q2hpbGQoJ3JlbmRlclRvb2xiYXInLCB7IHJlYWQ6IFZpZXdDb250YWluZXJSZWYgfSlcbiAgdmNSZWY/OiBWaWV3Q29udGFpbmVyUmVmO1xuXG4gIGNvbnN0cnVjdG9yKHByaXZhdGUgcmVzb2x2ZXI6IENvbXBvbmVudEZhY3RvcnlSZXNvbHZlcikgeyB9XG5cbiAgbmdPbkluaXQoKTogdm9pZCB7XG4gIH1cblxuICBuZ0FmdGVyVmlld0luaXQoKSB7XG4gICAgZm9yIChsZXQgYnRuIG9mIHRoaXMuYnV0dG9ucykge1xuICAgICAgY29uc3QgZmFjdG9yeSA9IHRoaXMucmVzb2x2ZXIucmVzb2x2ZUNvbXBvbmVudEZhY3RvcnkoU21hcnRUb29sYmFyQnV0dG9uQ29tcG9uZW50KTtcbiAgICAgIGNvbnN0IHJlZiA9IHRoaXMudmNSZWYhLmNyZWF0ZUNvbXBvbmVudChmYWN0b3J5KTtcbiAgICAgIGNvbnNvbGUubG9nKGJ0bik7XG4gICAgICByZWYuaW5zdGFuY2UuYnV0dG9uID0gYnRuO1xuICAgICAgcmVmLmNoYW5nZURldGVjdG9yUmVmLmRldGVjdENoYW5nZXMoKTtcbiAgICB9XG4gIH1cblxufVxuXG5leHBvcnQgZW51bSBUb29sYmFyRGlyZWN0aW9uIHtcbiAgQ09MLCBST1dcbn1cbiIsIjxkaXYgW25nQ2xhc3NdPVwiZGlyZWN0aW9uID09PSB0b29sYmFyRGlyZWN0aW9uLlJPVyA/ICdyb3cnIDogJ2NvbCdcIj5cblx0PG5nLXRlbXBsYXRlICNyZW5kZXJUb29sYmFyPjwvbmctdGVtcGxhdGU+XG48L2Rpdj5cbiJdfQ==