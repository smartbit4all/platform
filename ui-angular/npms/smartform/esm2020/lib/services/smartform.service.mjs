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
            if (widget.validators &&
                widget.isRequired &&
                !this.isValidatorInList(Validators.required, widget.validators)) {
                widget.validators.push(Validators.required);
            }
            else if (!widget.validators && widget.isRequired) {
                widget.validators = [Validators.required];
            }
            let formControl = new FormControl(widget.value || "", widget.validators);
            if (widget.isDisabled) {
                formControl.disable();
            }
            this.group[widget.key] = formControl;
            if (widget.type === SmartFormWidgetType.CONTAINER && widget.valueList?.length) {
                this.createFormControls(widget.valueList);
            }
        });
    }
    isValidatorInList(validator, list) {
        return list !== undefined && list.length > 0 && list.some((v) => v === validator);
    }
    toSmartForm(group, smartForm) {
        smartForm.widgets.forEach((widget) => {
            widget.value = group.controls[widget.key].value;
        });
        return smartForm;
    }
}
SmartFormService.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartFormService, deps: [], target: i0.ɵɵFactoryTarget.Injectable });
SmartFormService.ɵprov = i0.ɵɵngDeclareInjectable({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartFormService });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartFormService, decorators: [{
            type: Injectable
        }] });
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnRmb3JtLnNlcnZpY2UuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi8uLi8uLi9wcm9qZWN0cy9zbWFydGZvcm0vc3JjL2xpYi9zZXJ2aWNlcy9zbWFydGZvcm0uc2VydmljZS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQSxPQUFPLEVBQUUsVUFBVSxFQUFFLE1BQU0sZUFBZSxDQUFDO0FBQzNDLE9BQU8sRUFBRSxXQUFXLEVBQUUsU0FBUyxFQUFlLFVBQVUsRUFBRSxNQUFNLGdCQUFnQixDQUFDO0FBQ2pGLE9BQU8sRUFBOEIsbUJBQW1CLEVBQUUsTUFBTSxvQkFBb0IsQ0FBQzs7QUFHckYsTUFBTSxPQUFPLGdCQUFnQjtJQUQ3QjtRQUVJLFVBQUssR0FBUSxFQUFFLENBQUM7S0E0Q25CO0lBMUNHLFdBQVcsQ0FBQyxTQUFvQjtRQUM1QixJQUFJLENBQUMsa0JBQWtCLENBQUMsU0FBUyxDQUFDLE9BQU8sQ0FBQyxDQUFDO1FBRTNDLE9BQU8sSUFBSSxTQUFTLENBQUMsSUFBSSxDQUFDLEtBQUssQ0FBQyxDQUFDO0lBQ3JDLENBQUM7SUFFRCxrQkFBa0IsQ0FBQyxPQUErQjtRQUM5QyxPQUFPLENBQUMsT0FBTyxDQUFDLENBQUMsTUFBTSxFQUFFLEVBQUU7WUFDdkIsSUFDSSxNQUFNLENBQUMsVUFBVTtnQkFDakIsTUFBTSxDQUFDLFVBQVU7Z0JBQ2pCLENBQUMsSUFBSSxDQUFDLGlCQUFpQixDQUFDLFVBQVUsQ0FBQyxRQUFRLEVBQUUsTUFBTSxDQUFDLFVBQVUsQ0FBQyxFQUNqRTtnQkFDRSxNQUFNLENBQUMsVUFBVSxDQUFDLElBQUksQ0FBQyxVQUFVLENBQUMsUUFBUSxDQUFDLENBQUM7YUFDL0M7aUJBQU0sSUFBSSxDQUFDLE1BQU0sQ0FBQyxVQUFVLElBQUksTUFBTSxDQUFDLFVBQVUsRUFBRTtnQkFDaEQsTUFBTSxDQUFDLFVBQVUsR0FBRyxDQUFDLFVBQVUsQ0FBQyxRQUFRLENBQUMsQ0FBQzthQUM3QztZQUVELElBQUksV0FBVyxHQUFHLElBQUksV0FBVyxDQUFDLE1BQU0sQ0FBQyxLQUFLLElBQUksRUFBRSxFQUFFLE1BQU0sQ0FBQyxVQUFVLENBQUMsQ0FBQztZQUV6RSxJQUFJLE1BQU0sQ0FBQyxVQUFVLEVBQUU7Z0JBQ25CLFdBQVcsQ0FBQyxPQUFPLEVBQUUsQ0FBQzthQUN6QjtZQUVELElBQUksQ0FBQyxLQUFLLENBQUMsTUFBTSxDQUFDLEdBQUcsQ0FBQyxHQUFHLFdBQVcsQ0FBQztZQUNyQyxJQUFJLE1BQU0sQ0FBQyxJQUFJLEtBQUssbUJBQW1CLENBQUMsU0FBUyxJQUFJLE1BQU0sQ0FBQyxTQUFTLEVBQUUsTUFBTSxFQUFFO2dCQUMzRSxJQUFJLENBQUMsa0JBQWtCLENBQUMsTUFBTSxDQUFDLFNBQVMsQ0FBQyxDQUFDO2FBQzdDO1FBQ0wsQ0FBQyxDQUFDLENBQUM7SUFDUCxDQUFDO0lBRUQsaUJBQWlCLENBQUMsU0FBcUIsRUFBRSxJQUFvQjtRQUN6RCxPQUFPLElBQUksS0FBSyxTQUFTLElBQUksSUFBSSxDQUFDLE1BQU0sR0FBRyxDQUFDLElBQUksSUFBSSxDQUFDLElBQUksQ0FBQyxDQUFDLENBQUMsRUFBRSxFQUFFLENBQUMsQ0FBQyxLQUFLLFNBQVMsQ0FBQyxDQUFDO0lBQ3RGLENBQUM7SUFFRCxXQUFXLENBQUMsS0FBZ0IsRUFBRSxTQUFvQjtRQUM5QyxTQUFTLENBQUMsT0FBTyxDQUFDLE9BQU8sQ0FBQyxDQUFDLE1BQU0sRUFBRSxFQUFFO1lBQ2pDLE1BQU0sQ0FBQyxLQUFLLEdBQUcsS0FBSyxDQUFDLFFBQVEsQ0FBQyxNQUFNLENBQUMsR0FBRyxDQUFDLENBQUMsS0FBSyxDQUFDO1FBQ3BELENBQUMsQ0FBQyxDQUFDO1FBRUgsT0FBTyxTQUFTLENBQUM7SUFDckIsQ0FBQzs7NkdBNUNRLGdCQUFnQjtpSEFBaEIsZ0JBQWdCOzJGQUFoQixnQkFBZ0I7a0JBRDVCLFVBQVUiLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQgeyBJbmplY3RhYmxlIH0gZnJvbSBcIkBhbmd1bGFyL2NvcmVcIjtcbmltcG9ydCB7IEZvcm1Db250cm9sLCBGb3JtR3JvdXAsIFZhbGlkYXRvckZuLCBWYWxpZGF0b3JzIH0gZnJvbSBcIkBhbmd1bGFyL2Zvcm1zXCI7XG5pbXBvcnQgeyBTbWFydEZvcm0sIFNtYXJ0Rm9ybVdpZGdldCwgU21hcnRGb3JtV2lkZ2V0VHlwZSB9IGZyb20gXCIuLi9zbWFydGZvcm0ubW9kZWxcIjtcblxuQEluamVjdGFibGUoKVxuZXhwb3J0IGNsYXNzIFNtYXJ0Rm9ybVNlcnZpY2Uge1xuICAgIGdyb3VwOiBhbnkgPSB7fTtcblxuICAgIHRvRm9ybUdyb3VwKHNtYXJ0Rm9ybTogU21hcnRGb3JtKTogRm9ybUdyb3VwIHtcbiAgICAgICAgdGhpcy5jcmVhdGVGb3JtQ29udHJvbHMoc21hcnRGb3JtLndpZGdldHMpO1xuXG4gICAgICAgIHJldHVybiBuZXcgRm9ybUdyb3VwKHRoaXMuZ3JvdXApO1xuICAgIH1cblxuICAgIGNyZWF0ZUZvcm1Db250cm9scyh3aWRnZXRzOiBTbWFydEZvcm1XaWRnZXQ8YW55PltdKSB7XG4gICAgICAgIHdpZGdldHMuZm9yRWFjaCgod2lkZ2V0KSA9PiB7XG4gICAgICAgICAgICBpZiAoXG4gICAgICAgICAgICAgICAgd2lkZ2V0LnZhbGlkYXRvcnMgJiZcbiAgICAgICAgICAgICAgICB3aWRnZXQuaXNSZXF1aXJlZCAmJlxuICAgICAgICAgICAgICAgICF0aGlzLmlzVmFsaWRhdG9ySW5MaXN0KFZhbGlkYXRvcnMucmVxdWlyZWQsIHdpZGdldC52YWxpZGF0b3JzKVxuICAgICAgICAgICAgKSB7XG4gICAgICAgICAgICAgICAgd2lkZ2V0LnZhbGlkYXRvcnMucHVzaChWYWxpZGF0b3JzLnJlcXVpcmVkKTtcbiAgICAgICAgICAgIH0gZWxzZSBpZiAoIXdpZGdldC52YWxpZGF0b3JzICYmIHdpZGdldC5pc1JlcXVpcmVkKSB7XG4gICAgICAgICAgICAgICAgd2lkZ2V0LnZhbGlkYXRvcnMgPSBbVmFsaWRhdG9ycy5yZXF1aXJlZF07XG4gICAgICAgICAgICB9XG5cbiAgICAgICAgICAgIGxldCBmb3JtQ29udHJvbCA9IG5ldyBGb3JtQ29udHJvbCh3aWRnZXQudmFsdWUgfHwgXCJcIiwgd2lkZ2V0LnZhbGlkYXRvcnMpO1xuXG4gICAgICAgICAgICBpZiAod2lkZ2V0LmlzRGlzYWJsZWQpIHtcbiAgICAgICAgICAgICAgICBmb3JtQ29udHJvbC5kaXNhYmxlKCk7XG4gICAgICAgICAgICB9XG5cbiAgICAgICAgICAgIHRoaXMuZ3JvdXBbd2lkZ2V0LmtleV0gPSBmb3JtQ29udHJvbDtcbiAgICAgICAgICAgIGlmICh3aWRnZXQudHlwZSA9PT0gU21hcnRGb3JtV2lkZ2V0VHlwZS5DT05UQUlORVIgJiYgd2lkZ2V0LnZhbHVlTGlzdD8ubGVuZ3RoKSB7XG4gICAgICAgICAgICAgICAgdGhpcy5jcmVhdGVGb3JtQ29udHJvbHMod2lkZ2V0LnZhbHVlTGlzdCk7XG4gICAgICAgICAgICB9XG4gICAgICAgIH0pO1xuICAgIH1cblxuICAgIGlzVmFsaWRhdG9ySW5MaXN0KHZhbGlkYXRvcjogVmFsaWRhdG9ycywgbGlzdD86IFZhbGlkYXRvckZuW10pOiBib29sZWFuIHtcbiAgICAgICAgcmV0dXJuIGxpc3QgIT09IHVuZGVmaW5lZCAmJiBsaXN0Lmxlbmd0aCA+IDAgJiYgbGlzdC5zb21lKCh2KSA9PiB2ID09PSB2YWxpZGF0b3IpO1xuICAgIH1cblxuICAgIHRvU21hcnRGb3JtKGdyb3VwOiBGb3JtR3JvdXAsIHNtYXJ0Rm9ybTogU21hcnRGb3JtKTogU21hcnRGb3JtIHtcbiAgICAgICAgc21hcnRGb3JtLndpZGdldHMuZm9yRWFjaCgod2lkZ2V0KSA9PiB7XG4gICAgICAgICAgICB3aWRnZXQudmFsdWUgPSBncm91cC5jb250cm9sc1t3aWRnZXQua2V5XS52YWx1ZTtcbiAgICAgICAgfSk7XG5cbiAgICAgICAgcmV0dXJuIHNtYXJ0Rm9ybTtcbiAgICB9XG59XG4iXX0=