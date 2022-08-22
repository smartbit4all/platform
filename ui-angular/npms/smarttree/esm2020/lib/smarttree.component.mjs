import { NestedTreeControl } from "@angular/cdk/tree";
import { Component, Input } from "@angular/core";
import { MatTreeNestedDataSource } from "@angular/material/tree";
import * as i0 from "@angular/core";
import * as i1 from "@angular/router";
import * as i2 from "@angular/material/tree";
import * as i3 from "@angular/material/icon";
import * as i4 from "@angular/material/button";
import * as i5 from "@angular/common";
export class SmartTreeComponent {
    constructor(router) {
        this.router = router;
        this.treeControl = new NestedTreeControl((node) => node.childrenNodes);
        this.dataSource = new MatTreeNestedDataSource();
        this.hasChild = (_, node) => node.hasChildren;
    }
    ngOnInit() {
        this.dataSource.data = this.treeData.rootNodes;
    }
    onNodeClick(node) {
        if (this.tempActiveNode === node) {
            this.tempActiveNode.selected = false;
            this.tempActiveNode = undefined;
            node.selected = false;
            return;
        }
        if (this.tempActiveNode)
            this.tempActiveNode.selected = false;
        node.selected = true;
        this.tempActiveNode = node;
        let navigationUrlByNodeType = this.treeData.navigationUrlsByNodeType.find((nav) => {
            return nav.nodeType === node.nodeType;
        });
        if (navigationUrlByNodeType) {
            this.router.navigate([`${navigationUrlByNodeType.navigationUrl}`], {
                queryParams: { uri: node.objectUri },
            });
        }
    }
    getNodeStyle(node) {
        if (this.treeStyle) {
            var style = node.selected
                ? {
                    background: this.treeStyle.activeStyle?.backgroundColor,
                    color: this.treeStyle.activeStyle?.color,
                    "padding-left": 15 * node.level + "px",
                }
                : {
                    background: this.treeStyle.levelBackgroundColor[node.level - 1],
                    color: this.treeStyle.color,
                    "padding-left": 15 * node.level + "px",
                };
            return style;
        }
        return {};
    }
}
SmartTreeComponent.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartTreeComponent, deps: [{ token: i1.Router }], target: i0.ɵɵFactoryTarget.Component });
SmartTreeComponent.ɵcmp = i0.ɵɵngDeclareComponent({ minVersion: "12.0.0", version: "13.2.7", type: SmartTreeComponent, selector: "smart-tree", inputs: { treeData: "treeData", treeStyle: "treeStyle" }, ngImport: i0, template: "<mat-tree [dataSource]=\"dataSource\" [treeControl]=\"treeControl\" class=\"sm-tree\">\n\t<mat-nested-tree-node *matTreeNodeDef=\"let node; when: hasChild\">\n\t\t<div\n\t\t\tclass=\"mat-tree-node sm-tree-node\"\n\t\t\t(click)=\"onNodeClick(node)\"\n\t\t\t[ngStyle]=\"getNodeStyle(node)\"\n\t\t>\n\t\t\t<mat-icon class=\"mat-icon-rtl-mirror\">\n\t\t\t\t<div *ngIf=\"hasChild(node.level, node)\">\n\t\t\t\t\t{{ treeControl.isExpanded(node) ? 'expand_more' : 'chevron_right' }}\n\t\t\t\t</div>\n\t\t\t</mat-icon>\n\t\t\t<button mat-icon-button matTreeNodeToggle [attr.aria-label]=\"'Toggle ' + node.name\">\n\t\t\t\t<mat-icon>\n\t\t\t\t\t{{ node.icon }}\n\t\t\t\t</mat-icon>\n\t\t\t</button>\n\t\t\t<div class=\"sm-tree-node-name\">\n\t\t\t\t{{ node.caption }}\n\t\t\t\t<p class=\"sm-tree-node-id\">Azonos\u00EDt\u00F3:{{ node.shortDescription }}</p>\n\t\t\t</div>\n\t\t</div>\n\t\t<div [class.sm-tree-invisible]=\"!treeControl.isExpanded(node)\" role=\"group\">\n\t\t\t<ng-container matTreeNodeOutlet></ng-container>\n\t\t</div>\n\t</mat-nested-tree-node>\n</mat-tree>\n", styles: [".sm-tree-invisible{display:none}.sm-tree ul,.sm-tree li{margin-top:0;margin-bottom:0;list-style-type:none}.sm-tree div[role=group]>.mat-tree-node{padding-left:40px}.sm-tee-node{padding-left:40px}.sm-tree-node-name{padding-left:15px;padding-top:15px;display:flex;flex-direction:column}.sm-tee-node-id{font-weight:lighter}.mat-tree-node:hover{cursor:pointer}::ng-deep .mat-icon-rtl-mirror{display:flex;flex-direction:row}\n"], components: [{ type: i2.MatTree, selector: "mat-tree", exportAs: ["matTree"] }, { type: i3.MatIcon, selector: "mat-icon", inputs: ["color", "inline", "svgIcon", "fontSet", "fontIcon"], exportAs: ["matIcon"] }, { type: i4.MatButton, selector: "button[mat-button], button[mat-raised-button], button[mat-icon-button],             button[mat-fab], button[mat-mini-fab], button[mat-stroked-button],             button[mat-flat-button]", inputs: ["disabled", "disableRipple", "color"], exportAs: ["matButton"] }], directives: [{ type: i2.MatTreeNodeDef, selector: "[matTreeNodeDef]", inputs: ["matTreeNodeDefWhen", "matTreeNode"] }, { type: i2.MatNestedTreeNode, selector: "mat-nested-tree-node", inputs: ["role", "disabled", "tabIndex", "matNestedTreeNode"], exportAs: ["matNestedTreeNode"] }, { type: i5.NgStyle, selector: "[ngStyle]", inputs: ["ngStyle"] }, { type: i5.NgIf, selector: "[ngIf]", inputs: ["ngIf", "ngIfThen", "ngIfElse"] }, { type: i2.MatTreeNodeToggle, selector: "[matTreeNodeToggle]", inputs: ["matTreeNodeToggleRecursive"] }, { type: i2.MatTreeNodeOutlet, selector: "[matTreeNodeOutlet]" }] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartTreeComponent, decorators: [{
            type: Component,
            args: [{ selector: "smart-tree", template: "<mat-tree [dataSource]=\"dataSource\" [treeControl]=\"treeControl\" class=\"sm-tree\">\n\t<mat-nested-tree-node *matTreeNodeDef=\"let node; when: hasChild\">\n\t\t<div\n\t\t\tclass=\"mat-tree-node sm-tree-node\"\n\t\t\t(click)=\"onNodeClick(node)\"\n\t\t\t[ngStyle]=\"getNodeStyle(node)\"\n\t\t>\n\t\t\t<mat-icon class=\"mat-icon-rtl-mirror\">\n\t\t\t\t<div *ngIf=\"hasChild(node.level, node)\">\n\t\t\t\t\t{{ treeControl.isExpanded(node) ? 'expand_more' : 'chevron_right' }}\n\t\t\t\t</div>\n\t\t\t</mat-icon>\n\t\t\t<button mat-icon-button matTreeNodeToggle [attr.aria-label]=\"'Toggle ' + node.name\">\n\t\t\t\t<mat-icon>\n\t\t\t\t\t{{ node.icon }}\n\t\t\t\t</mat-icon>\n\t\t\t</button>\n\t\t\t<div class=\"sm-tree-node-name\">\n\t\t\t\t{{ node.caption }}\n\t\t\t\t<p class=\"sm-tree-node-id\">Azonos\u00EDt\u00F3:{{ node.shortDescription }}</p>\n\t\t\t</div>\n\t\t</div>\n\t\t<div [class.sm-tree-invisible]=\"!treeControl.isExpanded(node)\" role=\"group\">\n\t\t\t<ng-container matTreeNodeOutlet></ng-container>\n\t\t</div>\n\t</mat-nested-tree-node>\n</mat-tree>\n", styles: [".sm-tree-invisible{display:none}.sm-tree ul,.sm-tree li{margin-top:0;margin-bottom:0;list-style-type:none}.sm-tree div[role=group]>.mat-tree-node{padding-left:40px}.sm-tee-node{padding-left:40px}.sm-tree-node-name{padding-left:15px;padding-top:15px;display:flex;flex-direction:column}.sm-tee-node-id{font-weight:lighter}.mat-tree-node:hover{cursor:pointer}::ng-deep .mat-icon-rtl-mirror{display:flex;flex-direction:row}\n"] }]
        }], ctorParameters: function () { return [{ type: i1.Router }]; }, propDecorators: { treeData: [{
                type: Input
            }], treeStyle: [{
                type: Input
            }] } });
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnR0cmVlLmNvbXBvbmVudC5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uLy4uLy4uL3Byb2plY3RzL3NtYXJ0dHJlZS9zcmMvbGliL3NtYXJ0dHJlZS5jb21wb25lbnQudHMiLCIuLi8uLi8uLi8uLi9wcm9qZWN0cy9zbWFydHRyZWUvc3JjL2xpYi9zbWFydHRyZWUuY29tcG9uZW50Lmh0bWwiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUEsT0FBTyxFQUFFLGlCQUFpQixFQUFFLE1BQU0sbUJBQW1CLENBQUM7QUFDdEQsT0FBTyxFQUFFLFNBQVMsRUFBRSxLQUFLLEVBQVUsTUFBTSxlQUFlLENBQUM7QUFDekQsT0FBTyxFQUFFLHVCQUF1QixFQUFFLE1BQU0sd0JBQXdCLENBQUM7Ozs7Ozs7QUFVakUsTUFBTSxPQUFPLGtCQUFrQjtJQU8zQixZQUFvQixNQUFjO1FBQWQsV0FBTSxHQUFOLE1BQU0sQ0FBUTtRQU5sQyxnQkFBVyxHQUFHLElBQUksaUJBQWlCLENBQWdCLENBQUMsSUFBSSxFQUFFLEVBQUUsQ0FBQyxJQUFJLENBQUMsYUFBYSxDQUFDLENBQUM7UUFDakYsZUFBVSxHQUFHLElBQUksdUJBQXVCLEVBQWlCLENBQUM7UUFpQzFELGFBQVEsR0FBRyxDQUFDLENBQVMsRUFBRSxJQUFtQixFQUFFLEVBQUUsQ0FBQyxJQUFJLENBQUMsV0FBVyxDQUFDO0lBNUIzQixDQUFDO0lBQ3RDLFFBQVE7UUFDSixJQUFJLENBQUMsVUFBVSxDQUFDLElBQUksR0FBRyxJQUFJLENBQUMsUUFBUSxDQUFDLFNBQVMsQ0FBQztJQUNuRCxDQUFDO0lBRUQsV0FBVyxDQUFDLElBQW1CO1FBQzNCLElBQUksSUFBSSxDQUFDLGNBQWMsS0FBSyxJQUFJLEVBQUU7WUFDOUIsSUFBSSxDQUFDLGNBQWMsQ0FBQyxRQUFRLEdBQUcsS0FBSyxDQUFDO1lBQ3JDLElBQUksQ0FBQyxjQUFjLEdBQUcsU0FBUyxDQUFDO1lBQ2hDLElBQUksQ0FBQyxRQUFRLEdBQUcsS0FBSyxDQUFDO1lBQ3RCLE9BQU87U0FDVjtRQUVELElBQUksSUFBSSxDQUFDLGNBQWM7WUFBRSxJQUFJLENBQUMsY0FBYyxDQUFDLFFBQVEsR0FBRyxLQUFLLENBQUM7UUFDOUQsSUFBSSxDQUFDLFFBQVEsR0FBRyxJQUFJLENBQUM7UUFDckIsSUFBSSxDQUFDLGNBQWMsR0FBRyxJQUFJLENBQUM7UUFFM0IsSUFBSSx1QkFBdUIsR0FBRyxJQUFJLENBQUMsUUFBUSxDQUFDLHdCQUF3QixDQUFDLElBQUksQ0FBQyxDQUFDLEdBQUcsRUFBRSxFQUFFO1lBQzlFLE9BQU8sR0FBRyxDQUFDLFFBQVEsS0FBSyxJQUFJLENBQUMsUUFBUSxDQUFDO1FBQzFDLENBQUMsQ0FBQyxDQUFDO1FBRUgsSUFBSSx1QkFBdUIsRUFBRTtZQUN6QixJQUFJLENBQUMsTUFBTSxDQUFDLFFBQVEsQ0FBQyxDQUFDLEdBQUcsdUJBQXVCLENBQUMsYUFBYSxFQUFFLENBQUMsRUFBRTtnQkFDL0QsV0FBVyxFQUFFLEVBQUUsR0FBRyxFQUFFLElBQUksQ0FBQyxTQUFTLEVBQUU7YUFDdkMsQ0FBQyxDQUFDO1NBQ047SUFDTCxDQUFDO0lBSUQsWUFBWSxDQUFDLElBQW1CO1FBQzVCLElBQUksSUFBSSxDQUFDLFNBQVMsRUFBRTtZQUNoQixJQUFJLEtBQUssR0FBRyxJQUFJLENBQUMsUUFBUTtnQkFDckIsQ0FBQyxDQUFDO29CQUNJLFVBQVUsRUFBRSxJQUFJLENBQUMsU0FBUyxDQUFDLFdBQVcsRUFBRSxlQUFlO29CQUN2RCxLQUFLLEVBQUUsSUFBSSxDQUFDLFNBQVMsQ0FBQyxXQUFXLEVBQUUsS0FBSztvQkFDeEMsY0FBYyxFQUFFLEVBQUUsR0FBRyxJQUFJLENBQUMsS0FBTSxHQUFHLElBQUk7aUJBQzFDO2dCQUNILENBQUMsQ0FBQztvQkFDSSxVQUFVLEVBQUUsSUFBSSxDQUFDLFNBQVMsQ0FBQyxvQkFBcUIsQ0FBQyxJQUFJLENBQUMsS0FBTSxHQUFHLENBQUMsQ0FBQztvQkFDakUsS0FBSyxFQUFFLElBQUksQ0FBQyxTQUFTLENBQUMsS0FBTTtvQkFDNUIsY0FBYyxFQUFFLEVBQUUsR0FBRyxJQUFJLENBQUMsS0FBTSxHQUFHLElBQUk7aUJBQzFDLENBQUM7WUFDUixPQUFPLEtBQUssQ0FBQztTQUNoQjtRQUNELE9BQU8sRUFBRSxDQUFDO0lBQ2QsQ0FBQzs7K0dBckRRLGtCQUFrQjttR0FBbEIsa0JBQWtCLDRHQ1ovQiwraUNBMkJBOzJGRGZhLGtCQUFrQjtrQkFMOUIsU0FBUzsrQkFDSSxZQUFZOzZGQVFiLFFBQVE7c0JBQWhCLEtBQUs7Z0JBQ0csU0FBUztzQkFBakIsS0FBSyIsInNvdXJjZXNDb250ZW50IjpbImltcG9ydCB7IE5lc3RlZFRyZWVDb250cm9sIH0gZnJvbSBcIkBhbmd1bGFyL2Nkay90cmVlXCI7XG5pbXBvcnQgeyBDb21wb25lbnQsIElucHV0LCBPbkluaXQgfSBmcm9tIFwiQGFuZ3VsYXIvY29yZVwiO1xuaW1wb3J0IHsgTWF0VHJlZU5lc3RlZERhdGFTb3VyY2UgfSBmcm9tIFwiQGFuZ3VsYXIvbWF0ZXJpYWwvdHJlZVwiO1xuaW1wb3J0IHsgUm91dGVyIH0gZnJvbSBcIkBhbmd1bGFyL3JvdXRlclwiO1xuaW1wb3J0IHsgU21hcnRUcmVlTW9kZWwsIFNtYXJ0VHJlZU5vZGUgfSBmcm9tIFwiLi9zbWFydHRyZWUubW9kZWxcIjtcbmltcG9ydCB7IFRyZWVTdHlsZSB9IGZyb20gXCIuL3NtYXJ0dHJlZS5ub2RlLm1vZGVsXCI7XG5cbkBDb21wb25lbnQoe1xuICAgIHNlbGVjdG9yOiBcInNtYXJ0LXRyZWVcIixcbiAgICB0ZW1wbGF0ZVVybDogXCIuL3NtYXJ0dHJlZS5jb21wb25lbnQuaHRtbFwiLFxuICAgIHN0eWxlVXJsczogW1wiLi9zbWFydHRyZWUuY29tcG9uZW50LmNzc1wiXSxcbn0pXG5leHBvcnQgY2xhc3MgU21hcnRUcmVlQ29tcG9uZW50IGltcGxlbWVudHMgT25Jbml0IHtcbiAgICB0cmVlQ29udHJvbCA9IG5ldyBOZXN0ZWRUcmVlQ29udHJvbDxTbWFydFRyZWVOb2RlPigobm9kZSkgPT4gbm9kZS5jaGlsZHJlbk5vZGVzKTtcbiAgICBkYXRhU291cmNlID0gbmV3IE1hdFRyZWVOZXN0ZWREYXRhU291cmNlPFNtYXJ0VHJlZU5vZGU+KCk7XG4gICAgdGVtcEFjdGl2ZU5vZGU/OiBTbWFydFRyZWVOb2RlO1xuICAgIEBJbnB1dCgpIHRyZWVEYXRhITogU21hcnRUcmVlTW9kZWw7XG4gICAgQElucHV0KCkgdHJlZVN0eWxlPzogVHJlZVN0eWxlO1xuXG4gICAgY29uc3RydWN0b3IocHJpdmF0ZSByb3V0ZXI6IFJvdXRlcikge31cbiAgICBuZ09uSW5pdCgpOiB2b2lkIHtcbiAgICAgICAgdGhpcy5kYXRhU291cmNlLmRhdGEgPSB0aGlzLnRyZWVEYXRhLnJvb3ROb2RlcztcbiAgICB9XG5cbiAgICBvbk5vZGVDbGljayhub2RlOiBTbWFydFRyZWVOb2RlKSB7XG4gICAgICAgIGlmICh0aGlzLnRlbXBBY3RpdmVOb2RlID09PSBub2RlKSB7XG4gICAgICAgICAgICB0aGlzLnRlbXBBY3RpdmVOb2RlLnNlbGVjdGVkID0gZmFsc2U7XG4gICAgICAgICAgICB0aGlzLnRlbXBBY3RpdmVOb2RlID0gdW5kZWZpbmVkO1xuICAgICAgICAgICAgbm9kZS5zZWxlY3RlZCA9IGZhbHNlO1xuICAgICAgICAgICAgcmV0dXJuO1xuICAgICAgICB9XG5cbiAgICAgICAgaWYgKHRoaXMudGVtcEFjdGl2ZU5vZGUpIHRoaXMudGVtcEFjdGl2ZU5vZGUuc2VsZWN0ZWQgPSBmYWxzZTtcbiAgICAgICAgbm9kZS5zZWxlY3RlZCA9IHRydWU7XG4gICAgICAgIHRoaXMudGVtcEFjdGl2ZU5vZGUgPSBub2RlO1xuXG4gICAgICAgIGxldCBuYXZpZ2F0aW9uVXJsQnlOb2RlVHlwZSA9IHRoaXMudHJlZURhdGEubmF2aWdhdGlvblVybHNCeU5vZGVUeXBlLmZpbmQoKG5hdikgPT4ge1xuICAgICAgICAgICAgcmV0dXJuIG5hdi5ub2RlVHlwZSA9PT0gbm9kZS5ub2RlVHlwZTtcbiAgICAgICAgfSk7XG5cbiAgICAgICAgaWYgKG5hdmlnYXRpb25VcmxCeU5vZGVUeXBlKSB7XG4gICAgICAgICAgICB0aGlzLnJvdXRlci5uYXZpZ2F0ZShbYCR7bmF2aWdhdGlvblVybEJ5Tm9kZVR5cGUubmF2aWdhdGlvblVybH1gXSwge1xuICAgICAgICAgICAgICAgIHF1ZXJ5UGFyYW1zOiB7IHVyaTogbm9kZS5vYmplY3RVcmkgfSxcbiAgICAgICAgICAgIH0pO1xuICAgICAgICB9XG4gICAgfVxuXG4gICAgaGFzQ2hpbGQgPSAoXzogbnVtYmVyLCBub2RlOiBTbWFydFRyZWVOb2RlKSA9PiBub2RlLmhhc0NoaWxkcmVuO1xuXG4gICAgZ2V0Tm9kZVN0eWxlKG5vZGU6IFNtYXJ0VHJlZU5vZGUpIHtcbiAgICAgICAgaWYgKHRoaXMudHJlZVN0eWxlKSB7XG4gICAgICAgICAgICB2YXIgc3R5bGUgPSBub2RlLnNlbGVjdGVkXG4gICAgICAgICAgICAgICAgPyB7XG4gICAgICAgICAgICAgICAgICAgICAgYmFja2dyb3VuZDogdGhpcy50cmVlU3R5bGUuYWN0aXZlU3R5bGU/LmJhY2tncm91bmRDb2xvcixcbiAgICAgICAgICAgICAgICAgICAgICBjb2xvcjogdGhpcy50cmVlU3R5bGUuYWN0aXZlU3R5bGU/LmNvbG9yLFxuICAgICAgICAgICAgICAgICAgICAgIFwicGFkZGluZy1sZWZ0XCI6IDE1ICogbm9kZS5sZXZlbCEgKyBcInB4XCIsXG4gICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgOiB7XG4gICAgICAgICAgICAgICAgICAgICAgYmFja2dyb3VuZDogdGhpcy50cmVlU3R5bGUubGV2ZWxCYWNrZ3JvdW5kQ29sb3IhW25vZGUubGV2ZWwhIC0gMV0sXG4gICAgICAgICAgICAgICAgICAgICAgY29sb3I6IHRoaXMudHJlZVN0eWxlLmNvbG9yISxcbiAgICAgICAgICAgICAgICAgICAgICBcInBhZGRpbmctbGVmdFwiOiAxNSAqIG5vZGUubGV2ZWwhICsgXCJweFwiLFxuICAgICAgICAgICAgICAgICAgfTtcbiAgICAgICAgICAgIHJldHVybiBzdHlsZTtcbiAgICAgICAgfVxuICAgICAgICByZXR1cm4ge307XG4gICAgfVxufVxuIiwiPG1hdC10cmVlIFtkYXRhU291cmNlXT1cImRhdGFTb3VyY2VcIiBbdHJlZUNvbnRyb2xdPVwidHJlZUNvbnRyb2xcIiBjbGFzcz1cInNtLXRyZWVcIj5cblx0PG1hdC1uZXN0ZWQtdHJlZS1ub2RlICptYXRUcmVlTm9kZURlZj1cImxldCBub2RlOyB3aGVuOiBoYXNDaGlsZFwiPlxuXHRcdDxkaXZcblx0XHRcdGNsYXNzPVwibWF0LXRyZWUtbm9kZSBzbS10cmVlLW5vZGVcIlxuXHRcdFx0KGNsaWNrKT1cIm9uTm9kZUNsaWNrKG5vZGUpXCJcblx0XHRcdFtuZ1N0eWxlXT1cImdldE5vZGVTdHlsZShub2RlKVwiXG5cdFx0PlxuXHRcdFx0PG1hdC1pY29uIGNsYXNzPVwibWF0LWljb24tcnRsLW1pcnJvclwiPlxuXHRcdFx0XHQ8ZGl2ICpuZ0lmPVwiaGFzQ2hpbGQobm9kZS5sZXZlbCwgbm9kZSlcIj5cblx0XHRcdFx0XHR7eyB0cmVlQ29udHJvbC5pc0V4cGFuZGVkKG5vZGUpID8gJ2V4cGFuZF9tb3JlJyA6ICdjaGV2cm9uX3JpZ2h0JyB9fVxuXHRcdFx0XHQ8L2Rpdj5cblx0XHRcdDwvbWF0LWljb24+XG5cdFx0XHQ8YnV0dG9uIG1hdC1pY29uLWJ1dHRvbiBtYXRUcmVlTm9kZVRvZ2dsZSBbYXR0ci5hcmlhLWxhYmVsXT1cIidUb2dnbGUgJyArIG5vZGUubmFtZVwiPlxuXHRcdFx0XHQ8bWF0LWljb24+XG5cdFx0XHRcdFx0e3sgbm9kZS5pY29uIH19XG5cdFx0XHRcdDwvbWF0LWljb24+XG5cdFx0XHQ8L2J1dHRvbj5cblx0XHRcdDxkaXYgY2xhc3M9XCJzbS10cmVlLW5vZGUtbmFtZVwiPlxuXHRcdFx0XHR7eyBub2RlLmNhcHRpb24gfX1cblx0XHRcdFx0PHAgY2xhc3M9XCJzbS10cmVlLW5vZGUtaWRcIj5Bem9ub3PDrXTDszp7eyBub2RlLnNob3J0RGVzY3JpcHRpb24gfX08L3A+XG5cdFx0XHQ8L2Rpdj5cblx0XHQ8L2Rpdj5cblx0XHQ8ZGl2IFtjbGFzcy5zbS10cmVlLWludmlzaWJsZV09XCIhdHJlZUNvbnRyb2wuaXNFeHBhbmRlZChub2RlKVwiIHJvbGU9XCJncm91cFwiPlxuXHRcdFx0PG5nLWNvbnRhaW5lciBtYXRUcmVlTm9kZU91dGxldD48L25nLWNvbnRhaW5lcj5cblx0XHQ8L2Rpdj5cblx0PC9tYXQtbmVzdGVkLXRyZWUtbm9kZT5cbjwvbWF0LXRyZWU+XG4iXX0=