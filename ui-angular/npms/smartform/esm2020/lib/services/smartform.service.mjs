import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import * as i0 from "@angular/core";
export class SmartFormService {
    toFormGroup(smartForm) {
        const group = {};
        smartForm.widgets.forEach((widget) => {
            group[widget.key] = new FormControl(widget.value || '', widget.isRequired ? Validators.required : undefined);
        });
        return new FormGroup(group);
    }
}
SmartFormService.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartFormService, deps: [], target: i0.ɵɵFactoryTarget.Injectable });
SmartFormService.ɵprov = i0.ɵɵngDeclareInjectable({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartFormService });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartFormService, decorators: [{
            type: Injectable
        }] });
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnRmb3JtLnNlcnZpY2UuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi8uLi8uLi9wcm9qZWN0cy9zbWFydGZvcm0vc3JjL2xpYi9zZXJ2aWNlcy9zbWFydGZvcm0uc2VydmljZS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQSxPQUFPLEVBQUUsVUFBVSxFQUFFLE1BQU0sZUFBZSxDQUFDO0FBQzNDLE9BQU8sRUFBRSxXQUFXLEVBQUUsU0FBUyxFQUFFLFVBQVUsRUFBRSxNQUFNLGdCQUFnQixDQUFDOztBQUlwRSxNQUFNLE9BQU8sZ0JBQWdCO0lBQzVCLFdBQVcsQ0FBQyxTQUFvQjtRQUMvQixNQUFNLEtBQUssR0FBUSxFQUFFLENBQUM7UUFFdEIsU0FBUyxDQUFDLE9BQU8sQ0FBQyxPQUFPLENBQUMsQ0FBQyxNQUFNLEVBQUUsRUFBRTtZQUNwQyxLQUFLLENBQUMsTUFBTSxDQUFDLEdBQUcsQ0FBQyxHQUFHLElBQUksV0FBVyxDQUNsQyxNQUFNLENBQUMsS0FBSyxJQUFJLEVBQUUsRUFDbEIsTUFBTSxDQUFDLFVBQVUsQ0FBQyxDQUFDLENBQUMsVUFBVSxDQUFDLFFBQVEsQ0FBQyxDQUFDLENBQUMsU0FBUyxDQUNuRCxDQUFDO1FBQ0gsQ0FBQyxDQUFDLENBQUM7UUFFSCxPQUFPLElBQUksU0FBUyxDQUFDLEtBQUssQ0FBQyxDQUFDO0lBQzdCLENBQUM7OzZHQVpXLGdCQUFnQjtpSEFBaEIsZ0JBQWdCOzJGQUFoQixnQkFBZ0I7a0JBRDVCLFVBQVUiLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQgeyBJbmplY3RhYmxlIH0gZnJvbSAnQGFuZ3VsYXIvY29yZSc7XHJcbmltcG9ydCB7IEZvcm1Db250cm9sLCBGb3JtR3JvdXAsIFZhbGlkYXRvcnMgfSBmcm9tICdAYW5ndWxhci9mb3Jtcyc7XHJcbmltcG9ydCB7IFNtYXJ0Rm9ybSB9IGZyb20gJy4uL3NtYXJ0Zm9ybS5tb2RlbCc7XHJcblxyXG5ASW5qZWN0YWJsZSgpXHJcbmV4cG9ydCBjbGFzcyBTbWFydEZvcm1TZXJ2aWNlIHtcclxuXHR0b0Zvcm1Hcm91cChzbWFydEZvcm06IFNtYXJ0Rm9ybSk6IEZvcm1Hcm91cCB7XHJcblx0XHRjb25zdCBncm91cDogYW55ID0ge307XHJcblxyXG5cdFx0c21hcnRGb3JtLndpZGdldHMuZm9yRWFjaCgod2lkZ2V0KSA9PiB7XHJcblx0XHRcdGdyb3VwW3dpZGdldC5rZXldID0gbmV3IEZvcm1Db250cm9sKFxyXG5cdFx0XHRcdHdpZGdldC52YWx1ZSB8fCAnJyxcclxuXHRcdFx0XHR3aWRnZXQuaXNSZXF1aXJlZCA/IFZhbGlkYXRvcnMucmVxdWlyZWQgOiB1bmRlZmluZWRcclxuXHRcdFx0KTtcclxuXHRcdH0pO1xyXG5cclxuXHRcdHJldHVybiBuZXcgRm9ybUdyb3VwKGdyb3VwKTtcclxuXHR9XHJcbn1cclxuIl19