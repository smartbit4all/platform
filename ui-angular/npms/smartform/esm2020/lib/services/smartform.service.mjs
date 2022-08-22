import { Injectable } from "@angular/core";
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { SmartFormWidgetType } from "../smartform.model";
import * as i0 from "@angular/core";
export class SmartFormService {
    constructor() {
        this.group = {};
    }
    toFormGroup(smartForm) {
        this.createFormControls(smartForm.widgets);
        return new FormGroup(this.group);
    }
    createFormControls(widgets) {
        widgets.forEach((widget) => {
            let formControl = new FormControl(widget.value || "", widget.isRequired ? Validators.required : undefined);
            if (widget.isDisabled) {
                formControl.disable();
            }
            this.group[widget.key] = formControl;
            if (widget.type === SmartFormWidgetType.CONTAINER && widget.valueList?.length) {
                this.createFormControls(widget.valueList);
            }
        });
    }
}
SmartFormService.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartFormService, deps: [], target: i0.ɵɵFactoryTarget.Injectable });
SmartFormService.ɵprov = i0.ɵɵngDeclareInjectable({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartFormService });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartFormService, decorators: [{
            type: Injectable
        }] });
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnRmb3JtLnNlcnZpY2UuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi8uLi8uLi9wcm9qZWN0cy9zbWFydGZvcm0vc3JjL2xpYi9zZXJ2aWNlcy9zbWFydGZvcm0uc2VydmljZS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQSxPQUFPLEVBQUUsVUFBVSxFQUFFLE1BQU0sZUFBZSxDQUFDO0FBQzNDLE9BQU8sRUFBRSxXQUFXLEVBQUUsU0FBUyxFQUFFLFVBQVUsRUFBRSxNQUFNLGdCQUFnQixDQUFDO0FBQ3BFLE9BQU8sRUFBOEIsbUJBQW1CLEVBQUUsTUFBTSxvQkFBb0IsQ0FBQzs7QUFHckYsTUFBTSxPQUFPLGdCQUFnQjtJQUQ3QjtRQUVJLFVBQUssR0FBUSxFQUFFLENBQUM7S0F5Qm5CO0lBdkJHLFdBQVcsQ0FBQyxTQUFvQjtRQUM1QixJQUFJLENBQUMsa0JBQWtCLENBQUMsU0FBUyxDQUFDLE9BQU8sQ0FBQyxDQUFDO1FBRTNDLE9BQU8sSUFBSSxTQUFTLENBQUMsSUFBSSxDQUFDLEtBQUssQ0FBQyxDQUFDO0lBQ3JDLENBQUM7SUFFRCxrQkFBa0IsQ0FBQyxPQUErQjtRQUM5QyxPQUFPLENBQUMsT0FBTyxDQUFDLENBQUMsTUFBTSxFQUFFLEVBQUU7WUFDdkIsSUFBSSxXQUFXLEdBQUcsSUFBSSxXQUFXLENBQzdCLE1BQU0sQ0FBQyxLQUFLLElBQUksRUFBRSxFQUNsQixNQUFNLENBQUMsVUFBVSxDQUFDLENBQUMsQ0FBQyxVQUFVLENBQUMsUUFBUSxDQUFDLENBQUMsQ0FBQyxTQUFTLENBQ3RELENBQUM7WUFFRixJQUFJLE1BQU0sQ0FBQyxVQUFVLEVBQUU7Z0JBQ25CLFdBQVcsQ0FBQyxPQUFPLEVBQUUsQ0FBQzthQUN6QjtZQUVELElBQUksQ0FBQyxLQUFLLENBQUMsTUFBTSxDQUFDLEdBQUcsQ0FBQyxHQUFHLFdBQVcsQ0FBQztZQUNyQyxJQUFJLE1BQU0sQ0FBQyxJQUFJLEtBQUssbUJBQW1CLENBQUMsU0FBUyxJQUFJLE1BQU0sQ0FBQyxTQUFTLEVBQUUsTUFBTSxFQUFFO2dCQUMzRSxJQUFJLENBQUMsa0JBQWtCLENBQUMsTUFBTSxDQUFDLFNBQVMsQ0FBQyxDQUFDO2FBQzdDO1FBQ0wsQ0FBQyxDQUFDLENBQUM7SUFDUCxDQUFDOzs2R0F6QlEsZ0JBQWdCO2lIQUFoQixnQkFBZ0I7MkZBQWhCLGdCQUFnQjtrQkFENUIsVUFBVSIsInNvdXJjZXNDb250ZW50IjpbImltcG9ydCB7IEluamVjdGFibGUgfSBmcm9tIFwiQGFuZ3VsYXIvY29yZVwiO1xuaW1wb3J0IHsgRm9ybUNvbnRyb2wsIEZvcm1Hcm91cCwgVmFsaWRhdG9ycyB9IGZyb20gXCJAYW5ndWxhci9mb3Jtc1wiO1xuaW1wb3J0IHsgU21hcnRGb3JtLCBTbWFydEZvcm1XaWRnZXQsIFNtYXJ0Rm9ybVdpZGdldFR5cGUgfSBmcm9tIFwiLi4vc21hcnRmb3JtLm1vZGVsXCI7XG5cbkBJbmplY3RhYmxlKClcbmV4cG9ydCBjbGFzcyBTbWFydEZvcm1TZXJ2aWNlIHtcbiAgICBncm91cDogYW55ID0ge307XG5cbiAgICB0b0Zvcm1Hcm91cChzbWFydEZvcm06IFNtYXJ0Rm9ybSk6IEZvcm1Hcm91cCB7XG4gICAgICAgIHRoaXMuY3JlYXRlRm9ybUNvbnRyb2xzKHNtYXJ0Rm9ybS53aWRnZXRzKTtcblxuICAgICAgICByZXR1cm4gbmV3IEZvcm1Hcm91cCh0aGlzLmdyb3VwKTtcbiAgICB9XG5cbiAgICBjcmVhdGVGb3JtQ29udHJvbHMod2lkZ2V0czogU21hcnRGb3JtV2lkZ2V0PGFueT5bXSkge1xuICAgICAgICB3aWRnZXRzLmZvckVhY2goKHdpZGdldCkgPT4ge1xuICAgICAgICAgICAgbGV0IGZvcm1Db250cm9sID0gbmV3IEZvcm1Db250cm9sKFxuICAgICAgICAgICAgICAgIHdpZGdldC52YWx1ZSB8fCBcIlwiLFxuICAgICAgICAgICAgICAgIHdpZGdldC5pc1JlcXVpcmVkID8gVmFsaWRhdG9ycy5yZXF1aXJlZCA6IHVuZGVmaW5lZFxuICAgICAgICAgICAgKTtcblxuICAgICAgICAgICAgaWYgKHdpZGdldC5pc0Rpc2FibGVkKSB7XG4gICAgICAgICAgICAgICAgZm9ybUNvbnRyb2wuZGlzYWJsZSgpO1xuICAgICAgICAgICAgfVxuXG4gICAgICAgICAgICB0aGlzLmdyb3VwW3dpZGdldC5rZXldID0gZm9ybUNvbnRyb2w7XG4gICAgICAgICAgICBpZiAod2lkZ2V0LnR5cGUgPT09IFNtYXJ0Rm9ybVdpZGdldFR5cGUuQ09OVEFJTkVSICYmIHdpZGdldC52YWx1ZUxpc3Q/Lmxlbmd0aCkge1xuICAgICAgICAgICAgICAgIHRoaXMuY3JlYXRlRm9ybUNvbnRyb2xzKHdpZGdldC52YWx1ZUxpc3QpO1xuICAgICAgICAgICAgfVxuICAgICAgICB9KTtcbiAgICB9XG59XG4iXX0=