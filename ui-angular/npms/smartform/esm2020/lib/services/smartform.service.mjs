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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnRmb3JtLnNlcnZpY2UuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi8uLi8uLi9wcm9qZWN0cy9zbWFydGZvcm0vc3JjL2xpYi9zZXJ2aWNlcy9zbWFydGZvcm0uc2VydmljZS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQSxPQUFPLEVBQUUsVUFBVSxFQUFFLE1BQU0sZUFBZSxDQUFDO0FBQzNDLE9BQU8sRUFBRSxXQUFXLEVBQUUsU0FBUyxFQUFlLFVBQVUsRUFBRSxNQUFNLGdCQUFnQixDQUFDO0FBQ2pGLE9BQU8sRUFBOEIsbUJBQW1CLEVBQUUsTUFBTSxvQkFBb0IsQ0FBQzs7QUFHckYsTUFBTSxPQUFPLGdCQUFnQjtJQUQ3QjtRQUVJLFVBQUssR0FBUSxFQUFFLENBQUM7S0E0Q25CO0lBMUNHLFdBQVcsQ0FBQyxTQUFvQjtRQUM1QixJQUFJLENBQUMsa0JBQWtCLENBQUMsU0FBUyxDQUFDLE9BQU8sQ0FBQyxDQUFDO1FBRTNDLE9BQU8sSUFBSSxTQUFTLENBQUMsSUFBSSxDQUFDLEtBQUssQ0FBQyxDQUFDO0lBQ3JDLENBQUM7SUFFRCxrQkFBa0IsQ0FBQyxPQUErQjtRQUM5QyxPQUFPLENBQUMsT0FBTyxDQUFDLENBQUMsTUFBTSxFQUFFLEVBQUU7WUFDdkIsSUFDSSxNQUFNLENBQUMsVUFBVTtnQkFDakIsTUFBTSxDQUFDLFVBQVU7Z0JBQ2pCLENBQUMsSUFBSSxDQUFDLGlCQUFpQixDQUFDLFVBQVUsQ0FBQyxRQUFRLEVBQUUsTUFBTSxDQUFDLFVBQVUsQ0FBQyxFQUNqRTtnQkFDRSxNQUFNLENBQUMsVUFBVSxDQUFDLElBQUksQ0FBQyxVQUFVLENBQUMsUUFBUSxDQUFDLENBQUM7YUFDL0M7aUJBQU0sSUFBSSxDQUFDLE1BQU0sQ0FBQyxVQUFVLElBQUksTUFBTSxDQUFDLFVBQVUsRUFBRTtnQkFDaEQsTUFBTSxDQUFDLFVBQVUsR0FBRyxDQUFDLFVBQVUsQ0FBQyxRQUFRLENBQUMsQ0FBQzthQUM3QztZQUVELElBQUksV0FBVyxHQUFHLElBQUksV0FBVyxDQUFDLE1BQU0sQ0FBQyxLQUFLLElBQUksRUFBRSxFQUFFLE1BQU0sQ0FBQyxVQUFVLENBQUMsQ0FBQztZQUV6RSxJQUFJLE1BQU0sQ0FBQyxVQUFVLEVBQUU7Z0JBQ25CLFdBQVcsQ0FBQyxPQUFPLEVBQUUsQ0FBQzthQUN6QjtZQUVELElBQUksQ0FBQyxLQUFLLENBQUMsTUFBTSxDQUFDLEdBQUcsQ0FBQyxHQUFHLFdBQVcsQ0FBQztZQUNyQyxJQUFJLE1BQU0sQ0FBQyxJQUFJLEtBQUssbUJBQW1CLENBQUMsU0FBUyxJQUFJLE1BQU0sQ0FBQyxTQUFTLEVBQUUsTUFBTSxFQUFFO2dCQUMzRSxJQUFJLENBQUMsa0JBQWtCLENBQUMsTUFBTSxDQUFDLFNBQVMsQ0FBQyxDQUFDO2FBQzdDO1FBQ0wsQ0FBQyxDQUFDLENBQUM7SUFDUCxDQUFDO0lBRUQsaUJBQWlCLENBQUMsU0FBcUIsRUFBRSxJQUFvQjtRQUN6RCxPQUFPLElBQUksS0FBSyxTQUFTLElBQUksSUFBSSxDQUFDLE1BQU0sR0FBRyxDQUFDLElBQUksSUFBSSxDQUFDLElBQUksQ0FBQyxDQUFDLENBQUMsRUFBRSxFQUFFLENBQUMsQ0FBQyxLQUFLLFNBQVMsQ0FBQyxDQUFDO0lBQ3RGLENBQUM7SUFFRCxXQUFXLENBQUMsS0FBZ0IsRUFBRSxTQUFvQjtRQUM5QyxTQUFTLENBQUMsT0FBTyxDQUFDLE9BQU8sQ0FBQyxDQUFDLE1BQU0sRUFBRSxFQUFFO1lBQ2pDLE1BQU0sQ0FBQyxLQUFLLEdBQUcsS0FBSyxDQUFDLFFBQVEsQ0FBQyxNQUFNLENBQUMsR0FBRyxDQUFDLENBQUMsS0FBSyxDQUFDO1FBQ3BELENBQUMsQ0FBQyxDQUFDO1FBRUgsT0FBTyxTQUFTLENBQUM7SUFDckIsQ0FBQzs7NkdBNUNRLGdCQUFnQjtpSEFBaEIsZ0JBQWdCOzJGQUFoQixnQkFBZ0I7a0JBRDVCLFVBQVUiLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQgeyBJbmplY3RhYmxlIH0gZnJvbSBcIkBhbmd1bGFyL2NvcmVcIjtcclxuaW1wb3J0IHsgRm9ybUNvbnRyb2wsIEZvcm1Hcm91cCwgVmFsaWRhdG9yRm4sIFZhbGlkYXRvcnMgfSBmcm9tIFwiQGFuZ3VsYXIvZm9ybXNcIjtcclxuaW1wb3J0IHsgU21hcnRGb3JtLCBTbWFydEZvcm1XaWRnZXQsIFNtYXJ0Rm9ybVdpZGdldFR5cGUgfSBmcm9tIFwiLi4vc21hcnRmb3JtLm1vZGVsXCI7XHJcblxyXG5ASW5qZWN0YWJsZSgpXHJcbmV4cG9ydCBjbGFzcyBTbWFydEZvcm1TZXJ2aWNlIHtcclxuICAgIGdyb3VwOiBhbnkgPSB7fTtcclxuXHJcbiAgICB0b0Zvcm1Hcm91cChzbWFydEZvcm06IFNtYXJ0Rm9ybSk6IEZvcm1Hcm91cCB7XHJcbiAgICAgICAgdGhpcy5jcmVhdGVGb3JtQ29udHJvbHMoc21hcnRGb3JtLndpZGdldHMpO1xyXG5cclxuICAgICAgICByZXR1cm4gbmV3IEZvcm1Hcm91cCh0aGlzLmdyb3VwKTtcclxuICAgIH1cclxuXHJcbiAgICBjcmVhdGVGb3JtQ29udHJvbHMod2lkZ2V0czogU21hcnRGb3JtV2lkZ2V0PGFueT5bXSkge1xyXG4gICAgICAgIHdpZGdldHMuZm9yRWFjaCgod2lkZ2V0KSA9PiB7XHJcbiAgICAgICAgICAgIGlmIChcclxuICAgICAgICAgICAgICAgIHdpZGdldC52YWxpZGF0b3JzICYmXHJcbiAgICAgICAgICAgICAgICB3aWRnZXQuaXNSZXF1aXJlZCAmJlxyXG4gICAgICAgICAgICAgICAgIXRoaXMuaXNWYWxpZGF0b3JJbkxpc3QoVmFsaWRhdG9ycy5yZXF1aXJlZCwgd2lkZ2V0LnZhbGlkYXRvcnMpXHJcbiAgICAgICAgICAgICkge1xyXG4gICAgICAgICAgICAgICAgd2lkZ2V0LnZhbGlkYXRvcnMucHVzaChWYWxpZGF0b3JzLnJlcXVpcmVkKTtcclxuICAgICAgICAgICAgfSBlbHNlIGlmICghd2lkZ2V0LnZhbGlkYXRvcnMgJiYgd2lkZ2V0LmlzUmVxdWlyZWQpIHtcclxuICAgICAgICAgICAgICAgIHdpZGdldC52YWxpZGF0b3JzID0gW1ZhbGlkYXRvcnMucmVxdWlyZWRdO1xyXG4gICAgICAgICAgICB9XHJcblxyXG4gICAgICAgICAgICBsZXQgZm9ybUNvbnRyb2wgPSBuZXcgRm9ybUNvbnRyb2wod2lkZ2V0LnZhbHVlIHx8IFwiXCIsIHdpZGdldC52YWxpZGF0b3JzKTtcclxuXHJcbiAgICAgICAgICAgIGlmICh3aWRnZXQuaXNEaXNhYmxlZCkge1xyXG4gICAgICAgICAgICAgICAgZm9ybUNvbnRyb2wuZGlzYWJsZSgpO1xyXG4gICAgICAgICAgICB9XHJcblxyXG4gICAgICAgICAgICB0aGlzLmdyb3VwW3dpZGdldC5rZXldID0gZm9ybUNvbnRyb2w7XHJcbiAgICAgICAgICAgIGlmICh3aWRnZXQudHlwZSA9PT0gU21hcnRGb3JtV2lkZ2V0VHlwZS5DT05UQUlORVIgJiYgd2lkZ2V0LnZhbHVlTGlzdD8ubGVuZ3RoKSB7XHJcbiAgICAgICAgICAgICAgICB0aGlzLmNyZWF0ZUZvcm1Db250cm9scyh3aWRnZXQudmFsdWVMaXN0KTtcclxuICAgICAgICAgICAgfVxyXG4gICAgICAgIH0pO1xyXG4gICAgfVxyXG5cclxuICAgIGlzVmFsaWRhdG9ySW5MaXN0KHZhbGlkYXRvcjogVmFsaWRhdG9ycywgbGlzdD86IFZhbGlkYXRvckZuW10pOiBib29sZWFuIHtcclxuICAgICAgICByZXR1cm4gbGlzdCAhPT0gdW5kZWZpbmVkICYmIGxpc3QubGVuZ3RoID4gMCAmJiBsaXN0LnNvbWUoKHYpID0+IHYgPT09IHZhbGlkYXRvcik7XHJcbiAgICB9XHJcblxyXG4gICAgdG9TbWFydEZvcm0oZ3JvdXA6IEZvcm1Hcm91cCwgc21hcnRGb3JtOiBTbWFydEZvcm0pOiBTbWFydEZvcm0ge1xyXG4gICAgICAgIHNtYXJ0Rm9ybS53aWRnZXRzLmZvckVhY2goKHdpZGdldCkgPT4ge1xyXG4gICAgICAgICAgICB3aWRnZXQudmFsdWUgPSBncm91cC5jb250cm9sc1t3aWRnZXQua2V5XS52YWx1ZTtcclxuICAgICAgICB9KTtcclxuXHJcbiAgICAgICAgcmV0dXJuIHNtYXJ0Rm9ybTtcclxuICAgIH1cclxufVxyXG4iXX0=